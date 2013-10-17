/*************************************************

  Copyright (C), 2001-2010, Fone Net Info �C Tech Co., Ltd

  File name:      player_platform_file_sys.h

  Description:

  Others:

*************************************************/

#ifndef _player_PLATFORM_FILE_SYS_H_
#define _player_PLATFORM_FILE_SYS_H_

#include "player_std.h"

#ifdef __cplusplus 
extern "C"
{
#endif

#ifndef player_IMPORT
#define player_IMPORT
#endif

#ifndef player_EXPORT
#define player_EXPORT
#endif

typedef FN_INT player_FILE;

#define player_FILE_SEEK_SET    0               // �ļ��ͷ
#define player_FILE_SEEK_CUR    1               // �ļ���ǰλ��
#define player_FILE_SEEK_END    2               // �ļ�ĩβ

#define player_FILE_MODE_READ					1 // ֻ����ʽ������ļ������ڣ��򷵻�ʧ��
#define player_FILE_MODE_WRITE				2 // д�Ͷ���ʽ������ļ������ڣ��򷵻�ʧ��
#define player_FILE_MODE_CREATE_ALWAYS_WRITE	4 // �ļ����������Զ�����֮���Ѵ������������
#define player_FILE_MODE_APPEND				8 // ���ļ�ʹ�䴦��׷��״̬�����ļ�ĩβд�룻��ͨ���ƶ�fp�ı��д��λ�ã����ļ����Ѵ��ڵ����ݲ��ᱻ�Զ���գ�����ļ������ڣ��򴴽����ļ�
                                              // ע�⣺�÷�ʽ��Բ���ͬ��fopen("...", "a+b"),ʵ���߽�ֹʹ��"a+b"��ʽʵ�֣�"a+b"����"ab"�ᵼ��д������ʼ�����ļ�ĩβ��fseek�ƶ���Ч��
                                              // �����⣬����YanWei��ϵ

typedef struct tagFonePlatformFileSysMem
{
	void *pData;
} FonePlatformFileSysMem_t; // ��ģ����ʹ�õ�ȫ���ڴ棻�ڳ����ʼ��ʱֱ�������˳�


typedef enum tagFoneResDirType
{
    player_RES_DIR_TYPE_EXE = 0,                       // �����װĿ¼,��.exe���ڵ�Ŀ¼
    player_RES_DIR_TYPE_PIC,                           // �����װʱ�Դ��ͼƬĿ¼
    player_RES_DIR_TYPE_END
} FoneResDirType_e;


player_IMPORT void player_file_sys_init(void);
player_IMPORT void player_file_sys_uninit(void);
// Function name	: player_create_dir
// Description	    : ����Ŀ¼������丸Ŀ¼�����ڣ��ݹ鴴��
// Return type		: FN_BOOL 
// Argument         : FN_CONST FN_CHAR* a_pchDirName
player_IMPORT FN_BOOL player_create_dir(FN_CONST FN_CHAR* a_pchDirName); // recursive default

// Function name	: player_delete_file
// Description	    : ɾ���ļ�
// Return type		: FN_BOOL 
// Argument         : FN_CONST FN_CHAR* a_pchFileName
player_IMPORT FN_BOOL player_delete_file(FN_CONST FN_CHAR* a_pchFileName);

// Function name	: player_res_get_directory
// Description	    : ��ȡϵͳԤ�����Ŀ¼,��'\\'��β
// Return type		: FN_CONST FN_CHAR* :null-terminated string of a full path
// Argument         : FoneResDirType_e aType
player_IMPORT FN_BOOL player_res_get_directory(FoneResDirType_e aType, OUT FN_CHAR* a_pPathName);


// Function name	: player_delete_dir
// Description	    : ɾ��Ŀ¼����Ŀ¼�µ������ļ�(����Ŀ¼)
// Return type		: FN_BOOL 
// Argument         : FN_CONST FN_CHAR* a_pchDirName
// Argument         : FN_BOOL a_bIncludeDir���Ƿ����Ŀ¼����
player_IMPORT FN_BOOL player_delete_dir(FN_CONST FN_CHAR* a_pchDirName, FN_BOOL a_bIncludeDir);



// Function name	: player_rename_file
// Description	    : �������ļ�����������ļ�Ŀ¼�����ڣ�����ʧ��
// Return type		: FN_BOOL 
// Argument         : FN_CONST FN_CHAR* a_pchOldName
// Argument         : FN_CONST FN_CHAR* a_pchNewName
player_IMPORT FN_BOOL player_rename_file(FN_CONST FN_CHAR* a_pchOldName, FN_CONST FN_CHAR* a_pchNewName);


// Function name	: player_open_file
// Description	    : ���ļ���������ʹ���/��/׷�����
// Return type		: player_FILE*: NULL ��ʾʧ�ܣ���NULL��ɹ�
// Argument         : FN_CONST FN_CHAR* a_pchFileName
// Argument         : FN_INT a_nOpenMode
player_IMPORT player_FILE* player_open_file(FN_CONST FN_CHAR* a_pchFileName, FN_INT a_nOpenMode);

player_IMPORT FN_SIZE player_read_file(player_FILE*, void* aBuf, FN_SIZE a_tLen);
player_IMPORT FN_SIZE player_write_file(player_FILE*, FN_CONST void* a_pBuf, FN_SIZE a_tLen);
player_IMPORT FN_INT player_seek_file(player_FILE* aFileHandle, FN_INT a_nOffset, FN_INT a_nBase);
player_IMPORT FN_SIZE player_ftell(player_FILE*);
player_IMPORT void player_close_file(player_FILE* aFileHandle);
player_IMPORT FN_SIZE player_get_file_size( player_FILE* );
player_IMPORT FN_BOOL player_is_file_exist( FN_CONST FN_CHAR* pFileName );

// Function name	: player_commit_drive
// Description	    : �ύ���̷��Ӧ���ļ����ɵײ㱣֤�����ļ�������commit
// Return type		: void 
// Argument         : FN_CHAR a_chDriveName
player_IMPORT void player_commit_drive(FN_CHAR a_chDriveName);


// Function name	: player_get_sys_reset
// Description	    : Ϊ�����û�Ƶ����δ洢��������ң�����Ӧ�ڳ�ʼ����ǰ�˵��øú���
// Return type		: void 
// Argument         : void
player_IMPORT void player_get_sys_drive_reset(void);


player_IMPORT FN_CONST FN_CHAR* player_get_install_drive(void);

// Function name	: player_get_sys_drive
// Description	    : ��ȡfone��ݶ�Ӧ�ĸ�Ŀ¼������ %s\tv\builtin\...
// Return type		: FN_CONST FN_CHAR*: �����ַ������������ͷ��ڴ�
// Argument         : void
player_IMPORT FN_CONST FN_CHAR* player_get_sys_drive(void);


player_IMPORT FN_CONST FN_CHAR* player_get_media_drive(void);

/*
player_IMPORT FN_BOOL player_uncompress_gzipdata(FN_BYTE* a_pDesData,  FN_DWORD* a_nDesLen,
					FN_CONST FN_BYTE* a_pSrcData, FN_DWORD a_nSrcLen);
*/
#ifdef __cplusplus
}
#endif

#endif
