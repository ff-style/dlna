/*************************************************

  Copyright (C), 2001-2010, Fone Net Info – Tech Co., Ltd

  File name:      fone_std.h

  Description:

  Others:

*************************************************/

#ifndef FONE_STD_H_
#define FONE_STD_H_

#ifndef NULL
	#ifdef  __cplusplus
		#define NULL    0
	#else
		#define NULL    ((void *)0)
	#endif
#endif

#ifndef FONE_IMPORT
#define FONE_IMPORT
#endif

#ifndef FONE_EXPORT
#define FONE_EXPORT
#endif

#define FN_CONST const
#define FN_NULL NULL

#define FN_SUCCESS	0
#define FN_FAILED	-1

typedef int FN_BOOL;
#define FN_TRUE     1
#define FN_FALSE    0

#define IN      // 表示传入，默认，可以不用加
#define INOUT   // 表示传入、传出参数
#define OUT     // 表示传出参数

#ifndef FONE_UNREFERENCED_PARAMETER
//#define FONE_UNREFERENCED_PARAMETER(P){(P) = (P);}
#define FONE_UNREFERENCED_PARAMETER(P){(P);}
#endif

#define FONE_MIN(x,y)			(((x)<(y))?(x):(y))

typedef unsigned int    FN_SIZE;
typedef char            FN_CHAR;
typedef int             FN_INT;
typedef unsigned int    FN_UINT;
typedef long            FN_LONG;
typedef short           FN_SHORT;
typedef unsigned long   FN_DWORD;
typedef unsigned short  FN_WORD;
typedef unsigned char   FN_BYTE;
typedef unsigned char   FN_UTF8;
typedef unsigned short  FN_WCHAR;
typedef float           FN_FLOAT;
typedef double          FN_DOUBLE;
typedef void            FN_VOID;

#if defined(WIN32)
typedef __int64			FN_64;
#else
typedef long long       FN_64;
#endif

#define FN_MAKEDWORD(a, b)  ((FN_DWORD)(((FN_WORD)(a)) | ((FN_DWORD)((FN_WORD)(b))) << 16))
#define FN_MAKEWORD(a, b)   ((FN_WORD)(((FN_BYTE)(a)) | ((FN_WORD)((FN_BYTE)(b))) << 8))
#define FN_LOWORD(l)        ((FN_WORD)(l))
#define FN_HIWORD(l)        ((FN_WORD)(((FN_DWORD)(l) >> 16) & 0xFFFF))
#define FN_LOBYTE(w)         ((FN_BYTE)(w))
#define FN_HIBYTE(w)         ((FN_BYTE)(((FN_WORD)(w) >> 8) & 0xFF))

typedef struct fone_time 
{
    FN_INT year;    /* 年份。			*/
    FN_INT mon;     /* 月份，从1开始。	*/
    FN_INT day;     /* 天，从1开始。	*/
    FN_INT hour;    /* 小时，24小时制。	*/
    FN_INT min;     /* 分钟。			*/
    FN_INT sec;     /* 秒。				*/
    FN_INT millisec;   /* 毫秒          */
} Fone_TIME;

typedef struct tagFone_Rect
{
	FN_INT left;
	FN_INT top;
	FN_INT right;
	FN_INT bottom;
} FN_RECT;
typedef unsigned int FN_WPARAM;
typedef long FN_LPARAM;

typedef FN_INT fone_http_handle;
typedef FN_INT	FONE_TIMER_ID;

typedef enum tagFoneContentLookType
{
	FONE_CONTENT_LOOK_TYPE_FREELOAD_LIVE = 0,   // 免流量在线直播
	FONE_CONTENT_LOOK_TYPE_FREELOAD_VOD, 		// 免流量在线点播
	FONE_CONTENT_LOOK_TYPE_AVI_VOD,          // avi点播
	FONE_CONTENT_LOOK_TYPE_3GP_DOWNLOAD,     // 3gp下载
	FONE_CONTENT_LOOK_TYPE_FREELOAD_DOWNLOAD // 免流量下载
} FoneContentLookType_e;

//消息提示框类型
typedef enum tagFone_Message_Type
{
	FN_MESSAGE_TYPE_OK = 1,//”确定”
	FN_MESSAGE_TYPE_OKCANCEL ,//”确定””取消”
	FN_MESSAGE_TYPE_CANCEL,//”取消”
	FN_MESSAGE_TYPE_AUTO,//自动消失类型
	FN_MESSAGE_TYPE_YESNO,//"是","否"
	FN_MESSAGE_TYPE_RETRYCANCEL //"重试""取消"
}Fone_Message_Type_e;


typedef FN_UINT FN_CodecID;


//typedef signed FN_INT S32;
//typedef signed short S16;
//typedef signed FN_CHAR S8;

/*end add*/

// naming spec
// prefix ret_type func_name(arg_type1 arg_name1, art_type2 arg_name2, ...);
// prefix:     static extern
// ret_type:   FN_INT void * FN_CHAR ...
// func_name:  fone_set/get/file/...
// arg_type:   FN_INT void * FN_CHAR...
// arg_name:   (prefix) + (_) + (arg_type_abbr) + (arg_name_meaning)
//             prefix       : a
//             _            : _
//             arg_type_abbr: combination of ch(FN_CHAR), n(FN_INT), t(FN_SIZE), b(BOOL), p(*), u(unsigned)
//             arg_name_mean: Word(Capital in first letter) e.g. Volume, Quality...


#endif
