package com.erobbing.voice.phone;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothHeadsetClientCall;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.txznet.sdk.TXZCallManager;
import com.txznet.sdk.TXZCallManager.CallToolStatusListener;
import com.txznet.sdk.TXZCallManager.Contact;
import com.txznet.sdkdemo.R;
import com.txznet.sdkdemo.SDKDemoApp;

public class AndroidCallTool implements TXZCallManager.CallTool {
	public static final String TAG = "AndroidCallTool";
	private static AndroidCallTool mInstance = null;
	//public static BluetoothHeadsetClientCall btHCCall = null;
	public static boolean btStatus = false;
	public boolean isBtStatus() {
		return btStatus;
	}

	public void setBtStatus(boolean btStatus) {
		AndroidCallTool.btStatus = btStatus;
	}

	
	/*public BluetoothHeadsetClientCall getBtHCCall() {
		return btHCCall;
	}

	public void setBtHCCall(BluetoothHeadsetClientCall mbtHCCall) {
		btHCCall = mbtHCCall;
	}*/

	public static AndroidCallTool getInstance() {
		if (mInstance == null)
			mInstance = new AndroidCallTool();
		return mInstance;
	}

	@Override
	public boolean makeCall(Contact con) {
		Log.d(TAG, "AndroidCallTool makeCall");

		if (con == null)
			return false;
		if (callToPhone(SDKDemoApp.getApp(), con.getNumber()))
			return true;

		return false;
	}

	@Override
	public boolean acceptIncoming() {
		Log.d(TAG, "AndroidCallTool acceptIncoming--");
		// Make sure the phone is still ringing
		//BluetoothHeadsetClientCall mbt = AndroidCallTool.getInstance().getBtHCCall();
		//Log.d(TAG,"getBtHCCall:"+mbt.getState());
		//if (mbt == null || mbt.getState() != BluetoothHeadsetClientCall.CALL_STATE_INCOMING) {
		//	return false;
		//}
		// Answer the phone
		try {
			Log.d(TAG, "!--->acceptIncoming in");
			answerPhone(SDKDemoApp.getApp());
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Error trying to answer using telephony service.  Falling back to headset.");
			answerPhoneHeadsetHook(SDKDemoApp.getApp());
		}
		return true;
	}

	private static void answerPhoneHeadsetHook(Context context) {
		Log.d(TAG, "answerPhoneHeadsetHook");
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
				KeyEvent.KEYCODE_HEADSETHOOK));
		try {
			context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
			Log.d(TAG, "ACTION_MEDIA_BUTTON broadcasted...");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged 1 =
														// Headset with
														// microphone 2 =
														// Headset without
														// microphone
		headSetUnPluggedintent.putExtra("name", "Headset");
		// TODO: Should we require a permission?
		try {
			context.sendOrderedBroadcast(headSetUnPluggedintent, null);
			Log.d(TAG, "ACTION_HEADSET_PLUG broadcasted ...");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
			Log.d(TAG, "Call Answered From Catch Block !!");
		}

		Log.d(TAG, "Call Answered using headsethook");
	}

	@Override
	public boolean hangupCall() {
		Log.d(TAG, "AndroidCallTool hangupCall");
		endCall();
		return true;
	}

	@Override
	public boolean rejectIncoming() {
		Log.d(TAG, "AndroidCallTool rejectIncoming");
		endCall();
		return true;
	}

	private static void endCall() {
		// Silence the ringer and answer the call!
		Log.d(TAG, "endCall");
		hangupInCallPhone(SDKDemoApp.getApp());
	}

	private static CallStatus mLastStatus = null;
	private static Contact mLastContact = null;
	private static CallToolStatusListener mCallToolStatusListener = null;

	public void onIncoming(Contact con) {
		Log.d(TAG, "AndroidCallTool onIncoming");

		mLastContact = con;
		if (mCallToolStatusListener != null)
			mCallToolStatusListener.onIncoming(mLastContact, true /*是否tts播报来电信息*/ , true /*是否启动声控识别接听拒接*/);
	}

	public void onMakeCall(Contact con) {
		Log.d(TAG, "AndroidCallTool onMakeCall");

		mLastContact = con;
		if (mCallToolStatusListener != null)
			mCallToolStatusListener.onMakeCall(mLastContact);
	}

	public void onIdle() {
		Log.d(TAG, "AndroidCallTool onIdle");

		mLastContact = new Contact();
		if (mCallToolStatusListener != null)
			mCallToolStatusListener.onIdle();
	}

	public void onOffhook() {
		Log.d(TAG, "AndroidCallTool onOffhook");

		if (mCallToolStatusListener != null)
			mCallToolStatusListener.onOffhook();
	}
	
	public void onEnabled() {
		Log.d(TAG, "AndroidCallTool onEnabled");

		mLastContact = new Contact();
		if (mCallToolStatusListener != null)
			mCallToolStatusListener.onEnabled();
		// 取一次电话状态，同时设置电话的状态
		//getStatus();
		
		AndroidCallTool.getInstance().setBtStatus(true);
	}

	public void onDisabled(String reason) {
		Log.d(TAG, "AndroidCallTool onDisabled");

		if (mCallToolStatusListener != null){
			if(reason == null){
				mCallToolStatusListener.onDisabled(
						SDKDemoApp.getApp().getResources().getString(R.string.call_disable_reason));
			}else{
				mCallToolStatusListener.onDisabled(reason);
			}
			
			mCallToolStatusListener.onIdle();
		}
		AndroidCallTool.getInstance().setBtStatus(false);
	}

	@Override
	public CallStatus getStatus() {
		//BluetoothHeadsetClientCall mbt = AndroidCallTool.getInstance().getBtHCCall();

		//if (mbt != null) {
			//Log.d(TAG,"-->getState:"+mbt.getState());
			/*switch (mbt.getState()) {
			case 7://BluetoothHeadsetClientCall.CALL_STATE_TERMINATED: // 7 结束
				// case TelephonyManager.CALL_STATE_IDLE:
				if (mLastStatus != CallStatus.CALL_STATUS_IDLE) {
					onIdle();
				}
				return mLastStatus = CallStatus.CALL_STATUS_IDLE;

			case 0://BluetoothHeadsetClientCall.CALL_STATE_ACTIVE: // 0通话
				// case TelephonyManager.CALL_STATE_OFFHOOK:
				if (mLastStatus != CallStatus.CALL_STATUS_OFFHOOK) {
					onOffhook();
				}
				return mLastStatus = CallStatus.CALL_STATUS_OFFHOOK;
			case 4://BluetoothHeadsetClientCall.CALL_STATE_INCOMING: // 4 来电
				// case TelephonyManager.CALL_STATE_RINGING:
				if (mLastStatus != CallStatus.CALL_STATUS_RINGING) {
					onIncoming(mLastContact);
				}
				return mLastStatus = CallStatus.CALL_STATUS_RINGING;
			}*/

		//}
		if (mLastStatus != CallStatus.CALL_STATUS_IDLE) {
					onIdle();
				}
				return mLastStatus = CallStatus.CALL_STATUS_IDLE;
		//return mLastStatus = null;
	}

	//@Override
	public void setStatusListener(CallToolStatusListener listener) {
		mCallToolStatusListener = listener;
		if (mCallToolStatusListener != null ) {
			if(checkBluetooth()){
				// 通知电话功能可以用了
				AndroidCallTool.getInstance().onEnabled();
			}else{
				AndroidCallTool.getInstance().onDisabled(null);
			}
		}
		// 取一次电话状态，同时设置电话的状态
		//getStatus();
	}

	/**
	 * zhouyuhuan add about phone
	 * */

	// Dial through headset client profile
	public static final String ACTION_HFP_DIAL = "action.hfp.dial";
	// Phone Number of dial
	public static final String PHONE_NUMBER = "phone.number";
	// Reject incoming call from headset client profile
	public static final String ACTION_HFP_REJECT = "action.hfp.reject";
	// Accept incoming call from headset client profile
	public static final String ACTION_HFP_ACCEPT = "action.hfp.accept";
	// Terminate active call from headset client profile
	public static final String ACTION_HFP_TERMINATE = "action.hfp.terminate";

	public static boolean callToPhone(Context context, String num) {
		if (context == null) {
			Log.e(TAG, "callToPhone:context null!");
			return false;
		}
		//
		if (checkBluetooth()) {
			Intent intent = new Intent(ACTION_HFP_DIAL);
			intent.putExtra(PHONE_NUMBER, num);
			context.sendBroadcast(intent);
			return true;
		}
		return false;

	}

	public static boolean checkBluetooth() {
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			return false;
		}
		if (btAdapter.isEnabled()) {
			Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
			if (pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					Log.i("W", "device name:" + device.getName());
					if (device.createBond()) {
						Log.i("W", "device :isConnected");
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void hangupPhone(Context context) {
		if (context == null) {
			Log.e(TAG, "hangupPhone:context null!");
			return;
		}
		Intent intent = new Intent(ACTION_HFP_TERMINATE);
		context.sendBroadcast(intent);
	}

	public static void hangupInCallPhone(Context context) {
		if (context == null) {
			Log.e(TAG, "hangupInCallPhone:context null!");
			return;
		}
		Intent intent = new Intent(ACTION_HFP_REJECT);
		context.sendBroadcast(intent);
	}

	public static void answerPhone(Context context) {
		if (context == null) {
			Log.e(TAG, "answerPhone:context null!");
			return;
		}
		Intent intent = new Intent(ACTION_HFP_ACCEPT);
		context.sendBroadcast(intent);
	}
}
