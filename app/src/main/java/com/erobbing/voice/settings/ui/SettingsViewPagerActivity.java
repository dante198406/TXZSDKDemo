package com.erobbing.voice.settings.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.erobbing.voice.preference.UserPerferenceUtil;
import com.erobbing.voice.utils.Logger;
import com.erobbing.voice.utils.Network;
import com.erobbing.voice.utils.TTSUtil;
import com.erobbing.voice.view.EditWakeupWordPopWindow;
import com.erobbing.voice.view.SettingHelpPopupWindow;
import com.erobbing.voice.view.SettingLoadingPopupWindow;
import com.erobbing.voice.view.SettingHelpPopupWindow.ISettingHelpPopListener;
import com.erobbing.voice.view.SettingLoadingPopupWindow.ISettingLoadingPopListener;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZNavManager;
import com.txznet.sdk.TXZTtsManager;
import com.txznet.sdk.TXZNavManager.NavToolType;
import com.txznet.sdkdemo.R;
import com.txznet.sdkdemo.SDKDemoApp;
import com.txznet.sdkdemo.ui.ConfigActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class SettingsViewPagerActivity extends Activity {

	private static final String TAG = SettingsViewPagerActivity.class.getSimpleName();

	private Context mContext;

	private ArrayList<View> mViewList = new ArrayList<View>();
	private LayoutInflater mLayoutInflater;
	private ViewGroup indicatorViewGroup;

	private ImageView mImageView;
	private ImageView[] mImageViews;
	private ViewPager mViewPager;

	// --page-1
	// Wake up
	private CheckBox mCBWakeup;
	private TextView mTvWakeupStatusClose;
	private TextView mTvWakeupStatusOpen;
	private ImageView mIvEditWakeupword;
	public static final String ACTION_WAKEUP_WORDS_UPDATE = "com.unisound.unicar.ACTION_WAKEUP_WORDS_UPDATE";

	// TTS
	private RadioGroup mRgTTSSpeed;
	private RadioButton mRbTTSSlowly;
	private RadioButton mRbTTSStandard;
	private RadioButton mRbTTSFast;
	private TextView mTvTTSSpeedStatus;
	int mTtsTaskId = TXZTtsManager.INVALID_TTS_TASK_ID;

	private int mLastTtsTimbre = UserPerferenceUtil.VALUE_TTS_TIMBRE_STANDARD;

	// --page-2
	// Map
	private RadioGroup mRgMapChoose;
	private RadioButton mRbMapGaode;
	private RadioButton mRbMapBaidu;
	private RadioButton mBbMapMore;
	private TextView mTvMapChooseStatus;

	// weather
	private CheckBox mCBWeather;
	private TextView mTvWeatherStatus;
	// Address
	private LinearLayout mSettingAddrFavoriteLayout;

	private SettingHelpPopupWindow mSettingHelpPop;
	private SettingLoadingPopupWindow mSettingLoadingPop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.d(TAG, "!--->onCreate()----");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_settings_view_pager);
		mLayoutInflater = getLayoutInflater();
		mContext = getApplicationContext();

		TextView tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
		tvTopTitle.setText(R.string.common_control);
		ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
		returnBtn.setOnClickListener(mReturnListerner);

		// 添加layout
		mViewList.add(mLayoutInflater.inflate(R.layout.pager_settings_layout_1, null));
		mViewList.add(mLayoutInflater.inflate(R.layout.pager_settings_layout_2, null));

		initViewPager();

		UserPerferenceUtil.registerOnSharedPreferenceChangeListener(mContext,
				mPreferenceChangeListener);

		IntentFilter wakeupfilter = new IntentFilter(ACTION_WAKEUP_WORDS_UPDATE);
		registerReceiver(receiver, wakeupfilter);

	}

	private OnClickListener mReturnListerner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			onBackPressed();
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		Logger.d(TAG, "onResume");
		initMapUIStatus();

	}

	private void initViewPager() {
		mImageViews = new ImageView[mViewList.size()];

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pagerAdapter);

		indicatorViewGroup = (LinearLayout) findViewById(R.id.viewGroup);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_dot_normal);
		for (int i = 0; i < mViewList.size(); i++) {
			mImageView = new ImageView(SettingsViewPagerActivity.this);
			mImageView.setLayoutParams(new LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
			mImageView.setPadding(0, 20, 0, 20);

			if (i == 0) {
				mImageView.setBackgroundResource(R.drawable.icon_dot_selected);
			} else {
				mImageView.setBackgroundResource(R.drawable.icon_dot_normal);
			}
			mImageViews[i] = mImageView;
			indicatorViewGroup.addView(mImageViews[i]);
		}
		bitmap.recycle();
		mViewPager.setOnPageChangeListener(mPageChangeLinstener);
	}

	private OnPageChangeListener mPageChangeLinstener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			for (int i = 0; i < mImageViews.length; i++) {
				if (i == arg0) {
					mImageViews[i].setBackgroundResource(R.drawable.icon_dot_selected);
				} else {
					mImageViews[i].setBackgroundResource(R.drawable.icon_dot_normal);
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case ViewPager.SCROLL_STATE_IDLE:

				break;
			case ViewPager.SCROLL_STATE_DRAGGING:

				break;
			default:
				break;
			}
		}
	};

	PagerAdapter pagerAdapter = new PagerAdapter() {

		@Override
		public int getCount() {
			return mViewList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			int childCount = ((ViewPager) container).getChildCount();
			if (mViewList == null) {
				Logger.e(TAG, "!--->PagerAdapter instantiateItem error, mViewList is null.");
				return null;
			}
			if (null != mViewList && mViewList.size() < position) {
				Logger.e(TAG,
						"!--->PagerAdapter instantiateItem error, return null. ViewList size = "
								+ mViewList.size() + "; position = " + position);
				return null;
			}
			Logger.d(TAG, "!--->PagerAdapter--position:" + position + "; childCount:" + childCount
					+ "; mViewList.size:" + mViewList.size());
			switch (position) {
			case 0:
				((ViewPager) container).addView(mViewList.get(position), 0);
				// Wake up setting
				mCBWakeup = (CheckBox) findViewById(R.id.cb_wakeup);
				mTvWakeupStatusClose = (TextView) findViewById(R.id.tv_status_wakeup_close);
				mTvWakeupStatusOpen = (TextView) findViewById(R.id.tv_status_wakeup_open);
				mIvEditWakeupword = (ImageView) findViewById(R.id.iv_setting_edit_wakeupword);

				// TTS setting
				mRgTTSSpeed = (RadioGroup) findViewById(R.id.rg_setting_tts_speed);
				mRbTTSSlowly = (RadioButton) findViewById(R.id.rBtn_tts_slowly);
				mRbTTSStandard = (RadioButton) findViewById(R.id.rBtn_tts_standard);
				mRbTTSFast = (RadioButton) findViewById(R.id.rBtn_tts_fast);
				mTvTTSSpeedStatus = (TextView) findViewById(R.id.tv_status_tts_play);

				// update UI Status
				initWakeupUIStatus();
				initTtsSpeedUIStatus();

				// set listener
				mCBWakeup.setOnCheckedChangeListener(mCbListener);
				mRgTTSSpeed.setOnCheckedChangeListener(mRgCheckedChangeListener);
				break;

			case 1:
				if (childCount == 0) {
					Logger.w(TAG, "!--->position is 1 but childCount is 0");
					((ViewPager) container).addView(mViewList.get(position), 0);
				}
				((ViewPager) container).addView(mViewList.get(position), 1);

				// Map Choose setting
				mRgMapChoose = (RadioGroup) findViewById(R.id.rg_setting_map_choose);
				mRbMapGaode = (RadioButton) findViewById(R.id.rBtn_map_gaode);
				mRbMapBaidu = (RadioButton) findViewById(R.id.rBtn_map_baidu);
				mBbMapMore = (RadioButton) findViewById(R.id.rBtn_map_more);
				mBbMapMore.setVisibility(/*
										 * GUIConfig.isSupportMoreMapSetting ?
										 * View.VISIBLE :
										 */View.GONE);
				mTvMapChooseStatus = (TextView) findViewById(R.id.tv_status_map_choose);

				// Weather forecast setting
				mCBWeather = (CheckBox) findViewById(R.id.cb_weather_forecast);
				mTvWeatherStatus = (TextView) findViewById(R.id.tv_status_weather_forecast);
				// Address
				mSettingAddrFavoriteLayout = (LinearLayout) findViewById(R.id.ll_setting_addr);

				// update UI Status
				initMapUIStatus();
				initWeatherSettingUIStatus();

				// set listener
				mRgMapChoose.setOnCheckedChangeListener(mRgCheckedChangeListener);
				mBbMapMore.setOnClickListener(mOnClickListener);
				mCBWeather.setOnCheckedChangeListener(mCbListener);
				mSettingAddrFavoriteLayout.setOnClickListener(mOnClickListener);
				break;

			}

			return mViewList.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			Logger.d(TAG, "destroyItem-----position = " + position);
			if (mViewList != null && mViewList.size() > 0 && mViewList.size() >= position) {
				((ViewPager) container).removeView(mViewList.get(position));
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	};

	private void initWakeupUIStatus() {
		boolean isWakeUp = UserPerferenceUtil.isWakeupEnable(mContext);
		Logger.d(TAG, "!--->initWakeupUIStatus---isWakeUp = " + isWakeUp);
		updateWakeupwordTextView(isWakeUp, UserPerferenceUtil.getWakeupWords(mContext));
		mCBWakeup.setChecked(isWakeUp);
	}

	private void initTtsSpeedUIStatus() {
		int ttsSpeed = UserPerferenceUtil.getTTSSpeed(mContext);
		Logger.d(TAG, "!--->initTtsSpeedUIStatus---ttsSpeed = " + ttsSpeed);
		if (UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY == ttsSpeed) {
			mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_slowly);
			mRbTTSSlowly.setChecked(true);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD == ttsSpeed) {
			mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_standard);
			mRbTTSStandard.setChecked(true);
		} else if (UserPerferenceUtil.VALUE_TTS_SPEED_FAST == ttsSpeed) {
			mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_fast);
			mRbTTSFast.setChecked(true);
		}
	}

	private void initMapUIStatus() {
		int mapType = UserPerferenceUtil.getMapChoose(mContext);
		Logger.d(TAG, "!--->initMapUIStatus---mapType = " + mapType);
		if (null == mTvMapChooseStatus) {
			Logger.d(TAG, "!--->mTvMapChooseStatus is null, No need update Map UI Status.");
			return;
		}

		switch (mapType) {
		case UserPerferenceUtil.VALUE_MAP_AMAP:
			mTvMapChooseStatus.setText(R.string.setting_map_choose_gaode);
			mRbMapGaode.setChecked(true);
			break;
		case UserPerferenceUtil.VALUE_MAP_BAIDU:
			mTvMapChooseStatus.setText(R.string.setting_map_choose_baidu);
			mRbMapBaidu.setChecked(true);
			break;
		case UserPerferenceUtil.VALUE_MAP_TUBA:
			mTvMapChooseStatus.setText(R.string.setting_map_choose_tuba);
			mBbMapMore.setChecked(true);
			break;
		case UserPerferenceUtil.VALUE_MAP_DAODAOTONG:
			mTvMapChooseStatus.setText(R.string.setting_map_choose_daodaotong);
			mBbMapMore.setChecked(true);
			break;
		default:
			break;
		}

	}

	private void initWeatherSettingUIStatus() {
		boolean isEnableWeather = UserPerferenceUtil.getWeatherEnable(mContext);
		Logger.d(TAG, "!--->initWeatherSettingUIStatus---isEnableWeather = " + isEnableWeather);
		mTvWeatherStatus.setText(isEnableWeather ? R.string.setting_weather_open
				: R.string.setting_weather_closed);
		mCBWeather.setChecked(isEnableWeather);
	}

	private android.widget.CompoundButton.OnCheckedChangeListener mCbListener = new android.widget.CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switch (buttonView.getId()) {
			case R.id.cb_wakeup:
				Logger.d(TAG, "onCheckedChanged---cb_wakeup--isChecked = " + isChecked);
				if (isChecked) {
					updateWakeupwordTextView(true, UserPerferenceUtil.getWakeupWords(mContext));
					showHelpTextPopWindow(mContext, R.string.setting_title_wakeup,
							R.string.setting_help_text_wakeup,
							SettingHelpPopupWindow.TYPE_SWITCH_WAKE_UP);
					mTtsTaskId = TXZTtsManager.getInstance().speakText(
							TTSUtil.TTS_SETTING_WAKEUP_OPEN);
				} else {
					updateWakeupwordTextView(false, "");
					mTtsTaskId = TXZTtsManager.getInstance().speakText(
							TTSUtil.TTS_SETTING_WAKEUP_CLOSE);
				}
				UserPerferenceUtil.setWakeupEnable(mContext, isChecked);//
				Logger.d(TAG, "onCheckedChanged--cb_wakeup-----End.");
				break;
			case R.id.cb_weather_forecast:
				Logger.d(TAG, "!--->cb_weather isChecked = " + isChecked);
				if (isChecked) {
					mTvWeatherStatus.setText(R.string.setting_weather_open);
					showHelpTextPopWindow(mContext, R.string.setting_weather_forecast,
							R.string.setting_help_text_weather,
							SettingHelpPopupWindow.TYPE_SWITCH_WEATHER);
					mTtsTaskId = TXZTtsManager.getInstance().speakText(
							TTSUtil.TTS_SETTING_WEATHER_OPEN);
				} else {
					mTvWeatherStatus.setText(R.string.setting_weather_closed);
					mTtsTaskId = TXZTtsManager.getInstance().speakText(
							TTSUtil.TTS_SETTING_WEATHER_CLOSE);
				}
				UserPerferenceUtil.setWeatherEnable(mContext, isChecked);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 
	 */
	private android.widget.RadioGroup.OnCheckedChangeListener mRgCheckedChangeListener = new android.widget.RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			Logger.d(TAG, "!--->---mRgCheckedChangeListener---onCheckedChanged----checkedId = "
					+ checkedId);
			switch (checkedId) {
			case R.id.rBtn_map_gaode:
				Logger.d(TAG, "!--->map gaode---");
				mTvMapChooseStatus.setText(R.string.setting_map_choose_gaode);
				UserPerferenceUtil.setMapChoose(mContext, UserPerferenceUtil.VALUE_MAP_AMAP);
				TXZNavManager.getInstance().setNavTool(NavToolType.NAV_TOOL_GAODE_MAP_CAR);
				mTtsTaskId = TXZTtsManager.getInstance().speakText(TTSUtil.TTS_SETTING_MAP_AMAP);
				break;
			case R.id.rBtn_map_baidu:
				Logger.d(TAG, "!--->map baidu----");
				mTvMapChooseStatus.setText(R.string.setting_map_choose_baidu);
				UserPerferenceUtil.setMapChoose(mContext, UserPerferenceUtil.VALUE_MAP_BAIDU);
				TXZNavManager.getInstance().setNavTool(NavToolType.NAV_TOOL_BAIDU_NAV_HD);
				mTtsTaskId = TXZTtsManager.getInstance().speakText(TTSUtil.TTS_SETTING_MAP_BAIDU);
				break;
			case R.id.rBtn_map_more:
				Logger.d(TAG, "!--->mRgCheckedChangeListener---rBtn_map_more--do nothing--");
				break;
			case R.id.rBtn_tts_slowly:
				Logger.d(TAG, "!--->tts slowly----");
				mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_slowly);
				UserPerferenceUtil.setTTSSpeed(mContext, UserPerferenceUtil.VALUE_TTS_SPEED_SLOWLY);
				TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.TTS_SPEED_SLOW);
				mTtsTaskId = TXZTtsManager.getInstance().speakText(TTSUtil.TTS_SETTING_SPEED_SLOW);
				break;
			case R.id.rBtn_tts_standard:
				Logger.d(TAG, "!--->tts standard----");
				mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_standard);
				UserPerferenceUtil.setTTSSpeed(mContext,
						UserPerferenceUtil.VALUE_TTS_SPEED_STANDARD);
				TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.TTS_SPEED_STANDARD);
				mTtsTaskId = TXZTtsManager.getInstance().speakText(
						TTSUtil.TTS_SETTING_SPEED_STANDARD);
				break;
			case R.id.rBtn_tts_fast:
				Logger.d(TAG, "!--->tts fast----");
				mTvTTSSpeedStatus.setText(R.string.setting_tts_speed_fast);
				UserPerferenceUtil.setTTSSpeed(mContext, UserPerferenceUtil.VALUE_TTS_SPEED_FAST);
				TXZTtsManager.getInstance().setVoiceSpeed(UserPerferenceUtil.TTS_SPEED_FAST);
				mTtsTaskId = TXZTtsManager.getInstance().speakText(TTSUtil.TTS_SETTING_SPEED_FAST);
				break;

			default:
				break;
			}
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Logger.d(TAG, "!--->onClick = " + v.getId());
			switch (v.getId()) {
			case R.id.ll_setting_addr:
				/*
				 * Intent addrFavIntent = new
				 * Intent(SettingsViewPagerActivity.this,
				 * AddressFavoriteActivity.class); startActivity(addrFavIntent);
				 */
				break;
			case R.id.rBtn_map_more:
				Logger.d(TAG, "!--->---mOnClickListener--click rBtn_map_more----");
				/*
				 * Intent intentMore = new
				 * Intent(SettingsViewPagerActivity.this,
				 * SettingMapViewPagerActivity.class);
				 * startActivityForResult(intentMore,
				 * GUIConfig.ACTIVITY_REQUEST_CODE_CHOOSE_MAP);
				 */
				break;
			case R.id.tv_status_wakeup_open:
				Logger.d(TAG, "!--->---click tv_status_wakeup----");
				showChangeTextPopWindow(mContext);
				break;
			case R.id.iv_setting_edit_wakeupword:
				Logger.d(TAG, "!--->---click edit_wakeupword----");
				showChangeTextPopWindow(mContext);
				break;
			default:
				break;
			}
		}
	};

	private EditWakeupWordPopWindow pop;

	/**
	 * show Edit Wakeupword PopWindow
	 * 
	 * @param context
	 */
	private void showChangeTextPopWindow(Context context) {
		Logger.d(TAG, "showEditWakeupwordPopWindow----");
		pop = new EditWakeupWordPopWindow(context);
		pop.showPopWindow(mViewPager);
	}

	private OnSharedPreferenceChangeListener mPreferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Logger.d(TAG, "!--->onSharedPreferenceChanged: key " + key);
			if (UserPerferenceUtil.KEY_WAKEUP_WORDS.equals(key)) {
				String wakeupWord = UserPerferenceUtil.getWakeupWords(mContext);
				boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
				if (isWakeUpOpen) {
					updateWakeupwordTextView(isWakeUpOpen, wakeupWord);
				}
				String[] wakeupWords = UserPerferenceUtil.getWakeupWordArray(mContext);
				TXZConfigManager.getInstance().setWakeupKeywordsNew(wakeupWords);
				mTtsTaskId = TXZTtsManager.getInstance().speakText(SDKDemoApp.getApp().getResources().getString(R.string.wakeup_word_new) + wakeupWord);
			} else if (UserPerferenceUtil.KEY_ENABLE_WAKEUP.equals(key)) {
				// GuiSettingUpdateUtil.sendWakeupConfigure(mContext);
				boolean isWakeUpOpen = UserPerferenceUtil.isWakeupEnable(mContext);
				TXZConfigManager.getInstance().enableWakeup(isWakeUpOpen);
			}
		}
	};

	/**
	 * 
	 * @param isWakeupOpen
	 * @param wakeupWord
	 *            : if WakeUp is Open show wakeupWord
	 */
	private void updateWakeupwordTextView(boolean isWakeupOpen, String wakeupWord) {
		if (null == mTvWakeupStatusOpen) {
			return;
		}
		boolean isConnected = Network.isNetworkConnected(mContext);
		Logger.d(TAG, "updateWakeupwordTextView--isWakeupOpen = " + isWakeupOpen + "; showText = "
				+ wakeupWord + "; isConnected = " + isConnected);
		if (isWakeupOpen) {
			wakeupWord = addDoubleQuotationMarks(wakeupWord);
			mTvWakeupStatusOpen.setText(wakeupWord);

			mTvWakeupStatusClose.setVisibility(View.GONE);
			mTvWakeupStatusOpen.setVisibility(View.VISIBLE);
			/* if (GUIConfig.isSupportUpdateWakeupWordSetting && isConnected) { */
			mTvWakeupStatusOpen.setOnClickListener(mOnClickListener);
			mIvEditWakeupword.setOnClickListener(mOnClickListener);
			mIvEditWakeupword.setVisibility(View.VISIBLE);
			/*
			 * } else { mTvWakeupStatusOpen.setOnClickListener(null);
			 * mIvEditWakeupword.setOnClickListener(null);
			 * mIvEditWakeupword.setVisibility(View.GONE); }
			 */
		} else {
			mTvWakeupStatusOpen.setVisibility(View.GONE);
			mIvEditWakeupword.setVisibility(View.GONE);
			mTvWakeupStatusClose.setVisibility(View.VISIBLE);
			mTvWakeupStatusClose.setText(R.string.setting_wakeup_status_closed);
		}
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String addDoubleQuotationMarks(String text) {
		if (TextUtils.isEmpty(text)) {
			return text;
		}
		if (text.startsWith("“") && text.endsWith("”")) {
			return text;
		} else {
			text = "“" + text + "”";
			return text;
		}
	}

	/**
	 * 
	 * @param text
	 * @return
	 */
	public static String removeDoubleQuotationMarks(String text) {
		if (TextUtils.isEmpty(text)) {
			return text;
		}
		if (text.startsWith("“") && text.endsWith("”")) {
			text = text.substring(text.indexOf("“") + 1, text.lastIndexOf("”"));
		}
		return text;
	}

	/**
	 * showHelpTextPopWindow
	 * 
	 * @param context
	 * @param titleRes
	 * @param contentRes
	 * @param type
	 */
	private void showHelpTextPopWindow(Context context, String titleRes, String contentRes, int type) {
		showHelpTextPopWindow(context, titleRes, contentRes, type, null);
	}

	/**
	 * showHelpTextPopWindow
	 * 
	 * @param context
	 * @param titleRes
	 * @param contentRes
	 * @param type
	 */
	private void showHelpTextPopWindow(Context context, int titleRes, int contentRes, int type) {
		showHelpTextPopWindow(context, context.getResources().getString(titleRes), context
				.getResources().getString(contentRes), type, null);
	}

	/**
	 * showHelpTextPopWindow
	 *@param context
	 * @param titleRes
	 * @param contentRes
	 * @param type
	 * @param listener
	 */
	private void showHelpTextPopWindow(Context context, String titleRes, String contentRes,
			int type, ISettingHelpPopListener listener) {
		Logger.d(TAG, "showHelpTextPopWindow-----type = " + type);
		if (null != mSettingLoadingPop) {
			Logger.d(TAG, "showHelpTextPopWindow dismiss LoadingPop");
			mSettingLoadingPop.dismiss();
		}
		if (mSettingHelpPop != null && mSettingHelpPop.isShowing()) {
			mSettingHelpPop.dismiss();
		}
		mSettingHelpPop = new SettingHelpPopupWindow(context);
		mSettingHelpPop.setTitle(titleRes);
		mSettingHelpPop.setContent(contentRes);
		mSettingHelpPop.showPopWindow(mViewPager);

		mSettingHelpPop.setType(type);
		mSettingHelpPop.setSettingHelpPopListener(listener);
	}

	private ISettingLoadingPopListener mSettingLoadingPopListener = new ISettingLoadingPopListener() {
		@Override
		public void onCancelClick() {
			Logger.d(TAG, "onCancelClick--cancel switch");
			UserPerferenceUtil.setTtsTimbre(mContext, mLastTtsTimbre);
			// initTtsTimbreUIStatus();
		}
	};

	/**
	 * showSettingLoadingPopWindow
	* @param context
	 */
	private void showSettingLoadingPopWindow(Context context) {
		Logger.d(TAG, "showSettingLoadingPopWindow");
		if (null != mSettingLoadingPop && mSettingLoadingPop.isShowing()) {
			mSettingLoadingPop.dismiss();
		}
		mSettingLoadingPop = new SettingLoadingPopupWindow(context);
		mSettingLoadingPop.setSettingLoadingPopListener(mSettingLoadingPopListener);
		mSettingLoadingPop.showPopWindow(mViewPager);
	}

	/**
	 * 
	 */
	private void dismissSettingLoadingPopWindow() {
		if (null != mSettingLoadingPop) {
			Logger.d(TAG, "dismissSettingLoadingPopWindow");
			mSettingLoadingPop.dismiss();
			mSettingLoadingPop = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Logger.d(TAG, "!--->onActivityResult-----resultCode = " + resultCode);
		// if (resultCode == GUIConfig.ACTIVITY_RESULT_CODE_SETTING_MAP_FINISH)
		// {
		// initMapUIStatus(); //do it onResume()
		// }
	}

	/**
	 * unRegistReceiver
	 * 
	*/
	private void unRegistReceiver() {
		// unregisterReceiver(mReceiver);
		unregisterReceiver(receiver);
	}

	private ISettingHelpPopListener mSettingHelpPopListener = new ISettingHelpPopListener() {

		@Override
		public void onIKonwClick() {
			doOnIKonwClick();
		}

		@Override
		public void onBackClick() {
			Logger.d(TAG, "onBackClick...");
			doOnIKonwClick();
		}

	};

	private void doOnIKonwClick() {
		int type = -1;
		if (mSettingHelpPop != null) {
			type = mSettingHelpPop.getType();
		}
		Logger.d(TAG, "doOnIKonwClick--pop type:" + type);

	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.d(TAG, "onPause---");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.d(TAG, "onDestroy---");
		UserPerferenceUtil.unregisterOnSharedPreferenceChangeListener(mContext,
				mPreferenceChangeListener);
		unRegistReceiver();
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (pop != null) {
				pop.dismiss();
			}
		}

	};

}
