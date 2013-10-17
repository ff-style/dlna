/*************************************************

  Copyright (C), 2001-2010, Fone Net Info �C Tech Co., Ltd

  File name:      player_mem.h

  Description:

  Others:

*************************************************/


#ifndef _player_MEM_H_
#define _player_MEM_H_

#include "player_std.h"

#ifdef __cplusplus
extern "C"
{
#endif

/*
*  Macro defination
*/
#define KSIZE(n) ((n) * 1024)
#define player_CONFIG_AVAIL_MEM_SIZE   KSIZE(1500)
#define player_MEM_BLK_ARRAY_NUM  (1000)
#define player_CONFIG_MEM_DIAGNOSTICS

//#define player_CONFIG_MEM_DEBUG
// max freq policy that user can define
#define player_MEM_FREQ_POLICY_MAX  3

// if this is defined, use system allocating or free API instead of ours
#define player_MEM_USE_SYSTEM_API
// Some platforms need memory initliazation before we can get a piece of available memory
// In this case, you need to define this macro
//#define player_MEM_NEED_PLATFORM_INIT

// This is defined for multithreading
//#undef player_MEM_USE_LOCK
#define player_MEM_USE_LOCK

// If you open this macro, we provide the so called "aggressive free" service for you.
// This aggressive means that the free ptr which passed in would be set to empty(NULL).
// If you reuse this pointer in some case that is unexpected, crash would occur at once.
// SIDE EFFECT: if you open this macro, double free would be not detected!!
//#define player_CONFIG_MEM_USE_AGGRESSIVE_FREE

#if player_MEM_BLK_ARRAY_NUM > 1023
#error  blocks configured exceed max number
#endif

#if player_CONFIG_AVAIL_MEM_SIZE > KSIZE(2000)
#error  mem size configured exceed max allowed
#endif


#ifdef player_MEM_USE_SYSTEM_API
#undef player_CONFIG_MEM_DIAGNOSTICS
#undef player_CONFIG_MEM_DEBUG
#endif

#ifdef  player_CONFIG_MEM_DIAGNOSTICS
#define player_CONFIG_MEM_CHECK_BOUNDARY
#else
#undef player_CONFIG_MEM_CHECK_BOUNDARY
#undef player_CONFIG_MEM_USE_AGGRESSIVE_FREE
#endif

#ifdef  player_MEM_USE_DEFAULT_API
#include <malloc.h>
#include <memory.h>
#define player_MEM_MALLOC     malloc
#define player_MEM_FREE           free
#define player_MEM_MEMSET     memset
#else
#include "player_platform_mem.h"
#define player_MEM_MALLOC     player_os_malloc
#define player_MEM_FREE           player_os_free
#define player_MEM_ADAPTER_INIT           player_os_memory_init
#define player_MEM_ADAPTER_UNINIT      player_os_memory_uninit
#define player_MEM_MEMSET     player_memset
#endif

#ifndef player_PLATFORM_SYMBIAN_2X
#define player_MEM_USE_CONST_GLOBAL_VAR
#endif

// Function name	: player_memory_init
// Description	    : ��ʼ���ڴ����ģ��
// Return type		: FN_BOOL 
// Argument         : void
player_IMPORT extern FN_BOOL player_memory_init(void);

// Function name	: player_memory_uninit
// Description	    : �ͷ��ڴ����ģ����Դ
// Return type		: void 
// Argument         : void
player_IMPORT extern void player_memory_uninit(void);

/*
*  Page number and order defination  
*/
enum player_mem_blk_order_enum
{
   O_32B = 0,
   O_64B,
   O_128B,
   O_256B,
   O_512B,
   O_1K,
   O_2K,
   O_4K,
   O_8K,
   O_16K,
   O_32K,
   O_64K,
   O_128K,
   O_256K,
   O_512K,
   O_TOTAL,
};

//
// Highly used frequency region
//
typedef struct __player_mem_high_freq_policy__
{
    FN_WORD order;
    FN_WORD blk_num;
}player_mem_high_freq_policy_t;

//
// In some case, we need to reset our freqency region, in the first version, we reset
// all the whole mem region.
// NOTE: all memory that have allocated must have been freed. Please ensure this.
// @ freq_policy: [in] defines which order or policy you use most frequently
// @ num: [in] number of freqencies that you give
player_IMPORT extern FN_BOOL player_mem_reset_mem_region(player_mem_high_freq_policy_t *freq_policy,FN_BYTE num);

#ifdef player_CONFIG_MEM_DIAGNOSTICS

#define player_MEM_FILE_LINE_MAX_SIZE  64

player_IMPORT extern void *__player_malloc(FN_INT size,FN_CONST FN_CHAR *file,FN_INT line);
player_IMPORT extern void *__player_calloc(FN_INT size,FN_CONST FN_CHAR *file,FN_INT line);

#undef player_malloc
#undef player_calloc
#undef player_free
#define player_malloc(size)  __player_malloc(size,__FILE__,__LINE__)
#define player_calloc(size)   __player_calloc(size,__FILE__,__LINE__)
#define player_free(ptr)   __player_free((void *)(ptr),(void **)(&(ptr)),__FILE__,__LINE__)

player_IMPORT extern void __player_free(void *,void **,FN_CONST FN_CHAR *file,FN_INT line);

player_IMPORT extern void  player_mem_bound_check_everywhere(void);
player_IMPORT void player_mem_statistic_print(void);

#else
// Function name	: player_malloc
// Description	    : ����a_nSize�ֽ���
// Return type		: void* ��һ����������4�ֽڶ���
// Argument         : FN_INT a_nSize
player_IMPORT extern void* player_malloc(FN_INT);


// Function name	: player_calloc
// Description	    : ����a_nSize�ֽ���
// Return type		: void* ��һ����������4�ֽڶ���
// Argument         : FN_INT a_nSize
player_IMPORT extern void* player_calloc(FN_INT);


// Function name	: player_free
// Description	    : �ͷ��ڴ�
// Return type		: void 
// Argument         : void* a_pPtr
player_IMPORT extern void player_free(void *);

#endif  // #endif  player_CONFIG_MEM_DIAGNOSTICS

#ifdef __cplusplus
}
#endif

#endif  // _player_MEM_H_

