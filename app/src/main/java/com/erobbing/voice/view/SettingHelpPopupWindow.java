package com.erobbing.voice.view;

import com.erobbing.voice.utils.Logger;
import com.txznet.sdkdemo.R;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SettingHelpPopupWindow extends PopupWindow {

	private static final String TAG = SettingHelpPopupWindow.class.getSimpleName();
	private Context mContext;

	private TextView mTextViewTitle, mTextViewContent;
	private View mParent;
	private TextView mBtnIKnow;
	private int mType = 0;

	public static final int TYPE_SWITCH_WAKE_UP = 1;
	public static final int TYPE_SWITCH_VERSION_MODE = 2;
	public static final int TYPE_SWITCH_TTS_TIMBRE = 3;
	public static final int TYPE_SWITCH_FLOAT_MIC = 4;
	public static final int TYPE_SWITCH_AEC = 5;
	public static final int TYPE_SWITCH_ONESHOT = 6;
	public static final int TYPE_SWITCH_WEATHER = 7;

	private ISettingHelpPopListener mSettingHelpPopListener;

	public SettingHelpPopupWindow(Context context) {
		mContext = context;

		LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = mLayoutInflater.inflate(R.layout.pop_setting_help, null);

		this.setContentView(contentView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.MATCH_PARENT);
		// set PopupWindow clickable
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		contentView.setFocusableInTouchMode(true);
		// update state
		this.update();
		// zhouyuhuan add
		this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		findViews(contentView);

	}

	private void findViews(View contentView) {
		mTextViewTitle = (TextView) contentView.findViewById(R.id.tv_pop_title);
		mTextViewContent = (TextView) contentView.findViewById(R.id.tv_pop_content);
		mParent = (View) contentView.findViewById(R.id.ll_setting_pop);
		mBtnIKnow = (TextView) contentView.findViewById(R.id.tv_i_know);

		mBtnIKnow.setOnClickListener(mOnClickListener);
		mParent.setOnClickListener(mOnClickListener);

		contentView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (null != mSettingHelpPopListener) {
						mSettingHelpPopListener.onBackClick();
					}
					dismiss();
				}
				return false;
			}
		});
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_i_know:
				if (null != mSettingHelpPopListener) {
					mSettingHelpPopListener.onIKonwClick();
				}
				dismiss();
				break;
			case R.id.ll_setting_pop:
				if (null != mSettingHelpPopListener) {
					mSettingHelpPopListener.onIKonwClick();
				}
				dismiss();
				break;
			default:
				break;
			}

		}
	};

	public void setTitle(String title) {
		mTextViewTitle.setText(title);
	}

	public void setTitle(int titleRes) {
		mTextViewTitle.setText(titleRes);
	}

	public void setContent(String content) {
		mTextViewContent.setText(content);
	}

	public void setContent(int contentRes) {
		mTextViewContent.setText(contentRes);
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	/**
	 * showChangeTextPopWindow
	 */
	public void showPopWindow(View parent) {
		Logger.d(TAG, "showChangeTextPopWindow-----");
		if (!this.isShowing()) {
			this.showAtLocation(parent, Gravity.TOP, 0, 0);
		}
	}

	public void setSettingHelpPopListener(ISettingHelpPopListener listener) {
		this.mSettingHelpPopListener = listener;
	}

	public interface ISettingHelpPopListener {

		public void onIKonwClick();

		public void onBackClick();

	}

}
