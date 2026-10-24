#include "win_posix.hpp"
#include "../../engine/app/src/main/cpp/native_bridge.cpp"
#include <fcntl.h>
#include <filesystem>
#ifdef _WIN32
#include <io.h>
#else
#include <unistd.h>
#endif
namespace {
int openJavaPath(JNIEnv* env,jstring path,bool save){
 const jchar* chars=env->GetStringChars(path,nullptr);if(!chars)return -1;
 std::u16string text(reinterpret_cast<const char16_t*>(chars),env->GetStringLength(path));env->ReleaseStringChars(path,chars);
 try{
#ifdef _WIN32
  std::wstring name(text.begin(),text.end());return _wopen(name.c_str(),save?(_O_WRONLY|_O_CREAT|_O_TRUNC|_O_BINARY):(_O_RDONLY|_O_BINARY),_S_IREAD|_S_IWRITE);
#else
  std::string name=std::filesystem::path(text).string();return open(name.c_str(),save?(O_WRONLY|O_CREAT|O_TRUNC|O_CLOEXEC):(O_RDONLY|O_CLOEXEC),0600);
#endif
 }catch(...){return -1;}
}
void closeJavaFile(int fd){
#ifdef _WIN32
 _close(fd);
#else
 close(fd);
#endif
}
}
extern "C" JNIEXPORT jboolean JNICALL Java_art_velyntora_core_DrawingView_nativeProjectPath(JNIEnv* env,jclass cls,jstring path,jboolean save){
 int fd=openJavaPath(env,path,save);if(fd<0)return JNI_FALSE;auto result=save?Java_art_velyntora_core_DrawingView_nativeSaveProject(env,cls,fd):Java_art_velyntora_core_DrawingView_nativeOpenProject(env,cls,fd);closeJavaFile(fd);return result;
}
extern "C" JNIEXPORT jboolean JNICALL Java_art_velyntora_core_DrawingView_nativeStageObject(JNIEnv* env,jclass,jbyteArray encoded,jfloatArray transform,jbyteArray content,jintArray style){
 std::lock_guard<std::mutex> lock(guard);if(!placedImage||!encoded||!transform||env->GetArrayLength(transform)!=9)return JNI_FALSE;
 try{int count=env->GetArrayLength(encoded);if(count<1||count>velyntora::images::maxSourceBytes)return JNI_FALSE;auto image=std::make_shared<velyntora::images::Element>();env->GetFloatArrayRegion(transform,0,9,image->transform.data());
 std::size_t at=0;image->source=std::make_shared<velyntora::images::Source>(count,[&](void* out,std::size_t n){env->GetByteArrayRegion(encoded,at,n,reinterpret_cast<jbyte*>(out));at+=n;});if(env->ExceptionCheck()||!image->valid())return JNI_FALSE;placedImage->setActiveImage(image);
 if(content){if(!style||env->GetArrayLength(style)!=5)return JNI_FALSE;auto text=std::make_shared<velyntora::text::Element>();text->content.resize(env->GetArrayLength(content));env->GetByteArrayRegion(content,0,text->content.size(),reinterpret_cast<jbyte*>(text->content.data()));env->GetIntArrayRegion(style,0,5,text->style.data());if(env->ExceptionCheck()||!text->valid())return JNI_FALSE;placedImage->setActiveText(text);}return JNI_TRUE;
 }catch(...){return JNI_FALSE;}
}
extern "C" JNIEXPORT jbyteArray JNICALL Java_art_velyntora_core_DrawingView_nativeObjectSource(JNIEnv* env,jclass,jint index){
 std::lock_guard<std::mutex> lock(guard);if(!layers||index<0||std::size_t(index)>=layers->layerCount()||!layers->layer(index).image)return nullptr;
 try{auto source=layers->layer(index).image->source;auto out=env->NewByteArray(source->bytes);if(!out)return nullptr;std::size_t at=0;source->write([&](const void* data,std::size_t n){env->SetByteArrayRegion(out,at,n,reinterpret_cast<const jbyte*>(data));at+=n;});return out;}catch(...){return nullptr;}
}
#ifdef _WIN32
#include <windows.h>
#include <atomic>
#include <unordered_map>
namespace {std::atomic<float> penPressure{1.f};std::atomic<ULONGLONG> penTime{0};std::mutex penGuard;std::unordered_map<HWND,WNDPROC> penWindows;thread_local bool injectingPen=false;
LRESULT CALLBACK penProcedure(HWND hwnd,UINT message,WPARAM wp,LPARAM lp){WNDPROC original;{std::lock_guard<std::mutex> lock(penGuard);auto found=penWindows.find(hwnd);if(found==penWindows.end())return DefWindowProc(hwnd,message,wp,lp);original=found->second;}
 if(message==WM_POINTERDOWN||message==WM_POINTERUPDATE||message==WM_POINTERUP){POINTER_PEN_INFO info{};if(GetPointerPenInfo(GET_POINTERID_WPARAM(wp),&info)){penPressure=info.penMask&PEN_MASK_PRESSURE?std::max(.01f,info.pressure/1024.f):1.f;penTime=GetTickCount64();POINT point=info.pointerInfo.ptPixelLocation;ScreenToClient(hwnd,&point);UINT forwarded=message==WM_POINTERDOWN?WM_LBUTTONDOWN:message==WM_POINTERUP?WM_LBUTTONUP:WM_MOUSEMOVE;injectingPen=true;LRESULT result=CallWindowProc(original,hwnd,forwarded,(message!=WM_POINTERUP&&(info.pointerInfo.pointerFlags&POINTER_FLAG_INCONTACT))?MK_LBUTTON:0,MAKELPARAM(point.x,point.y));injectingPen=false;return result;}}
 if((message==WM_MOUSEMOVE||message==WM_LBUTTONDOWN)&&!injectingPen){penPressure=1;penTime=0;}
 LRESULT result=CallWindowProc(original,hwnd,message,wp,lp);if(message==WM_NCDESTROY){std::lock_guard<std::mutex> lock(penGuard);penWindows.erase(hwnd);}return result;
}}
namespace {
void attachPenWindow(HWND hwnd){std::lock_guard<std::mutex> lock(penGuard);if(!IsWindow(hwnd)||penWindows.count(hwnd))return;auto original=reinterpret_cast<WNDPROC>(SetWindowLongPtr(hwnd,GWLP_WNDPROC,reinterpret_cast<LONG_PTR>(penProcedure)));if(original)penWindows[hwnd]=original;}
BOOL CALLBACK attachPenChild(HWND hwnd,LPARAM){attachPenWindow(hwnd);return TRUE;}
}
extern "C" JNIEXPORT void JNICALL Java_art_velyntora_core_DrawingView_nativeAttachPointer(JNIEnv*,jclass,jlong value){HWND hwnd=reinterpret_cast<HWND>(static_cast<std::intptr_t>(value));attachPenWindow(hwnd);EnumChildWindows(hwnd,attachPenChild,0);}
extern "C" JNIEXPORT jfloat JNICALL Java_art_velyntora_core_DrawingView_nativePointerPressure(JNIEnv*,jclass){return GetTickCount64()-penTime.load()<500?penPressure.load():1.f;}
#else
#include "../../linux/native/LinuxPointer.hpp"
#endif
extern "C" JNIEXPORT jboolean JNICALL Java_art_velyntora_core_DrawingView_nativeRecoveryPath(JNIEnv* env,jclass cls,jstring path,jlong expected){
 int fd=openJavaPath(env,path,true);if(fd<0)return JNI_FALSE;auto result=Java_art_velyntora_core_DrawingView_nativeSaveRecovery(env,cls,fd,expected);closeJavaFile(fd);return result;
}
// Actual allocated pixel payload in the current layer set (not a dense canvas estimate).
extern "C" JNIEXPORT jlong JNICALL Java_art_velyntora_core_DrawingView_nativeDocumentBytes(JNIEnv*,jclass){
 std::lock_guard<std::mutex> lock(guard);if(!layers)return 0;std::uint64_t bytes=0;for(std::size_t i=0;i<layers->layerCount();++i)bytes+=layers->layer(i).pixels.allocatedPixels()*sizeof(std::uint32_t);return static_cast<jlong>(bytes);
}
