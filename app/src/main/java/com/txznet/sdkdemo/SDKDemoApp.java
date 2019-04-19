package com.txznet.sdkdemo;

import android.app.Application;
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

public class SDKDemoApp extends Application implements InitListener,
		ActiveListener {
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

	InitParam mInitParam;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		{
			// TODO 获取接入分配的appId和appToken
			String appId = this.getResources().getString(
					R.string.txz_sdk_init_app_id);
			String appToken = this.getResources().getString(
					R.string.txz_sdk_init_app_token);
			// TODO 设置初始化参数
			mInitParam = new InitParam(appId, appToken);
			// TODO 可以设置自己的客户ID，同行者后台协助计数/鉴权
			// mInitParam.setAppCustomId("ABCDEFG");
			// TODO 可以设置自己的硬件唯一标识码
			// mInitParam.setUUID("0123456789");
		}

		{
			// TODO 设置识别和tts引擎类型
			mInitParam.setAsrType(AsrEngineType.ASR_YUNZHISHENG).setTtsType(
					TtsEngineType.TTS_YUNZHISHENG);
		}

		{
			// TODO 设置唤醒词
			String[] wakeupKeywords = this.getResources().getStringArray(
					R.array.txz_sdk_init_wakeup_keywords);
			mInitParam.setWakeupKeywordsNew(wakeupKeywords);
		}

		{
			// TODO 可以按需要设置自己的对话模式
			// mInitParam.setAsrMode(AsrMode.ASR_MODE_CHAT);
			// TODO 设置识别模式，默认自动模式即可
			// mInitParam.setAsrServiceMode(AsrServiceMode.ASR_SVR_MODE_AUTO);
			// TODO 设置是否允许启用服务号
			// mInitParam.setEnableServiceContact(true);
			// TODO 设置开启回音消除模式
			// mInitParam.setFilterNoiseType(1);
			// TODO 其他设置
		}

		// TODO 初始化在这里
		TXZConfigManager.getInstance().initialize(this, mInitParam, this, this);
	}

	@Override
	public void onFirstActived() {
		// TODO 首次联网激活，如果需要出厂激活提示，可以在这里完成
	}

	@Override
	public void onError(int errCode, String errDesc) {
		// TODO 初始化出错
	}

	@Override
	public void onSuccess() {
		// TODO 初始化成功，可以在这里根据需要执行一些初始化操作，参考其他Activity
		// TODO 设置一些参数(参考ConfigActivity)
		// TODO 注册指令(参考AsrActivity)
		// TODO 设置电话(参考CallActivity)、音乐(参考MusicActivity)、导航(参考NavActivity)工具
		// TODO 同步联系人(参考CallActivity)
		
		TXZTtsManager.getInstance().speakText("同行者引擎初始化成功");
		
		DebugUtil.showTips("同行者引擎初始化成功");
	}
}
