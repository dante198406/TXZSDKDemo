package com.txznet.sdkdemo.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.erobbing.voice.oem.RomCustomerProcessing;
import com.txznet.sdk.TXZResourceManager;
import com.txznet.sdk.TXZSceneManager;
import com.txznet.sdk.TXZSceneManager.SceneTool;
import com.txznet.sdk.TXZSceneManager.SceneType;
import com.txznet.sdkdemo.SDKDemoApp;
import com.txznet.sdkdemo.bean.DebugUtil;
import com.txznet.sdkdemo.bean.DemoButton;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SceneActivity extends BaseActivity {
	private final static String TAG = "SceneActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addDemoButtons(new DemoButton(this, "捕获微信场景", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZSceneManager.getInstance().setSceneTool(
						TXZSceneManager.SceneType.SCENE_TYPE_WECHAT, mCommandSceneTool);

				DebugUtil.showTips(((Button) v).getText());
			}
		}), new DemoButton(this, "取消场景捕获", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZSceneManager.getInstance().setSceneTool(
						TXZSceneManager.SceneType.SCENE_TYPE_WECHAT, null);

				DebugUtil.showTips(((Button) v).getText());
			}
		}));
	}

	/**
	 * 
	 */
	public static void regInitScene() {
		TXZSceneManager txzSceneManager = TXZSceneManager.getInstance();
		txzSceneManager.setSceneTool(TXZSceneManager.SceneType.SCENE_TYPE_WECHAT, mWeChatSceneTool);
		txzSceneManager.setSceneTool(TXZSceneManager.SceneType.SCENE_TYPE_COMMAND, mCommandSceneTool);
	}

	static TXZSceneManager.SceneTool mWeChatSceneTool = new TXZSceneManager.SceneTool() {
		@Override
		public boolean process(SceneType type, String data) {
			try {
				Log.d(TAG, "-->process:" + data);
				JSONObject json = new JSONObject(data);
				String sence = json.optString("sence");
				String action = json.optString("action");
				Log.d(TAG, "-->sence:" + sence+",action:"+action);
				if ("weixin".equals(sence) && "open".equals(action)) {
					//close dvr
					RomCustomerProcessing.sendMessageToDVR(SDKDemoApp.getApp(),false);
					return false; // 已处理的命令返回true，否则还是会走内部交互
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return false;
		}

	};
	static SceneTool mCommandSceneTool = new SceneTool() {
		@Override
		public boolean process(SceneType type, String data) {
			try {
				JSONObject json = new JSONObject(data);
				String cmd = json.optString("cmd");
				Log.d(TAG, "-->CMD:" + cmd);
				if ("WECHAT_LANUCH".equals(cmd)) {
					RomCustomerProcessing.sendMessageToDVR(SDKDemoApp.getApp(),false);
					return false; // 已处理的命令返回true，否则还是会走内部交互
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return false;
		}
	};
}
