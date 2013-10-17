#define APP_SOCKET_H

#ifdef release_version
#define TRACE(fmt,args...)
#else
#define TRACE(fmt,args...) printf("##: "fmt,##args)
#endif

typedef struct
{
	int msgType;
	char msgContent[1]; //this is a skill to malloc buffer,because array in struct is continue buffer
}
st_Process_Msg;

#define SERVER_PORT		10013
#define APP_PORT		10014
//#define SERVER_IP       "192.168.169.1"   //coment this you could debug on you linux pc



struct sockaddr_in srcesaddr = {0};

#define INIT_SOCKET_SERVER			socket_init(SERVER_PORT)

#define INIT_SOCKET_APP		        socket_init(APP_PORT)

#define SEND_SOCKET_MSG_TO_APP(msgType, msgContect, lenContent)		        Send_Socket_Msg_To_APP(msgType, msgContect, lenContent)

#define SEND_SOCKET_MSG_TO_SERVER(msgType, msgContect, lenContent)		        Send_Socket_Msg_To_SERVER(msgType, msgContect, lenContent)

#define RECV_SOCKET_MSG_FROM_APP(buffer, bufferLen, wait)			Recv_Socket_Msg_From_APP(buffer, bufferLen, wait)

#define RECV_SOCKET_MSG_FROM_SERVER(buffer, bufferLen, wait)	  		Recv_Socket_Msg_From_SERVER(buffer, bufferLen, wait)

int server_socket=0;
int app_socket=0;

char g_ssid_set[20]={0};
char g_password_set[20]={0};
char g_way_set=0;

typedef int (*SocketMsgProcessFun)(void *buffer, int bufferLen);

enum{
APP_MSG_WIFI_INFO_REQ= 0,
APP_MSG_WIFI_INFO_CNF= 1,
APP_MSG_WIFI_AUTH_REQ,
APP_MSG_WIFI_AUTH_CNF,
APP_MSG_WIFI_REBOOT_REQ,
SOCKET_MSG_UNKNOWN,
};

typedef enum
{
WIFI_OPEN = 0, /*open,no encryption*/
WIFI_ENCRYPT = 1, /*encryption with wpa2 default*/
ENCRYPT_WAY_UNKNOWN,
}WifiSecurity;



typedef struct
{
	char ssid[50];
	WifiSecurity way;
	char password[30];
}
st_wifi_info;

typedef struct
{
	char mac[30];
	char encryption[33];
	char dlna;
}
st_encryption_cnf;

extern int SocketMsgPro_AppSetWifiInfo(void *buffer, int bufferLen);
extern int SocketMsgPro_UnknownReq(void *buffer, int bufferLen);
extern int SocketMsgPro_AppAuthReq(void *buffer, int bufferLen);
extern int SocketMsgPro_AppAuthCnf(void *buffer, int bufferLen);
extern int encryption_calc(char *,char *);
extern int SocketMsgPro_AppSetWifiInfoCnf(void *buffer, int bufferLen);
extern int SocketMsgPro_AppRebootReq(void *buffer, int bufferLen);

SocketMsgProcessFun SocMsgProFunGp[]=
{
	SocketMsgPro_AppSetWifiInfo,
	SocketMsgPro_AppSetWifiInfoCnf,
	SocketMsgPro_AppAuthReq,
	SocketMsgPro_AppAuthCnf,
	SocketMsgPro_AppRebootReq,
	SocketMsgPro_UnknownReq,
};

#undef  APP_SOCKET_H
