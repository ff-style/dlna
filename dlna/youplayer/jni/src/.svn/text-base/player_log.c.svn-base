/*************************************************

  Copyright (C), 2001-2010, Fone Net Info 锟� Tech Co., Ltd

  File name:      player_log.c

  Description:

  Others:

*************************************************/


#include "player_std.h"
#include "player_platform_file_sys.h"
#include "player_log.h"
#include "player_jni.h"
#include <stdarg.h>
#include <stdio.h>
#include <jni.h>
#define player_strlen strlen

#include <sys/stat.h>
#include <sys/types.h>
#include  <unistd.h>
#ifdef player_LOG_ON
player_FILE* player_open_file(FN_CONST FN_CHAR* a_pchFileName, FN_INT a_nOpenMode)
{
    player_FILE *fp = NULL;
    char *pOpenMode;
    switch (a_nOpenMode)
    {
    case player_FILE_MODE_READ:
        pOpenMode = "rb";
        break;
    case player_FILE_MODE_WRITE:
        pOpenMode = "r+b";
        break;
    case player_FILE_MODE_CREATE_ALWAYS_WRITE:
        pOpenMode = "wb";
        break;
    case player_FILE_MODE_APPEND:
        pOpenMode = "r+b"; // 锟斤拷�╋拷锟斤拷锝�拷锟姐�锟斤拷锟解�锟斤拷锟斤拷锟�b"/"a+b",锟斤拷锟芥�姊�拷�ワ拷锟斤拷锟斤拷��拷锟斤拷��拷锛��锟介�锟斤拷锟斤拷锟斤拷锟斤拷锟借�纰�拷锟斤拷锟斤拷锟斤拷搴�拷锟斤拷
        break;
    default:
        return (player_FILE*)NULL;
    }

    fp = (player_FILE*)fopen(a_pchFileName, pOpenMode);

    if(a_nOpenMode == player_FILE_MODE_APPEND)
    {
        if(fp != NULL) // 锟斤拷锟斤拷锟斤拷妤硷拷锟斤拷锟界�锟斤拷锟芥�锟芥�绡�拷锟斤拷锟斤拷锟�锟斤拷锟斤拷锟斤拷婊粹�锟斤拷�革拷锟解�锟斤拷
            fseek((FILE*)fp, 0, SEEK_END);
        else // 锟斤拷锟斤拷��拷��拷锟斤拷锟界�锟斤拷锟芥�锟斤饥��拷锟芥ゼ锟介�锟解�锟戒豢锟斤拷锟斤拷瀛わ饥锟斤拷韫�拷灏�拷�匡拷锟斤拷锟斤拷锟芥ゼ妤兼�搴�拷锟斤拷锟斤拷娼�拷
            fp = (player_FILE*)fopen(a_pchFileName, "w+b");
    }

    return (player_FILE*)fp;
}

FN_SIZE player_read_file(player_FILE* hFile, void* aBuf, FN_SIZE a_tLen)
{
    return fread(aBuf, 1, a_tLen, (FILE*)hFile);
}

FN_SIZE player_write_file(player_FILE* hFile, FN_CONST void* a_pBuf, FN_SIZE a_tLen)
{
    return fwrite(a_pBuf, 1, a_tLen, (FILE*)hFile);
}

FN_INT player_seek_file(player_FILE* aFileHandle, FN_INT a_nOffset, FN_INT a_nBase)
{
    return fseek((FILE*)aFileHandle, a_nOffset, a_nBase);
}

void player_close_file(player_FILE* aFileHandle)
{
    fclose((FILE*)aFileHandle);
}

void player_log(FN_CONST void* a_pBuf)
{
   	if (!a_pBuf)
    		return;
    
	LOGI("%s\n", a_pBuf);
	
#if 0
    FN_CONST FN_CHAR *pBuf = (FN_CONST FN_CHAR*)a_pBuf;
    player_FILE* fileHandle;
    FN_INT mode;
    FN_CHAR szFullPathName[player_FULL_PATHFILENAME_LEN];
    FoneLogMem_t* pMem = player_log_get_global_mem();
    
	if(!pMem->bOn)
		return;

    player_sprintf(szFullPathName, player_LOG_PATHNAME_FORMAT, player_get_sys_drive());
    
    mode = player_FILE_MODE_APPEND;
    fileHandle = player_open_file(szFullPathName, mode);
    
    if(fileHandle != NULL)
    {
        FN_INT nBufLen = player_strlen(pBuf);
        player_TIME ftm;
        FN_CHAR pDateTime[200];

        player_get_date_time(&ftm);
        player_sprintf(pDateTime, "[%4d-%.2d-%.2d %.2d:%.2d:%.2d.%.3d] ",
            ftm.year, ftm.mon, ftm.day, ftm.hour, ftm.min, ftm.sec, ftm.millisec);

        if(pMem->szFlagBuf[0] != 0x00)
        {
        	player_write_file(fileHandle, "(", 1);
        	player_write_file(fileHandle, pMem->szFlagBuf, player_strlen(pMem->szFlagBuf));
        	player_write_file(fileHandle, ")", 1);
        }
        
        player_write_file(fileHandle, pDateTime, player_strlen(pDateTime));
        player_write_file(fileHandle, pBuf, nBufLen);
        player_write_file(fileHandle, "\r\n", 2);
        player_close_file(fileHandle);
    }
#endif
}

void player_log_bin(FN_CONST void* a_pFileName, FN_CONST void* a_pBuf, FN_CONST FN_INT a_nBufLen)
{
	if (a_pFileName && a_pBuf)
	{
		FN_CONST FN_CHAR *pFileName = (FN_CONST FN_CHAR*)a_pFileName;
		player_FILE* fp = player_open_file(pFileName, player_FILE_MODE_CREATE_ALWAYS_WRITE);
		if(fp != NULL)
		{
			player_write_file(fp, a_pBuf, a_nBufLen);
			player_close_file(fp);
		}
	}
}

//
// Add by Higher, Provide an easy way to log multi-variables, as easy as printf 
//
void player_log_easy(char *fmt,...)
{
    static FN_CHAR buf[player_LOG_SIZE_OF_EASY_WAY];
    va_list ap; 
    if(player_strlen(fmt) >= (player_LOG_SIZE_OF_EASY_WAY >> 1))
    {
        // Make the best way to avoid bound overflow
        return;
    }    
    va_start(ap,fmt); 
    vsprintf(buf,fmt,ap);
    va_end(ap); 
    buf[player_LOG_SIZE_OF_EASY_WAY - 1] = 0;
    player_log(buf);
}

#endif

