package com.txznet.sdkdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.erobbing.voice.oem.RomCustomerProcessing;
import com.erobbing.voice.oem.RomSystemSetting;
import com.erobbing.voice.utils.Logger;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZAsrManager.AsrComplexSelectCallback;
import com.txznet.sdk.TXZAsrManager.CommandListener;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdkdemo.SDKDemoApp;
import com.txznet.sdkdemo.bean.DebugUtil;
import com.txznet.sdkdemo.bean.DemoButton;

public class AsrActivity extends BaseActivity {
	private final static String TAG = AsrActivity.class.getSimpleName();
	public static final String OPEN_BLUETOOTH = "OPEN_BLUETOOTH";
	public static final String CLOSE_BLUETOOTH = "CLOSE_BLUETOOTH";
	public static final String OPEN_DVR = "OPEN_DVR";
	public static final String CLOSE_DVR = "CLOSE_DVR";
	
	static String[] arrOpenBluetooth = new String[] { "打开蓝牙", "开启蓝牙" };
	static String[] arrCloseBluetooth = new String[] { "关闭蓝牙", "关掉蓝牙" };
	static String[] arrOpenDVR = new String[] { "打开行车记录仪", "显示行车记录仪","打开记录仪","显示记录仪"  };
	static String[] arrCloseDVR = new String[] { "关闭行车记录仪", "退出行车记录仪","关闭记录仪" ,"退出记录仪"};
	public static final String SHOW_PAGE = "SHOW_PAGE";
	public static final String  OPEN_SCREEN= "OPEN_SCREEN";
	public static final String  CLOSE_SCREEN= "CLOSE_SCREEN";
	static String[] asrOpenHome = new String[] { "返回主界面", "返回首页", "打开主界面","打开主页","显示主页","显示主界面" };
	static String [] asrOpenScreen = new String[]{"打开屏幕", "开启屏幕", "点亮屏幕","亮屏"};
	static String [] asrCloseScreen = new String[] {"关闭屏幕" ,"灭屏","关屏"};
	
	static CommandListener mCommandListener = new CommandListener() {
		@Override
		public void onCommand(String cmd, String data) {
			Logger.d(TAG,"onCommand cmd="+cmd+",data="+data);
			if (SHOW_PAGE.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您显示主界面", true, new Runnable() {
							public void run() {
								Intent intent = new Intent(Intent.ACTION_MAIN);
								intent.addCategory(Intent.CATEGORY_HOME);
								SDKDemoApp.getApp().startActivity(intent);
							}
						});
				return;
			}else if (OPEN_SCREEN.equals(data)) {
				// TODO 亮屏
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您点亮屏幕", true, new Runnable() {
							public void run() {
								//PowerManager pm = (PowerManager) SDKDemoApp.getApp().getSystemService(
								//		Context.POWER_SERVICE);
								//pm.wakeUp(SystemClock.uptimeMillis());
								RomSystemSetting.wakeUp(mContext);
							}
						});
				return;
			}else if (CLOSE_SCREEN.equals(data)) {
				// TODO 熄屏
				TXZResourceManager.getInstance().speakTextOnRecordWin(
						"将为您关闭屏幕", true, new Runnable() {
							public void run() {
								//PowerManager pm = (PowerManager) SDKDemoApp.getApp().getSystemService(
								//		Context.POWER_SERVICE);
								//pm.goToSleep(SystemClock.uptimeMillis());
								RomSystemSetting.goToSleep(mContext);
							}
						});
				
				return;
			}else if (OPEN_BLUETOOTH.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开蓝牙", true,
						new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.setBluetoothEnabled(true);
							}
						});
				return;
			} else if (CLOSE_BLUETOOTH.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭蓝牙", true,
						new Runnable() {
							@Override
							public void run() {
								RomSystemSetting.setBluetoothEnabled(false);
							}
						});
				return;
			} else if ("OPEN_AIRCON".equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开空调", true,
						new Runnable() {
							@Override
							public void run() {
								// TODO 打开空调，先提示后打开空调
							}
						});
				return;
			} else if ("CLOSE_AIRCON".equals(data)) {
				// TODO 关闭空调，先关后提示
				TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭空调", true, null);
				return;
			}
			if (data.startsWith("FM#")) {
				DebugUtil.showTips("调频到：" + data.substring("FM#".length()));
				return;
			}
		}
	};
	// zhouyuhuan modify :20160613
	String[] arrOpenAircon = new String[] { "打开空调", "开启空调" };
	String[] arrCloseAircon = new String[] { "关闭空调", "关掉空调" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// zhouyuhuan modify : 20160613
		addDemoButtons(new DemoButton(this, "模拟声控按钮", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().triggerRecordButton();
			}
		}), new DemoButton(this, "停止录音立即识别", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().stop();
			}
		}), new DemoButton(this, "取消声控", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().cancel();
			}
		}));
		// zhouyuhuan modify :20160613
		addDemoButtons(new DemoButton(this, "无界面启动声控", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().start();
			}
		}), new DemoButton(this, "带提示启动声控", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().start("有什么可以帮您");
			}
		}), new DemoButton(this, "通过文本启动声控", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().startWithRawText("导航去世界之窗");
			}
		}));

		addDemoButtons(new DemoButton(this, "注册命令", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().addCommandListener(mCommandListener);
				// zhouyuhuan add : 20160613
				TXZAsrManager.getInstance().regCommand(arrOpenBluetooth, OPEN_BLUETOOTH);
				TXZAsrManager.getInstance().regCommand(arrCloseBluetooth, CLOSE_BLUETOOTH);
				// TXZAsrManager.getInstance().regCommand(arrOpenDVR, OPEN_DVR);
				// TXZAsrManager.getInstance().regCommand(arrCloseDVR,
				// CLOSE_DVR);
				// TXZAsrManager.getInstance().regCommand(arrOpenAircon,
				// "OPEN_AIRCON");
				// TXZAsrManager.getInstance().regCommand(arrCloseAircon,
				// "CLOSE_AIRCON");
				// zhouyuhuan add :end
				DebugUtil.showTips("已增加命令字：" + DebugUtil.convertArrayToString(arrOpenBluetooth)
						+ "、" + DebugUtil.convertArrayToString(arrCloseBluetooth));
			}
		}), new DemoButton(this, "反注册命令", new OnClickListener() {
			@Override
			public void onClick(View v) {
				// zhouyuhuan add : 20160613
				TXZAsrManager.getInstance().unregCommand(arrOpenBluetooth);
				TXZAsrManager.getInstance().unregCommand(arrCloseBluetooth);
				// TXZAsrManager.getInstance().unregCommand(arrOpenDVR);
				// TXZAsrManager.getInstance().unregCommand(arrCloseDVR);
				TXZAsrManager.getInstance().removeCommandListener(mCommandListener);

				DebugUtil.showTips("已删除命令字：" + DebugUtil.convertArrayToString(arrOpenBluetooth)
						+ "、" + DebugUtil.convertArrayToString(arrCloseBluetooth));
			}
		}), new DemoButton(this, "注册FM命令", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().addCommandListener(mCommandListener);
				TXZAsrManager.getInstance().regCommandForFM(88.0F, 108.9F, "FM");

				DebugUtil.showTips("已注册FM频段：88.0~108.9");
			}
		}));

		addDemoButtons(new DemoButton(this, "注册唤醒命令", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().useWakeupAsAsr(mAsrComplexSelectCallback);

				DebugUtil.showTips("已增加全局唤醒词："
						+ DebugUtil.convertArrayToString(mAsrComplexSelectCallback.genKeywords()));
			}
		}), new DemoButton(this, "反注册唤醒命令", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZAsrManager.getInstance().recoverWakeupFromAsr(
						mAsrComplexSelectCallback.getTaskId());

				DebugUtil.showTips("已删除全局唤醒词："
						+ DebugUtil.convertArrayToString(mAsrComplexSelectCallback.genKeywords()));
			}
		}));
	}


	/**
	 * zhouyuhuan add : init Asr 20160623
	 */
	public static Context mContext;
	public static void regInitAsr(Context context) {
		mContext = context;
		TXZAsrManager txzAsrManager = TXZAsrManager.getInstance();
		// 唤醒词 : 屏幕
		txzAsrManager.useWakeupAsAsr(mAsrComplexSelectCallback);
		// 行车记录仪
		txzAsrManager.addCommandListener(mInitListener);
		txzAsrManager.regCommand(arrOpenDVR, OPEN_DVR);
		txzAsrManager.regCommand(arrCloseDVR, CLOSE_DVR);
		// 蓝牙 、FM
		txzAsrManager.addCommandListener(mCommandListener);
		txzAsrManager.regCommand(arrOpenBluetooth, OPEN_BLUETOOTH);
		txzAsrManager.regCommand(arrCloseBluetooth, CLOSE_BLUETOOTH);
		txzAsrManager.regCommand(asrOpenHome,SHOW_PAGE);
		txzAsrManager.regCommand(asrOpenScreen,OPEN_SCREEN);
		txzAsrManager.regCommand(asrCloseScreen,CLOSE_SCREEN);
		txzAsrManager.regCommandForFM(88.0F, 108.9F, "FM");
	}
	
	public static CommandListener mInitListener = new CommandListener() {
		@Override
		public void onCommand(String cmd, String data) {
			// zhouyuhuan add :20160613
			if (OPEN_DVR.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin("将为您打开行车记录仪", true,
						new Runnable() {
							@Override
							public void run() {
								sendMessageToDVR(SDKDemoApp.getApp(), true);
							}
						});
				return;
			} else if (CLOSE_DVR.equals(data)) {
				TXZResourceManager.getInstance().speakTextOnRecordWin("已为您关闭行车记录仪", true,
						new Runnable() {
							@Override
							public void run() {
								sendMessageToDVR(SDKDemoApp.getApp(), false);
							}
						});
				return;
			}
		}
	};
	static AsrComplexSelectCallback mAsrComplexSelectCallback = new AsrComplexSelectCallback() {
		@Override
		public boolean needAsrState() {
			// TODO 是否需要识别状态，识别会对系统静音
			return false;
		}

		@Override
		public String getTaskId() {
			// TODO 返回任务ID，可以取消唤醒识别任务
			return "WAKEUP_TASK";
		}

		@Override
		public void onCommandSelected(String type, String command) {
			if("TAKE_PICTURE".equals(type)){
				Logger.d(TAG,"TAKE_PICTURE");
				TXZTtsManager.getInstance().speakText("已执行拍照");
				RomCustomerProcessing.takePhoto(SDKDemoApp.getApp());
			}
		};
	} //
	.addCommand("TAKE_PICTURE", "我要抓拍");
	//.
	/**
	 * zhouyuhuan add : dvr
	 */
	public static final String ACTION_DVR_OPEN = "com.erobbing.action.navi_show";
	public static final String ACTION_DVR_CLOSE = "com.erobbing.action.navi_hide";

	public static void sendMessageToDVR(Context context, boolean enabled) {
		Log.d(TAG, "action = " + ACTION_DVR_OPEN);
		Intent intent = new Intent();
		intent.setAction(enabled ? ACTION_DVR_OPEN : ACTION_DVR_CLOSE);
		context.sendBroadcast(intent);
	}
}