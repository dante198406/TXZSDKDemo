package com.erobbing.voice.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

@SuppressLint("NewApi")
public class DeviceTool {
	public static final String TAG = "DeviceTool";

	private static final String INVALID_IMEI = "000000000000000";
	/*//zhouyuhuan delete for telephony changed
	public static String getDeviceId(Context context) {
		String deviceId = getIMEI(context);
		return (deviceId == null || deviceId.equals("")) ? INVALID_IMEI : deviceId;
	}
	
	public static String getIMEI(Context context) {
		String imei = "";
		imei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		if (imei != null && !"".equals(imei) && !imei.equals(INVALID_IMEI)) {
			return imei;
		}

		imei = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		if (imei != null && !"".equals(imei) && !imei.equals(INVALID_IMEI)) {
			return imei;
		}
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info != null) {
			return info.getMacAddress();
		}
		return INVALID_IMEI;

	}*/

	public static String getMac(Context context) {
		if (context == null) {
			return INVALID_IMEI;
		}
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (info != null) {
			return info.getMacAddress();
		}
		return INVALID_IMEI;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";

		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

			versionName = packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}

	public static String getAppPackageName(Context context) {
		String packageName = "";

		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

			packageName = packageInfo.packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packageName;
	}


	/**
	 * check Apk Exist
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			Logger.w(TAG, e.toString());
			return false;
		}
	}

	/**
	 * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡] XD added
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * is Available SDcard Space
	 * 
	 * @param sizeMb
	 *            : MIN size
	 * @return
	 */
	public static boolean isAvailableSDcardSpace(int sizeMb) {
		boolean isHasSpace = false;
		if (isSdCardExist()) {
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			long availableSpare = (blocks * blockSize) / (1024 * 1024);
			Logger.d(TAG, "SDCard availableSpare = " + availableSpare + " MB");
			if (availableSpare > sizeMb) {
				isHasSpace = true;
			}
		} else {
			Logger.e(TAG, "No sdcard!");
		}
		return isHasSpace;
	}

	/**
	 * is Available Internal Memory
	 * 
	 * @param sizeMb
	 *            : MIN limit size
	 * @return
	 */
	public static boolean isAvailableInternalMemory(int sizeMb) {
		long availableSpare = getAvailableInternalMemorySize();
		if (availableSpare > sizeMb) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get device Available Internal Memory Size
	 * 
	 * @return MB
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		long availableSpare = (availableBlocks * blockSize) / (1024 * 1024);
		Logger.d(TAG, "Device Available InternalMemorySize = " + availableSpare + " MB");
		return availableSpare;
	}

	/**
	 * 
	 * @return 10 Android 2.3.3-2.3.7; 13 Android 3.2; 14 Android 4.0; 19
	 *         Android 4.4 KitKat
	 */
	@SuppressWarnings("deprecation")
	public static int getDeviceSDKVersion() {
		int sdkVersion = Build.VERSION.SDK_INT;// Integer.parseInt(Build.VERSION.SDK);
		Logger.d(TAG, "!--->sdkVersion = " + sdkVersion);
		return sdkVersion;
	}

	/**
	 * 
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		Resources resources = context.getResources();
		return resources.getDisplayMetrics();
	}

	/**
	 * getScreenHight pix 
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHight(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.heightPixels;
	}

	/**
	 * get Screen Width pix 
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.widthPixels;
	}

	/**
	 * get Screen Density
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.density;
	}

	/**
	 * get Status Bar Height
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = res.getDimensionPixelSize(resourceId);
		}
		return result;
	}

	/**
	 * isScreenLandscape
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isScreenLandscape(Context context) {
		int orientation = context.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Logger.i(TAG, "!--->isScreenLandscape---landscape");
			return true;
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			Logger.i(TAG, "!--->isScreenLandscape---portrait");
		}
		return false;
	}

	
	public static void showEditTextKeyboard(EditText editText, boolean isShow) {
		Logger.d(TAG, "showKeyboard isShow = " + isShow);
		if (null == editText) {
			return;
		}
		InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isShow) {
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED); // show
		} else {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); // hide
		}
	}

}
