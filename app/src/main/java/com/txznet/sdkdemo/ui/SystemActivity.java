package com.txznet.sdkdemo.ui;

import java.lang.reflect.Method;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.erobbing.voice.oem.RomCustomerProcessing;
import com.erobbing.voice.utils.Logger;
import com.txznet.sdk.TXZSysManager;
import com.txznet.sdk.TXZSysManager.AppInfo;
import com.txznet.sdk.TXZSysManager.AppMgrTool;
import com.txznet.sdk.TXZSysManager.VolumeMgrTool;
import com.txznet.sdkdemo.SDKDemoApp;
import com.txznet.sdkdemo.bean.DebugUtil;
import com.txznet.sdkdemo.bean.DemoButton;

public class SystemActivity extends BaseActivity {
	private static String TAG = SystemActivity.class.getSimpleName();
	private static final String APP_EXIT_ACTION = "com.unisound.unicar.app.action.close";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//zhouyuhuan add :20160613
		addDemoButtons(new DemoButton(this, "设置音量工具", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZSysManager.getInstance().setVolumeMgrTool(mVolumeMgrTool);

				DebugUtil.showTips(((Button) v).getText());
			}
		}), new DemoButton(this, "取消音量工具", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZSysManager.getInstance().setVolumeMgrTool(null);

				DebugUtil.showTips(((Button) v).getText());
			}
		}));

		addDemoButtons(new DemoButton(this, "同步应用列表", new OnClickListener() {
			@Override
			public void onClick(View v) {
				syncAppInfoList();
			}
		}), new DemoButton(this, "设置应用管理工具", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZSysManager.getInstance().setAppMgrTool(mAppMgrTool);

				DebugUtil.showTips(((Button) v).getText());
			}
		}), new DemoButton(this, "取消应用管理工具", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZSysManager.getInstance().setAppMgrTool(null);

				DebugUtil.showTips(((Button) v).getText());
			}
		}));
	}

	
	private VolumeMgrTool mVolumeMgrTool = new VolumeMgrTool() {
		@Override
		public void mute(boolean enable) {
			if (enable)
				DebugUtil.showTips("关闭声音");
			else
				DebugUtil.showTips("打开声音");
			// TODO 静音控制
		}

		@Override
		public void minVolume() {
			DebugUtil.showTips("最小音量");
			// TODO 最小音量
		}

		@Override
		public void maxVolume() {
			DebugUtil.showTips("最大音量");
			// TODO 最大音量
		}

		@Override
		public void incVolume() {
			DebugUtil.showTips("增加音量");
			// TODO 增加音量
		}

		@Override
		public void decVolume() {
			DebugUtil.showTips("减小音量");
			// TODO 减小音量
		}

		@Override
		public boolean isMaxVolume() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isMinVolume() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	private void syncAppInfoList() {
		// TODO 默认从系统同步应用列表，使用该方法同步后，将不再读取系统的
		AppInfo[] apps = new AppInfo[2];
		apps[0] = new AppInfo();
		apps[0].strAppName = "行车记录仪";
		apps[0].strPackageName = "com.txznet.record";
		apps[1] = new AppInfo();
		apps[1].strAppName = "电子狗";
		apps[1].strPackageName = "com.txznet.edog";
		TXZSysManager.getInstance().syncAppInfoList(apps);

		DebugUtil.showTips("已同步应用列表：行车记录仪、电子狗");
	}
	
	
	/**
	 * zhouyuhuan add : init System 20160623
	 */
	public static void regInitSystem() {
		TXZSysManager txzSysManager = TXZSysManager.getInstance();
		txzSysManager.setAppMgrTool(mAppMgrTool);
	}
	
	private static AppMgrTool mAppMgrTool = new AppMgrTool() {

		@Override
		public void openApp(String packageName) {
			//DebugUtil.showTips("将打开" + packageName);
			Logger.d(TAG,"openApp packageName="+packageName);
			RomCustomerProcessing.sendMessageToDVR(SDKDemoApp.getApp(),false);
			// TODO 打开应用
			super.openApp(packageName);
		}

		@Override
		public void closeApp(String packageName) {
			//DebugUtil.showTips("将关闭" + packageName);
			// TODO 关闭应用
			Logger.d(TAG,"closeApp packageName="+packageName);
			if(packageName != null && (packageName.equals("com.erobbing.btdialer")
					|| packageName.equals("com.erobbing.hotspot")
					|| packageName.equals("com.android.fmtx"))){
				Intent intent = new Intent();
				intent.setAction(APP_EXIT_ACTION);
				intent.putExtra("packageName", packageName);
				SDKDemoApp.getApp().sendBroadcast(intent);
			}else{
				KillApps(SDKDemoApp.getApp(),packageName);
			}
			super.closeApp(packageName);
			
		}
	};
	public static void KillApps(Context context,String packageName){
		Log.d(TAG, "KillApps ===" + packageName);
		if(/*checkAppsForgeground(context,packageName) || */checkAppsBackground(context,packageName)){
			Log.d(TAG, "KillApps forceStopPackage ===" + packageName);
			try {
		        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		        Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		        method.invoke(mActivityManager, packageName);
		    }catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
	private static boolean checkAppsBackground(Context context,String packName) {
	    Log.d(TAG, "checkPlayersBackground---------Begin");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		if (list != null && list.size() > 0) {
			for (RunningTaskInfo info : list) {
				Log.d(TAG, info.topActivity.getPackageName() + " is running.");
				String topPkgName = info.topActivity.getPackageName();
				String basePkgName = info.baseActivity.getPackageName();
				if (topPkgName.equals(packName) || basePkgName.equals(packName)) {
					Log.d(TAG, "isAppRunning " + packName + " is running.");
					return true;
				}
			}
		}
		return false;
	}
}
