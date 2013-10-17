#ifndef _Included_player_jni_h
#define _Included_player_jni_h

#include <string.h>
#include <jni.h>
#include <android/log.h> 
#include "player_std.h"
#include "player_mem.h"

#ifndef LOG_TAG  
#define LOG_TAG    "player_engine"
#endif 
 
#ifdef player_LOG_ON
#define LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)    
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)  
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)  
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)  
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)  
#define LOGF(...)  __android_log_print(ANDROID_LOG_FATAL,LOG_TAG,__VA_ARGS__)
#else
#define LOGV(...)  
#define LOGD(...)
#define LOGI(...)
#define LOGW(...)
#define LOGE(...)
#define LOGF(...)
#endif

#ifdef __cplusplus
extern "C" {
#endif
    
    extern JavaVM *g_JavaVM;
	extern jclass g_Player_UIManager_Class;
	extern jclass g_File_Class;
	extern jclass g_java_string;
    extern JNIEnv *Adapter_GetEnv();
    /*
    //定义目标类名称
    //static const char * classname = "cn/Load_Jni";
/*    extern char *classPlayerUIManager;	
    extern char *classMediaDesc;
    extern char *classLyricsOne;
    extern char *classLyrics;
	extern char *classBitmapname;
	extern char *classAudioInfoname;    
	extern char *classVisualizer;

    JNIEXPORT jboolean		JNICALL  jni_player_media_player_init(JNIEnv* env,jobject thiz, jint ver);
    JNIEXPORT jboolean  	JNICALL  jni_player_media_player_uninit(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean  	JNICALL  jni_player_media_player_open(JNIEnv* env,jobject thiz,jstring content);
    JNIEXPORT jboolean  	JNICALL  jni_player_media_player_close(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean      JNICALL  jni_player_media_player_start(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean      JNICALL  jni_player_media_player_stop(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean      JNICALL  jni_player_media_player_pause(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean      JNICALL  jni_player_media_player_play(JNIEnv* env,jobject thiz);
    JNIEXPORT jint      	JNICALL  jni_player_media_player_seek_to(JNIEnv* env,jobject thiz, jint a_SeekPos);
    JNIEXPORT jint      	JNICALL  jni_player_media_player_set_audio_volume(JNIEnv* env,jobject thiz, jint a_nVolume);
	JNIEXPORT jint      	JNICALL  jni_player_media_player_get_audio_volume(JNIEnv* env,jobject thiz);
    JNIEXPORT jobject   	JNICALL  jni_player_media_player_get_description(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean  	JNICALL  jni_player_media_player_set_dynamic_reference_table(JNIEnv* env,jobject thiz);
    
    JNIEXPORT void  	JNICALL  jni_player_media_player_seek_preview_init(JNIEnv* env,jobject thiz, jint a_width, jint a_height);
    JNIEXPORT void  	JNICALL  jni_player_media_player_seek_preview_uninit(JNIEnv* env,jobject thiz);
    JNIEXPORT jobject  	JNICALL  jni_player_media_player_seek_preview(JNIEnv* env,jobject thiz);
    JNIEXPORT jboolean 	JNICALL  jni_player_media_player_lyric_subtitle_open(JNIEnv* env,jobject thiz,jint type,jstring content);
    JNIEXPORT void	    JNICALL  jni_player_media_player_lyric_subtitle_close(JNIEnv* env,jobject thiz);
    JNIEXPORT jobject 	JNICALL	 jni_player_media_player_get_lyric_subtitle(JNIEnv* env,jobject thiz,jint pos);
    JNIEXPORT void      JNICALL  jni_player_media_thumbnail_init(JNIEnv* env,jobject thiz, jint a_width, jint a_height);
    JNIEXPORT void      JNICALL  jni_player_media_thumbnail_uninit(JNIEnv* env,jobject thiz);
    JNIEXPORT void      JNICALL  jni_player_media_player_thumbnail_stop(JNIEnv* env,jobject thiz);
    JNIEXPORT jobject   JNICALL  jni_player_media_player_get_thumbnail_from_video(JNIEnv* env,jobject thiz, jstring a_file, jint a_pos, jint bpp);
    JNIEXPORT void      JNICALL  jni_player_media_thumbnail32_init(JNIEnv* env,jobject thiz, jint a_width, jint a_height);
    JNIEXPORT void      JNICALL  jni_player_media_thumbnail32_uninit(JNIEnv* env,jobject thiz);
    JNIEXPORT void      JNICALL  jni_player_media_player_thumbnail32_stop(JNIEnv* env,jobject thiz);
    JNIEXPORT jobject   JNICALL  jni_player_media_player_get_thumbnail32_from_video(JNIEnv* env,jobject thiz, jstring a_file, jint a_pos);
    JNIEXPORT jobject   JNICALL  jni_player_media_player_get_audio_info(JNIEnv* env,jobject thiz, jstring a_file);
    JNIEXPORT jboolean  JNICALL  jni_player_media_player_set_play_rate(JNIEnv* env, jobject thiz, jint a_rate);
    
	JNIEXPORT jint      JNICALL  jni_player_visualizer_init(JNIEnv* env, jobject thiz, jstring, jint width, jint height);
    JNIEXPORT void      JNICALL  jni_player_visualizer_uninit(JNIEnv* env, jobject thiz);
    JNIEXPORT void      JNICALL  jni_player_visualizer_select_preset(JNIEnv* env, jobject thiz, jint index);
    JNIEXPORT void      JNICALL  jni_player_visualizer_resize(JNIEnv* env, jobject thiz, jint width, jint height);
    JNIEXPORT void      JNICALL  jni_player_visualizer_render(JNIEnv* env, jobject thiz);

	JNIEXPORT jint      JNICALL  jni_player_get_audio_channel_count(JNIEnv* env, jobject thiz);
    JNIEXPORT jstring   JNICALL  jni_player_get_audio_channel_desc(JNIEnv* env, jobject thiz, jint index);
    JNIEXPORT jint      JNICALL  jni_player_get_cur_audio_channel(JNIEnv* env, jobject thiz);
    JNIEXPORT void      JNICALL  jni_player_set_cur_audio_channel(JNIEnv* env, jobject thiz, jint index);
    JNIEXPORT jint      JNICALL  jni_player_get_sub_channel_count(JNIEnv* env, jobject thiz);
    JNIEXPORT jstring   JNICALL  jni_player_get_sub_channel_desc(JNIEnv* env, jobject thiz, jint index);
    JNIEXPORT jint      JNICALL  jni_player_get_cur_sub_channel(JNIEnv* env, jobject thiz);
    JNIEXPORT void      JNICALL  jni_player_set_cur_sub_channel(JNIEnv* env, jobject thiz, jint index);
    JNIEXPORT void      JNICALL  jni_player_media_player_attach_surface(JNIEnv *env, jobject thiz, jobject s);

    static JNINativeMethod methodsplayer[] = {
 
    	{"player_media_player_init",                		"(I)Z", (void *)jni_player_media_player_init},
    	{"player_media_player_uninit",              		"()Z", (void *)jni_player_media_player_uninit},
    	{"player_media_player_open",          			"(Ljava/lang/String;)Z", (void *)jni_player_media_player_open},
        {"player_media_player_close",         			"()Z", (void *)jni_player_media_player_close},
        {"player_media_player_start",          	    	"()Z", (void *)jni_player_media_player_start},
        {"player_media_player_stop",           	    	"()Z", (void *)jni_player_media_player_stop},
        {"player_media_player_pause",          	    	"()Z", (void *)jni_player_media_player_pause},
        {"player_media_player_play",         	    		"()Z", (void *)jni_player_media_player_play},
        {"player_media_player_seek_to",             		"(I)I", (void *)jni_player_media_player_seek_to},
        {"player_media_player_set_audio_volume",    		"(I)I", (void *)jni_player_media_player_set_audio_volume},
        {"player_media_player_get_audio_volume",    		"()I", (void *)jni_player_media_player_get_audio_volume},
        {"player_media_player_get_description",			"()Ljava/lang/Object;", (void *)jni_player_media_player_get_description},
        {"player_media_player_set_dynamic_reference_table","()V", (void *)jni_player_media_player_set_dynamic_reference_table},
        
        {"player_media_player_seek_preview_init",               "(II)V",(void *)jni_player_media_player_seek_preview_init},
        {"player_media_player_seek_preview_uninit",               "()V",(void *)jni_player_media_player_seek_preview_uninit},
        {"player_media_player_seek_preview",               "()Ljava/lang/Object;",(void *)jni_player_media_player_seek_preview},
    	{"player_media_player_lyric_subtitle_open",        "(ILjava/lang/String;)Z", (void *)jni_player_media_player_lyric_subtitle_open},
        {"player_media_player_lyric_subtitle_close",       "()V", (void *)jni_player_media_player_lyric_subtitle_close},
        {"player_media_player_get_lyric_subtitle",	     "(I)Ljava/lang/Object;", (void *)jni_player_media_player_get_lyric_subtitle},
        {"player_media_thumbnail_init",		             "(II)V",(void *)jni_player_media_thumbnail_init},
        {"player_media_thumbnail_uninit",		             "()V", (void *)jni_player_media_thumbnail_uninit},
        {"player_media_player_thumbnail_stop",	         "()V", (void *)jni_player_media_player_thumbnail_stop},
        {"player_media_player_get_thumbnail_from_video",   "(Ljava/lang/String;II)Ljava/lang/Object;", (void *)jni_player_media_player_get_thumbnail_from_video},
        {"player_media_player_get_audio_info",             "(Ljava/lang/String;)Ljava/lang/Object;", (void *)jni_player_media_player_get_audio_info},
        {"player_media_player_set_play_rate",             "(I)Z", (void *)jni_player_media_player_set_play_rate},
    	{"player_get_audio_channel_count",                "()I", (void *)jni_player_get_audio_channel_count},
    	{"player_get_audio_channel_desc",              	"(I)Ljava/lang/String;", (void *)jni_player_get_audio_channel_desc},
    	{"player_get_cur_audio_channel",          		"()I", (void *)jni_player_get_cur_audio_channel},
        {"player_set_cur_audio_channel",                	"(I)V", (void *)jni_player_set_cur_audio_channel},
    	{"player_get_sub_channel_count",                "()I", (void *)jni_player_get_sub_channel_count},
    	{"player_get_sub_channel_desc",              	"(I)Ljava/lang/String;", (void *)jni_player_get_sub_channel_desc},
    	{"player_get_cur_sub_channel",          		"()I", (void *)jni_player_get_cur_sub_channel},
        {"player_set_cur_sub_channel",                	"(I)V", (void *)jni_player_set_cur_sub_channel},
        {"player_media_player_attach_surface",     	"(Landroid/view/Surface;)V", (void *)jni_player_media_player_attach_surface},
    };

    static JNINativeMethod methodsvisualizer[] = {
        {"player_visualizer_init",		        "(Ljava/lang/String;II)I", (void *)jni_player_visualizer_init},
        {"player_visualizer_uninit",		        "()V", (void *)jni_player_visualizer_uninit},
        {"player_visualizer_select_preset",		"(I)V", (void *)jni_player_visualizer_select_preset},
        {"player_visualizer_resize",		        "(II)V", (void *)jni_player_visualizer_resize},
        {"player_visualizer_render",		        "()V", (void *)jni_player_visualizer_render},
    };
*/
#ifdef __cplusplus
}
#endif
typedef enum {
    player_CHARSET_NONE = -1,
    player_CHARSET_GB = 0,
    player_CHARSET_UTF8 = 1,
    player_CHARSET_UTF16 = 2,
} FoneCharset_t;
#endif
