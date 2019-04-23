package com.txznet.sdkdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZConfigManager.ActiveListener;
import com.txznet.sdk.TXZConfigManager.AsrEngineType;
import com.txznet.sdk.TXZConfigManager.FloatToolType;
import com.txznet.sdk.TXZConfigManager.InitListener;
import com.txznet.sdk.TXZConfigManager.InitParam;
import com.txznet.sdk.TXZConfigManager.TtsEngineType;
import com.txznet.sdk.TXZTtsManager;

public class VoioceButtonPressReceiver extends BroadcastReceiver implements InitListener, ActiveListener {
    private Context mContext;
    private static final String VOICE_BUTTON_PRESS_ACTION = "android.erobbing.action.VOICE_BUTTON_PRESS_ACTION";
    private static final String TAG = "VoioceButtonPress";
    InitParam mInitParam;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (VOICE_BUTTON_PRESS_ACTION.equals(intent.getAction())) {
            TXZAsrManager.getInstance().triggerRecordButton();
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            initTXZManager();
        }
    }

    private void initTXZManager() {
        {
            // TODO 获取接入分配的appId和appToken
            String appId = mContext.getResources().getString(R.string.txz_sdk_init_app_id);
            String appToken = mContext.getResources().getString(R.string.txz_sdk_init_app_token);
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
            //String[] wakeupWords = UserPerferenceUtil.getWakeupWordArray(mContext);
            String[] wakeupWords = new String[]{"你好悠悠"};
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
        TXZConfigManager.getInstance().initialize(mContext, mInitParam, this, this);
    }

    @Override
    public void onFirstActived() {
        // TODO 首次联网激活，如果需要出厂激活提示，可以在这里完成
    }

    @Override
    public void onError(int errCode, String errDesc) {
        // TODO 初始化出错
        Log.e(TAG, "同行者引擎初始化出错!!errCode:" + errCode + "--errDesc:" + errDesc);
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "onSuccess");
        //speakWakeupWords();
        TXZTtsManager.getInstance().setDefaultAudioStream(AudioManager.STREAM_MUSIC);
        //DebugUtil.showTips("同行者引擎初始化成功");
        // TODO 初始化成功，可以在这里根据需要执行一些初始化操作，参考其他Activity
        // TODO 设置一些参数(参考ConfigActivity)
        //sendMessageConfig();
        // TODO 注册指令(参考AsrActivity)
        //sendMessageAsr();
        // TODO 设置电话(参考CallActivity)、音乐(参考MusicActivity)、导航(参考NavActivity)工具
        //sendMessageCall();
        // TODO 同步联系人(参考CallActivity)
        // 同步抓拍唤醒词
        //sendMessageCamera();
        // 初始化应用
        //sendMessageSystem();
        // 注册场景检测
        //sendMessageScene();
        // 设置天气信息 weather
        //startWeatherInfoService();
    }
}
