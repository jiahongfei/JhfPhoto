package com.jhf.photo.util;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * Created by jiahongfei on 16/9/22.
 */
public class ImageLoaderUtil {

    public static void initImageLoader(Context context){
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .writeDebugLogs()
//                .memoryCacheExtraOptions(maxImageWidthForMemoryCache,maxImageHeightForMemoryCache)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 用于相册显示缩略图
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public static void setThumbnailPhoto(String url, ImageView imageView, int holderId) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(holderId)
                .showImageForEmptyUri(holderId)
                .showImageOnFail(holderId)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(url,new ImageViewAware(imageView),options,new ImageSize(300,300),null,null);
    }

    /**
     * 用于相册显示大图
     *
     * @param url
     * @param imageView
     * @param holderId
     */
    public static void setBigPhoto(String url, ImageView imageView, int holderId) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(holderId)
                .showImageForEmptyUri(holderId)
                .showImageOnFail(holderId)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();
        ImageLoader.getInstance().displayImage(url,new ImageViewAware(imageView),options,new ImageSize(720,720),null,null);
    }

}
