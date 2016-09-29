package com.jhf.photo.util;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;

import com.jhf.photo.entity.Photo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName: PhotoUtil
 * @Description: 相册工具类，主要是返回Photo列表，手机中相册所有图片
 * @date 2015-7-15 下午3:03:33
 */
public final class PhotoUtil {

    public interface OnScanPhotoListener {
        void onStartScanPhotoListener();

        void onScanPhotoSuccessListener(List<Photo> photos);
    }

    public static final String[] IMAGE_PROJECTION = new String[]{
            Images.ImageColumns._ID, Images.ImageColumns.DATE_MODIFIED,
            Images.ImageColumns.DATA};
    public static final String[] THUMB_PROJECTION = {Thumbnails._ID,
            Thumbnails.IMAGE_ID, Thumbnails.DATA};

    public static final long REFRESH_TIME_MILLIS = 60*1000*60;//不刷新

    private static long sLastRefreshTimeMillis = System.currentTimeMillis();
    private static List<Photo> sPhotoList = new ArrayList<>();

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);
    private OnScanPhotoListener mOnScanPhotoListener = null;
    private static PhotoUtil sPhotoUtil = null;
    private static boolean isRunning = false;

    public static PhotoUtil getInstance() {
        if (null == sPhotoUtil) {
            synchronized (PhotoUtil.class) {
                if (null == sPhotoUtil) {
                    sPhotoUtil = new PhotoUtil();
                }
            }
        }
        return sPhotoUtil;
    }

    private PhotoUtil() {
    }

    public boolean isRunning() {
        return isRunning;
    }

    public List<Photo> getPhotoList() {
        return sPhotoList;
    }

    public final void getPhotoList(final Application context, final OnScanPhotoListener onScanPhotoListener){
        getPhotoList(context, false, onScanPhotoListener);
    }

    public final void getPhotoList(final Application context, final boolean isForce, final OnScanPhotoListener onScanPhotoListener) {

        mOnScanPhotoListener = onScanPhotoListener;
        if (null != mOnScanPhotoListener) {
            mOnScanPhotoListener.onStartScanPhotoListener();
        }
        if (isRunning) {
            return;
        }
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                long curTimeMillis = System.currentTimeMillis();
                if (isForce || (curTimeMillis-sLastRefreshTimeMillis > REFRESH_TIME_MILLIS) ||
                        null == sPhotoList ||
                        0 == sPhotoList.size()) {
                    List<Photo> photos = getPhotoList(context);
                    sLastRefreshTimeMillis = System.currentTimeMillis();
                    mHandler.sendMessage(mHandler.obtainMessage(0, photos));
                } else {
                    mHandler.sendEmptyMessage(1);
                }

            }
        });
    }

    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    List<Photo> photos = (List<Photo>) msg.obj;
                    sPhotoList.clear();
                    sPhotoList.addAll(photos);
                    mHandler.sendEmptyMessage(1);
                    break;
                }
                case 1: {
                    if (null != mOnScanPhotoListener) {
                        mOnScanPhotoListener.onScanPhotoSuccessListener(sPhotoList);
                    }
                    break;
                }
                default:
                    break;
            }
            isRunning = false;
            return false;
        }
    });

    private final List<Photo> getPhotoList(final Context context) {

        List<Photo> pictures = new ArrayList<Photo>();
        final Uri uriImages = Images.Media.EXTERNAL_CONTENT_URI;
        final Uri uriThumbImages = Thumbnails.EXTERNAL_CONTENT_URI;

        final ContentResolver cr = context.getContentResolver();
        Cursor cursorImages = null;
        try {
            cursorImages = cr.query(uriImages, IMAGE_PROJECTION, null, null,
                    null);

            if (cursorImages != null && cursorImages.moveToFirst()) {
                final int size = cursorImages.getCount();
                // DebugLog.logI("Images.Media.EXTERNAL_CONTENT_URI : " + size);
                do {
                    if (Thread.interrupted()) {
                        break;
                    }

                    Photo picture = new Photo();

                    int id = cursorImages
                            .getColumnIndexOrThrow(Images.ImageColumns._ID);
                    String idString = cursorImages.getString(id);
//					picture.pictureIds = idString;
                    // DebugLog.logI("id : " + idString);

                    Cursor cursorThumb = null;
                    try {
                        cursorThumb = context.getContentResolver().query(
                                uriThumbImages,// 指定缩略图数据库的Uri
                                THUMB_PROJECTION,// 指定所要查询的字段
                                Thumbnails.IMAGE_ID + " = ?",// 查询条件
                                new String[]{idString}, // 查询条件中问号对应的值
                                null);
                        cursorThumb.moveToFirst();
                        int thumbId = cursorImages
                                .getColumnIndexOrThrow(Thumbnails._ID);
                        String thumbIdString = cursorThumb.getString(thumbId);
//						picture.thumbIds = thumbIdString;
                        // DebugLog.logI("thumbIdString : " + thumbIdString);

                        int thumbIData = cursorImages
                                .getColumnIndexOrThrow(Thumbnails.DATA);
                        String thumbIDataString = cursorThumb
                                .getString(thumbIData);
                        picture.setThumbPath(thumbIDataString);
                        // DebugLog.logI("thumbIDataString : " +
                        // thumbIDataString);

                        int thumbImageId = cursorImages
                                .getColumnIndexOrThrow(Thumbnails.IMAGE_ID);
                        String imageIdStrigng = cursorThumb
                                .getString(thumbImageId);
                        System.out
                                .println("imageIdStrigng : " + imageIdStrigng);

                    } catch (Exception e) {
//                        e.printStackTrace();
                    } finally {
                        if (null != cursorThumb) {
                            cursorThumb.close();
                            cursorThumb = null;
                        }
                    }

                    int data = cursorImages
                            .getColumnIndexOrThrow(Images.ImageColumns.DATA);
                    String dataString = cursorImages.getString(data);
                    picture.setPath(dataString);
                    // DebugLog.logI("picPathString : " + dataString);

                    // DebugLog.logI("----------------------------------------------------------");
                    if (!TextUtils.isEmpty(dataString)
                            && !TextUtils.isEmpty(imgSuffix(dataString))) {

                        pictures.add(0, picture);
                    }
                } while (cursorImages.moveToNext());

            }

        } catch (Exception e) {
            // If the database operation failed for any reason
            e.printStackTrace();
        } finally {
            if (null != cursorImages) {
                cursorImages.close();
                cursorImages = null;
            }
        }

        return pictures;
    }

    public static final String IMAGE_SUFFIX_PNG = ".png";
    public static final String IMAGE_SUFFIX_PNG_ = ".PNG";
    public static final String IMAGE_SUFFIX_JPG = ".jpg";
    public static final String IMAGE_SUFFIX_JPG_ = ".JPG";
    public static final String IMAGE_SUFFIX_JPEG = ".jpeg";
    public static final String IMAGE_SUFFIX_JPEG_ = ".JPEG";

    public static String imgSuffix(String imgUrl) {
        String suffix = "";
        if (imgUrl.contains(IMAGE_SUFFIX_PNG) || imgUrl.contains(IMAGE_SUFFIX_PNG_)) {
            suffix = IMAGE_SUFFIX_PNG;
        } else if (imgUrl.contains(IMAGE_SUFFIX_JPG) || imgUrl.contains(IMAGE_SUFFIX_JPG_)) {
            suffix = IMAGE_SUFFIX_JPG;
        } else if (imgUrl.contains(IMAGE_SUFFIX_JPEG) || imgUrl.contains(IMAGE_SUFFIX_JPEG_)) {
            suffix = IMAGE_SUFFIX_JPEG;
        } else {

        }

        return suffix;
    }

}
