
#ifndef _STREAM_H_
#define _STREAM_H_
#include <stdio.h>
#ifndef O_BINARY
#define O_BINARY 0
#endif
#include "player_jni.h"
#define STREAMTYPE_DUMMY -1    // for placeholders, when the actual reading is handled in the demuxer
#define STREAMTYPE_FILE 0      // read from seekable file
#define STREAMTYPE_VCD  1      // raw mode-2 CDROM reading, 2324 bytes/sector
#define STREAMTYPE_STREAM 2    // same as FILE but no seeking (for net/stdin)
#define STREAMTYPE_DVD  3      // libdvdread
#define STREAMTYPE_MEMORY  4   // read data from memory area
#define STREAMTYPE_PLAYLIST 6  // FIXME!!! same as STREAMTYPE_FILE now
#define STREAMTYPE_DS   8      // read from a demuxer stream
#define STREAMTYPE_DVDNAV 9    // we cannot safely "seek" in this...
#define STREAMTYPE_CDDA 10     // raw audio CD reader
#define STREAMTYPE_SMB 11      // smb:// url, using libsmbclient (samba)
#define STREAMTYPE_VCDBINCUE 12      // vcd directly from bin/cue files
#define STREAMTYPE_DVB 13
#define STREAMTYPE_VSTREAM 14
#define STREAMTYPE_SDP 15
#define STREAMTYPE_PVR 16
#define STREAMTYPE_TV 17
#define STREAMTYPE_MF 18
#define STREAMTYPE_RADIO 19
#define STREAMTYPE_BLURAY 20
#define STREAMTYPE_BD 21

#define STREAM_BUFFER_SIZE 2048
#define STREAM_MAX_SECTOR_SIZE (8*1024)

#define VCD_SECTOR_SIZE 2352
#define VCD_SECTOR_OFFS 24
#define VCD_SECTOR_DATA 2324

/// atm it will always use mode == STREAM_READ
/// streams that use the new api should check the mode at open
#define STREAM_READ  0
#define STREAM_WRITE 1
/// Seek flags, if not mannualy set and s->seek isn't NULL
/// MP_STREAM_SEEK is automaticly set
#define MP_STREAM_SEEK_BW  2
#define MP_STREAM_SEEK_FW  4
#define MP_STREAM_SEEK  (MP_STREAM_SEEK_BW|MP_STREAM_SEEK_FW)
/** This is a HACK for live555 that does not respect the
    separation between stream an demuxer and thus is not
    actually a stream cache can not be used */
#define STREAM_NON_CACHEABLE 8

//////////// Open return code
#define STREAM_REDIRECTED -2
/// This can't open the requested protocol (used by stream wich have a
/// * protocol when they don't know the requested protocol)
#define STREAM_UNSUPPORTED -1
#define STREAM_ERROR 0
#define STREAM_OK    1

#define MAX_STREAM_PROTOCOLS 10

#define STREAM_CTRL_RESET 0
#define STREAM_CTRL_GET_TIME_LENGTH 1
#define STREAM_CTRL_SEEK_TO_CHAPTER 2
#define STREAM_CTRL_GET_CURRENT_CHAPTER 3
#define STREAM_CTRL_GET_NUM_CHAPTERS 4
#define STREAM_CTRL_GET_CURRENT_TIME 5
#define STREAM_CTRL_SEEK_TO_TIME 6
#define STREAM_CTRL_GET_SIZE 7
#define STREAM_CTRL_GET_ASPECT_RATIO 8
#define STREAM_CTRL_GET_NUM_ANGLES 9
#define STREAM_CTRL_GET_ANGLE 10
#define STREAM_CTRL_SET_ANGLE 11

#define DEMUXER_TYPE_UNKNOWN 0

// A virtual demuxer type for the network code
#define DEMUXER_TYPE_PLAYLIST (2<<16)

typedef enum {
	streaming_stopped_e,
	streaming_playing_e
} streaming_status;

typedef struct {
	char *url;
	char *noauth_url;
	char *protocol;
	char *hostname;
	char *file;
	unsigned int port;
	char *username;
	char *password;
} URL_t;

typedef struct streaming_control {
	URL_t *url;
	streaming_status status;
	int buffering;	// boolean
	unsigned int prebuffer_size;
	char *buffer;
	unsigned int buffer_size;
	unsigned int buffer_pos;
	unsigned int bandwidth;	// The downstream available
	int (*streaming_read)( int fd, char *buffer, int buffer_size, struct streaming_control *stream_ctrl );
	int (*streaming_seek)( int fd, off_t pos, struct streaming_control *stream_ctrl );
	void *data;
} streaming_ctrl_t;

struct stream;
typedef struct stream_info_st {
  const char *info;
  const char *name;
  const char *author;
  const char *comment;
  /// mode isn't used atm (ie always READ) but it shouldn't be ignored
  /// opts is at least in it's defaults settings and may have been
  /// altered by url parsing if enabled and the options string parsing.
  int (*open)(struct stream* st, int mode, void* opts, int* file_format);
  const char* protocols[MAX_STREAM_PROTOCOLS];
  const void* opts;
  int opts_url; /* If this is 1 we will parse the url as an option string
		 * too. Otherwise options are only parsed from the
		 * options string given to open_stream_plugin */
} stream_info_t;
#if 0
typedef enum {
    FONE_CHARSET_NONE = -1,
    FONE_CHARSET_GB = 0,
    FONE_CHARSET_UTF8 = 1,
    FONE_CHARSET_UTF16 = 2,
} FoneCharset_t;
#endif
typedef struct stream {
	// Read
	int (*fill_buffer)(struct stream *s, char* buffer, int max_len);
	// Write
	int (*write_buffer)(struct stream *s, char* buffer, int len);
	// Seek
	int (*seek)(struct stream *s, off_t pos);
	// Control
	// Will be later used to let streams like dvd and cdda report
	// their structure (ie tracks, chapters, etc)
	int (*control)(struct stream *s, int cmd, void* arg);
	// Close
	void (*close)(struct stream *s);

	int fd; // file descriptor, see man open(2)
	int type; // see STREAMTYPE_*
	int flags;
	int sector_size; // sector size (seek will be aligned on this size if non 0)
	int read_chunk; // maximum amount of data to read at once to limit latency (0 for default)
	unsigned int buf_pos, buf_len;
	off_t pos, start_pos, end_pos;
	int eof;
	int mode; //STREAM_READ or STREAM_WRITE
	unsigned int cache_pid;
	void* cache_data;
	void* priv; // used for DVD, TV, RTSP etc
	char* url; // strdup() of filename/url
	unsigned char
			buffer[STREAM_BUFFER_SIZE > STREAM_MAX_SECTOR_SIZE ? STREAM_BUFFER_SIZE
					: STREAM_MAX_SECTOR_SIZE];
	FILE *capture_file;
    FoneCharset_t charset;
} stream_t;

stream_t* open_stream(const char* filename, char** options, int* file_format);
void free_stream(stream_t *s);
uint8_t *stream_read_until(stream_t *s, uint8_t *mem, int max, uint8_t term, int utf16);
inline static uint8_t *stream_read_line(stream_t *s, uint8_t *mem, int max, int utf16)
{
  return stream_read_until(s, mem, max, '\n', utf16);
}
int stream_fill_buffer(stream_t *s);
#define cache_stream_fill_buffer(x) stream_fill_buffer(x)
int stream_seek(stream_t *s,off_t pos);
void stream_reset(stream_t *s);
FoneCharset_t string_char_detect(const char* buf, int len);

#endif  //_STREAM_H_


