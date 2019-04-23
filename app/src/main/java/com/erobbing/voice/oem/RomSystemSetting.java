package com.erobbing.voice.oem;

import android.app.WallpaperManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

public class RomSystemSetting {
	public static final String TAG = "RomSystemSetting";

	private static void startActivityFromService(Context context, Intent intent) {
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void openDisplaySettings(Context context) {
		startActivityFromService(context, new Intent(Settings.ACTION_DISPLAY_SETTINGS));
	}

	// zhouyuhuan add
	public static void openHomePage(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivityFromService(context, intent);
	}

	public static void openTimeSettings(Context context) {
		startActivityFromService(context, new Intent(Settings.ACTION_DATE_SETTINGS));
	}

	public static void openSoundSettings(Context context) {
		startActivityFromService(context, new Intent(Settings.ACTION_SOUND_SETTINGS));
	}

	public static void openWallPaperSettings(Context context) {
		startActivityFromService(context, new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER));
	}

	
	public static void setBluetoothEnabled(boolean enabled) {
		try{
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			if (enabled) {
					adapter.enable();
				} else {
					adapter.disable();
				}
			
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void setFlightModeEnabled(Context context, boolean enabled) {
		Settings.Global.putInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, enabled ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabled);
		context.sendBroadcast(intent);
	}

	public static void setAutoOrientationEnabled(Context context, boolean enabled) {
		Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
	}

	public static void setRingerMode(Context context, boolean silent, boolean vibrate) {
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (silent) {
			mAudioManager.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_SILENT);
		} else {
			mAudioManager.setRingerMode(vibrate ? AudioManager.RINGER_MODE_VIBRATE : AudioManager.RINGER_MODE_NORMAL);
			mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, vibrate ? AudioManager.VIBRATE_SETTING_ON : AudioManager.VIBRATE_SETTING_OFF);
		}
	}

	public static void openUrl(Context context, String url) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri contentUri = Uri.parse(url);
			intent.setData(contentUri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void openCallLog(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(android.provider.CallLog.Calls.CONTENT_URI);
		// intent.putExtra("android.provider.extra.CALL_TYPE_FILTER",
		// CallLog.Calls.MISSED_TYPE);
		startActivityFromService(context, intent);
	}

	// 音量加减----vain
	public static int RaiseOrLowerVolume(Context context, boolean isAdd, int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mStreamVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (isAdd) {
			Log.d(TAG, "volume before raise : " + mStreamVolume);
			mStreamVolume += volumeValue;
			Log.d(TAG, "volume after raise : " + mStreamVolume);
			Log.d(TAG, "getStreamMaxVolume : " + am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			if (mStreamVolume >= am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
				mStreamVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			}
			am.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, AudioManager.FLAG_SHOW_UI);
		} else {
			Log.d(TAG, "volume before lower : " + mStreamVolume);
			mStreamVolume -= volumeValue;
			Log.d(TAG, "volume after lower : " + mStreamVolume);
			if (mStreamVolume <= 0) {
				mStreamVolume = 0;
			}
			am.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, AudioManager.FLAG_SHOW_UI);
		}
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	// 最大音量--vain
	public static int setMaxVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
		mStreamVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	// 最小音量--vain
	public static int setMinVolume(Context context) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		mStreamVolume = 0;
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	private static int mStreamVolume = 0;

	// 设置到某个音量值--vain
	public static int setVolume(Context context, int volumeValue) {
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(AudioManager.STREAM_MUSIC, volumeValue, 0);
		// 返回当前媒体音量
		return am.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	private static int mScreenBright = 0;
	private static int mScreenBright_default = 150;
	private static int SCREEN_BRIGHTNESS_MIN = 20;
	private static int SCREEN_BRIGHTNESS_MAX = 255;

	// zhouyuhuan add for setting light
	public static int RaiseOrLowerLight(Context context, boolean isAdd, int LightValue) {
		// 取得当前亮度
		int before = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mScreenBright_default);
		if (isAdd) {
			Log.d(TAG, "bright before raise : " + before);
			mScreenBright = before + LightValue;
			Log.d(TAG, "bright after raise : " + mScreenBright);
			if (mScreenBright >= SCREEN_BRIGHTNESS_MAX) {
				mScreenBright = SCREEN_BRIGHTNESS_MAX;
			}

		} else {
			Log.d(TAG, "bright before lower : " + before);
			mScreenBright = before - LightValue;
			Log.d(TAG, "bright after lower : " + mScreenBright);
			if (mScreenBright <= SCREEN_BRIGHTNESS_MIN) {
				mScreenBright = SCREEN_BRIGHTNESS_MIN;
			}

		}
		try {
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, mScreenBright);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return mScreenBright;
	}

	// 最大亮度
	public static void setMaxBrightness(Context context) {
		try {
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS_MAX);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	// 最小亮度
	public static void setMinBrightness(Context context) {
		try {
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS_MIN);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public static void setScreenMode(Context context, boolean enabled) {
		try {
			Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, enabled ? 1 : 0);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public static void setMute(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.d(TAG, "setMute mStreamVolume : " + mStreamVolume);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
	}

	public static void setUnMute(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		Log.d(TAG, "setUnMute mStreamVolume : " + mStreamVolume);
		// 隐藏音乐进度条
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
	
	// zhouyuhuan add for screen on
	public static void setScreenDisplay(Context context, boolean enabled) {
		Log.d(TAG,"-----setScreenDisplay:"+enabled);
		if(enabled){
			//PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			//pm.wakeUp(SystemClock.uptimeMillis());
			wakeUp(context);
		} else {
			//PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			//pm.goToSleep(SystemClock.uptimeMillis());
			goToSleep(context);
		}
	}


	public static void goToSleep(Context context) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		try {
			powerManager.getClass().getMethod("goToSleep", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public static void wakeUp(Context context) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		try {
			powerManager.getClass().getMethod("wakeUp", new Class[]{long.class}).invoke(powerManager, SystemClock.uptimeMillis());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
