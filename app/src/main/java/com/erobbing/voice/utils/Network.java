package com.erobbing.voice.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
public class Network {
	public static final String TAG = "Network";
	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	public static final String NETWORD_TYPE_NULL_POINT = "TYPE_NULL_POINT";
	public static final String NETWORK_TYPE_UNCONNECT = "TYPE_UNCONNECT";
	public static final String NETWORK_TYPE_WIFI = "TYPE_WIFI";
	public static final String NETWORL_TYPE_GPRS = "TYPE_GPRS";

	private static final int NETWORK_STATUS_NO_CONNECT = 0x0;
	private static final int NETWORK_STATUS_WIFI_CONNECT = 0x1;
	// private static final int NETWORK_STATUS_MOBILE_CONNECT = 0x2;

	private static int mNetWorkStatus = NETWORK_STATUS_WIFI_CONNECT;

	public static boolean hasNetWorkConnect() {
		return mNetWorkStatus > NETWORK_STATUS_NO_CONNECT;
	}

	public static String getIP(Context context) {
		String ip = "";
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 检查Wifi状态
		// if(!wm.isWifiEnabled())
		// wm.setWifiEnabled(true);
		WifiInfo wi = wm.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAdd = wi.getIpAddress();
		// 把整型地址转换成“*.*.*.*”地址
		ip = intToIp(ipAdd);
		return ip;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	/*public static boolean checkNetworkConnected(Context context) {
		int oldNetWork = mNetWorkStatus;
		if (isNetworkConnected(context)) {
			mNetWorkStatus = NETWORK_STATUS_WIFI_CONNECT;
		} else {
			mNetWorkStatus = NETWORK_STATUS_NO_CONNECT;
		}

		if (oldNetWork != mNetWorkStatus && context != null) {
			context.sendBroadcast(new Intent(ACTION_CONNECTIVITY_CHANGE));
		}

		return mNetWorkStatus > NETWORK_STATUS_NO_CONNECT;
	}*/

	/**
	 * isNetworkConnected
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (null == context) {
			Logger.e(TAG, "!--->isNetworkConnected:--context is null");
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) { 
			return false; 
		} 
		NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					Log.w(TAG, "checkNetwork  true");
					return true;
				}
			}
		}
		return false;
	}

	public static String judgeCurrentNetTpe(Context context) {
		String type = NETWORD_TYPE_NULL_POINT;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				if (networkInfo.isConnected()) {
					switch (networkInfo.getType()) {
					case ConnectivityManager.TYPE_WIFI:
						type = NETWORK_TYPE_WIFI;
						break;
					case ConnectivityManager.TYPE_MOBILE:
						// Log.i(TAG , "subType: "+networkInfo.getSubtypeName()
						// +" : "+networkInfo.getTypeName());
						// type = judge2GOr3G(context) ? TYPE_3G : TYPE_2G;
						type = NETWORL_TYPE_GPRS;
						break;
					}
				} else {
					type = NETWORK_TYPE_UNCONNECT;
				}
			}
		}
		return type;
	}

}
