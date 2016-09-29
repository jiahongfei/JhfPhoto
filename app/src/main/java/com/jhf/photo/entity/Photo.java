package com.jhf.photo.entity;

import android.text.TextUtils;

import com.jhf.photo.util.ToolUtils;

import java.io.Serializable;

/**
 * Created by jiahongfei on 16/8/29.
 */
public class Photo implements Serializable, Comparable {

    public enum UPLOAD_TYPE {
        /**
         * 开始上传
         */
        UPLOAD_START {
            @Override
            public int getTypeValue() {
                return 0;
            }
        },
        /**
         * 正在上传
         */
        UPLOADING {
            @Override
            public int getTypeValue() {
                return 1;
            }
        },
        /**
         * 上传完按成
         */
        UPLOAD_DONE {
            @Override
            public int getTypeValue() {
                return 2;
            }
        },
        /**
         * 默认情况
         */
        UPLOAD_DEFAULT {
            @Override
            public int getTypeValue() {
                return 3;
            }
        },
        /**
         * 上传失败
         */
        UPLOAD_FAILS {
            @Override
            public int getTypeValue() {
                return 4;
            }
        };

        public abstract int getTypeValue();

        public static UPLOAD_TYPE valueOf(int type) {
            switch (type) {
                case 0: {
                    return UPLOAD_START;
                }
                case 1: {
                    return UPLOADING;
                }
                case 2: {
                    return UPLOAD_DONE;
                }
                case 3: {
                    return UPLOAD_DEFAULT;
                }
                case 4: {
                    return UPLOAD_FAILS;
                }
                default:
                    break;
            }
            return UPLOAD_DEFAULT;
        }
    }

    //上传图片返回
    private String imageId;//":"2016-08-29",
    private String imageName;//":"身份证",
    /**
     * 图片URL
     */
    private String imageUrl;//":"http://10.20.180.190/app.jpg",
    private Integer imageType;//":1


    /**
     * 图片路径
     */
    private String path;
    /**
     * 是否选中图片
     */
    private boolean isSelected = false;

    /**
     * 图片缩略图地址，不是所有图片都有这个地址
     */
    private String thumbPath;
    /**
     * 图片旋转方向
     */
    public int orientation;

    /**
     * 用来标识是哪个类型的图片
     */
//    private UploadPhotoType uploadPhotoType;
    /**
     * 上传状态type
     */
    private UPLOAD_TYPE uploadType = UPLOAD_TYPE.UPLOAD_DEFAULT;
    /**
     * 上传进度
     */
    private int uploadProgress;

    /**
     * 返回图片路径
     *
     * @return
     */
    public String getNativePicturePath() {
        return ToolUtils.nativePhotoPath(getPath());
    }

    /**
     * 返回缩略图路径
     *
     * @return
     */
    public String getNativeThumbPath() {
        return ToolUtils.nativePhotoPath(getThumbPath());
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getImageType() {
        return imageType;
    }

    public void setImageType(Integer imageType) {
        this.imageType = imageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

//    public UploadPhotoType getUploadPhotoType() {
//        return uploadPhotoType;
//    }
//
//    public void setUploadPhotoType(UploadPhotoType uploadPhotoType) {
//        this.uploadPhotoType = uploadPhotoType;
//    }

    public UPLOAD_TYPE getUploadType() {
        return uploadType;
    }

    public void setUploadType(UPLOAD_TYPE uploadType) {
        this.uploadType = uploadType;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    /**
     * 返回缩略图路径
     *
     * @return
     */
    public String getThumbPath() {
        return thumbPath;
    }

    /**
     * 从photo2的内容复制到photo1中
     *
     * @param photo1
     * @param photo2
     */
    public static void photo2ToPhoto1(Photo photo1, Photo photo2) {
        photo1.setUploadType(photo2.getUploadType());
        photo1.setImageUrl(photo2.getImageUrl());
        photo1.setImageId(photo2.getImageId());
        photo1.setImageName(photo2.getImageName());
        photo1.setImageType(photo2.getImageType());
    }

    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }
        if (!(o instanceof Photo)) {
            return false;
        }
        if (!TextUtils.isEmpty(getPath())) {
            if (getPath().equals(((Photo) o).getPath())) {
                return true;
            } else {
                return false;
            }
        } else if (!TextUtils.isEmpty(getImageUrl())) {
            if (getImageUrl().equals(((Photo) o).getImageUrl())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(getPath())) {
            return getPath().hashCode();
        } else if (!TextUtils.isEmpty(getImageUrl())) {
            return getImageUrl().hashCode();
        }
        return 0;
    }

    @Override
    public String toString() {
        return "imageUrl : " + imageUrl + "\n" + "path : "
                + path + "\n" + "isSelected : " + isSelected + "\n"
                + "thumbPath : " + thumbPath + "\n" + "orientation : "
                + orientation + "\n";
    }

    //比较用于添加Set只是比较是否相等，不用排序
    @Override
    public int compareTo(Object another) {
        Photo photo1 = (Photo) this;
        Photo photo2 = (Photo) another;

        return photo1.equals(photo2) ? 0 : -1;

//        if(!TextUtils.isEmpty(photo1.getPath())){
//            int x = photo1.getPath().compareTo(photo2.getPath());
//            return x;
//        }else if(!TextUtils.isEmpty(photo1.getImageUrl())){
//            int x = photo1.getImageUrl().compareTo(photo2.getImageUrl());
//            return x;
//        }
//        return 0;
    }
}
