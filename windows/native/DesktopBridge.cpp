#include "win_posix.hpp"
#include "../../engine/app/src/main/cpp/native_bridge.cpp"
#include <fcntl.h>
#include <io.h>
extern "C" JNIEXPORT jboolean JNICALL Java_art_velyntora_core_DrawingView_nativeProjectPath(JNIEnv* env,jclass cls,jstring path,jboolean save){
    const jchar* text=env->GetStringChars(path,nullptr);if(!text)return JNI_FALSE;
    std::wstring filename(reinterpret_cast<const wchar_t*>(text),env->GetStringLength(path));
    int fd=_wopen(filename.c_str(),save?(_O_WRONLY|_O_CREAT|_O_TRUNC|_O_BINARY):(_O_RDONLY|_O_BINARY),_S_IREAD|_S_IWRITE);
    env->ReleaseStringChars(path,text);if(fd<0)return JNI_FALSE;
    auto result=save?Java_art_velyntora_core_DrawingView_nativeSaveProject(env,cls,fd):Java_art_velyntora_core_DrawingView_nativeOpenProject(env,cls,fd);
    _close(fd);return result;
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
#include <windows.h>
#include <atomic>
#include <unordered_map>
namespace {std::atomic<float> penPressure{1.f};std::atomic<ULONGLONG> penTime{0};std::mutex penGuard;std::unordered_map<HWND,WNDPROC> penWindows;thread_local bool injectingPen=false;
LRESULT CALLBACK penProcedure(HWND hwnd,UINT message,WPARAM wp,LPARAM lp){WNDPROC original;{std::lock_guard<std::mutex> lock(penGuard);auto found=penWindows.find(hwnd);if(found==penWindows.end())return DefWindowProc(hwnd,message,wp,lp);original=found->second;}
 if(message==WM_POINTERDOWN||message==WM_POINTERUPDATE||message==WM_POINTERUP){POINTER_PEN_INFO info{};if(GetPointerPenInfo(GET_POINTERID_WPARAM(wp),&info)){penPressure=info.penMask&PEN_MASK_PRESSURE?std::max(.01f,info.pressure/1024.f):1.f;penTime=GetTickCount64();POINT point=info.pointerInfo.ptPixelLocation;ScreenToClient(hwnd,&point);UINT forwarded=message==WM_POINTERDOWN?WM_LBUTTONDOWN:message==WM_POINTERUP?WM_LBUTTONUP:WM_MOUSEMOVE;injectingPen=true;LRESULT result=CallWindowProc(original,hwnd,forwarded,message==WM_POINTERUP?0:MK_LBUTTON,MAKELPARAM(point.x,point.y));injectingPen=false;return result;}}
 if((message==WM_MOUSEMOVE||message==WM_LBUTTONDOWN)&&!injectingPen){penPressure=1;penTime=0;}
 LRESULT result=CallWindowProc(original,hwnd,message,wp,lp);if(message==WM_NCDESTROY){std::lock_guard<std::mutex> lock(penGuard);penWindows.erase(hwnd);}return result;
}}
extern "C" JNIEXPORT void JNICALL Java_art_velyntora_core_DrawingView_nativeAttachPointer(JNIEnv*,jclass,jlong value){HWND hwnd=reinterpret_cast<HWND>(static_cast<std::intptr_t>(value));if(!IsWindow(hwnd))return;std::lock_guard<std::mutex> lock(penGuard);if(penWindows.count(hwnd))return;auto original=reinterpret_cast<WNDPROC>(SetWindowLongPtr(hwnd,GWLP_WNDPROC,reinterpret_cast<LONG_PTR>(penProcedure)));if(original){penWindows[hwnd]=original;RegisterPointerInputTarget(hwnd,PT_PEN);}}
extern "C" JNIEXPORT jfloat JNICALL Java_art_velyntora_core_DrawingView_nativePointerPressure(JNIEnv*,jclass){return GetTickCount64()-penTime.load()<500?penPressure.load():1.f;}
extern "C" JNIEXPORT jboolean JNICALL Java_art_velyntora_core_DrawingView_nativeRecoveryPath(JNIEnv* env,jclass cls,jstring path,jlong expected){
 const jchar* chars=env->GetStringChars(path,nullptr);if(!chars)return JNI_FALSE;std::wstring file(reinterpret_cast<const wchar_t*>(chars),env->GetStringLength(path));env->ReleaseStringChars(path,chars);int fd=_wopen(file.c_str(),_O_CREAT|_O_TRUNC|_O_WRONLY|_O_BINARY,_S_IREAD|_S_IWRITE);if(fd<0)return JNI_FALSE;auto result=Java_art_velyntora_core_DrawingView_nativeSaveRecovery(env,cls,fd,expected);_close(fd);return result;
}
