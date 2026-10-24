#pragma once
#include <X11/Xlib.h>
#include <X11/extensions/XInput2.h>
#include <atomic>
#include <chrono>
#include <thread>
#include <mutex>
#include <map>
#include <sys/select.h>
/** Independent XInput2 connection; AWT still delivers positions/buttons to the canvas. */
namespace {
class LinuxPen {
 struct Axis{int number;double minimum,maximum;};
 std::atomic<bool> stop{false},started{false};std::thread reader;
 std::atomic<float> pressure{1.f};std::atomic<long long> updated{0};
 static long long now(){return std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::steady_clock::now().time_since_epoch()).count();}
 void run(){Display* display=XOpenDisplay(nullptr);if(!display)return;int opcode,event,error,major=2,minor=0;if(!XQueryExtension(display,"XInputExtension",&opcode,&event,&error)||XIQueryVersion(display,&major,&minor)!=Success){XCloseDisplay(display);return;}
  std::map<int,Axis> axes;Atom absolute=XInternAtom(display,"Abs Pressure",False),touch=XInternAtom(display,"Abs MT Pressure",False);
  auto devices=[&]{axes.clear();int count=0;XIDeviceInfo* list=XIQueryDevice(display,XIAllDevices,&count);if(!list)return;for(int i=0;i<count;i++)for(int c=0;c<list[i].num_classes;c++){auto info=list[i].classes[c];if(info->type!=XIValuatorClass)continue;auto value=reinterpret_cast<XIValuatorClassInfo*>(info);if((value->label==absolute||value->label==touch)&&value->max>value->min)axes[list[i].deviceid]={value->number,value->min,value->max};}XIFreeDeviceInfo(list);};devices();
  unsigned char bits[XIMaskLen(XI_LASTEVENT)]{};XISetMask(bits,XI_RawMotion);XISetMask(bits,XI_RawButtonPress);XISetMask(bits,XI_RawButtonRelease);XISetMask(bits,XI_HierarchyChanged);XIEventMask mask{XIAllDevices,sizeof(bits),bits};XISelectEvents(display,DefaultRootWindow(display),&mask,1);XFlush(display);
  while(!stop){while(XPending(display)){XEvent event{};XNextEvent(display,&event);if(event.type!=GenericEvent||event.xcookie.extension!=opcode||!XGetEventData(display,&event.xcookie))continue;
    if(event.xcookie.evtype==XI_HierarchyChanged)devices();else if(event.xcookie.evtype==XI_RawMotion||event.xcookie.evtype==XI_RawButtonPress||event.xcookie.evtype==XI_RawButtonRelease){auto raw=static_cast<XIRawEvent*>(event.xcookie.data);auto found=axes.find(raw->sourceid);if(found==axes.end())found=axes.find(raw->deviceid);if(found!=axes.end()){if(event.xcookie.evtype==XI_RawButtonRelease){pressure=1.f;updated=0;}else{int at=0;for(int axis=0;axis<raw->valuators.mask_len*8;axis++)if(XIMaskIsSet(raw->valuators.mask,axis)){double value=raw->valuators.values[at++];if(axis==found->second.number){pressure=std::clamp(float((value-found->second.minimum)/(found->second.maximum-found->second.minimum)),.01f,1.f);updated=now();}}}}else if(event.xcookie.evtype==XI_RawButtonPress){pressure=1.f;updated=0;}}
    XFreeEventData(display,&event.xcookie);
   }fd_set fds;FD_ZERO(&fds);FD_SET(ConnectionNumber(display),&fds);timeval timeout{0,25000};select(ConnectionNumber(display)+1,&fds,nullptr,nullptr,&timeout);
  }XCloseDisplay(display);
 }
public:
 void attach(){bool expected=false;if(started.compare_exchange_strong(expected,true)){XInitThreads();reader=std::thread([this]{run();});}}
 float sample(){return now()-updated.load()<500?pressure.load():1.f;}
 ~LinuxPen(){stop=true;if(reader.joinable())reader.join();}
};
LinuxPen& linuxPen(){static LinuxPen pen;return pen;}
}
extern "C" JNIEXPORT void JNICALL Java_art_velyntora_core_DrawingView_nativeAttachPointer(JNIEnv*,jclass,jlong){linuxPen().attach();}
extern "C" JNIEXPORT jfloat JNICALL Java_art_velyntora_core_DrawingView_nativePointerPressure(JNIEnv*,jclass){return linuxPen().sample();}
