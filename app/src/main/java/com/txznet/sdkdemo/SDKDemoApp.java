package com.txznet.sdkdemo;

import android.app.Application;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZConfigManager.ActiveListener;
import com.txznet.sdk.TXZConfigManager.AsrEngineType;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZConfigManager.TtsEngineType;
import com.txznet.sdkdemo.bean.DebugUtil;

public class SDKDemoApp extends Application{
	public final static String TAG = "SDKDemoApp";

	private static SDKDemoApp instance;
	protected static Handler uiHandler = new Handler(Looper.getMainLooper());

	public static SDKDemoApp getApp() {
		return instance;
	}

	public static void runOnUiGround(Runnable r, long delay) {
		if (delay > 0) {
			uiHandler.postDelayed(r, delay);
		} else {
			uiHandler.post(r);
		}
	}

	public static void removeUiGroundCallback(Runnable r) {
		uiHandler.removeCallbacks(r);
	}

	

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		
	}

}
