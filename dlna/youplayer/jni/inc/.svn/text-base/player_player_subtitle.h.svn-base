#ifndef __player_PLAYER_SUBTITLE_H__
#define __player_PLAYER_SUBTITLE_H__

//#include "player_media_player_engine.h"
#include "player_player_stream.h"
#include "player_std.h"

#define SUB_MAX_TEXT 12

typedef struct
{
    int lines;

    unsigned long start;
    unsigned long end;

    char *text[SUB_MAX_TEXT];
    double endpts[SUB_MAX_TEXT];
    unsigned char alignment;
} subtitle;

void fn_player_subtitle_open(const FN_CHAR *file);
void fn_player_subtitle_close();
void fn_player_subtitle_update_ts(FN_UINT ts); // in ms
const char* fn_player_get_current_sub();
FoneCharset_t fn_get_sub_char_set();
int fn_player_get_lrc_offset();
void fn_player_set_lrc_offset(const FN_CHAR *file, int offset);

// get subtitle totle count
int fn_player_sub_totle_count();

// get one subtitle item
subtitle* fn_player_get_sub(int index);

typedef struct
{
    subtitle *subtitles;
    char *filename;
    int sub_uses_time;
    int sub_num;          // number of subtitle structs
    int sub_errs;
    FoneCharset_t	char_set;
} sub_data;

FN_BOOL fn_enable_internal_sub();
void fn_player_subtitle_internal_open();
void fn_player_subtitle_internal_reset();

// ts_start: 	start ts in 10ms
// ts_end:		end ts in 10 ms
void fn_player_subtitle_internal_add(char* sub_text, FN_UINT ts_start, FN_UINT ts_end);

// show or hide copyright info
void player_set_copyright_info(FN_UTF8* szUrl);

#endif //__player_PLAYER_SUBTITLE_H__
