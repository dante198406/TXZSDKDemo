package com.erobbing.voice.oem;

import java.lang.reflect.Method;
import java.util.List;

import com.erobbing.voice.utils.Logger;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * 用于处理客户处理协议
 * 
 * @author tyz
 * 
 */

public class RomCustomerProcessing {

	private static final String TAG = "RomCustomerProcessing";
	/**
	 * zhouyuhuan add : capture
	 */
	public static void takePhoto(Context context) {
		if(context == null)
			return;
		Log.d(TAG, "takePhoto");
		Intent intent2 = new Intent("com.erobbing.action.videosnap");
		context.sendBroadcast(intent2);
	}
	
	/**
	 * zhouyuhuan add : dvr
	 */
	private static final String ACTION_DVR_OPEN = "com.erobbing.action.navi_show";
	private static final String ACTION_DVR_CLOSE = "com.erobbing.action.navi_hide";
	public static void sendMessageToDVR(Context context, boolean enabled) {
		if(context == null)
			return;
		Logger.d(TAG, "action = " + ACTION_DVR_OPEN+" ,enabled="+enabled);
		Intent intent = new Intent();
		intent.setAction(enabled ? ACTION_DVR_OPEN : ACTION_DVR_CLOSE);
		context.sendBroadcast(intent);
	}

}
