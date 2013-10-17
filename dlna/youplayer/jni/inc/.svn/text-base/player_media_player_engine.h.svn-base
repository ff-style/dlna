
#ifndef _player_MEDIA_PLAYER_ENGINE_H_
#define _player_MEDIA_PLAYER_ENGINE_H_

#include "player_std.h"
#include "player_mem.h"
#include "player_log.h"

//#include "player_media_player.h"
//#include "player_platform_sys_info.h"
//
//#include "player_platform_audio_render_filter.h"
//#include "player_platform_video_render_filter.h"

#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#include <signal.h>
#include <sys/types.h>

//#include "libavcodec/avcodec.h"
//#include "libavformat/avformat.h"
//#include "libavutil/avutil.h"
//#include "libswscale/swscale.h"
//#include "libavcodec/audioconvert.h"

#include "player_ffmpeg_list.h"
#include "player_socket_ipc.h"

#ifdef USE_PKT_POOL
#include "player_pool.h"
#endif

#ifdef __cplusplus 
extern "C"
{ 
#endif

#define player_DBG_DECODER

#ifdef DEBUG
#define player_log_easy  printf
#define player_memset memset
#define player_malloc malloc
#define player_calloc calloc
#define player_free free
#define player_memcpy memcpy
#define player_strstr strstr
#define player_strlen strlen
#endif

#ifdef player_DBG_DECODER
#define player_DBG_POS \
do{ \
player_log_easy("engine: pos %s() line %d\n",__FUNCTION__,__LINE__); \
}while(0)
#define player_DBG_FUNC_ENTER \
do{ \
player_log_easy("engine: enter %s() line %d\n",__FUNCTION__,__LINE__); \
}while(0)
#define player_DBG_FUNC_EXIT \
do{ \
player_log_easy("engine: exit %s() line %d\n",__FUNCTION__,__LINE__); \
}while(0)
#else
#define player_DBG_POS
#define player_DBG_FUNC_ENTER
#define player_DBG_FUNC_EXIT
#endif

#define player_DBG_LOG(fmt,args...)  \
do{ \
player_log_easy(fmt,##args); \
}while(0)

// scale 1920x1080 to 960x540, to reduce the buffer size
#define player_SCALE_1080P_YUV

typedef struct tagFoneAVSample
{
	FN_UTF8*                                  pBuffer;
	FN_UINT                                   nBufferSize;
	FN_64                                  dwTS;
	FN_INT width;						// frame width	
	FN_INT height;						// frame height
	FN_BOOL 								bVolumeAdjusted;
} FoneAVSample_t;


typedef enum _fn_player_engine_state_e_
{
	FN_ENGINE_STATE_UNKNOWN = -1,
	FN_ENGINE_CENTRAL_STATE_IDLE,	// 0
	FN_ENGINE_CENTRAL_STATE_PREPARE,
	FN_ENGINE_CENTRAL_STATE_OPENED,
	FN_ENGINE_CENTRAL_STATE_BUFFERING,
	FN_ENGINE_CENTRAL_STATE_PLAY_WAIT,
	FN_ENGINE_CENTRAL_STATE_PLAYING,	// 5
	FN_ENGINE_CENTRAL_STATE_PLAY_FILE_END,
	FN_ENGINE_CENTRAL_STATE_PAUSED,
	FN_ENGINE_CENTRAL_STATE_SEEK_WAIT,
	FN_ENGINE_CENTRAL_STATE_PLAY_COMPLETE,
	FN_ENGINE_CENTRAL_STATE_STOPPED,	// 10
	/* state below is used for decoding state machine*/
	FN_ENGINE_DECODE_STATE_START,		// start state of the decode thread
	FN_ENGINE_DECODE_STATE_WAIT,
	FN_ENGINE_DECODE_STATE_SEEK_WAIT,	
	FN_ENGINE_DECODE_STATE_WORK,
	FN_ENGINE_VIDEO_SYNC_STATE_IDLE,	// 15
	FN_ENGINE_VIDEO_SYNC_STATE_WAIT,
	FN_ENGINE_VIDEO_SYNC_STATE_WORK,
	/*decoder controller*/
	FN_ENGINE_DECODE_CTRL_START,
	FN_ENGINE_DECODE_CTRL_NORMAL,
	FN_ENGINE_DECODE_CTRL_L1,           // 20   
	FN_ENGINE_DECODE_CTRL_L2,           
	FN_ENGINE_STATE_END,
}fn_player_engine_state_e;


typedef enum _fn_player_engine_event_e_
{
	FN_ENGINE_EVT_MAC_GO_ON = 0,  // not external event
	FN_ENGINE_EVT_DECODE_MAC_GO_ON,	
	FN_ENGINE_EVT_VIDEO_SYNC_MAC_GO_ON,	
	FN_ENGINE_EVT_OPEN,
	FN_ENGINE_EVT_CLOSE,
	FN_ENGINE_EVT_START, // 5
	FN_ENGINE_EVT_PAUSE,
	FN_ENGINE_EVT_PLAY,
	FN_ENGINE_EVT_SEEK_PAUSE,
	FN_ENGINE_EVT_SEEK,
	FN_ENGINE_EVT_READY_TO_SEEK, // 10
	FN_ENGINE_EVT_SEEK_DONE,
	FN_ENGINE_EVT_STOP,
	FN_ENGINE_EVT_BUFFERING,
	FN_ENGINE_EVT_STOP_DECODE_WAIT,
	FN_ENGINE_EVT_RESTART, // 15
	FN_ENGINE_EVT_PLAY_COMPLETE,
	FN_ENGINE_EVT_EXIT_THREAD,	// this event is used for quitting  thread
	FN_ENGINE_EVT_MAKE_MAC_SLOW,		// make machine slow
	FN_ENGINE_EVT_MAKE_MAC_FAST,		// make machine fast
	FN_ENGINE_EVT_END
}fn_player_engine_evt_e;

typedef enum _fn_player_engine_AV_support_e_
{
	FN_ENGINE_HAS_NONE = 0,
	FN_ENGINE_HAS_AUDIO,	// has audio
	FN_ENGINE_HAS_VIDEO,	// has video
	FN_ENGINE_HAS_BOTH,		//has all
	FN_ENGINE_HAS_END
}fn_player_engine_AV_support_e;

typedef enum _fn_player_media_type_e_
{
	FN_MEDIA_VIDEO = 0,
	FN_MEDIA_AUDIO,
	FN_MEDIA_END
}fn_player_media_type_e;


typedef struct _fn_ffmpeg_packet_qnode_t_
{
	//AVPacket packet;
	struct list_head list;
}fn_ffmpeg_packet_qnode_t;


typedef struct _fn_ffmpeg_frame_qnode_t_
{
	FoneAVSample_t frm_sample;
	unsigned long real_buf_size;
	struct list_head list;
}fn_ffmpeg_frame_qnode_t;

typedef enum _fn_player_engine_state_mahine_type_e_
{
	fn_state_machine_engine = 1,
	fn_state_machine_video_decoder,
	fn_state_machine_audio_decoder,
	fn_state_machine_video_syncer,
	fn_state_machine_decoder_controller,
	fn_state_machine_max
}fn_player_engine_state_machine_type_e;

typedef struct _fn_player_engine_state_machine_t_
{
	fn_player_engine_state_e state;
	fn_player_engine_state_e old_state;
	fn_player_engine_state_machine_type_e  type;
	fn_ipc_msg_q_name_e ipc_fd;
}fn_player_engine_state_machine_t;

typedef struct _fn_player_network_error
{
	FN_BOOL berror;
	FN_INT	msg;
	FN_INT	msg1;
}fn_player_network_error;

#define MAX_SUB_NAME	32
#define MAX_SUB_COUNT	10

typedef struct _fn_subtitle_info
{
	int		stream_index;
	short	stream_opened;
	char	name[MAX_SUB_NAME];
} fn_subtitle_info;

#define MAX_AUDIO_NAME	32
#define MAX_AUDIO_COUNT	4

typedef struct _fn_audio_info
{
	int		stream_index;
	short	stream_opened;
	char	name[MAX_AUDIO_NAME];
} fn_audio_info;

typedef struct _fn_player_engine_decoded_file_obj_t_
{
	//AVFormatContext *pFormatCtx;
	fn_player_engine_state_machine_t engine;
	fn_player_engine_state_machine_t video_decoder;
	fn_player_engine_state_machine_t audio_decoder;
	fn_player_engine_state_machine_t video_syncer;
	fn_player_engine_state_machine_t decoder_controller;
	fn_player_engine_evt_e event;	// event that can be received
	fn_player_engine_AV_support_e av_support;
	FN_UTF8 *pszUrlOrPathname; // File name to be demuxed and decoded
//	AVStream *audio_stream;
//	AVStream *video_stream;	
	int64_t last_video_dts;
	int64_t last_audio_dts;
//	int st_index[AVMEDIA_TYPE_NB];	// stream index
	int audio_channels_cnt;        // audio channels
	struct q_head video_pkt_q;	// raw packet q that not be decoded
	struct q_head audio_pkt_q;
	struct q_head video_frm_q;  // frame q that has already be decoded
	struct q_head audio_frm_q;
	struct q_head free_pkt_q;	// free packet q that not be decoded
	struct q_head free_video_frm_q;  // free frame q that has already be decoded
	struct q_head free_audio_frm_q;  // free frame q that has already be decoded
	void *mutex;				// mutex lock for this object
	int64_t seekpos;
	int64_t duration;
	int64_t buffering_time;   // in ms unit
	int64_t play_buffering_time;
	int64_t current_video_pts;
	int buffering_percent;
	int video_frm_q_max_cnt;    // max video frame q count
	FN_BOOL has_been_closed;
	FN_BOOL is_mms_file;			// file is an mms based url
	FN_BOOL is_network_media;      // source media is from network 
	FN_BOOL is_live_media;			// whether the meida playing is live
	FN_BOOL need_slow_engine_reading;
	FN_BOOL is_file_eof;

	int		frame_count;
	int		avg_frame_time;
	FN_64	start_time;
	FN_64	first_video_ts;
	FN_64	cur_video_ts;
	FN_64	av_sync_time;
	
	FN_64 audio_start_node_pts;
	FN_64 audio_current_node_pts;
	int		av_seeking;
	unsigned long video_frame_size;
//	AVFrame * tmp_frame;
	struct SwsContext * sws_ctx;
	FN_INT nAudioVolume;
	FN_BOOL  key_frame;
	
	int lastmsg;
	FN_64 lastmsgts;

	fn_player_network_error neterror;

	// for renders
	FoneVideoRenderFilter_t  *video_render;
	FoneAudioRenderFilter_t  *audio_render;
	FoneVideoDesc_t 		 sFoneVideoDesc;
	FoneAudioDesc_t 		 sFoneAudioDesc;
	FoneMediaMuxerDesc_t	 sFoneMediaMuxerDesc;
	FN_BOOL					 bHaveDesc;

	// for avsync
	int					consume_count;	
	FN_64 					base_sys_time;
	FN_64					dwVideoPlayDelta;
	FN_64                	first_video_pts_per_sync;
	FN_64                	first_audio_pts_per_sync;
	FN_64                 	i64CurrentPos;		// current play-back position, audio or video
	FN_64					beginning_video_pts;
	FN_64					beginning_audio_pts;
	FN_64					video_consume_time;
	FN_64					audio_consume_time;
	FN_64					delta_consume;
	FN_64					delta_consume_last;
	FN_64 					delta_consume_central;					
	FN_64					delta_adjusted_sys_time;
	int 	consume_central_probe_count;
	FN_BOOL consume_central_hold;
	
	FN_LONG 				nEmptyCount;		// audio empty count
	FN_LONG 				video_sync_sleep_time;
	FN_BOOL					is_video_syncing_first_frame;
	FN_BOOL					is_audio_syncing_first_frame;
	FN_BOOL					should_base_time_be_updated;

#ifdef USE_PKT_POOL
	struct pool_head video_pkt_pool;
	struct pool_head audio_pkt_pool;
#endif
	// new sync
	FN_64 current_video_decoded_ts;
	FN_64 first_video_decoded_ts;
	FN_64 avg_video_decoded_time;
	FN_64 drop_frame_time_base;
	FN_BOOL					video_pkt_q_exceed_space_limit;
	FN_BOOL stop_thread_exit;

	// network
	FN_INT read_retry_count;

	// subtitle
	fn_subtitle_info		sub_info[MAX_SUB_COUNT];
	int						sub_count;
	int						sub_index;		// current subtitle

	// audio
	fn_audio_info			audio_info[MAX_AUDIO_COUNT];
	int						audio_count;
	int						audio_index;	// current audio track

	// for statistics
	long bytes_recv;    // bytes received from network
	long bytes_decoded; // bytes decoded
	FN_64 recv_tm;		// 10s span
	FN_64 decode_tm;	// 10s span
	float recv_ps;
	float decode_ps;
}fn_player_engine_decoded_file_obj_t;

typedef struct _fn_player_engine_obj_t_
{
	pthread_t engine_tid;
	pthread_t decode_tid;
	pthread_t decode_audio_tid;
	pthread_t video_sync_tid;
	FN_FLOAT price_for_audio_syncer;
	FN_FLOAT price_for_video_syncer;
	FN_FLOAT price_for_central_engine;
//	AVFrame 		*pVideoFrame;    // frame for video decoder
	int16_t *audio_decode_buf;		 // buffer for audio decoder
	int16_t *audio_decode_buf2;		// used for convert audio sample format
	FN_BOOL need_notify_ready;
	FN_BOOL notified_ready2;
	FN_BOOL has_init;
	FN_BOOL need_sending_upper_msg;
	fn_dynamic_reference_table_t dynamic_reference_table;

	FN_INT screen_width;
	FN_INT screen_height;
	unsigned long pkt_video_q_space_limit;
	unsigned long pkt_audio_q_space_limit;	
	unsigned long frm_video_q_space_limit;	
}fn_player_engine_obj_t;

#ifndef _WIN32
#pragma pack(1)

typedef unsigned short WORD;
typedef unsigned int DWORD;
typedef unsigned char BYTE;
typedef long LONG;

typedef struct tagBITMAPFILEHEADER {
  WORD    bfType;
  DWORD   bfSize;
  WORD    bfReserved1;
  WORD    bfReserved2;
  DWORD   bfOffBits;
} BITMAPFILEHEADER, *PBITMAPFILEHEADER;

typedef struct tagRGBQUAD {
        BYTE    rgbBlue;
        BYTE    rgbGreen;
        BYTE    rgbRed;
        BYTE    rgbReserved;
} RGBQUAD;

#pragma pack()

#define BI_BITFIELDS 3

typedef struct tagBITMAPINFOHEADER{
  DWORD biSize;
  LONG  biWidth;
  LONG  biHeight;
  WORD  biPlanes;
  WORD  biBitCount;
  DWORD biCompression;
  DWORD biSizeImage;
  LONG  biXPelsPerMeter;
  LONG  biYPelsPerMeter;
  DWORD biClrUsed;
  DWORD biClrImportant;
} BITMAPINFOHEADER, *PBITMAPINFOHEADER;

typedef struct tagBITMAPINFO {
  BITMAPINFOHEADER bmiHeader;
  RGBQUAD          bmiColors[1];
} BITMAPINFO, *PBITMAPINFO;
#endif // !_WIN32

typedef struct _fn_upper_layer_msg_t_
{
	long msg;
	long arg1;
	long arg2;
	long arg3;
}fn_upper_layer_msg_t;

typedef enum _fn_q_id_e_
{
	FN_Q_ID_NONE_ID = 0,
	FN_Q_ID_PKT_VIDEO,
	FN_Q_ID_PKT_AUDIO,
	FN_Q_ID_PKT_FREE,
	FN_Q_ID_FRM_VIDEO,
	FN_Q_ID_FRM_AUDIO,
	FN_Q_ID_FRM_FREE_VIDEO,
	FN_Q_ID_FRM_FREE_AUDIO,
	FN_Q_ID_MAX
}fn_q_id_e;

#define fn_min(a,b) (((a) < (b)) ? (a):(b))
#define fn_max(a,b) (((a) > (b)) ? (a):(b))
#define fn_abs(a) (((a) >= 0) ? (a) : -(a))

#define FN_MS(sec) ((sec) * 1000)
#define FN_MS_2_US(msec) ((msec) * 1000)

/*--------- Packet buffer ------------*/
// for local file, stands for the minimum of buffering time of the packet queues
#define FN_MAX_PKT_Q_TS 	FN_MS(1)  			// in ms unit

//--------- for the network file -----------------
#define FN_MAX_PKT_Q_NETWORK_TS FN_MS(50)	// in ms unit
// minumum buffering time, for the initial buffering (the 1st time)
#define FN_MAX_PKT_Q_NETWORK_FIRST_BUFFERING_TS FN_MS(1)	// in ms unit
// buffering time. Not for the initial buffering
#define FN_MAX_PKT_Q_NETWORK_BUFFERING_TS FN_MS(15)	// in ms unit


/*--------- Frame(Video/Audio) buffer ------------*/
// FrameQueue max node count, can be configured
// but this is not the only factor that determine the frame queue size
#define FN_MAX_FRM_VIDEO_Q_NODE_CNT 20	// max frm q video frame count
// #define FN_MAX_DELAY_TS				 2000

// FrameQueue max buffering time
#define FN_MAX_FRM_Q_TIME_LIMIT (0.7)  // 1s

// AudioFrameQueue maximum buffering node count
#define FN_MAX_FRM_AUDIO_Q_NODE_CNT 40	// max frm q audio frame count

/*-----------  the size limit ----------*/
#define FN_MEGA_SIZE(t) ((t) * 1024 * 1024)
#define FN_MAX_FRM_VIDEO_Q_MEM_SPACE  FN_MEGA_SIZE(40)  // 40M space size
#define FN_MAX_PKT_VIDEO_Q_MEM_SPACE  FN_MEGA_SIZE(10)  // 10M space size
#define FN_MAX_PKT_AUDIO_Q_MEM_SPACE  FN_MEGA_SIZE(5)  // 5M space size
/*-----------------------------------------------------------------
Above all , the common rule is that the final result is the combination
of all the related factors(expressed in macros) that control the queues.
Any given queue is controlled by all of its related constraints, or factors,
which are expressed in macros
------------------------------------------------------------------*/


#define FN_MAX_READ_CNT_IN_ONE_READ_MSG 30

#define AVCODEC_MAX_AUDIO_FRAME_SIZE 192000 // 1 second of 48khz 32bit audio
#define FN_MAX_AUDIO_FRAME_SIZE (AVCODEC_MAX_AUDIO_FRAME_SIZE * 2)

extern fn_player_engine_decoded_file_obj_t g_player_engine_file_obj;
extern fn_player_engine_obj_t	g_player_engine_obj;

FN_BOOL player_player_engine_init(int screenW, int screenH);
FN_BOOL player_player_engine_uninit();
FN_BOOL player_player_engine_open_media(FN_CONST FN_UTF8* a_pszUrlOrPathname);
FN_BOOL player_player_engine_close_media();
FN_BOOL player_player_engine_get_description(OUT FoneMediaMuxerDesc_t *aMediaMuxerDesc, OUT FoneVideoDesc_t *aVideoDesc, OUT FoneAudioDesc_t *aAudioDesc);

FN_BOOL player_player_engine_start(void);
FN_BOOL player_player_engine_stop(void);
FN_BOOL player_player_engine_pause(void);
FN_INT  player_player_engine_seek_to(FN_SIZE a_SeekPos);
FN_BOOL player_player_engine_play(void);
FN_INT  player_player_engine_set_audio_volume(FN_SIZE a_nVolume);

FN_VOID player_player_engine_notify(FN_INT a_nNotifyMsg, FN_VOID* wParam,FN_VOID* lParam);
FN_BOOL player_decoder_get_video_sample(FoneAVSample_t** aFoneVideoSample);
FN_BOOL player_decoder_get_audio_sample(FoneAVSample_t** aFoneAudioSample);
FN_BOOL player_decoder_consume_video_sample(FoneAVSample_t** aFoneVideoSample);
FN_BOOL player_decoder_consume_audio_sample(FoneAVSample_t** aFoneAudioSample);
FN_VOID player_decoder_free_audio_buffer(FoneAVSample_t *audio_sample);
FN_VOID player_decoder_free_video_buffer(FoneAVSample_t *video_samp);


// return 1 means want interrupt
#ifdef VERSION_FFMPEG
int fn_url_interrupt(void*);
#else
int fn_url_interrupt(void);
#endif
#ifdef VERSION_FFMPEG
int fn_url_noninterrupt(void*);
#else
int fn_url_noninterrupt(void);
#endif
#if 0
// for  internal use
void player_do_close_central_engine(void);
void fn_seek_determine_stream(AVStream **stream,int *st_index);
FN_64 fn_pos_to_pts(FN_64 pos, AVStream *stream);
void fn_adjust_ffmpeg_mms_url(int type,char *url);
void player_player_engine_central_state_machine(fn_player_engine_state_machine_t *mac,fn_player_engine_evt_e evt);
void fn_state_machine_change_state(fn_player_engine_state_machine_t *mac,
	fn_player_engine_state_e new_state);
void *fn_video_sync_thread(void *arg);
void player_player_engine_decoder_state_machine(fn_player_engine_state_machine_t *mac,fn_player_engine_evt_e evt);

void player_frm_q_full_wait_available_cb(void *mac);
extern int sync_av_after_seek(FN_64* av_sync_time);
void player_pkt_q_empty_goto_buffering_cb(void *mac);
FN_VOID packet_node_free(fn_ffmpeg_packet_qnode_t *pqnode);
void player_decoder_controller_state_machine(fn_player_engine_state_machine_t *mac,fn_player_engine_evt_e evt);
int player_decode_one_video_packet(fn_ffmpeg_packet_qnode_t *pkt_qnode);
void fn_reset_decoder_controller(void);
void fn_resume_decoder_controller(void);



#ifdef USE_PKT_POOL
void fn_pkt_pool_write_cb(void *param,struct list_head *new);
void fn_pkt_pool_read_cb(void *param,struct list_head *poped);
FN_64 fn_pkt_pool_get_duration(struct pool_head *pool_head);
void player_pkt_pool_empty_goto_buffering_cb(void *mac);
#else
void fn_pkt_q_push_cb(void *q,struct list_head *new_item);
void fn_pkt_q_pop_cb(void *q,struct list_head *poped_item);
FN_BOOL fn_pkt_q_drop_non_key_packet(void);
void fn_force_engine_buffering(void);
#endif

FN_BOOL player_engine_start_render();
void player_engine_stop_render();
void player_engine_pause_render();
void player_engine_resume_render();
void player_engine_reset_render();
FN_BOOL player_player_preview_one_frame();

// player decoder media mm collection system
extern fn_ffmpeg_packet_qnode_t *fn_allocate_packet_node(void);
extern void fn_free_packet_node(fn_ffmpeg_packet_qnode_t *free_node);
extern fn_ffmpeg_frame_qnode_t *fn_allocate_frm_node(fn_player_media_type_e type,
	unsigned long required_size);
extern void fn_free_frm_node(fn_player_media_type_e type,fn_ffmpeg_frame_qnode_t *free_node);
extern void fn_init_free_qs(void);
extern void fn_destroy_free_qs(void);

int av_error_notifify_cb(int type, int error, char *msgstr);

int player_player_seek_preview(int thumbWidth, int thumbHeight, FN_BYTE* pBitmapBuffer, int bytesPP);

void player_thumbnail_init();
void player_thumbnail_uninit();

void player_control_player_thumbnail_stop();

/*
 * Usage:          get thumbnail bitmap from a video file.
 * szFilePathName: full path name of input video file
 * pBitmapBuffer:  bitmap data buffer to fill
 * width:		   output bitmap width
 * height:		   output bitmap height
 * pos:			   thumbnail position in second, -1 means not care
 * pIsHD:		   0 - not HD, 1 - is HD
 * pDuration:	   media duration in ms
 * return:         non-zero if OK. 0 if failed.
 */
int player_get_thumbnail_from_video(const char* szFilePathName, unsigned char* pBitmapBuffer,
									   int width, int height, int pos, int* pIsHD, int* pDuration, int bpp);

void fn_register_external_protocol(void);

FN_BOOL player_engine_set_play_rate(FonePlayerPlayRate rate);
FonePlayerPlayRate player_engine_get_play_rate();

AVFrame *alloc_picture(enum PixelFormat pix_fmt, int width, int height);

int player_get_codecid_by_filename(const char* szFilePathName);

void player_Converter_Video(const char *inputpath,const char* outputpath,int endtime);

void Convert2Dto3D(void *src,int nPicWidth, int nPicHeight);
#endif
#ifdef __cplusplus
}
#endif


#endif // _player_MEDIA_PLAYER_ENGINE_H_

