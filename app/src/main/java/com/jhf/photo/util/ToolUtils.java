package com.jhf.photo.util;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * 
 * @author hongfeijia
 * 
 */
public class ToolUtils {

	/**
	 * 其中languag为语言码： zh：汉语 en：英语
	 * 
	 * @return
	 */
	public static boolean isLanguage(Context context, String language) {
		Locale locale = context.getResources().getConfiguration().locale;
		String tempLanguage = locale.getLanguage();
		if (tempLanguage.endsWith(language))
			return true;
		else
			return false;
	}

	public static String getNumberFromString(String string) {
		String reg = "[^0-9]";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(string);
		return m.replaceAll("").trim();
	}

	public static int intByString(String numberString) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		int result = 0;
		try {
			Number number = numberFormat.parse(numberString);
			result = number.intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 切换语言，只改变当前应用的 android.permission.CHANGE_CONFIGURATION
	 * android:configChanges="locale" 调用完一定要更新当前界面的string
	 * 例如：调用setContentView(R.layout.main); 从新设置 TextView textView =
	 * (TextView)findViewById(R.id.textView);
	 * textView.setText(getString(R.string.hello_world));
	 * 
	 * @param context
	 * @param locale
	 */
	public static void switchLanguage(Context context, Locale locale) {
		Configuration config = context.getResources().getConfiguration();// 获得设置对象
		Resources resources = context.getResources();// 获得res资源对象
		DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
		config.locale = locale;
		resources.updateConfiguration(config, dm);
	}

	/**
	 * 实现文本复制功能
	 * 
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	// 用序列化与反序列化实现深克隆
	public static Object deepClone(Object src) {
		Object o = null;
		try {
			if (src != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(src);
				oos.close();
				ByteArrayInputStream bais = new ByteArrayInputStream(
						baos.toByteArray());
				ObjectInputStream ois = new ObjectInputStream(bais);
				o = ois.readObject();
				ois.close();
				baos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * 本地照片路径，返回URI路径
	 * 
	 * @param photoPathString
	 * @return
	 */
	public static String nativePhotoPath(String photoPathString) {
		if (TextUtils.isEmpty(photoPathString)) {
			return null;
		}
		return Uri.decode(Uri.fromFile(new File(photoPathString)).toString());
	}

	/**
	 * 保留几位小数
	 * 
	 * @param newScale
	 * @param bigDecimal
	 * @return
	 */
	public static double bigDecimal(int newScale, double bigDecimal) {
		BigDecimal b = new BigDecimal(bigDecimal);

		return b.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 判断当前应用是否在前台运行
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isRunningForeground(Context context) {
		String packageName = getPackageName(context);
		String topActivityClassName = getTopActivityName(context);
		System.out.println("packageName=" + packageName
				+ ",topActivityClassName=" + topActivityClassName);
		if (packageName != null && topActivityClassName != null
				&& topActivityClassName.startsWith(packageName)) {
			System.out.println("---> isRunningForeGround");
			return true;
		} else {
			System.out.println("---> isRunningBackGround");
			return false;
		}
	}

	public static String getTopActivityName(Context context) {
		String topActivityClassName = null;
		android.app.ActivityManager activityManager = (android.app.ActivityManager) (context
				.getSystemService(Context.ACTIVITY_SERVICE));
		List<RunningTaskInfo> runningTaskInfos = activityManager
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			topActivityClassName = f.getClassName();
		}
		return topActivityClassName;
	}

	public static String getPackageName(Context context) {
		String packageName = context.getPackageName();
		return packageName;
	}
	
	public static boolean isAPPRunning(Context context, String appPackageName){
		boolean isAppRunning = false;
		android.app.ActivityManager am = (android.app.ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		    List<RunningTaskInfo> list = am.getRunningTasks(Integer.MAX_VALUE);
		    for (RunningTaskInfo info : list) {
		        if (info.topActivity.getPackageName().equals(appPackageName) 
		        		&& info.baseActivity.getPackageName().equals(appPackageName)) {
		            isAppRunning = true;
		            //find it, break
		            break;
		        }
		    }
		    return isAppRunning;
	}
	
	/**
	 * 判断Activity是否运行，无论在前台还是后天只要运行就返回true
	 * @param context
	 * @param activityClassName
	 * @return
	 */
	public static boolean isActivityRunning(Context context,String activityClassName){
		boolean isAppRunning = false;
		android.app.ActivityManager am = (android.app.ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		    List<RunningTaskInfo> list = am.getRunningTasks(Integer.MAX_VALUE);
		    for (RunningTaskInfo info : list) {
		        if (info.topActivity.getClassName().equals(activityClassName) 
		        		|| info.baseActivity.getClassName().equals(activityClassName)) {
		            isAppRunning = true;
		            //find it, break
		            break;
		        }
		    }
		    return isAppRunning;
	}
}