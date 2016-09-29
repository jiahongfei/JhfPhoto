package com.jhf.photo.util;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * app缓存数据文件夹
 * Created by jiahongfei on 16/8/29.
 */
public class AppDataDir {

    private static final List<String> mRootPath = new ArrayList<>();

    private static AppDataDir sAppDataPath = null;

    private AppDataDir() {
    }

    public static AppDataDir getInstance() {
        if (null == sAppDataPath) {
            synchronized (AppDataDir.class) {
                if (null == sAppDataPath) {
                    sAppDataPath = new AppDataDir();
                }
            }
        }
        return sAppDataPath;
    }

    /**
     * 清空文件保存目录,该方法比较耗时建议在子线程中使用
     * @param context
     * @return
     */
    public boolean delAllFiles(Context context){
        File f1 = getCacheDirectory(context, "");
        File f2 = getAppDataDir(context,"");
        boolean result = delAllFiles(f1);
        result = delAllFiles(f2);
        return result;
    }

    /**
     * 返回保存文件目录下文件总大小，该方法比较耗时建议在子线程中使用
     * @param context
     * @return
     */
    public long getAppDataDirSize(Context context){
        File f1 = getCacheDirectory(context, "");
        File f2 = getAppDataDir(context,"");
        long size1 = 0L;
        long size2 = 0L;
        try {
            if (f1.exists()) {
                size1 = getFileSize(f1);
            } else {
                size1 = 0L;
            }
            if(f2.exists()){
                size2 = getFileSize(f2);
            }else {
                size2 = 0L;
            }
        } catch (Exception e) {
            e.printStackTrace();
            size1 = 0L;
            size2 = 0L;
        } finally {
            return size1+size2;
        }


    }

    /**
     * 创建缓存目录/Sdcard/Android/data/package/dirPath
     * 当应用程序卸载时，会自动删除这个目录下的文件释放SD卡缓存空间，多用于缓存
     *
     * @param context
     * @param dirPath 例如：cache/img
     * @return
     */
    public File getCacheDirectory(Context context, String dirPath) {
        mRootPath.clear();
        mRootPath.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        mRootPath.add("Android");
        mRootPath.add("data");
        mRootPath.add(context.getPackageName());
        if(!TextUtils.isEmpty(dirPath)) {
            mRootPath.add(dirPath);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : mRootPath) {
            stringBuilder.append(s).append(File.separator);
        }
        return getAppDataDir(context, stringBuilder.toString());
    }

    /**
     * 创建文件保存目录/Sdcard/dirPath
     * 这个目录会永远保存文件，多用于下载有用的数据，防止app卸载数据删除
     *
     * @param context
     * @param dirPath 例如：cache/img
     * @return
     */
    public File getSdcardDirectory(Context context, String dirPath) {
        mRootPath.clear();
        mRootPath.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        if(!TextUtils.isEmpty(dirPath)) {
            mRootPath.add(dirPath);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : mRootPath) {
            stringBuilder.append(s).append(File.separator);
        }
        return getAppDataDir(context, stringBuilder.toString());
    }

    /**
     * 获取App Data Dir
     *
     * @param context
     * @param dirPath 自定义的路径例如：/App/Cache/Image
     *                需要将Environment.getExternalStorageDirectory()SD卡root路径传入进来
     *                如果当前没有sd卡或者没有添加
     *                android.permission.WRITE_EXTERNAL_STORAGE
     *                android.permission.MOUNT_UNMOUNT_FILESYSTEMS
     *                权限直接返回app系统目录context.getCacheDir()
     * @return
     */
    private File getAppDataDir(final Context context, String dirPath) {
        File appDataDir = null;
        if (CheckPermission.hasPermissions(context, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //TODO:不知道为什么Android6.0不能检查这个权限了
//                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        }) && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            appDataDir = new File(dirPath);
        }

        if (appDataDir == null || (!appDataDir.exists() && !appDataDir.mkdirs())) {
            appDataDir = context.getCacheDir();
        }

        return appDataDir;
    }
    /**
     * @param f
     * @return
     * @throws Exception
     *             功能：获取文件夹大小
     */
    public long getFileSize(File f) throws Exception {
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
     * 递归删除给定目录下的所有文件夹和文件
     *
     * @param file
     *            文件目录
     *
     * @return true删除成功 false删除失败
     */
    private boolean delAllFiles(File file) {
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


}
