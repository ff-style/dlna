//package com.talent.allshare.more;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//import android.util.Log;
//
//public class Netgear_IpAddressTranfer {
//	public static int str2Ip(String ip) throws UnknownHostException {
//		InetAddress address = InetAddress.getByName(ip);// 在给定主机名的情况下确定主机的 // IP
//														// 址。
//		byte[] bytes = address.getAddress();// 返回此 InetAddress 对象的原始 IP 地址
//		int a, b, c, d;
//		a = byte2int(bytes[0]);
//		b = byte2int(bytes[1]);
//		c = byte2int(bytes[2]);
//		d = byte2int(bytes[3]);
//		int result = (a << 24) | (b << 16) | (c << 8) | d;
//		return result;
//	}
//
//	public static int byte2int(byte b) {
//		int l = b & 0x07f;
//		if (b < 0) {
//			l |= 0x80;
//		}
//		return l;
//	}
//
//	public static long ip2long(String ip) throws UnknownHostException {
//		int ipNum = str2Ip(ip);
//		return int2long(ipNum);
//	}
//
//	public static long int2long(int i) {
//		long l = i & 0x7fffffffL;
//		if (i < 0) {
//			l |= 0x080000000L;
//		}
//		return l;
//	}
//
//	public static String long2ip(long ip) {
//		int[] b = new int[4];
//		b[0] = (int) ((ip >> 24) & 0xff);
//		b[1] = (int) ((ip >> 16) & 0xff);
//		b[2] = (int) ((ip >> 8) & 0xff);
//		b[3] = (int) (ip & 0xff);
//		String x;
//		x = Integer.toString(b[3]) + "." + Integer.toString(b[2]) + "."
//				+ Integer.toString(b[1]) + "." + Integer.toString(b[0]);
//		return x;
//	}
//
//	// 获取默认网关的ＩＰ地址
//	public static String getDefaultGatewayIp() throws Exception {
//		try {
//			Process result = Runtime.getRuntime().exec("su");
//			BufferedReader output = new BufferedReader(new InputStreamReader(
//					result.getInputStream()));
//			String line = output.readLine();
//			while (line != null) {
//				Log.e("new line is", line);
//				line = output.readLine();
//			}
//		} catch (Exception e) {
//			System.out.println(e.toString());
//		}
//		return null;
//	}
//}
