#pragma once
#ifdef _WIN32
#include <io.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <algorithm>
#include <cstring>
#include <cstdlib>
#include <cstdint>
#include <cstdio>
inline int velyntora_mkstemp(char* name){
    for(int n=0;n<100;n++){
        auto length=std::strlen(name);char candidate[7];std::snprintf(candidate,sizeof(candidate),"%06x",unsigned(std::rand())&0xffffff);std::memcpy(name+length-6,candidate,6);
        int fd=_open(name,_O_CREAT|_O_EXCL|_O_RDWR|_O_BINARY|_O_TEMPORARY,_S_IREAD|_S_IWRITE);if(fd>=0)return fd;
    }return -1;
}
inline long long velyntora_pwrite(int fd,const void* data,std::size_t count,std::uint64_t offset){if(_lseeki64(fd,offset,SEEK_SET)<0)return -1;return _write(fd,data,unsigned(std::min<std::size_t>(count,0x7fffffff)));}
inline long long velyntora_pread(int fd,void* data,std::size_t count,std::uint64_t offset){if(_lseeki64(fd,offset,SEEK_SET)<0)return -1;return _read(fd,data,unsigned(std::min<std::size_t>(count,0x7fffffff)));}
#define mkstemp velyntora_mkstemp
#define pwrite64 velyntora_pwrite
#define pread64 velyntora_pread
#endif
