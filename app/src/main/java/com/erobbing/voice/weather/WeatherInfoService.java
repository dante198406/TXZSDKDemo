package com.erobbing.voice.weather;

import java.util.Date;

import com.erobbing.voice.preference.UserPerferenceUtil;
import com.erobbing.voice.utils.Logger;
import com.erobbing.voice.utils.Network;
import com.txznet.sdk.TXZNetDataProvider;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZTtsManager.ITtsCallback;
import com.txznet.sdk.bean.WeatherData;
import com.txznet.sdk.bean.WeatherData.WeatherDay;
import com.txznet.sdkdemo.R;
import com.txznet.sdkdemo.bean.DebugUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class WeatherInfoService extends Service {
	private String TAG = "WeatherInfoService";
	public static final String WEATHER_INFO_RECEIVED_ACTION = "com.unisound.intent.action.WEATHERINFO";
	private Context mContext;
	TXZNetDataProvider.NetDataCallback<WeatherData> weatherResult;
	private String header ="";
	private WeatherDay[] mWeatherDays;
	private String city;
	private Date updateTime;
	private String weather;
	private int lowestTemperature;
	private int highestTemperature;
	private String wind;
	private int pm25 = -1;
	private String carWashIndex = "";
	private String airQuality;

	private static final int MSG_GET_RESULT = 2000;
	private long SEND_RESULT_TIME_FIRST = 10 * 1000L;
	private long SEND_RESULT_TIME = 5 * 1000L;
	private long SEND_WEATHER_DELAY_TIME = 20 * 60 * 1000L;// 20 min
	private static final int MSG_SEND_TTS = 2001;
	private long SEND_TTS_TIME = 2 * 1000L;
	private static final int MSG_SEND_DELAY = 2002;
	private long SET_DELAY_TIME = 40 * 1000L;
	private static final int MSG_SEND_CANCEL = 2003;
	public static final String ACTION_SHOW_WEATHER_LAUNCHER = "com.unisound.intent.action.SHOW_WEATHER_LAUNCHER";
	public static final String ACTION_WEATHER_UPDATE = "com.unisound.intent.action.WEATHER_UPDATE";
	private boolean isShowWeatherInfo = true;
	private boolean hasGetWeatherResult = false;
	private boolean isSearch = true;

	@Override
	public void onCreate() {

		super.onCreate();
		mContext = this;

		weatherResult = new TXZNetDataProvider.NetDataCallback<WeatherData>() {
			@Override
			public void onError(int arg0) {
				Logger.d(TAG, "!--->onWeatherResult null");
				if (isSearch) {
					searchWeatherInfo();
				}
			}

			@Override
			public void onResult(WeatherData weatherData) {
				hasGetWeatherResult = true;
				boolean isEnableWeather = UserPerferenceUtil.getWeatherEnable(mContext);
				Logger.d(TAG, "!--->isEnableWeather " + isEnableWeather);
				if (isShowWeatherInfo && isEnableWeather /* && checkNewDatetime() */) {
					Logger.e(TAG, "onWeatherResult");
					isShowWeatherInfo = false;
					setWeatherInfo(weatherData);
				}
				Logger.d(TAG, "!--->sendBroadcast launcher");
				sendBroadcastLauncher(weatherData);
			}
		};
		registerReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.d(TAG, "!--->onStartCommand: intent " + intent);
		mUIHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				searchWeatherInfo();
			}
		}, SEND_RESULT_TIME_FIRST);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		Logger.d(TAG, "onDestroy");
		super.onDestroy();
		isSearch = false;
		hasGetWeatherResult = false;
		isShowWeatherInfo = false;
		unregisterReceiver();

		if (mUIHandler != null) {
			mUIHandler.removeMessages(MSG_GET_RESULT);
			mUIHandler.removeMessages(MSG_SEND_TTS);
			mUIHandler.removeMessages(MSG_SEND_DELAY);
			mUIHandler.removeMessages(MSG_SEND_CANCEL);
			mUIHandler = null;
		}
	}

	private Handler mUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GET_RESULT:
				//Logger.d(TAG,"MSG_GET_RESULT");
				searchWeatherInfo();
				break;
			case MSG_SEND_TTS:
				Logger.d(TAG,"Weather header :" + header);
				TXZTtsManager.getInstance().speakText(
						AudioManager.STREAM_MUSIC, header,
						TXZTtsManager.PreemptType.PREEMPT_TYPE_NEXT,
						new ITtsCallback() {
							@Override
							public void onSuccess() {
								Logger.d(TAG,"Weather onSuccess ");
								//DebugUtil.showTips("播报完成");
							}

							@Override
							public void onCancel() {
								Logger.d(TAG,"Weather onCancel ");
								//DebugUtil.showTips("播报取消");
							}
						});
				break;
			default:
				break;
			}
		}
	};

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_WEATHER_UPDATE);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "!--->mReceiver--onReceive:intent " + intent);
			String action = intent.getAction();
			if (ACTION_WEATHER_UPDATE.equals(action)) {
				searchWeatherInfo();
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				boolean isConnected = Network.isNetworkConnected(mContext);
				if (isConnected) {
					searchWeatherInfo();
				}
			}
		}
	};

	private void searchWeatherInfo() {
		if (mUIHandler == null)
			return;
		mUIHandler.removeMessages(MSG_GET_RESULT);
		getNetWeatherData();
		if (!hasGetWeatherResult) {
			mUIHandler.sendEmptyMessageDelayed(MSG_GET_RESULT, SEND_RESULT_TIME);
		} else {
			mUIHandler.sendEmptyMessageDelayed(MSG_GET_RESULT, SEND_WEATHER_DELAY_TIME);
		}
	}

	private void getNetWeatherData() {

		TXZNetDataProvider.getInstance().getWeatherInfo(weatherResult);
	}

	public void setWeatherInfo(WeatherData result) {
		city = result.cityName;
		mWeatherDays = result.weatherDays;
		updateTime = result.updateTime;
		Logger.d(TAG,"updateTime:"+updateTime.toGMTString());
		
		WeatherDay todayWeather = mWeatherDays[0];
		weather = modifyWeatherImage(todayWeather.weather);
		wind = todayWeather.wind;
		lowestTemperature = todayWeather.lowestTemperature;
		highestTemperature = todayWeather.highestTemperature;
		pm25 = todayWeather.pm2_5;
		carWashIndex = todayWeather.carWashIndex;
		airQuality = todayWeather.quality;
		if(city != null){
			header = city;
		}
	    header += getResources().getString(R.string.weather_header,weather)
	    	+ getResources().getString(R.string.weather_temperature,lowestTemperature,highestTemperature);
		if(wind != null){
			header += wind + getResources().getString(R.string.period);
		}if (pm25 != 0) {
			header += getResources().getString(R.string.weather_pm25_value, pm25);
		}
		if (airQuality != null) {
			header += getResources().getString(R.string.weather_air_quality_value, airQuality);
		}
		if (carWashIndex != null) {
			header += getResources().getString(R.string.weather_car_wash_value, carWashIndex);
		}
		Message msg = new Message();
		msg.what = MSG_SEND_TTS;
		mUIHandler.sendMessageDelayed(msg, SEND_TTS_TIME);

	}

	private void sendBroadcastLauncher(WeatherData result) {
		WeatherDay[] WeatherDays = result.weatherDays;
		WeatherDay todayWeather = WeatherDays[0];
		int currentTemperature = todayWeather.currentTemperature;
		String weather = modifyWeatherImage(todayWeather.weather);
		int lowestTemperature = todayWeather.lowestTemperature;
		int highestTemperature = todayWeather.highestTemperature;
		String wind = todayWeather.wind;
		int pm25 = todayWeather.pm2_5;
		String carWashIndex = todayWeather.carWashIndex;
		String airQuality = todayWeather.quality;

		Bundle bundle = new Bundle();
		bundle.putString("city", result.cityName);
		bundle.putInt("currentTemperature", currentTemperature);
		bundle.putString("weather", weather);// null
		bundle.putInt("lowestTemperature", lowestTemperature);
		bundle.putInt("highestTemperature", highestTemperature);
		bundle.putString("wind", wind);
		bundle.putInt("pm25", pm25);
		bundle.putString("carWashIndex", carWashIndex);
		bundle.putString("airQuality", airQuality);

		Intent intent = new Intent(ACTION_SHOW_WEATHER_LAUNCHER);
		intent.putExtra("WEATHER_BUNDLE", bundle);
		sendBroadcast(intent);
	}

	private String modifyWeatherImage(String weather) {
		String weatherDao = mContext.getString(R.string.to);
		String weatherZhuan = mContext.getString(R.string.turn);

		if (weather != null && !(weather = weather.trim()).equals("")) {
			int zhuanIndex = -1;
			int daoIndex = -1;

			if ((zhuanIndex = weather.indexOf(weatherZhuan)) > 0) {
				weather = weather.substring(0, zhuanIndex);
			}

			if ((daoIndex = weather.indexOf(weatherDao)) > 0) {
				weather = weather.substring(daoIndex + weatherDao.length(), weather.length());
			}
		}
		return weather;
	}

}