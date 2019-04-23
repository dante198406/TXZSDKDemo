package com.erobbing.voice.view;

import com.erobbing.voice.preference.UserPerferenceUtil;
import com.erobbing.voice.utils.DeviceTool;
import com.erobbing.voice.utils.Logger;
import com.txznet.sdkdemo.R;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

public class EditWakeupWordPopWindow extends PopupWindow {

	private static final String TAG = EditWakeupWordPopWindow.class.getSimpleName();

	private Context mContext;

	private EditText mEtInputText;
	private Button mBtnOk;

	private OnEditWakeupwordPopListener mOnEditWakeupwordPopListener;
	private ProgressBar mPb_waiting;
	private LinearLayout ll_loading_text;

	/**
	 * 
	 * @param context
	 */
	public EditWakeupWordPopWindow(Context context) {
		mContext = context;

		LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View contentView = mLayoutInflater.inflate(R.layout.pop_edit_wakeupword_layout, null);

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
		mEtInputText = (EditText) contentView.findViewById(R.id.et_change_wakeupword);
		mEtInputText.setHint(R.string.default_hint_edit_wakeup_word);
		//zhouyuhuan delete 
		//mEtInputText.setFocusable(true);
		mBtnOk = (Button) contentView.findViewById(R.id.btn_change_wakeup_ok);
		Button btnCancel = (Button) contentView.findViewById(R.id.btn_change_wakeup_cancel);
		mBtnOk.setOnClickListener(mOnClickListener);
		mBtnOk.setEnabled(false);
		btnCancel.setOnClickListener(mOnClickListener);

		mEtInputText.addTextChangedListener(mPopEditTextWatcher);

		contentView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Logger.d(TAG, "onKey--contentView--keyCode = " + keyCode);
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					dismiss();
					if (null != mOnEditWakeupwordPopListener) {
						mOnEditWakeupwordPopListener.onCancelClick();
					}
					return true;
				}
				return false;
			}
		});
		mPb_waiting = (ProgressBar) contentView.findViewById(R.id.pb_waiting);
		ll_loading_text = (LinearLayout) contentView.findViewById(R.id.ll_editWakeUp);
	}

	/**
	 * showChangeTextPopWindow
	 */
	public void showPopWindow(View parent) {
		Logger.d(TAG, "showPopWindow-----");
		if (!this.isShowing()) {
			this.showAtLocation(parent, Gravity.TOP, 0, 0);
			// show Input Keypad
			//mEtInputText.requestFocus();
			mEtInputText.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					Logger.d(TAG, "onKey---mEtInputText---keyCode = " + keyCode);
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						if (null != mEtInputText) {
							mEtInputText.clearFocus();
						}
					}
					return false;
				}
			});
			DeviceTool.showEditTextKeyboard(mEtInputText, true);
		}
	}

	private TextWatcher mPopEditTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Logger.d(TAG, "mPopEditTextWatcher--onTextChanged--s=" + s + "; count = " + count + "; start = " + start + "; before = " + before);
			if (null == mBtnOk) {
				return;
			}
			if (TextUtils.isEmpty(s)) {
				mBtnOk.setEnabled(false);
			} else {
				mBtnOk.setEnabled(true);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			Logger.d(TAG, "mPopEditTextWatcher--beforeTextChanged--s=" + s + "; count = " + count + "; start = " + start + "; after = " + after);
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_change_wakeup_ok:
				Logger.d(TAG, "mPopupWindowOnClickListener-OK--");
				if (null != mEtInputText) {
					String newText = mEtInputText.getText().toString();
					// mPb_waiting.setVisibility(View.VISIBLE);
					ll_loading_text.setVisibility(View.VISIBLE);
					Logger.d(TAG, "mPopupWindowOnClickListener--OK--newText = " + newText);
					if (!TextUtils.isEmpty(newText)) {
						if (null != mOnEditWakeupwordPopListener) {
							mOnEditWakeupwordPopListener.onOkClick(newText);
						} else {
							sendUpdateWakeupWord(mContext, newText);
						}
					}
				}
			    EditWakeupWordPopWindow.this.dismiss();
				break;
			case R.id.btn_change_wakeup_cancel:
				Logger.d(TAG, "mPopupWindowOnClickListener--Cancel click");
				if (null != mOnEditWakeupwordPopListener) {
					mOnEditWakeupwordPopListener.onCancelClick();
				}
				EditWakeupWordPopWindow.this.dismiss();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 
	 * @param wakeupWord
	 */
	private void sendUpdateWakeupWord(Context mContext, String wakeupWord) {
		Logger.d(TAG, "sendChangeWakeupWord--wakeupWord-" + wakeupWord);
		//zhouyuhuan add 20160627
		UserPerferenceUtil.setWakeupWords(mContext, wakeupWord);
	}

	public void setOnEditWakeupwordPopListener(OnEditWakeupwordPopListener listener) {
		mOnEditWakeupwordPopListener = listener;
	}

	public interface OnEditWakeupwordPopListener {

		public void onOkClick(String wakeupWord);

		public void onCancelClick();

	}
}
