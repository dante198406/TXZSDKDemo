package com.txznet.sdkdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.erobbing.voice.preference.UserPerferenceUtil;
import com.erobbing.voice.service.aidl.ITXZService;
import com.erobbing.voice.utils.Logger;
import com.erobbing.voice.weather.WeatherInfoService;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.ActiveListener;
import com.txznet.sdk.TXZConfigManager.AsrEngineType;
import com.txznet.sdk.TXZConfigManager.FloatToolType;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZConfigManager.TtsEngineType;
import com.txznet.sdk.TXZNavManager;
import com.txznet.sdk.TXZNavManager.NavToolType;
import com.txznet.sdk.TXZPowerManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdkdemo.ui.AsrActivity;
import com.txznet.sdkdemo.ui.CallActivity;
import com.txznet.sdkdemo.ui.CameraActivity;
import com.txznet.sdkdemo.ui.ConfigActivity;
import com.txznet.sdkdemo.ui.SceneActivity;
import com.txznet.sdkdemo.ui.SystemActivity;

public class SDKDemoService extends Service implements InitListener, ActiveListener {
	public static final String TAG = "SDKDemoService";
	private Context mContext;
	InitParam mInitParam;
	public static final int MSG_INIT_ASR = 2001;
	public static final int MSG_INIT_CALL = 2002;
	public static final int MSG_INIT_CAMERA = 2003;
	public static final int MSG_INIT_CONFIG = 2004;
	public static final int MSG_INIT_MUSIC = 2005;
	public static final int MSG_INIT_NAV = 2006;
	public static final int MSG_INIT_POWER = 2007;
	public static final int MSG_INIT_RESOURCE = 2008;
	public static final int MSG_INIT_SENCE = 2009;
	public static final int MSG_INIT_STATUS = 2010;
	public static final int MSG_INIT_SYSTEM = 2011;
	public static final int MSG_INIT_SCENE = 2012;
	public static final int MSG_INIT_TTS = 2013;

	public static final int MSG_WAKELOCK_START = 3001;
	public static final int MSG_WAKELOCK_STOP = 3002;
	
	public boolean isDormant = false;
	public static final String ACTION_WAKELOCK_START = "com.erobbing.action.navi_wakelock_start";
	public static final String ACTION_WAKELOCK_STOP = "com.erobbing.action.navi_wakelock_stop";
	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	/*
	 * zhouyuhuan add for remote service
	 */
	public class MyBinder extends ITXZService.Stub {
		@Override
		public void startUniTalk() throws RemoteException {
			if (!TXZConfigManager.getInstance().isInitedSuccess()) {
				//DebugUtil.showTips("同行者引擎尚未初始化成功");
				initTXZManager();
				return;
			}
			if(isDormant){
				isDormant = false;
				reInitTXZ();
			}
			TXZAsrManager.getInstance().triggerRecordButton();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		Logger.d(TAG,"onCreate");
		mContext = this;
		initTXZManager();
		registerReceiver();
		
	}
	@Override
	public void onDestroy() {
		Logger.d(TAG, "onDestroy");
		unregisterReceiver(mReceiver);
	}
	private void initTXZManager() {
		{
			// TODO 获取接入分配的appId和appToken
			String appId = this.getResources().getString(R.string.txz_sdk_init_app_id);
			String appToken = this.getResources().getString(R.string.txz_sdk_init_app_token);
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
			// zhouyuhuan 20160623
			//String[] wakeupKeywords = this.getResources().getStringArray(
			//		R.array.txz_sdk_init_wakeup_keywords);
			String[] wakeupWords = UserPerferenceUtil.getWakeupWordArray(mContext);
			mInitParam.setWakeupKeywordsNew(wakeupWords);
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
			// zhouyuhuan add : 20160613
			mInitParam.setFloatToolType(FloatToolType.FLOAT_NONE);
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
		Logger.e(TAG,"同行者引擎初始化出错!!errCode:"+errCode+"--errDesc:"+errDesc);
	
	}

	@Override
	public void onSuccess() {
		Logger.d(TAG,"onSuccess");
		speakWakeupWords();

		TXZTtsManager.getInstance().setDefaultAudioStream(AudioManager.STREAM_MUSIC);
		//DebugUtil.showTips("同行者引擎初始化成功");
		// TODO 初始化成功，可以在这里根据需要执行一些初始化操作，参考其他Activity
		// TODO 设置一些参数(参考ConfigActivity)
		sendMessageConfig();
		// TODO 注册指令(参考AsrActivity)
		sendMessageAsr();
		// TODO 设置电话(参考CallActivity)、音乐(参考MusicActivity)、导航(参考NavActivity)工具
		sendMessageCall();
		// TODO 同步联系人(参考CallActivity)
		// 同步抓拍唤醒词
		//sendMessageCamera();
		// 初始化应用
		sendMessageSystem();
		// 注册场景检测
		sendMessageScene();
		// 设置天气信息 weather
		startWeatherInfoService();
		
	}

	private void speakWakeupWords() {
		// TXZTtsManager.getInstance().speakText("同行者引擎初始化成功");
		TXZTtsManager.getInstance().speakText("悠悠上线了！唤醒词为"+UserPerferenceUtil.getWakeupWords(mContext));
	}
	/**
	 * register Receiver 
	 */
	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_WAKELOCK_STOP);
		filter.addAction(ACTION_WAKELOCK_START);
		registerReceiver(mReceiver, filter);
	}
	
	/**
	 * mScreenReceiver
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "!--->mScreenReceiver--onReceive:intent " + intent);
			String action = intent.getAction();
			if (ACTION_WAKELOCK_STOP.equals(action)) {
				Message msg = new Message();
				msg.what = MSG_WAKELOCK_STOP;
				mUIHandler.sendMessage(msg);
			} else if (ACTION_WAKELOCK_START.equals(action)) {
				Message msg = new Message();
				msg.what = MSG_WAKELOCK_START;
				mUIHandler.sendMessage(msg);
			}
		}
	};
	private Handler mUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case MSG_INIT_ASR:
				AsrActivity.regInitAsr(mContext);
				break;
			case MSG_INIT_CALL:
				CallActivity.regInitCall();
				break;
			case MSG_INIT_CAMERA:
				CameraActivity.regInitCamera(mContext);
				break;
			case MSG_INIT_CONFIG:
				ConfigActivity.regInitConfig();
				syncConfigure();
				break;
			case MSG_INIT_MUSIC:
				break;
			case MSG_INIT_NAV:
				break;
			case MSG_INIT_POWER:
				break;
			case MSG_INIT_RESOURCE:
				break;
			case MSG_INIT_SENCE:
				break;
			case MSG_INIT_STATUS:
				break;
			case MSG_INIT_SYSTEM:
				SystemActivity.regInitSystem();
				break;
			case MSG_INIT_SCENE:
				SceneActivity.regInitScene();
				break;
			case MSG_INIT_TTS:
				break;
			case MSG_WAKELOCK_START:
				isDormant = false;
				reInitTXZ();
				startWeatherInfoService();
				break;
			case MSG_WAKELOCK_STOP:
				isDormant = true;
				releaseTXZ();
				//startWeatherInfoService
				stopWeatherInfoService();
				TXZNavManager.getInstance().exitNav();
				break;
			}
		}
	};
	private void reInitTXZ() {
		Logger.d(TAG,"reInitTXZ");
		TXZPowerManager.getInstance().reinitTXZ(new Runnable() {
			@Override
			public void run() {
				TXZPowerManager.getInstance().notifyPowerAction(
						TXZPowerManager.PowerAction.POWER_ACTION_WAKEUP);
				speakWakeupWords();
			}
		});
		
	}
	private void releaseTXZ() {
		Logger.d(TAG,"releaseTXZ");
		TXZPowerManager.getInstance().notifyPowerAction(
				TXZPowerManager.PowerAction.POWER_ACTION_BEFORE_SLEEP);
		TXZPowerManager.getInstance().releaseTXZ();
	}
	/**
	 * init config
	 */
	private void sendMessageConfig() {
		Message msg = new Message();
		msg.what = MSG_INIT_CONFIG;
		mUIHandler.sendMessage(msg);
	}
	/**
	 * init asr
	 */
	private void sendMessageAsr() {
		Message msg = new Message();
		msg.what = MSG_INIT_ASR;
		mUIHandler.sendMessage(msg);
	}
	/**
	 * init call
	 */
	private void sendMessageCall() {
		Message msg = new Message();
		msg.what = MSG_INIT_CALL;
		mUIHandler.sendMessage(msg);
	}
	/**
	 * init camera
	 */
	private void sendMessageCamera() {
		Message msg = new Message();
		msg.what = MSG_INIT_CAMERA;
		mUIHandler.sendMessage(msg);
	}
	/**
	 * init system app
	 */
	private void sendMessageSystem(){
		Message msg = new Message();
		msg.what = MSG_INIT_SYSTEM;
		mUIHandler.sendMessage(msg);
	}
	/**
	 * init system app
	 */
	private void sendMessageScene(){
		Message msg = new Message();
		msg.what = MSG_INIT_SCENE;
		mUIHandler.sendMessage(msg);
	}
	/**
	 * sync Configure to VUI
	 */
	private void syncConfigure() {
		Logger.d(TAG, "syncConfigure----");

		// send switch WakeUp
		boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
		TXZConfigManager.getInstance().enableWakeup(isWakeUpOpen);
		// setDefault Stream
		TXZTtsManager.getInstance().setDefaultAudioStream(AudioManager.STREAM_ALARM);
		// send switch TTSSpeed
		int speed = UserPerferenceUtil.getTTSSpeed(mContext);
		Logger.d(TAG, "-->speedType:" + speed);
		if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == speed) {
			TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.TTS_SPEED_SLOW);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == speed) {
			TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.TTS_SPEED_STANDARD);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == speed) {
			TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.TTS_SPEED_FAST);
		}

		// send switch map
		int mapType = UserPerferenceUtil.getMapChoose(mContext);
		Logger.d(TAG, "-->mapType:" + mapType);
		if (UserPerferenceUtil.VALUE_MAP_AMAP == mapType) {
			TXZNavManager.getInstance().setNavTool(NavToolType.NAV_TOOL_GAODE_MAP_CAR);
		} else if (UserPerferenceUtil.VALUE_MAP_BAIDU == mapType) {
			TXZNavManager.getInstance().setNavTool(NavToolType.NAV_TOOL_BAIDU_NAV_HD);
		}
		// sendFavoriteAddress(GuiSettingUpdateUtil.VALUE_ADD_ADDRESS_TYPE_HOME);
		// sendFavoriteAddress(GuiSettingUpdateUtil.VALUE_ADD_ADDRESS_TYPE_COMPANY);
	}
	private void startWeatherInfoService() {
		Logger.e(TAG, "START WeatherInfoService");
		Intent i = new Intent(mContext, WeatherInfoService.class);
		startService(i);
	}
	private void stopWeatherInfoService() {
		Intent intent = new Intent(mContext, WeatherInfoService.class);
		mContext.stopService(intent);
	}

	
}
