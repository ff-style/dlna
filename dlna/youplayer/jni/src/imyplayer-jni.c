#define LOG_TAG "player_engine"

#include <fcntl.h>
#include "player_jni.h"
#include <dlfcn.h>
#include "jni.h"
jclass g_class_video_db_adapter=NULL;
//#include "Bluray_log.h"
//#include "bluray_type.h"
#define construct_jstring(env,str,jstr) \
{\
	if(str != NULL || env == NULL)\
	{\
		int len = strlen(str);\
		if(len > 0)\
		{\
			jstr = stoJstring(env,str,len);\
			if(NULL == jstr)LOGE("out of memory");\
		}\
		else\
		{\
			jstr = stoJstring(env," ",1);\
		}\
	}\
}
#define initMakeGlobalRef() \
		jobject localref = NULL;\
		char full_name[1000]={0};

#define makeGlobalRef(env,g_class_ref,path_name,class_name) \
		sprintf(full_name,"%s/%s",path_name,class_name); \
		localref = (*env)->FindClass(env, full_name);\
		if(NULL == localref)\
		{\
			LOGE("cannot find class %s",class_name);\
			return;\
		}\
		g_class_ref = (*env)->NewGlobalRef(env,localref);\
		if(NULL == g_class_ref)\
		{\
			LOGE("cannot globally reference %s",class_name);\
			return;\
		}\
		(*env)->DeleteLocalRef(env,localref);

#define removeGlobalRef(env,g_class_ref) \
		(*env)->DeleteGlobalRef(env,g_class_ref);

//#include "player_media_player.h"

#define player_free free

#define TOTAL_MID_NUM   8
static char *arrstrclass[TOTAL_MID_NUM];

JavaVM *g_JavaVM;
jclass g_Player_UIManager_Class;
jclass g_File_Class;
static jclass g_Visualizer_Class;
jclass g_java_string;
int g_CPU_NEON = 0;
static char *adapter_path = "com/wireme/activity";
static const char *str_video_db_adapter	=  "Wifi_Setting";
FN_BOOL fn_player_add_subtitle(FN_CHAR *file);
static char *classPlayerUIManager = "com/wireme/activity/Wifi_Setting";
static char *bt_titles = "com/letv/bdplayer/natives/bt_titles";
static char *bt_item = "com/letv/bdplayer/natives/bt_titles$bt_item";
static char *mainfilm = "com/letv/bdplayer/natives/bt_titles$Main_film";
static char *chapter_duration = "com/letv/bdplayer/natives/bt_titles$Main_film$Chapter_Duration";
static char *Pid_Lang = "com/letv/bdplayer/natives/bt_titles$Main_film$Pid_Lang";

static char *Film_footage_item = "com/letv/bdplayer/natives/bt_titles$Film_footage";
JNIEXPORT jobject 	JNICALL  
jni_player_get_titles(JNIEnv* env,jobject thiz,jstring path);
JNIEXPORT jobject 	JNICALL
jni_get_bt_titles(JNIEnv* env,jobject thiz,jstring path);
JNIEXPORT jboolean 	JNICALL
jni_is_bluray_disk(JNIEnv* env,jobject thiz,jstring path);
JNIEXPORT jobject 	JNICALL
jni_bluray_get_version(JNIEnv* env,jobject thiz);
JNIEXPORT jboolean 	JNICALL
jni_mac_crypto(JNIEnv* env,jobject thiz,jstring ip,jstring mac);
JNIEXPORT jboolean 	JNICALL
jni_wifi_username_password(JNIEnv* env,jobject thiz,jstring ip,jstring username,jstring password);
JNIEXPORT jboolean 	JNICALL
jni_restart(JNIEnv* env,jobject thiz,jstring jreboot);
#if 1//def ADD_CORE
static JNINativeMethod methodsplayer[] = {
//    {"get_bt_titles",        "(Ljava/lang/String;)Ljava/lang/Object;", (void *)jni_get_bt_titles},
//    {"is_bluray_disk",        "(Ljava/lang/String;)Z", (void *)jni_is_bluray_disk},
		{"reboot",        "(Ljava/lang/String;)Z", (void *)jni_restart},
	{"mac_crypto",        "(Ljava/lang/String;Ljava/lang/String;)Z", (void *)jni_mac_crypto},
    {"set_username_password",        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z", (void *)jni_wifi_username_password},
};
#endif
jstring stoJstring(JNIEnv* env, const char* pat,int nlen)
{
	return (*env)->NewStringUTF(env,pat);
}
char* jstringTostring(JNIEnv* env, jstring jstr,int* length)
{
	char* rtn = NULL;
	static jmethodID mid = NULL;

	jstring strencode = (*env)->NewStringUTF(env,"utf-8");

	if(NULL == mid)
	{
		jclass j_string = (*env)->FindClass(env, "java/lang/String");
		if(NULL == j_string)
		{
			LOGE("cannot find class java/lang/String");
			return NULL;
		}

		mid = (*env)->GetMethodID(env, j_string, "getBytes","(Ljava/lang/String;)[B");
		if(mid == NULL)return NULL;
	}

	jbyteArray barr= (jbyteArray)(*env)->CallObjectMethod(env,jstr, mid, strencode);
    (*env)->DeleteLocalRef(env, strencode);

	jsize alen = (*env)->GetArrayLength(env,barr);
	jbyte* ba = (*env)->GetByteArrayElements(env,barr, JNI_FALSE);
	if (alen > 0)
	{
		rtn = (char*)malloc(alen + 1);
        if (!rtn)
            return rtn;

		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	*length = alen;
	(*env)->ReleaseByteArrayElements(env,barr,ba,0);
    (*env)->DeleteLocalRef(env, barr);
	return rtn;
}

/************ JAVA C nedia player interface ******************************************************************************/
JNIEnv *Adapter_GetEnv()
{
	int status;
	JNIEnv *env = FN_NULL;
	status = (*g_JavaVM)->GetEnv(g_JavaVM,(void **) &env, JNI_VERSION_1_4);
	if(status < 0) 
	{
		status = (*g_JavaVM)->AttachCurrentThread(g_JavaVM,&env, NULL);
		if(status < 0) 
		{
			return FN_NULL;
		}
		LOGW("info:AttachCurrentThread***********************");
	}
	return env;
}

void JNI_OnUnload(JavaVM *vm, void *reserved)
{    
    JNIEnv *env = Adapter_GetEnv();

    if (NULL != g_java_string)
        (*env)->DeleteGlobalRef(env, g_java_string);
    if (NULL != g_Player_UIManager_Class)
        (*env)->DeleteGlobalRef(env, g_Player_UIManager_Class);
    if (NULL != g_File_Class)
        (*env)->DeleteGlobalRef(env, g_File_Class);
    if (NULL != g_Visualizer_Class)
        (*env)->DeleteGlobalRef(env, g_Visualizer_Class);
    removeGlobalRef(env,g_class_video_db_adapter);
    int i;
    for (i=0; i<TOTAL_MID_NUM; ++i )
    {
        if (arrstrclass[i])
        {
            free(arrstrclass[i]);
    		arrstrclass[i] = NULL;
        }
	}
    //jni_thumbnail_uninit();
	LOGE("*****Unload Called!\n");
}

jboolean Reg_Uim(JNIEnv* env);
jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv *env;
	g_JavaVM = vm;

	LOGV("enter JNI_OnLoad");
	//jni_thumbnail_init();
	if ((*vm)->GetEnv(vm,(void**) &env, JNI_VERSION_1_4) != JNI_OK) 
	{
		return -1;
	}

	
	(*env)->PushLocalFrame(env,128);
	initMakeGlobalRef();
	makeGlobalRef(env,g_class_video_db_adapter,adapter_path,str_video_db_adapter);

	g_java_string = (*env)->FindClass(env, "java/lang/String");
	g_java_string = (*env)->NewGlobalRef(env,g_java_string);

	if(!Reg_Uim(env))
	{
        LOGE("error reg uim");
        goto ON_LOAD_ERROR;
	}

	(*env)->PopLocalFrame(env,0);
    
	LOGV("exit JNI_OnLoad ok 0");
	return JNI_VERSION_1_4;
    
ON_LOAD_ERROR:
   	(*env)->PopLocalFrame(env,0);
    return -1;       
}

jboolean Reg_Uim(JNIEnv* env)
{
	jclass strClassUIManager = (*env)->FindClass(env,classPlayerUIManager);
#if 1//def ADD_CORE

	if (strClassUIManager == NULL) {
		LOGE("error:Native registration unable to find class'%s'", classPlayerUIManager);
		return JNI_FALSE;
	}
	if ((*env)->RegisterNatives(env, strClassUIManager,methodsplayer, sizeof(methodsplayer) / sizeof(methodsplayer[0])) < 0) {
		LOGE("error:register natives failed for class'%s'", strClassUIManager);
		return JNI_FALSE;
	}

#endif
	g_Player_UIManager_Class = (*env)->NewGlobalRef(env,strClassUIManager);

//	(*env)->PopLocalFrame(env,0);
	return JNI_TRUE;

}

#ifndef ADD_CORE
void bd_log(const char * str)
{
	
LOGV(str);
}
#if 0
jobject bd_get_titles(JNIEnv* env,const char * str)
{   
	LOGV("%s() line=%d",__FUNCTION__,__LINE__);
	bluray_set_debug_handler(bd_log);
	bluray_set_debug_mask(BLURAY_AACS);
// ����ر����꣬�ֱ����}��ģʽ
//#define _MODE_OPEN_FILE_HANDLE
#ifdef _MODE_OPEN_FILE_HANDLE

	LOGV( "Open File Handle Mode!" ) ;

	int fd = open(str, 0x8000 );
	if ( is_bluray_disk( 0, fd ) == 0 )
	{
		LOGV( "Not BD ISO!" ) ;

		return NULL;
	}

	BLURAY* bd = bluray_open( NULL, fd ) ;
#else

	LOGV( "Open File Path Mode!" ) ;

	if ( is_bluray_disk( str, -1 ) == 0 )
	{
		LOGV( "Not BD ISO!" ) ;

		return NULL;
	}

	BLURAY *bd = bluray_open( str, -1 );
#endif

    if ( bd )
    {
    	LOGV("Open bd iso succeed!");
    }
    else
    {
    	LOGV("open bd filed!");

    	return NULL;
    }
    int count = bluray_get_titles( bd, 10 ) ;
//	 int count = 10 ;
	 LOGV( "bd_get_titles count = %d", count ) ;
	 if(count <= 0)
	 {
		 LOGV( "bd_get_titles count error" ) ;
		 return NULL;
	 }

/////��ȡ���Ӱ����Ϣ
	 int nLargeTitle = bluray_get_largest_titile( bd, 10 ) ;
	LOGV( "bluray_get_largest_titile = %d", nLargeTitle ) ;
	if(nLargeTitle < 0)
	{
		LOGV( "bluray_get_largest_titile nLargeTitle error" ) ;
		return NULL;
	}
	int nReturn = bluray_select_title( bd, nLargeTitle ) ;
//	LOGV( "bd_select_title index = %d, return = %d ", nLargeTitle, nReturn ) ;
	BLURAY_TITLE_INFO* mainfilm_ti = bluray_get_title_info(bd, nLargeTitle, 0);

	jclass chapter_duration_Class = (*env)->FindClass(env, chapter_duration);
	jmethodID chapter_duration_method = (*env)->GetMethodID(env,chapter_duration_Class, "<init>", "(I)V");
	jobjectArray chapter_duration_array= (*env)->NewObjectArray(env, (jsize)mainfilm_ti->chapter_count, chapter_duration_Class, 0);

	jclass Pid_Lang_Class = (*env)->FindClass(env, Pid_Lang);
	jmethodID Pid_Lang_method = (*env)->GetMethodID(env,Pid_Lang_Class, "<init>", "(ILjava/lang/String;)V");

	int j = 0;
	for(;j < mainfilm_ti->chapter_count ; j ++)
	{
//		LOGV( "chapter_duration[%d] = %lld\n", j,mainfilm_ti->chapters[j].duration) ;
		int duration = (mainfilm_ti->chapters[j].duration + 45000) / 90000;
		LOGV( "duration[%d] = %d,mainfilm_ti=%lld\n",j,duration,mainfilm_ti->chapters[j].duration) ;
		jobject jitem = (*env)->NewObject(env,chapter_duration_Class,chapter_duration_method,duration);
		(*env)->SetObjectArrayElement(env, chapter_duration_array, j, jitem);
	}
	////audio pid
	jobjectArray audio_pid_array;
	if(mainfilm_ti->clips->audio_stream_count > 0)
	{
		audio_pid_array= (*env)->NewObjectArray(env, (jsize)mainfilm_ti->clips->audio_stream_count, Pid_Lang_Class, 0);
		j = 0;
		for(;j < mainfilm_ti->clips->audio_stream_count ; j ++)
		{
			int pid = mainfilm_ti->clips->audio_streams[j].pid;
			jstring jlang;
			char lang[4] ;
			LOGV( "bd_get_titles audio_streams = %s", &mainfilm_ti->clips->audio_streams[j].lang[0] ) ;
			memcpy(&lang[0],&mainfilm_ti->clips->audio_streams[j].lang[0],4);
			jlang = (*env)->NewStringUTF(env,&lang[0]);
			jobject jitem = (*env)->NewObject(env,Pid_Lang_Class,Pid_Lang_method,pid,jlang);
			(*env)->DeleteLocalRef(env,jlang);
			(*env)->SetObjectArrayElement(env, audio_pid_array, j, jitem);
		}
	}
	////pg pid
	jobjectArray pg_pid_array;
	if(mainfilm_ti->clips->pg_stream_count > 0)
	{
		pg_pid_array= (*env)->NewObjectArray(env, (jsize)mainfilm_ti->clips->pg_stream_count, Pid_Lang_Class, 0);
		j = 0;
		for(;j < mainfilm_ti->clips->pg_stream_count ; j ++)
		{
			int pid = mainfilm_ti->clips->pg_streams[j].pid;
			jstring jlang;
			char lang[4] ;
			LOGV( "bd_get_titles pg_streams = %s", &mainfilm_ti->clips->pg_streams[j].lang[0] ) ;
			memcpy(&lang[0],&mainfilm_ti->clips->pg_streams[j].lang[0],4);
			jlang = (*env)->NewStringUTF(env,&lang[0]);
			jobject jitem = (*env)->NewObject(env,Pid_Lang_Class,Pid_Lang_method,pid,jlang);
			(*env)->DeleteLocalRef(env,jlang);
			(*env)->SetObjectArrayElement(env, pg_pid_array, j, jitem);
		}
	}

	////ig pid
	jobjectArray ig_pid_array = NULL;
	if(mainfilm_ti->clips->ig_stream_count > 0)
	{
		ig_pid_array= (*env)->NewObjectArray(env, (jsize)mainfilm_ti->clips->ig_stream_count, Pid_Lang_Class, 0);
		j = 0;
		for(;j < mainfilm_ti->clips->ig_stream_count ; j ++)
		{
			int pid = mainfilm_ti->clips->ig_streams[j].pid;
			jstring jlang;
			char lang[4] ;
			LOGV( "bd_get_titles ig_streams = %s", &mainfilm_ti->clips->ig_streams[j].lang[0] ) ;
			memcpy(&lang[0],&mainfilm_ti->clips->ig_streams[j].lang[0],4);
			jlang = (*env)->NewStringUTF(env,&lang[0]);
			jobject jitem = (*env)->NewObject(env,Pid_Lang_Class,Pid_Lang_method,pid,jlang);
			(*env)->DeleteLocalRef(env,jlang);
			(*env)->SetObjectArrayElement(env, ig_pid_array, j, jitem);
		}
	}

	int mainfilm_ti_duration = mainfilm_ti->duration / 90000;
	LOGV( "mainfilm_ti=%lld,mainfilm_ti_duration = %d\n",mainfilm_ti->duration,mainfilm_ti_duration) ;
	jclass mainfilm_class = (*env)->FindClass(env, mainfilm);
	jmethodID mainfilm_method = (*env)->GetMethodID(env,mainfilm_class, "<init>", "([Lcom/letv/bdplayer/natives/bt_titles$Main_film$Pid_Lang;[Lcom/letv/bdplayer/natives/bt_titles$Main_film$Pid_Lang;[Lcom/letv/bdplayer/natives/bt_titles$Main_film$Pid_Lang;[Lcom/letv/bdplayer/natives/bt_titles$Main_film$Chapter_Duration;IIIIII)V");
	jobject jmainfilm = (*env)->NewObject(env,mainfilm_class,mainfilm_method,
			audio_pid_array,
			pg_pid_array,
			ig_pid_array,
			chapter_duration_array,
			nLargeTitle,
			mainfilm_ti->chapter_count,
			mainfilm_ti->clips->pg_stream_count + mainfilm_ti->clips->ig_stream_count,
			mainfilm_ti->clips->audio_stream_count,
			mainfilm_ti->angle_count,
			mainfilm_ti_duration
			);
//////////////
////����
	   jclass Film_footage_Class = (*env)->FindClass(env, Film_footage_item);


	   jobjectArray subtitles= (*env)->NewObjectArray(env, (jsize)count-1, Film_footage_Class, 0);


	jclass object_titles_Class = (*env)->FindClass(env, bt_titles);
//	jobjectArray subtitles= (*env)->NewObjectArray(env, (jsize)count, object_item_Class, 0);
	
	jmethodID bt_titles_method = (*env)->GetMethodID(env,object_titles_Class, "<init>", "([Lcom/letv/bdplayer/natives/bt_titles$Film_footage;Lcom/letv/bdplayer/natives/bt_titles$Main_film;)V");

	int i=0;

	int y = 0;
	for ( ; i < count ; ++ i)
		{	
				jmethodID bt_item_method = (*env)->GetMethodID(env,Film_footage_Class, "<init>", "(IIZ)V");
				
				if(NULL == bt_item_method)
				{
					LOGE("error:jni_player_get_subtitle GetMethodID init");
					continue;
				}
				if(i == nLargeTitle)
				{
					continue;
				}
				int nReturn = bluray_select_title( bd, i ) ;

				BLURAY_TITLE_INFO* ti = bluray_get_title_info(bd, i, 0);
				if(!ti)
				{
					continue;
				}
				int ti_duration = ti->duration / 90000;
				jboolean flag = bluray_invalid_title( bd, i );
				LOGV( "ti->duration = %lld,i = %d,y=%d,ti_duration=%d flag=%d\n", ti->duration , i,y,ti_duration,flag) ;
				
				jobject jitem = (*env)->NewObject(env,Film_footage_Class,bt_item_method,i,ti_duration,flag);//,data->items[i].refresher_cnt,data->items[i].url_type);
				(*env)->SetObjectArrayElement(env, subtitles, y++, jitem);
		}
	 bluray_close(bd);

	jobject jdata = (*env)->NewObject(env,object_titles_Class,bt_titles_method,subtitles,jmainfilm);
	LOGV("%s() line=%d",__FUNCTION__,__LINE__);
return jdata;
}

JNIEXPORT jobject 	JNICALL  
jni_get_bt_titles(JNIEnv* env,jobject thiz,jstring path)
{
	LOGV("%s() line=%d",__FUNCTION__,__LINE__);
	jobject value ;
	JNIEnv *penv = Adapter_GetEnv();
	char *url = NULL;
	int length;
	if (NULL != path)
		url = (char *) jstringTostring(penv, path,&length);
	LOGV("%s() line=%d url=%s",__FUNCTION__,__LINE__,url);

	value = bd_get_titles(env,url);

	if (NULL != url)
		free(url);

    return value;
}
JNIEXPORT jobject 	JNICALL
jni_bluray_get_version(JNIEnv* env,jobject thiz)
{
	LOGV("%s() line=%d",__FUNCTION__,__LINE__);
	jobject jversion = NULL;
	const char* version = bluray_get_version() ;
	if(!version)
			return jversion;
	LOGV("%s() line=%d version=%s\n",__FUNCTION__,__LINE__,version);

	jversion = (*env)->NewStringUTF(env,version);
//	(*env)->DeleteLocalRef(env,jversion);

    return jversion;
}
JNIEXPORT jboolean 	JNICALL
jni_is_bluray_disk(JNIEnv* env,jobject thiz,jstring path)
{
	LOGV("%s() line=%d",__FUNCTION__,__LINE__);
	jboolean value = JNI_TRUE;
	JNIEnv *penv = Adapter_GetEnv();
	char *url = NULL;
	int length;
	if (NULL != path)
		url = (char *) jstringTostring(penv, path,&length);

	LOGV("%s() line=%d url=%s",__FUNCTION__,__LINE__,url);

	if ( is_bluray_disk( url, -1 ) == 0 )
	{
		LOGV( "Not BD ISO!" ) ;

		value = JNI_FALSE;
	}

	if (NULL != url)
		free(url);
	LOGV("%s() line=%d value=%d",__FUNCTION__,__LINE__,value);
    return value;
}
#endif
JNIEXPORT jboolean 	JNICALL
jni_wifi_username_password(JNIEnv* env,jobject thiz,jstring ip,jstring username,jstring password)
{
	jboolean value = JNI_FALSE;
	int ret = -1;
	JNIEnv *penv = Adapter_GetEnv();
	int url_length;
	int name_length;
	int password_length;
	char *url = NULL;
	char *name = NULL;
	char* pass = NULL;
	if (NULL == ip || username == NULL || password == NULL)
	{
		goto END;
	}
	url = (char *) jstringTostring(penv, ip,&url_length);
	if(url_length < 10)
	{
		goto END;
	}
	name = (char *) jstringTostring(penv, username,&name_length);

	pass = (char *) jstringTostring(penv, password,&password_length);

	if(url == NULL || name == NULL || pass == NULL)
	{
		goto END;
	}
	LOGV("%s() line=%d url=%s,name=%s,pass=%s\n",__FUNCTION__,__LINE__,url,name,pass);
	ret = socket_username_password(url,url_length,name,pass);
	value = JNI_TRUE;
END:
	if (NULL != url)
		free(url);
	if (NULL != name)
		free(name);
	if (NULL != pass)
		free(pass);
	return value;
}
JNIEXPORT jboolean 	JNICALL
jni_mac_crypto(JNIEnv* env,jobject thiz,jstring ip,jstring jmac)
{
	int ret;
	jboolean value = JNI_FALSE;
	JNIEnv *penv = Adapter_GetEnv();
	int url_length;
	int mac_length;
	char *url = NULL;
	char *mac = NULL;
	if (NULL == ip || jmac == NULL)
	{
		goto END;
	}
	LOGV("%s() line=%d url=%s,mac=%s\n",__FUNCTION__,__LINE__,url,mac);
	url = (char *) jstringTostring(penv, ip,&url_length);
	if(url_length < 10)
	{
		goto END;
	}
	LOGV("%s() line=%d url=%s,mac=%s\n",__FUNCTION__,__LINE__,url,mac);
	mac = (char *) jstringTostring(penv, jmac,&mac_length);

	if(url == NULL || mac == NULL)
	{
		goto END;
	}
	LOGV("%s() line=%d url=%s,mac=%s\n",__FUNCTION__,__LINE__,url,mac);
	ret = socket_mac(url,mac);
	value = JNI_TRUE;
END:
	if (NULL != url)
		free(url);
	if (NULL != mac)
		free(mac);
	return value;

}
JNIEXPORT jboolean 	JNICALL
jni_restart(JNIEnv* env,jobject thiz,jstring jreboot)
{
	int ret;
	jboolean value = JNI_FALSE;
	JNIEnv *penv = Adapter_GetEnv();
	int reboot_length;
	char *reboot = NULL;

	if (NULL == jreboot)
	{
		goto END;
	}
	LOGV("%s() line=%d jreboot=%s\n",__FUNCTION__,__LINE__,jreboot);
	reboot = (char *) jstringTostring(penv, jreboot,&reboot_length);
	if (NULL == reboot)
	{
		goto END;
	}
	LOGV("%s() line=%d reboot=%s\n",__FUNCTION__,__LINE__,reboot);

	ret = socket_restart(reboot);
	value = JNI_TRUE;
END:
	if (NULL != reboot)
		free(reboot);
	return value;

}
#endif
int wifi_setting_jni_to_java(const char* str,int type)
{
	int value = -1;
	LOGI("wifi_setting_jni_to_java()\n");
	if(NULL == str)
	{
		LOGE("cid failed \n");
		return value;
	}

	JNIEnv *env = Adapter_GetEnv();
	(*env)->PushLocalFrame(env, 128);

	jmethodID mid = (*env)->GetStaticMethodID(env,g_class_video_db_adapter,"jni_callback", "(Ljava/lang/String;I)I");
	if (NULL == mid)
	{
		LOGE("android_adapter_video_DB_select_get_count_by_cid failed \n");
		goto end;
	}

	jstring jcid = NULL;
	construct_jstring(env,str,jcid);
	if(jcid == NULL)
		goto end;
	value = (*env)->CallStaticIntMethod(env, g_class_video_db_adapter, mid,jcid,type);

	(*env)->DeleteLocalRef(env,jcid);

end:
	(*env)->PopLocalFrame(env, 0);

	return value;
}
