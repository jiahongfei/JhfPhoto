package com.jhf.photo;

import com.jhf.photo.entity.Photo;

/**
 * Created by jiahongfei on 16/9/1.
 */
public interface UploadPhotoListener {

    /**
     * 开始上传调用
     * @param photo
     */
    void uploadPhotoStart(Photo photo);

    /**
     * 上传进度
     * @param photo
     * @param progress
     */
    void uploadPhotoProgress(Photo photo, int progress);

    /**
     * 上传成功
     * @param photo
     */
    void uploadPhotoSuccess(Photo photo);

    /**
     * 上传失败
     * @param photo
     */
    void uploadPhotoFails(Photo photo);

    /**
     * 上传完成（成功和失败，都会调用）
     * @param photo
     */
    void uploadPhotoDone(Photo photo);

}
