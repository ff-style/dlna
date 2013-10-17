/*************************************************

  Copyright (C), 2001-2010, Fone Net Info – Tech Co., Ltd

  File name:      player_log.h

  Description:

  Others:

*************************************************/

#ifndef _player_LOG_H_
#define _player_LOG_H_

#include "player_std.h"

#ifdef __cplusplus
extern "C"
{
#endif

#if defined(player_PLATFORM_ANDROID)
#include "player_jni.h"
#elif defined(player_PLATFORM_IPHONE)
#include "player_iOS_log.h"
// #else
#endif

#ifdef player_LOG_ON


// Function name	: player_log
// Description	    : 以文本方式（\0结束）写入该字符串，另外在行首自动添加当前时间
// Return type		: void
// Argument         : FN_CONST void *a_p
player_IMPORT void player_log(FN_CONST void *a_p);

// Function name	: player_log_bin
// Description	    : 写入[a_pBuf,a_pBuf+a_nBufLen-1]到a_pFileName文件中
// Return type		: void
// Argument         : FN_CONST void* a_pFileName
// Argument         : FN_CONST void* a_pBuf
// Argument         : FN_CONST FN_INT a_nBufLen
player_IMPORT void player_log_bin(FN_CONST void* a_pFileName, FN_CONST void* a_pBuf, FN_CONST FN_INT a_nBufLen);

//
// Add by Higher, Provide an easy way to log multi-variables, as easy as printf 
//
player_IMPORT void player_log_easy(char *fmt,...);

#else
#define player_log(a)
#define player_log_bin(a, b, c)
#define player_log_easy(fmt,...)
#endif

// Add by Higher, Provide an easy way to log multi-variables, as easy as printf 
#define player_LOG_SIZE_OF_EASY_WAY  2048  //96

#ifdef __cplusplus
}
#endif

#endif
