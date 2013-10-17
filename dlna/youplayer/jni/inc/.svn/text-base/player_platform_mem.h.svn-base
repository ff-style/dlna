/*************************************************

  Copyright (C), 2001-2010, Fone Net Info �C Tech Co., Ltd

  File name:      player_platform_mem.h

  Description:

  Others:

*************************************************/
 
#ifndef _player_PLATFORM_MEM_H_
#define _player_PLATFORM_MEM_H_

#include "player_std.h"
#include "player_mem.h"
#include "player_platform_file_sys.h"
#ifdef __cplusplus
extern "C"
{
#endif

typedef struct tagFonePlatformMemMem
{
    int dummy;    
} FonePlatformMemMem_t; // ��ģ����ʹ�õ�ȫ���ڴ棻�ڳ����ʼ��ʱֱ�������˳�

/*
����
1. ������ڲ��꿪�أ������׶�ʹ���ڲ�����ͳ���ڴ棬�����ж�����/���硢�ڴ�й©�����ֵ����Ϣ�������쳣ʱ����player_assert��ӡ��������Ϣ��
   ���������ʱ�ر�ͳ�ƹ���
2. player_memory_global_malloc ����ı�����һ���Ƿ�����player_memory_init��player_malloc�������֮ǰ
*/


// ����������Ϊ�����ĺ����װ�����²��ĺ���û���κ�Ӱ��
// Function name	: player_memory_global_malloc
// Description	    : ����һƬ�ڴ�Ϊ��������ģ���ȫ�ֱ�������̬����ʹ�ã�
//                    һ��ֻ����һ�Σ�ʵ���߱��뱣֤���κ�ʱ�̵��øú����ܳɹ����䵽�ÿ��ڴ棬���������κ�������Ҳ�������ڴ�ͳ��
// Return type		: void* 
// Argument         : FN_SIZE a_nSize
player_IMPORT void* player_memory_global_malloc(FN_SIZE a_nSize);

// Function name	: player_memory_global_free
// Description	    : �ͷŵ����ڴ棬һ��Ҫ�ڳ����˳���ĩ�˵��ã���֮���κ�ģ�����ȫ���ڴ���ʧ��
// Return type		: void 
// Argument         : void* a_pGlobalPtr
player_IMPORT void player_memory_global_free(void* a_pGlobalPtr);


// Function name	: player_os_malloc
// Description	    : �ڴ����ģ����Ҫ��ϵͳ�ڴ����ӿ�
// Argument         : FN_INT a_nSize
player_IMPORT void* player_os_malloc(FN_INT a_nSize);


// Function name	: player_free
// Description	    : �ڴ����ģ����Ҫ��ϵͳ�ڴ��ͷŽӿ�
// Return type		: void 
// Argument         : void* a_pPtr
player_IMPORT void player_os_free(void* a_pPtr);

#ifdef player_MEM_NEED_PLATFORM_INIT
// Function name	: player_free
// Description	    : �ڴ����ģ����Ҫ��ϵͳ�ڴ��ʼ���ӿ�
// Return type		: void 
player_IMPORT FN_BOOL player_os_memory_init(void);

// Function name	: player_free
// Description	    : �ڴ����ģ����Ҫ��ϵͳ�ڴ������ӿ�
// Return type		: void 
player_IMPORT void player_os_memory_uninit(void);
#endif

player_IMPORT void player_memset(void* ptr, FN_INT ch, FN_INT size);
player_IMPORT FN_INT player_memcmp(FN_CONST void*, FN_CONST void*, FN_INT size);
player_IMPORT void player_memcpy(void*, FN_CONST void*, FN_INT size);
player_IMPORT void player_memmove(void*, FN_CONST void*, FN_INT size);

#ifdef __cplusplus
}
#endif

#endif
