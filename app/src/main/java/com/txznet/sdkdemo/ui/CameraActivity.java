package com.txznet.sdkdemo.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.erobbing.voice.oem.RomCustomerProcessing;
import com.erobbing.voice.oem.RomSystemSetting;
import com.erobbing.voice.preference.UserPerferenceUtil;
import com.erobbing.voice.utils.Logger;
import com.txznet.sdk.TXZCameraManager;
import com.txznet.sdk.TXZCameraManager.CaptureVideoListener;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZCameraManager.CameraTool;
import com.txznet.sdk.TXZCameraManager.CapturePictureListener;
import com.txznet.sdkdemo.SDKDemoApp;
import com.txznet.sdkdemo.bean.DebugUtil;
import com.txznet.sdkdemo.bean.DemoButton;

public class CameraActivity extends BaseActivity {
	private static final String TAG = CameraActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addDemoButtons(new DemoButton(this, "设置抓拍工具", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZCameraManager.getInstance().setCameraTool(mCameraTool);
				
				DebugUtil.showTips(((Button)v).getText());
			}
		}), new DemoButton(this, "取消抓拍工具", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZCameraManager.getInstance().setCameraTool(null);
				
				DebugUtil.showTips(((Button)v).getText());
			}
		}));

		addDemoButtons(new DemoButton(this, "启用全局唤醒抓拍", new OnClickListener() {
			@Override
			public void onClick(View v) {
				//zhouyuhuan add : 20160613
				TXZCameraManager.getInstance().setCameraTool(mCameraTool);//
				TXZCameraManager.getInstance().useWakeupCapturePhoto(true);
				
				DebugUtil.showTips("全局抓拍唤醒词:我要拍照/抓拍照片/抓拍图片");
			}
		}), new DemoButton(this, "取消全局唤醒抓拍", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TXZCameraManager.getInstance().useWakeupCapturePhoto(false);
				
				DebugUtil.showTips(((Button)v).getText());
			}
		}));
	}

	
	/**
	 * zhouyuhuan add : init Camera
	 */
	private static Context mContext;
	public static void regInitCamera(Context context) {
		mContext = context;
		TXZCameraManager txtCameraManager = TXZCameraManager.getInstance();
		txtCameraManager.setCameraTool(mCameraTool);
		txtCameraManager.useWakeupCapturePhoto(true);
	}
	
	public static CameraTool mCameraTool = new CameraTool() {

		@Override
		public boolean capturePicure(long time,
				final CapturePictureListener listener) {
			Logger.d(TAG,"capturePicure");
			TXZTtsManager.getInstance().speakText("已执行拍照");
			RomCustomerProcessing.takePhoto(SDKDemoApp.getApp());
			// TODO 抓拍实现
			/*SDKDemoApp.runOnUiGround(new Runnable() {
				@Override
				public void run() {
					
					
					// TODO 保存
					listener.onSave("/storage/emulated/0/DCIM/Camera/123.jpg");
					// TODO 出错
					//listener.onError(TXZCameraManager.CAPTURE_ERROR_NO_CAMERA,
					//		"没有摄像头");
					//RomSystemSetting.takePhoto(SDKDemoApp.getApp());
				}
			}, 2000);*/
			
			//DebugUtil.showTips("收到抓拍请求");
			
			return true;
		}

		@Override
		public boolean captureVideo(CaptureVideoListener arg0, CaptureVideoListener arg1) {
			// TODO Auto-generated method stub
			return false;
		}
	};
}
