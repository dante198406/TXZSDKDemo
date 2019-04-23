package com.txznet.sdkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.txznet.sdk.TXZAsrManager;

public class VoioceButtonPressReceiver extends BroadcastReceiver {
    private Context mContext;
    private static final String VOICE_BUTTON_PRESS_ACTION = "android.erobbing.action.VOICE_BUTTON_PRESS_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (VOICE_BUTTON_PRESS_ACTION.equals(intent.getAction())) {
            TXZAsrManager.getInstance().triggerRecordButton();
            //TXZAsrManager.getInstance().start();//启动不了
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        }
    }
}
