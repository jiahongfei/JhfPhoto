package com.jhf.photo.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.jhf.photo.entity.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 用于压缩图片
 * Created by jiahongfei on 16/8/31.
 */
public class PhotoCompressUtil {

    public static final int TARGET_SIZE_MINI_THUMBNAIL = 768;

    /**
     * 保存临时的Bitmap图片
     */
    private static File saveTempBitmapFile(Context context, Bitmap bitmap) {
        try {
            String cachePathString = "cache";
            File tempFile = AppDataDir.getInstance().getCacheDirectory(context, cachePathString);
            File file = File.createTempFile("img", ".jpg", tempFile);
            // 在程序退出时删除临时文件
//			file.deleteOnExit();
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    out.flush();
                    out.close();
                }
                return file;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩图片，返回压缩后图片的地址
     */
    public static ArrayList<Photo> compressPhoto(Context context, ArrayList<Photo> selectedPhotoList) {
        ArrayList<Photo> photos = new ArrayList<Photo>();
        for (int i = 0; i < selectedPhotoList.size(); i++) {
            File file = new File(selectedPhotoList.get(i).getPath());
            if (file.exists()) {
                Bitmap bitmap = ThumbnailUtils.createImageByTargetSizeMiniThumbnail(file.getAbsolutePath(), TARGET_SIZE_MINI_THUMBNAIL);
                File tmpFile = saveTempBitmapFile(context,bitmap);
                selectedPhotoList.get(i).setPath(tmpFile.getAbsolutePath());
                photos.add(selectedPhotoList.get(i));
                bitmap.recycle();
                bitmap = null;
            }
        }
        return photos;
    }

    public static Bitmap setThumbnailBitmap(String photoPath){
        return  ThumbnailUtils.createImageByTargetSizeMiniThumbnail(photoPath, TARGET_SIZE_MINI_THUMBNAIL);
    }

}
