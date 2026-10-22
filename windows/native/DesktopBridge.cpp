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
