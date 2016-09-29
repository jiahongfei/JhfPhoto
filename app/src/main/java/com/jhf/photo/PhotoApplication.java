package com.jhf.photo;

import android.app.Application;

import com.jhf.photo.util.ImageLoaderUtil;
import com.jhf.photo.util.PhotoUtil;

/**
 * Created by jiahongfei on 16/9/12.
 */
public class PhotoApplication extends Application {

    private static PhotoApplication sInstance;

    public static final PhotoApplication getInstance(){
        return sInstance;
    }

    public PhotoApplication(){
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoaderUtil.initImageLoader(sInstance);

        PhotoUtil.getInstance().getPhotoList(sInstance,null);

    }
}
