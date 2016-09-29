package com.jhf.photo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 
 * @Title: FileUtils.java
 * @Package com.android.support.framework.utils
 * @ClassName: FileUtils
 * @Description: 操作文件类
 * @author jiahongfei jiahongfeinew@163.com
 * @date Nov 5, 2014 2:19:01 PM
 * @version V1.0.0
 */
public class FileUtils {

	/**
	 * 拷贝assets文件夹下的文件到指定的路径
	 * 
	 * @param context
	 * @param assets
	 * @param targetPath
	 * @throws IOException
	 */
	public static void copyAssetsFile(Context context, String assets,
			String targetPath) throws IOException {
		File targetFile = new File(targetPath);
		FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
		InputStream inputStream = context.getAssets().open(assets);
		int len = -1;
		byte[] b = new byte[4096];
		while (-1 != (len = inputStream.read(b))) {
			fileOutputStream.write(b, 0, len);
		}
		fileOutputStream.flush();
		inputStream.close();
		fileOutputStream.close();
	}
	
	/**
	 * 读取assets目录下的
	 * @param context
	 * @param assets
	 * @return
	 * @throws IOException
	 */
	public static String getAssetsFile(Context context, String assets) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		InputStream inputStream = context.getAssets().open(assets);
		int len = -1;
		byte[] b = new byte[4096];
		while (-1 != (len = inputStream.read(b))) {
			byteArrayOutputStream.write(b, 0, len);
		}
		byteArrayOutputStream.flush();
		inputStream.close();
		return byteArrayOutputStream.toString();
	}

	/**
	 * 拷贝文件
	 * 
	 * @param sourcePath
	 * @param targetPath
	 * @throws IOException
	 */
	public static void copySingleFile(String sourcePath, String targetPath)
			throws IOException {
		File targetFile = new File(targetPath);
		FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
		InputStream inputStream = new FileInputStream(sourcePath);
		int len = -1;
		byte[] b = new byte[4096];
		while (-1 != (len = inputStream.read(b))) {
			fileOutputStream.write(b, 0, len);
		}
		fileOutputStream.flush();
		inputStream.close();
		fileOutputStream.close();
	}

	public static void saveFile(String soure, File file) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(soure);
			fw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String readFile(File file) {
		String resultString = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[4096];
			try {
				while (-1 != (len = fileInputStream.read(buffer))) {
					byteArrayOutputStream.write(buffer, 0, len);
				}
				byteArrayOutputStream.flush();
				resultString = byteArrayOutputStream.toString();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return resultString;
	}

	/**
	 * 
	 * @param bitmap
	 * @param fileName
	 *            功能：创建图片文件
	 */
	public static void saveBitmap(Bitmap bitmap, String fileName,
			CompressFormat format) {
		File file = new File(fileName);

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		FileOutputStream out;

		try {
			out = new FileOutputStream(file);
			if (bitmap.compress(format, 80, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns specified application cache directory. Cache directory will be
	 * created on SD card by defined path if card is mounted and app has
	 * appropriate permission. Else - Android defines cache directory on
	 * device's file system.
	 * 
	 * @param context
	 *            Application context
	 * @param cacheDir
	 *            Cache directory path (e.g.: "AppCacheDir",
	 *            "AppDir/cache/images")
	 *            如果有SD卡返回Environment.getExternalStorageDirectory() + cacheDir
	 *            没有SD卡或没有权限写SD卡返回 context.getCacheDir()
	 * @return Cache {@link File directory}
	 */
	public static File getCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				&& hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(),
					cacheDir);
		}
		if (appCacheDir == null
				|| (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

	/**
	 * 判断是否有读写权限
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasExternalStoragePermission(Context context) {
		int perm = context
				.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 递归返回给定目录下的所有文件不包括文件夹
	 * 
	 * @param file
	 *            文件目录
	 * @return null没有文件
	 */
	public static List<File> getPathAllFiles(File file) {
		List<File> fileList = new ArrayList<File>();
		File[] files = file.listFiles();
		if (null != files) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {// 目录
					fileList.addAll(getPathAllFiles(files[i]));
				} else if (files[i].isFile()) {// 文件
					fileList.add(files[i]);
				}
			}
		}
		return fileList;
	}

	/**
	 * 返回该文件夹下的所有文件夹,列表中不包括自己
	 * 
	 * @param file
	 * @return
	 */
	public static List<File> getPathAllDir(File file) {

		List<File> dirList = new ArrayList<File>();
		File[] files = file.listFiles();
		if (null != files) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {// 目录
					dirList.add(files[i]);
					dirList.addAll(getPathAllDir(files[i]));
				} else if (files[i].isFile()) {// 文件
				}
			}
		}
		return dirList;
	}

	/**
	 * 递归删除给定目录下的所有文件夹和文件
	 * 
	 * @param file
	 *            文件目录
	 * 
	 * @return true删除成功 false删除失败
	 */
	public static boolean delAllFiles(File file) {
		File[] files = file.listFiles();
		if (null != files) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {// 目录
					if (!delAllFiles(files[i])) {
						return false;
					}
				} else {// 文件
					if (!files[i].delete()) {
						return false;
					}
				}
			}
		}
		if (!file.delete()) {
			return false;
		}
		return true;
	}

	/**
	 * @param f
	 * @return
	 * @throws Exception
	 *             功能：获取文件夹大小
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * byte数组转换成16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 根据文件流读取图片文件真实类型
	 * 
	 * @param is
	 * @return
	 */
	public static String getTypeByStream(FileInputStream is) {
		byte[] b = new byte[4];
		try {
			is.read(b, 0, b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String type = bytesToHexString(b).toUpperCase();
		if (type.contains("FFD8FF")) {
			return "jpg";
		} else if (type.contains("89504E47")) {
			return "png";
		} else if (type.contains("47494638")) {
			return "gif";
		} else if (type.contains("49492A00")) {
			return "tif";
		} else if (type.contains("424D")) {
			return "bmp";
		}
		return type;
	}

	// public static void main(String[] args) throws Exception {
	// // String src = "D:/workspace//8129.jpg";
	// // String src = "D:/workspace//temp/1.gif";
	// String src = "D:/workspace//temp/2.bmp";
	// FileInputStream is = new FileInputStream(src);
	// // byte[] b = new byte[4];
	// // is.read(b, 0, b.length);
	// // System.out.println(bytesToHexString(b));
	//
	// String type = getTypeByStream(is);
	// System.out.println(type);
	// /*
	// * JPEG (jpg)，文件头：FFD8FF
	// PNG (png)，文件头：89504E47
	// GIF (gif)，文件头：47494638
	// TIFF (tif)，文件头：49492A00
	// Windows Bitmap (bmp)，文件头：424D
	//
	// */
	// }

	// ==================================

	// 用文件头判断。直接读取文件的前几个字节。
	// 常用文件的文件头如下：
	// JPEG (jpg)，文件头：FFD8FF
	// PNG (png)，文件头：89504E47
	// GIF (gif)，文件头：47494638
	// TIFF (tif)，文件头：49492A00
	// Windows Bitmap (bmp)，文件头：424D
	// CAD (dwg)，文件头：41433130
	// Adobe Photoshop (psd)，文件头：38425053
	// Rich Text Format (rtf)，文件头：7B5C727466
	// XML (xml)，文件头：3C3F786D6C
	// HTML (html)，文件头：68746D6C3E
	// Email [thorough only] (eml)，文件头：44656C69766572792D646174653A
	// Outlook Express (dbx)，文件头：CFAD12FEC5FD746F
	// Outlook (pst)，文件头：2142444E
	// MS Word/Excel (xls.or.doc)，文件头：D0CF11E0
	// MS Access (mdb)，文件头：5374616E64617264204A
	// WordPerfect (wpd)，文件头：FF575043
	// Postscript. (eps.or.ps)，文件头：252150532D41646F6265
	// Adobe Acrobat (pdf)，文件头：255044462D312E
	// Quicken (qdf)，文件头：AC9EBD8F
	// Windows Password (pwl)，文件头：E3828596
	// ZIP Archive (zip)，文件头：504B0304
	// RAR Archive (rar)，文件头：52617221
	// Wave (wav)，文件头：57415645
	// AVI (avi)，文件头：41564920
	// Real Audio (ram)，文件头：2E7261FD
	// Real Media (rm)，文件头：2E524D46
	// MPEG (mpg)，文件头：000001BA
	// MPEG (mpg)，文件头：000001B3
	// Quicktime (mov)，文件头：6D6F6F76
	// Windows Media (asf)，文件头：3026B2758E66CF11
	// MIDI (mid)，文件头：4D546864

	/**
	 * 判断文件是否是图片，返回后缀
	 * 
	 * @param file
	 * @return 文件不存在返回null，不是图片返回null，是String数组，第一个元素是图片返回后缀第二个元素是Content-type
	 */
	public static String[] isFileImage(File file) {
		String[][] suffix = new String[][] { { ".bmp", "image/bmp" },
				{ ".gif", "image/gif" }, { ".jpeg", "image/jpeg" },
				{ ".jpg", "image/jpeg" }, { ".png", "image/png" } };
		if (file.exists()) {
			String fileName = file.getName().toLowerCase();
			for (int i = 0; i < suffix.length; i++) {
				if(fileName.endsWith(suffix[i][0])){
					return suffix[i];
				}else {
				}
			}
		}
		return null;
	}
}
