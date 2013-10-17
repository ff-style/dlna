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


#include "app_socket.h"

int socket_init(unsigned int port)
{
	int mysocket;
	int ret;
	int reuse_addr = 1;
	struct sockaddr_in host_sockaddr={0};
	
	mysocket =  socket(AF_INET,SOCK_DGRAM,IPPROTO_UDP);
	if(mysocket < 0)
	{
	    printf("socket fail\n");
	    goto error_handler;
	}

	ret = setsockopt(mysocket,SOL_SOCKET,SO_REUSEADDR,(char *)&reuse_addr,sizeof(reuse_addr));
	
	if(ret)
	{
	    printf("setsockopt fail\n");
	    goto error_handler;
	}

	host_sockaddr.sin_family=AF_INET;
	host_sockaddr.sin_port= htons(port);
	host_sockaddr.sin_addr.s_addr = htonl(INADDR_ANY);

	ret = bind(mysocket,(struct sockaddr *)&host_sockaddr,sizeof(struct sockaddr_in));
	if(ret != 0)
	{
	    printf("bind fail\n");
	    goto error_handler;
	}

    //printf("mysocket=%d\n",mysocket);
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
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
	pProcessMsg->msgType = msgType;

	if(msgContect != NULL && lenContent > 0)
	{
		memcpy(pProcessMsg->msgContent, msgContect, lenContent);
	}
	
    //if( !inet_aton("192.168.169.100",&destsaddr.sin_addr)) 
        //printf("bad address"); 

    #ifdef SERVER_IP
    TRACE("Handling client %s\n",inet_ntoa(srcesaddr.sin_addr));
    destsaddr.sin_addr=srcesaddr.sin_addr;
	destsaddr.sin_family = AF_INET;
	destsaddr.sin_port = htons(port_type);
	#else
    destsaddr.sin_family = AF_INET;
    destsaddr.sin_addr.s_addr = INADDR_ANY;
    destsaddr.sin_port = htons(port_type);
	#endif

	
	ret = sendto(mysocket, (void *)buffer, bufferLen, 0, (struct sockaddr *)&destsaddr, sizeof(struct sockaddr_in));
	free(buffer);
	
	if(ret == -1)
	{
		printf("%s() return_2\n", __FUNCTION__);
		return -1;
	}

	TRACE("sendto success! ret: %d\n", ret);
	
	return 0;
}

int Recv_Socket_Msg(int mysocket, void *buffer, int bufferLen, int wait)
{

	TRACE("socket: %d, wait: %d\n", mysocket, wait);

	int ret = 0;
	int fromlen = sizeof(srcesaddr);
	//struct sockaddr_in srcesaddr = {0};

	if(wait <= 0)
	{
		ret = recvfrom(mysocket, (void *)buffer, bufferLen, 0, (struct sockaddr *)&srcesaddr, &fromlen);
		if(ret == -1)
		{
			printf("%s() return_1\n", __FUNCTION__);
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
			printf("%s() return_2\n", __FUNCTION__);
			return -1;
		}
		else if(ret == 0)//timeout
		{
			printf("%s() return_3\n", __FUNCTION__);
			return -2;
		}
		else
		{
			if(FD_ISSET(mysocket, &fds))
			{
				ret = recvfrom(mysocket, (void *)buffer, bufferLen, MSG_DONTWAIT, (struct sockaddr *)&srcesaddr, &fromlen);
				if(ret == -1)
				{
					printf("%s() return_4\n", __FUNCTION__);
					return -1;
				}
			}
			else
			{
				printf("%s() return_5\n", __FUNCTION__);
				return -1;
			}
		}
	}
	

	TRACE( "ret: %d\n", ret);
	return ret;
}


int Send_Socket_Msg_To_APP(int msgType, void *msgContect, int lenContent)
{
	
	if(server_socket == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Send_Socket_Msg(APP_PORT, server_socket, msgType, msgContect, lenContent);

}

int Recv_Socket_Msg_From_APP(void *buffer, int bufferLen, int wait)
{

	
	if(server_socket == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Recv_Socket_Msg(server_socket, buffer, bufferLen, wait);
}


int Send_Socket_Msg_To_SERVER(int msgType, void *msgContect, int lenContent)
{
	
	if(app_socket == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	return Send_Socket_Msg(SERVER_PORT, app_socket, msgType, msgContect, lenContent);

}

int Recv_Socket_Msg_From_SERVER(void *buffer, int bufferLen, int wait)
{

	if(app_socket == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
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

    TRACE("ssid=%s\n",Msg_Wifi->ssid);
    TRACE("way=%d\n",Msg_Wifi->way);
    TRACE("password=%s\n",Msg_Wifi->password);

    
	if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_INFO_CNF, (void *)Msg_Wifi, sizeof(st_wifi_info)) == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	
    
	return 0;
}



int SocketMsgPro_AppSetWifiInfoCnf(void *buffer, int bufferLen)
{

   st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
   char reboot = 0;
   st_wifi_info *Msg_Wifi;

   
   if(pProcessMsg->msgContent != NULL)
   Msg_Wifi = (st_wifi_info *)pProcessMsg->msgContent;

   if(strcmp(Msg_Wifi->ssid,g_ssid_set))
   return -1;

   if(Msg_Wifi->way != g_way_set)
   return -1;

   if(strcmp(Msg_Wifi->password,g_password_set))
   return -1;    


   TRACE("wifi set password successfully\n");

   TRACE("did you need reboot router (y/n)=");

   scanf("%c",&reboot);

   if(reboot)
   {
       if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_REBOOT_REQ, (void *)&reboot, sizeof(reboot)) == -1)
       {
           printf("%s() return_1\n", __FUNCTION__);
           return -1;
       }
   }
   return 0;

}


int check_storage_disk(void)
{
       
    FILE *fstream=NULL;       
    char buff[30];     
    
    memset(buff,0,sizeof(buff));     
    if(NULL==(fstream=popen("check_media.sh","r")))       
    {   
        printf("execute command failed\n");       
        return -1;       
    }      
    if(NULL!=fgets(buff, sizeof(buff), fstream))      
    {      
        printf("%s",buff);
        if(strlen(buff)>0)
        {
            printf("command return=%s\n",buff);
            return 1;
        }
    }      
    else     
    {     
        pclose(fstream);     
        return -1;     
    }   
    
    pclose(fstream);     
    return 0;      
} 


int SocketMsgPro_AppAuthReq(void *buffer, int bufferLen)
{

        int ret;
        
        st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;

   
        st_encryption_cnf Msg_Wifi;

        if(pProcessMsg->msgContent != NULL)
        TRACE("SocketMsgPro_AppAuth mac=%s\n",(char *)pProcessMsg->msgContent);
        
        strcpy(Msg_Wifi.mac,(char *)pProcessMsg->msgContent);

        if( check_storage_disk() == 1 )
        Msg_Wifi.dlna = 1;
        else
        Msg_Wifi.dlna = 0;

        ret = encryption_calc(Msg_Wifi.mac,Msg_Wifi.encryption);
        if(ret < 0)
        return -1;
        
        //strcpy(Msg_Wifi.encryption,"i am a vaild client\n");
        if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_AUTH_CNF, (void *)&Msg_Wifi, sizeof(Msg_Wifi)) == -1)
        {
            printf("%s() return_1\n", __FUNCTION__);
            return -1;
        }
        
        
        return 0;
}


int SocketMsgPro_AppAuthCnf(void *buffer, int bufferLen)
{
	st_encryption_cnf *pProcessMsg = (st_encryption_cnf *)buffer;


    printf("MAC Cnf=%s\n",pProcessMsg->mac);
    printf("Encryption=%s\n",pProcessMsg->encryption);

    /*
	if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_INFO_CNF, (void *)&t_SysWifiInfo_Dynamic, sizeof(t_SysStatusInfo_Dynamic)) == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	
    */
	return 0;
}




int SocketMsgPro_UnknownReq(void *buffer, int bufferLen)
{

	/*
	if(SEND_SOCKET_MSG_TO_APP(APP_MSG_WIFI_INFO_REQ, (void *)&t_SysWifiInfo_Dynamic, sizeof(t_SysStatusInfo_Dynamic)) == -1)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	*/
	printf("SocketMsgPro_UnknownReq\n");
	return 0;
}

int SocketMsgPro_AppRebootReq(void *buffer, int bufferLen)
{
    system("reboot");
	printf("SocketMsgPro reboot successfully\n");
	return 0;
}



int FilterAppMsg(void *buffer, int bufferLen)
{
	
	int ret = 0;
	st_Process_Msg *pProcessMsg = (st_Process_Msg *)buffer;
	TRACE("pProcessMsg->msgType: %d\n", pProcessMsg->msgType);
	int commandtype = 0;
	
	if(pProcessMsg->msgType < 0 || pProcessMsg->msgType >= SOCKET_MSG_UNKNOWN)
	{
		//SocMsgProFunGp[SOCKET_MSG_UNKNOWN](commandtype, 0);
		return 0;
	}

	
	if(SocMsgProFunGp[pProcessMsg->msgType](buffer, bufferLen) == -1)
	{
		printf("%s() return_3\n", __FUNCTION__);
		return -1;
	}
	
	return 0;

}


int main(void)
{
	int ret=0;
	char buffer[128] = {0};

	server_socket = INIT_SOCKET_SERVER;
	if(server_socket < 0)
	{
		printf("%s() return_1\n", __FUNCTION__);
		return -1;
	}
	while(1)
	{
        
		ret = RECV_SOCKET_MSG_FROM_APP(buffer, sizeof(buffer), 0);
		if(ret < 0)
		{
			printf("%s() return_2\n", __FUNCTION__);
			break;
		}

		FilterAppMsg(buffer, ret);
	}

}



