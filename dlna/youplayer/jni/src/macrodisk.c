/***********************************************************************
 ** Author: wuz@macroinfo.com.cn   Date:2013-06-28
 ** Applock:This bin is used for monitor socket msg from app macrodisk
 **         to check if this router is valid for app access.	 	
 **	    applock will get socket command then return socket result.
 ************************************************************************/

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/times.h>
#include <sys/select.h>
#include <unistd.h>           //unlink
#include <stdlib.h>            //free()
#include <ctype.h>           //isspace()
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>        //stat()
#include <sys/syscall.h>    //syscall()
#include <signal.h>           //kill()
#include <dirent.h>           //opendir()
#include <fcntl.h>
#include <termios.h>
#include <sys/wait.h>
#include <time.h>
#include <stdarg.h>
#include <stdbool.h>
#include <net/if.h>  /*read_interface*/
#include <linux/sockios.h>
#include "player_jni.h"
#include <pthread.h> 
#include "app_socket.h"
static char server_ip[64];
static st_wifi_info test_wifi_req;
static pthread_t g_thread_id = -1;
int socket_init(unsigned int port)
{
	int mysocket;
	int ret;
	int reuse_addr=1;
	int so_broadcast=1;
	struct sockaddr_in host_sockaddr={0};

	LOGE("macrodisk port=%d\n",port);
	mysocket =  socket(AF_INET,SOCK_DGRAM,IPPROTO_UDP);
	LOGE("%s line=%d mysocket=%d\n",__FUNCTION__,__LINE__,mysocket);
	if(mysocket < 0)
	goto error_handler;
	LOGE("%s line=%d\n",__FUNCTION__,__LINE__);
	ret = setsockopt(mysocket,SOL_SOCKET,SO_REUSEADDR,(char *)&reuse_addr,sizeof(reuse_addr));
	LOGE("%s line=%d ret=%d\n",__FUNCTION__,__LINE__,ret);
   // ret = setsockopt(mysocket,SOL_SOCKET,SO_BROADCAST,&so_broadcast,sizeof(so_broadcast));
	if(ret)
	goto error_handler;

    memset(&host_sockaddr,0,sizeof(host_sockaddr));
LOGE("%s line=%d ret=%d\n",__FUNCTION__,__LINE__,ret);
	host_sockaddr.sin_family=AF_INET;
	host_sockaddr.sin_port= htons(port);
	host_sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	
      //if( !inet_aton("192.168.169.1",&host_sockaddr.sin_addr)) 
    //printf("bad address"); 
    
LOGE("%s line=%d\n",__FUNCTION__,__LINE__);
	ret = bind(mysocket,(struct sockaddr *)&host_sockaddr,sizeof(struct sockaddr_in));
	if(ret != 0)
	goto error_handler;
LOGE("%s line=%d\n",__FUNCTION__,__LINE__);

	return mysocket;

	error_handler:
	if(mysocket > 0)
	close(mysocket);

}


int Send_Socket_Msg(int port_type, int mysocket, int msgType, void *msgContect, int lenContent)
{

	int ret = 0;
	struct sockaddr_in destsaddr = {0};
	

	int bufferLen = sizeof(st_Process_Msg) + lenContent;
	char *buffer = malloc(bufferLen);
	if(buffer == NULL)
	{
		LOGE("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
	pProcessMsg->msgType = msgType;

	if(msgContect != NULL && lenContent > 0)
	{
		memcpy(pProcessMsg->msgContent, msgContect, lenContent);
	}

    LOGE("send msg=%s\n",pProcessMsg->msgContent);
    LOGE("macrodisk port2=%d\n",port_type);

    #ifdef SERVER_IP
    if( !inet_aton(SERVER_IP,&destsaddr.sin_addr)) 
     LOGE("bad address\n"); 
    #else
     destsaddr.sin_addr.s_addr = inet_addr(&server_ip);//inet_addr("192.168.0.127"); //htonl(INADDR_ANY);
    #endif

	destsaddr.sin_family = AF_INET;
	destsaddr.sin_port = htons(port_type);
	
	ret = sendto(mysocket, (void *)buffer, bufferLen, 0, (struct sockaddr *)&destsaddr, sizeof(struct sockaddr_in));
	free(buffer);
	
	if(ret == -1)
	{
		LOGE("%s() return_2\n", __FUNCTION__);
		return -1;
	}
	LOGE("sendto success! ret: %d\n", ret);
	
	return 0;
}

int Recv_Socket_Msg(int mysocket, void *buffer, int bufferLen, int wait)
{

	TRACE("socket: %d, wait: %d\n", mysocket, wait);

	int ret = 0;
	int fromlen = 0;
	
	struct sockaddr_in srcesaddr;


	if(wait <= 0)
	{
		ret = recvfrom(mysocket, (void *)buffer, bufferLen, 0, (struct sockaddr *)&srcesaddr, &fromlen);
		if(ret == -1)
		{
			LOGE("%s() return_1\n", __FUNCTION__);
			return -1;
		}
	}
	else
	{
		fd_set fds;
		struct timeval timeout = {wait, 0};
		
		FD_ZERO(&fds);
		FD_SET(mysocket, &fds);

		ret = select(mysocket+1, &fds, NULL, NULL, &timeout);
		if(ret < 0)
		{
			LOGE("%s() return_2\n", __FUNCTION__);
			return -1;
		}
		else if(ret == 0)//timeout
		{
			LOGE("%s() return_3\n", __FUNCTION__);
			return -2;
		}
		else
		{
			if(FD_ISSET(mysocket, &fds))
			{
				ret = recvfrom(mysocket, (void *)buffer, bufferLen, MSG_DONTWAIT, (struct sockaddr *)&srcesaddr, &fromlen);
				if(ret == -1)
				{
					LOGE("%s() return_4\n", __FUNCTION__);
					return -1;
				}
			}
			else
			{
				LOGE("%s() return_5\n", __FUNCTION__);
				return -1;
			}
		}
	}

	LOGE( "ret: %d\n", ret);
	return ret;
}


int Send_Socket_Msg_To_APP(int msgType, void *msgContect, int lenContent)
{
	
	if(server_socket == -1)
	{
		LOGE("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Send_Socket_Msg(APP_PORT, server_socket, msgType, msgContect, lenContent);

}

int Recv_Socket_Msg_From_APP(void *buffer, int bufferLen, int wait)
{

	
	if(server_socket == -1)
	{
		LOGE("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Recv_Socket_Msg(server_socket, buffer, bufferLen, wait);
}


int Send_Socket_Msg_To_SERVER(int msgType, void *msgContect, int lenContent)
{
	
	if(app_socket == -1)
	{
		LOGE("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Send_Socket_Msg(SERVER_PORT, app_socket, msgType, msgContect, lenContent);

}

int Recv_Socket_Msg_From_SERVER(void *buffer, int bufferLen, int wait)
{

	if(app_socket == -1)
	{
		LOGE("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Recv_Socket_Msg(app_socket, buffer, bufferLen, wait);
}
	     
int SocketMsgPro_AppSetWifiInfo(void *buffer, int bufferLen)
{	

    st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;

    st_wifi_info *Msg_Wifi;

    
    if(pProcessMsg->msgContent != NULL)
    Msg_Wifi = (st_wifi_info *)pProcessMsg->msgContent;

    LOGE("ssid=%s\n",Msg_Wifi->ssid);
    LOGE("way=%d\n",Msg_Wifi->way);
    LOGE("password=%s\n",Msg_Wifi->password);

	if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_INFO_CNF, (void *)&Msg_Wifi, sizeof(st_wifi_info)) == -1)
	{
		LOGE("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	
    
	return 0;
}

int SocketMsgPro_AppRebootReq(void *buffer, int bufferLen)
{
    system("reboot &");
	LOGE("SocketMsgPro reboot successfully\n");
	return 0;
}


int SocketMsgPro_AppAuthReq(void *buffer, int bufferLen)
{
   
        st_encryption_cnf Msg_Wifi={0};

        if(strlen((char*)buffer) > 0)
        LOGE("SocketMsgPro_AppAuth mac=%s\n",(char *)buffer);

        memcpy(Msg_Wifi.mac,(char *)buffer,bufferLen);
    
        
        if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_AUTH_CNF, (void *)&Msg_Wifi, sizeof(Msg_Wifi)) == -1)
        {
            LOGE("%s() return_1\n", __FUNCTION__);
            return -1;
        }
        
        
        return 0;
}


 int SocketMsgPro_AppSetWifiInfoCnf(void *buffer, int bufferLen)
 {

    st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
//    char reboot;
    st_wifi_info *Msg_Wifi;

    
    if(pProcessMsg->msgContent != NULL)
    Msg_Wifi = (st_wifi_info *)pProcessMsg->msgContent;

    LOGE("Msg_Wifi->ssid=%s\n",Msg_Wifi->ssid);
    LOGE("Msg_Wifi->password=%s\n",Msg_Wifi->password);
    LOGE("Msg_Wifi->way=%d\n",Msg_Wifi->way);

    if(strcmp(Msg_Wifi->ssid,g_ssid_set))
    return -1;

    if(Msg_Wifi->way != g_way_set)
    return -1;

    if(strcmp(Msg_Wifi->password,g_password_set))
    return -1;    


    LOGE("wifi set password successfully\n");

    LOGE("did you need reboot router (y/n)=");
    wifi_setting_jni_to_java("did you need reboot router (y/n)=",3);
//    scanf("%c",&reboot);
//
//    if(reboot == 'y')
//    {
//        if(SEND_SOCKET_MSG_TO_SERVER(APP_MSG_WIFI_REBOOT_REQ, (void *)&reboot, sizeof(reboot)) == -1)
//        {
//            LOGE("%s() return_1\n", __FUNCTION__);
//            return -1;
//        }
//    }
    return 0;

 }


int SocketMsgPro_AppAuthCnf(void *buffer, int bufferLen)
{
    int ret=0;
    
	st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
	char app_encription[33];
	
    st_encryption_cnf *Msg_Wifi;
    
    if(pProcessMsg->msgContent != NULL)
    {
        
        Msg_Wifi = (st_encryption_cnf *)pProcessMsg->msgContent;
   
    }

    LOGE("MAC Cnf=%s\n",Msg_Wifi->mac);
    LOGE("Encryption=%s\n",Msg_Wifi->encryption);

    if(Msg_Wifi->dlna == 0)
    {
        LOGE("@-@: dlna not open ,there is not storage disk\n");
        wifi_setting_jni_to_java("@-@: dlna not open ,there is not storage disk\n",1);
        return 0;
    }
   
    ret = encryption_calc(Msg_Wifi->mac,app_encription);
    if(ret < 0)
    return -1;


    LOGE("app_encription =%s\n",app_encription);

    if( ! strncmp(app_encription,Msg_Wifi->encryption,sizeof(app_encription)) )
    {
    LOGE("@-@: it is a vaild client,open macrodisk app\n");
    wifi_setting_jni_to_java("@-@: it is a vaild client,open macrodisk app\n",2);
    }

	return 0;
}



int SocketMsgPro_UnknownReq(void *buffer, int bufferLen)
{
	return 0;
}


/*please use this fuction to get wifi gateway ip address*/
static char CetGatewayIP(char *ip)
{
  int ra0_sock;
  struct ifreq ifr;
  ra0_sock = socket(AF_INET,SOCK_DGRAM,0);
  strcpy(ifr.ifr_name,"eth0");

  if(ioctl(ra0_sock,SIOCGIFADDR,&ifr))
  {
     TRACE("get eth0 state error\n\r");
     return 0;
  }
  else
  {
    TRACE("eth0 ip address:%s\n", inet_ntoa(((struct sockaddr_in*)&(ifr.ifr_addr))->sin_addr));
    strcpy(ip,inet_ntoa(((struct sockaddr_in*)&(ifr.ifr_addr))->sin_addr));
    return 1;
  }

}



int FilterAppMsg(void *buffer, int bufferLen)
{
	
	int ret = 0;
	st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
	LOGE("pProcessMsg->msgType: %d\n", pProcessMsg->msgType);
	int commandtype = 0;
	
	if(pProcessMsg->msgType < 0 || pProcessMsg->msgType >= SOCKET_MSG_UNKNOWN)
	{
		//SocMsgProFunGp[SOCKET_MSG_UNKNOWN](commandtype, 0);
		return 0;
	}

	
	if(SocMsgProFunGp[pProcessMsg->msgType](buffer, bufferLen) == -1)
	{
		LOGE("%s() return_3\n", __FUNCTION__);
		return -1;
	}
	
	return 0;

}



static void *thread_func(void *arg)
{
	int ret=0;
	char buffer[128] = {0};

	/*this is wifi setting requstion example*/
//	st_wifi_info test_wifi_req;

//	test_wifi_req.ssid = &username[0];
//	test_wifi_req.way = WIFI_ENCRYPT;
//	test_wifi_req.password = &password[0];

//    strcpy(g_ssid_set,test_wifi_req.ssid);
//    strcpy(g_password_set,test_wifi_req.password);
//    g_way_set = test_wifi_req.way;
    
//    char mac[]={"00:0c:0a:ad:12"};

//	app_socket = INIT_SOCKET_APP;
//	if(app_socket < 0)
//	{
//		LOGE("%s() return_1\n", __FUNCTION__);
//		return -1;
//	}
	
    
    /*Test: Macrodisk app request modify wifi ssid or password*/
//    ret = SEND_SOCKET_MSG_TO_SERVER(APP_MSG_WIFI_INFO_REQ,(void *)&test_wifi_req,sizeof(test_wifi_req));
//    if(ret < 0)
//    {
//         LOGE("%s() APP_MSG_WIFI_INFO_REQ FAIL\n", __FUNCTION__);
//         return -1;
//    }

    /*Test: Macrodisk app request authrentication of identity */
//    ret = SEND_SOCKET_MSG_TO_SERVER(APP_MSG_WIFI_AUTH_REQ,(void *)&mac,sizeof(mac));
//    if(ret < 0)
//    {
//       LOGE("%s() APP_MSG_WIFI_INFO_REQ FAIL\n", __FUNCTION__);
//       return -1;
//    }

    /*Test: Macrodisk app receive authrentication reply */
    while(1)
    {
		ret = RECV_SOCKET_MSG_FROM_SERVER(buffer, sizeof(buffer), 0);
		if(ret < 0)
		{
			LOGE("%s() return_2\n", __FUNCTION__);
			return -1;
		}

		FilterAppMsg(buffer, ret);
	}
        

}

/* 
static void *thread_func(void *arg)
{
	#ifdef ANDROID_PLAYER
	fone_sys_attach_env_for_http();
	#endif
	while(1)
	{
	fn_cache_progress_to_ui(1);
	usleep(2*1000*1000);
	}
	#ifdef ANDROID_PLAYER
	fone_sys_detach_env_for_http();
	#endif
}

int socket_begin(char * ip,int crypto,char* name,char* pass)
{ 
 	int ret;
	pthread_t thread_id;

	server_ip[0] = 0;
	strncpy(server_ip,ip,strlen(ip));
	server_ip[strlen(ip)] = 0;

	test_wifi_req.ssid[0] = 0;
	strncpy(test_wifi_req.ssid,name,strlen(name));
	test_wifi_req.ssid[strlen(name)] = 0;

	test_wifi_req.password[0] = 0;
	strncpy(test_wifi_req.password,pass,strlen(pass));
	test_wifi_req.password[strlen(pass)] = 0;

	test_wifi_req.way = WIFI_ENCRYPT;
	LOGE("%s() username=%s,password=%s\n", __FUNCTION__,test_wifi_req.ssid,test_wifi_req.password);
    ret = pthread_create(&thread_id,NULL,thread_func,NULL);
	return ret;
}
*/
int init_socket_thread()
{
	int ret;
	ret = pthread_create(&g_thread_id,NULL,thread_func,NULL);
	LOGE("%s() g_thread_id=%d,ret=%d\n", __FUNCTION__,g_thread_id,ret);
	if(ret < 0 || g_thread_id < 0)
	{
		return -1;
	}
	return 0;
}
int socket_username_password(char * ip,int crypto,char* name,char* pass)
{
	int ret = -1;
	if(0 == app_socket)
	{
		app_socket = INIT_SOCKET_APP;
		LOGE("%s() app_socket=%d\n", __FUNCTION__,app_socket);
		if(app_socket < 0)
		{
			LOGE("%s() return_1\n", __FUNCTION__);
			return -1;
		}
	}
	if(g_thread_id < 0)
	{
		int ret = init_socket_thread();
		if(ret < 0)
			return -1;
	}

	server_ip[0] = 0;
	strncpy(server_ip,ip,strlen(ip));
	server_ip[strlen(ip)] = 0;

	test_wifi_req.ssid[0] = 0;
	strncpy(test_wifi_req.ssid,name,strlen(name));
	test_wifi_req.ssid[strlen(name)] = 0;

	test_wifi_req.password[0] = 0;
	strncpy(test_wifi_req.password,pass,strlen(pass));
	test_wifi_req.password[strlen(pass)] = 0;

	test_wifi_req.way = WIFI_ENCRYPT;
	LOGE("%s() username=%s,password=%s\n", __FUNCTION__,test_wifi_req.ssid,test_wifi_req.password);

    strcpy(g_ssid_set,test_wifi_req.ssid);
    strcpy(g_password_set,test_wifi_req.password);
    g_way_set = test_wifi_req.way;

	ret = SEND_SOCKET_MSG_TO_SERVER(APP_MSG_WIFI_INFO_REQ,(void *)&test_wifi_req,sizeof(test_wifi_req));
	if(ret < 0)
	{
		 LOGE("%s() APP_MSG_WIFI_INFO_REQ FAIL\n", __FUNCTION__);
		 return -1;
	}
	return 0;
}
int socket_mac(char * ip,char* mac)
{
	int ret = -1;
	if(0 == app_socket)
	{
		app_socket = INIT_SOCKET_APP;
		LOGE("%s() app_socket=%d\n", __FUNCTION__,app_socket);
		if(app_socket < 0)
		{
			LOGE("%s() return_1\n", __FUNCTION__);
			return -1;
		}
	}
	if(g_thread_id < 0)
	{
		int ret = init_socket_thread();
		if(ret < 0)
			return -1;
	}

	server_ip[0] = 0;
	strncpy(server_ip,ip,strlen(ip));
	server_ip[strlen(ip)] = 0;

	/*Test: Macrodisk app request authrentication of identity */
	ret = SEND_SOCKET_MSG_TO_SERVER(APP_MSG_WIFI_AUTH_REQ,(void *)mac,strlen(mac));
	if(ret < 0)
	{
	   LOGE("%s() APP_MSG_WIFI_INFO_REQ FAIL\n", __FUNCTION__);
	   return -1;
	}
	return 0;
}
int socket_restart(char * reboot)
{
	int ret = -1;
	if(0 == app_socket)
	{
		app_socket = INIT_SOCKET_APP;
		LOGE("%s() app_socket=%d\n", __FUNCTION__,app_socket);
		if(app_socket < 0)
		{
			LOGE("%s() return_1\n", __FUNCTION__);
			return -1;
		}
	}
	if(g_thread_id < 0)
	{
		int ret = init_socket_thread();
		if(ret < 0)
			return -1;
	}

	if(reboot[0] == 'y')
	{
		if(SEND_SOCKET_MSG_TO_SERVER(APP_MSG_WIFI_REBOOT_REQ, (void *)&reboot, sizeof(reboot)) == -1)
		{
			LOGE("%s() return_1\n", __FUNCTION__);
			return -1;
		}
		else{
			app_socket = 0;
		}
	}

	return 0;
}
