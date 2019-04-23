package com.txznet.sdkdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.erobbing.voice.preference.UserPerferenceUtil;
import com.erobbing.voice.settings.ui.SettingsViewPagerActivity;
import com.erobbing.voice.utils.Logger;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.FloatToolType;
import com.txznet.sdk.TXZConfigManager.UserConfigListener;
import com.txznet.sdkdemo.SDKDemoApp;
import com.txznet.sdkdemo.bean.DebugUtil;
import com.txznet.sdkdemo.bean.DemoButton;

public class ConfigActivity extends BaseActivity {
	private final static String TAG = ConfigActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addDemoButtons(new DemoButton(this, "判断初始化是否成功", new OnClickListener() {
			@Override
			public void onClick(View v) {
				DebugUtil.showTips("初始化状态: "
						+ TXZConfigManager.getInstance().isInitedSuccess());
				
			}
		}),new DemoButton(this, "用户自定义设置", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ConfigActivity.this,SettingsViewPagerActivity.class);
				startActivity(intent);
				
			}
		}));

		addDemoButtons(new DemoButton(this, "隐藏声控按钮", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().showFloatTool(
						FloatToolType.FLOAT_NONE);
				
				DebugUtil.showTips("已为您隐藏声控按钮");
			}
		}), new DemoButton(this, "桌面声控按钮", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().showFloatTool(
						FloatToolType.FLOAT_NORMAL);
				
				DebugUtil.showTips("已为您显示桌面声控按钮");
			}
		}), new DemoButton(this, "置顶声控按钮", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().showFloatTool(
						FloatToolType.FLOAT_TOP);
				
				DebugUtil.showTips("已为您置顶声控按钮");
			}
		}));
		//zhouyuhuan modify :20160613
		addDemoButtons(new DemoButton(this, "打开调试日志", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().setLogLevel(Log.DEBUG);
				DebugUtil.showTips("已为您打开调试日志");
			}
		}), new DemoButton(this, "关闭调试日志", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().setLogLevel(Log.ERROR);
				DebugUtil.showTips("已为您关闭日志");
			}
		}));

		addDemoButtons(new DemoButton(this, "允许用户修改唤醒词", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().enableChangeWakeupKeywords(true);
			}
		}), new DemoButton(this, "禁止用户修改唤醒词", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance()
						.enableChangeWakeupKeywords(false);
			}
		}));
		
		addDemoButtons(new DemoButton(this, "清空唤醒词", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().setWakeupKeywordsNew(
						new String[] {});
				DebugUtil.showTips("已为您清空唤醒词");
			}
		}), new DemoButton(this, "修改唤醒词", new OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] kws = new String[] { "你好同行者", "同行者你好" };
				TXZConfigManager.getInstance().setWakeupKeywordsNew(kws);
				DebugUtil.showTips("唤醒词修改为："
						+ DebugUtil.convertArrayToString(kws));
			}
		}));
		addDemoButtons(new DemoButton(this, "监听声控修改唤醒词", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZConfigManager.getInstance().setUserConfigListener(
						mUserConfigListener);
				DebugUtil.showTips("已为您监听声控修改唤醒词");
			}
		}));
	}
	
	/**
	 * zhouyuhuan add : init Config
	 */
	public static void regInitConfig() {
		TXZConfigManager txtConfigManager = TXZConfigManager.getInstance();
		// 打开日志
		//txtConfigManager.setLogLevel(Log.ERROR);
		//txtConfigManager.setLogLevel(Log.DEBUG);
		// 允许用户修改唤醒词
		//txtConfigManager.enableChangeWakeupKeywords(true);
		//允许设置入口
		TXZConfigManager.getInstance().enableSettings(true);
		// 监听声控修改唤醒词
		//TXZConfigManager.getInstance().setUserConfigListener(
		//		mUserConfigListener);
	}
	public static void WakeupEnableConfig(Context context, boolean enable) {
		/*if(enable){
			TXZConfigManager.getInstance().enableChangeWakeupKeywords(true);
			String[] wakeupWord = UserPerferenceUtil.getWakeupWordArray(context);
			TXZConfigManager.getInstance().setWakeupKeywordsNew(wakeupWord);
		}else{
			TXZConfigManager.getInstance().setWakeupKeywordsNew(new String[] {});
			TXZConfigManager.getInstance().enableChangeWakeupKeywords(false);
		}*/
		TXZConfigManager.getInstance().enableWakeup(enable);
	}

	public static UserConfigListener mUserConfigListener = new UserConfigListener() {
		@Override
		public void onChangeWakeupKeywords(String[] keywords) {
			DebugUtil.showTips("用户修改唤醒词为："
					+ DebugUtil.convertArrayToString(keywords));
			Logger.d(TAG,"-->onChangeWakeupKeywords:"+DebugUtil.convertArrayToString(keywords));
			/*StringBuffer sb = new StringBuffer();
			sb.append(keywords[0]);
			for(int i = 1;i < keywords.length;i ++){
				sb.append("，").append(keywords[i]);
			}
			UserPerferenceUtil.setWakeupWords(SDKDemoApp.getApp(), sb.toString());*/
		}

		@Override
		public void onChangeCommunicationStyle(String style) {
			DebugUtil.showTips("用户修改语音交互风格为：" + style);
		}
	};
}
