package com.erobbing.voice.phone;

import android.app.Service;
//import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothHeadsetClient;
//import android.bluetooth.BluetoothHeadsetClientCall;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.txznet.sdk.TXZCallManager.Contact;
import com.txznet.sdkdemo.R;
import com.txznet.sdkdemo.SDKDemoApp;

public class AndroidCallStateReceiver extends BroadcastReceiver {
	private static final String TAG = "AndroidCallStateReceiver";
	private static final String ACTION_PHONE_STATE = "android.intent.action.PHONE_STATE";
	private static final String ACTION_CALL_CHANGED_BTHEADSET = "android.bluetooth.headsetclient.profile.action.AG_CALL_CHANGED";
	private String mPhoneNumber = null;
	private static int mPhoneState = 0;//BluetoothHeadsetClientCall.CALL_STATE_TERMINATED;
	private static String mLastPhoneNumber = null;
	private static String mLastActivePhoneNumber = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive:intent " + intent);

		String action = intent.getAction();
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
			Contact con = new Contact();
			con.setNumber(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
			AndroidCallTool.getInstance().onMakeCall(con);
		} else if (ACTION_PHONE_STATE.equals(action)) {
			// 来电
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);
			switch (tm.getCallState()) {
			case TelephonyManager.CALL_STATE_RINGING:
				Contact con = new Contact();
				con.setNumber(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));

				AndroidCallTool.getInstance().onIncoming(con);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				AndroidCallTool.getInstance().onOffhook();
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				AndroidCallTool.getInstance().onIdle();
				break;
			default:
				Log.e(TAG, "unknown phont state:" + tm.getCallState());
				break;
			}
		} else if (ACTION_CALL_CHANGED_BTHEADSET.equals(action)) {
			/* zhouyuhuan add for call 20160318 start */
			//BluetoothHeadsetClientCall mClient = intent
			//		.getParcelableExtra(BluetoothHeadsetClient.EXTRA_CALL);
			//Log.d(TAG, "State =====" + mClient.getState());
			//Log.d(TAG, "Number =====" + mClient.getNumber());
			//Log.d(TAG, "isOutgoing =====" + mClient.isOutgoing());
			//Log.d(TAG, "mClient.getNumber() =====" + mClient.getNumber());
			//AndroidCallTool.getInstance().setBtHCCall(mClient);//
			//handleState(mClient);
			/* zhouyuhuan add for call 20160318 end */
		} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			Log.d(TAG, "STATE_CHANGED--" + state);
			if (state == BluetoothAdapter.STATE_OFF) {
				if (AndroidCallTool.getInstance().isBtStatus()) {
					AndroidCallTool.getInstance().onDisabled(null);
				}
				/*if (mPhoneState != BluetoothHeadsetClientCall.CALL_STATE_TERMINATED) {
					AndroidCallTool.getInstance().onIdle();
					mLastPhoneNumber = null;
					mLastActivePhoneNumber = null;
					mPhoneState = BluetoothHeadsetClientCall.CALL_STATE_TERMINATED;
				}*/
			}
		/*} else if (BluetoothHeadsetClient.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE,
					BluetoothProfile.STATE_DISCONNECTED);
			Log.d(TAG, "HeadsetClient state--" + state);
			if (state == BluetoothProfile.STATE_CONNECTED) {
				AndroidCallTool.getInstance().onEnabled();
			} else if (state == BluetoothProfile.STATE_DISCONNECTED) {
				if (AndroidCallTool.getInstance().isBtStatus()) {
					AndroidCallTool.getInstance().onDisabled(null);
				}
				if (mPhoneState != BluetoothHeadsetClientCall.CALL_STATE_TERMINATED) {
					AndroidCallTool.getInstance().onIdle();
					mLastPhoneNumber = null;
					mLastActivePhoneNumber = null;
					mPhoneState = BluetoothHeadsetClientCall.CALL_STATE_TERMINATED;
				}
			}
		} else if (BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
			int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE,
					BluetoothProfile.STATE_DISCONNECTED);
			Log.d(TAG, "a2dpsink state: " + state);
			if (state == BluetoothProfile.STATE_CONNECTED) {
				AndroidCallTool.getInstance().onEnabled();
			} else if (state == BluetoothProfile.STATE_DISCONNECTED) {
				if (AndroidCallTool.getInstance().isBtStatus()) {
					AndroidCallTool.getInstance().onDisabled(null);
				}
				if (mPhoneState != BluetoothHeadsetClientCall.CALL_STATE_TERMINATED) {
					AndroidCallTool.getInstance().onIdle();
					mLastPhoneNumber = null;
					mLastActivePhoneNumber = null;
					mPhoneState = BluetoothHeadsetClientCall.CALL_STATE_TERMINATED;
				}
			}*/
		}
	}

	/** zhouyuhuan add for call
	 * */
	/*public void handleState(BluetoothHeadsetClientCall client) {
		mPhoneNumber = client.getNumber();
		mPhoneState = client.getState();
		switch (mPhoneState) {
		case BluetoothHeadsetClientCall.CALL_STATE_ACTIVE: // 0通话
			Log.d(TAG, "CALL_STATE_ACTIVE :" + mPhoneNumber + "-" + mLastActivePhoneNumber);
			if (mPhoneNumber == null
					|| (mLastActivePhoneNumber != null && mLastActivePhoneNumber
							.equals(mPhoneNumber)))
				break;
			AndroidCallTool.getInstance().onOffhook();
			mLastActivePhoneNumber = mPhoneNumber;
			break;

		case BluetoothHeadsetClientCall.CALL_STATE_DIALING: // 2 呼叫
			Log.d(TAG, "CALL_STATE_DIALING :" + mPhoneNumber);
			Contact con = new Contact();
			con.setNumber(mPhoneNumber);
			AndroidCallTool.getInstance().onMakeCall(con);
			break;
		case BluetoothHeadsetClientCall.CALL_STATE_ALERTING: // 3
			Log.d(TAG, "CALL_STATE_ALERTING :" + mPhoneNumber);
			break;
		case BluetoothHeadsetClientCall.CALL_STATE_INCOMING: // 4 来电
			Log.d(TAG, "CALL_STATE_INCOMING :" + mPhoneNumber + "-" + mLastPhoneNumber);
			if (mPhoneNumber == null
					|| (mLastPhoneNumber != null && mLastPhoneNumber.equals(mPhoneNumber)))
				break;
			String name = getContactNameFromPhoneBook(SDKDemoApp.getApp(), mPhoneNumber);
			Log.d(TAG, "CALL_STATE_INCOMING :name-" + name);
			Contact con2 = new Contact();
			con2.setNumber(mPhoneNumber);
			con2.setName(name);
			AndroidCallTool.getInstance().onIncoming(con2);
			mLastPhoneNumber = mPhoneNumber;
			break;

		case BluetoothHeadsetClientCall.CALL_STATE_TERMINATED: // 7 结束
			Log.d(TAG, "CALL_STATE_TERMINATED :" + mPhoneNumber);
			AndroidCallTool.getInstance().onIdle();
			mLastPhoneNumber = null;
			mLastActivePhoneNumber = null;
			break;
		default:
			break;
		}
	}*/

	public String getContactNameFromPhoneBook(Context context, String phoneNum) {
		String contactName = "";
		ContentResolver cr = context.getContentResolver();
		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?", new String[] { phoneNum },
				null);
		if (pCur.moveToFirst()) {
			contactName = pCur.getString(pCur
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			pCur.close();
		}
		return contactName;
	}
}
