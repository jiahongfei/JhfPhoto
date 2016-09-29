package com.jhf.photo.upload;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jhf.photo.PhotoApplication;
import com.jhf.photo.PhotoSelectedActivity;
import com.jhf.photo.entity.Photo;
import com.jhf.photo.util.AppDataDir;
import com.jhf.photo.util.ImageUtil;
import com.jhf.photo.util.PhotoCompressUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用于上传照片，通过照相机，或者通过相册
 *
 * @author hongfeijia
 */
public class UploadPic {

    /**
     * @version V1.0.0
     * @Description: 获取图片成功监听
     */
    public interface UploadPictureSucceed {
        /**
         * 图片获取成功
         *
         * @param photoList 图片保存路径
         */
        public void onUploadPictureSucceed(List<Photo> photoList);
    }

    /**
     * 用来标识请求照相功能的activity
     */
    private static final int CAMERA_WITH_DATA = 3023;
    /**
     * 用来标识请求gallery的activity
     */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;

    private Activity mActivity;
    private Fragment mFragment;
    /**
     * 照片的名字以时间命名
     */
    public String mCameraPicNameString;
    /**已经选中的照片，跳转到相册中展示*/
    private ArrayList<Photo> mSelectedPhotoList = new ArrayList<>();

    private int mPhotoCountMax;

//    private PopupForPhoto popupWindow;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private UploadPictureSucceed mUploadPictureSucceed;

    public UploadPic(Activity activity) {
        mActivity = activity;
    }

    public void setUploadPictureSucceed(UploadPictureSucceed uploadPictureSucceed) {
        mUploadPictureSucceed = uploadPictureSucceed;
    }

    /**
     * 照相或者在Gallery中选择
     * @param selectedPhotoList 已经选中的相册
     */
    public void doPickPhotoAction(List<Photo> selectedPhotoList, int photoCountMax){
        mPhotoCountMax = photoCountMax;
        mSelectedPhotoList.clear();
        if(null != selectedPhotoList && selectedPhotoList.size() > 0){
            mSelectedPhotoList.addAll(selectedPhotoList);
        }

        showDialog();
    }

    /**
     * 照相或者在Gallery中选择
     */
    public void doPickPhotoAction() {

//        Context context = null;
//        if (null != mFragment) {
//            context = mFragment.getActivity();
//        } else {
//            context = mActivity;
//        }
//		if (!Utils.checkSdcard(context, Constant.SDCARD_MIN_FREE_SIZE_PICTURE,
//				new String[] {
//						"没有SD卡写权限，无法进行上传照片",
//						"没有插入SD卡，无法进行上传照片",
//						"内存卡容量小于" + Constant.SDCARD_MIN_FREE_SIZE_PICTURE
//								/ 1024 / 1024 + "M" + "，无法进行上传照片" })) {
//			return;
//
//		}

       doPickPhotoAction(new ArrayList<Photo>(), 9);
    }

    /**
     * 显示对话框
     */
    private void showDialog() {

//        final UploadChannelDialog dialog = new UploadChannelDialog(getContext(), R.style.CustomUploadChannelDialog);
//        dialog.setGalleryListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doPickPhotoFromGallery();// 从相册中去获取
//                dialog.dismiss();
//            }
//        });
//        dialog.setCameraListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String status = Environment.getExternalStorageState();
//                if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
//                    doTakePhoto();// 用户点击了从照相机获取
//                } else {
//                    Toast.makeText(mActivity, "没有SD卡", Toast.LENGTH_SHORT).show();
//                }
//                dialog.dismiss();
//            }
//        });
//        dialog.show();

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doPickPhotoFromGallery();// 从相册中去获取
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "相机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
                    doTakePhoto();// 用户点击了从照相机获取
                } else {
                    Toast.makeText(mActivity, "没有SD卡", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private Context getContext() {
        Context context = null;
        if (null != mFragment) {
            context = mFragment.getActivity();
        } else {
            context = mActivity;
        }
        return context;
    }

    private boolean isFinishing(){
        Context context = null;
        if (null != mFragment) {
            context = mFragment.getActivity();
        } else {
            context = mActivity;
        }
        return ((Activity)context).isFinishing();
    }

    /**
     * 压缩后的照片路径
     *
     * @return
     */
    private String getPhotoCompressPath(String fileName) {
        String photoCompressPath = "";
        File file = AppDataDir.getInstance().getCacheDirectory(getContext(), photoCompressPath);
        return file.getAbsolutePath() + fileName;
    }

    /**
     * 照相机照完的照片路径
     *
     * @param fileName
     * @return
     */
    private String getCameraPhotoPath(String fileName) {

        String photoCompressPath = "PA/Photo";
        File file = AppDataDir.getInstance().getSdcardDirectory(getContext(), photoCompressPath);
        return file.getAbsolutePath() + "/" + fileName;
    }

    /**
     * 返回照相机拍摄的bitmap,经过压缩的
     *
     * @return
     */
    private Bitmap getCameraBitmap(String photoPath) {
        int degree = ImageUtil.readPictureDegree(photoPath);
        Bitmap bitmap = PhotoCompressUtil.setThumbnailBitmap(photoPath);

        if (Math.abs(degree) > 0) {
            bitmap = ImageUtil.rotaingImageView(degree, bitmap);

        } else {

        }
        return bitmap;
    }

    /**
     * 请求照相机
     */
    private void doTakePhoto() {

        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd-HH-mm-ss");
            mCameraPicNameString = format.format((new Date()));

            Log.e("", mCameraPicNameString);

            // 创建文件
            File picFile = new File(
                    getCameraPhotoPath(mCameraPicNameString + ".jpg"));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 存储卡可用 将照片存储在 sdcard
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
            if (mFragment != null) {
                mFragment.startActivityForResult(intent, CAMERA_WITH_DATA);
            } else {
                mActivity.startActivityForResult(intent, CAMERA_WITH_DATA);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 请求相册程序
     */
    private void doPickPhotoFromGallery() {
        try {
            Intent intent = new Intent(getContext(), PhotoSelectedActivity.class);
            intent.putExtra(PhotoSelectedActivity.PHOTO_COUNT_MAX,mPhotoCountMax);
            intent.putExtra(PhotoSelectedActivity.SELECTED_PHOTO_LIST,mSelectedPhotoList);
            if (mFragment != null) {
                mFragment.startActivityForResult(intent,
                        PHOTO_PICKED_WITH_DATA);
            } else {
                mActivity.startActivityForResult(intent,
                        PHOTO_PICKED_WITH_DATA);
            }

        } catch (ActivityNotFoundException e) {
            Toast.makeText(mActivity, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 返回照相机存储图片的路径，没有经过压缩
     *
     * @return
     */
    private String getCameraPicturePath() {

        String imagePathString = getCameraPhotoPath(mCameraPicNameString
                + ".jpg");

        return imagePathString;
    }

//    /**
//     * 返回照相机存储图片的路径,经过压缩的
//     *
//     * @return
//     */
//    private String getCameraPictureCompressPath() {
//
//        String imagePathString = getCameraPhotoPath(mCameraPicNameString
//                + ".jpg");
//        Bitmap bitmap = getCameraBitmap(imagePathString);
//        String imageCompressPath = getPhotoCompressPath(mCameraPicNameString + ".jpg");
//        File file = new File(imageCompressPath);
//        if (file.exists()) {
//            file.delete();
//        }
//        FileUtils.saveBitmap(bitmap, imageCompressPath, CompressFormat.JPEG);
//
//        return imagePathString;
//    }

    private List<Photo> getGalleryPicturePath(Intent data) {
        List<Photo> photos = (List<Photo>) data.getSerializableExtra(PhotoSelectedActivity.SELECTED_PHOTO_LIST);
        return photos;
    }

//    /**
//     * 从相册中获取照片，没有经过压缩
//     *
//     * @return
//     */
//    private void getGalleryCompressPicturePath(Intent data, final UploadPictureSucceed uploadPictureSucceed) {
//        final ArrayList<Photo> photos = (ArrayList<Photo>) data.getSerializableExtra(PhotoSelectedActivity.INTENT_SELECTED_PHOTO_LIST);
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                final ArrayList<Photo> tmpPhoto = PhotoCompressUtil.compressPhoto(getContext(), photos);
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (null != uploadPictureSucceed) {
//                            uploadPictureSucceed.onUploadPictureSucceed(resultPhoto);
//                        }
//                    }
//                });
//
//
//            }
//        }).start();
//    }

    /**
     * 将照相成功的照片添加到相册数据库中
     */
    private void addPhotoContentProvider(String filePath, MediaScannerConnection.OnScanCompletedListener onScanCompletedListener) {
        MediaScannerConnection.scanFile(PhotoApplication.getInstance(), new String[]{filePath}, null, onScanCompletedListener);
    }


    /************************ 外部调用方法 ******************************/

    /**
     * 如果是Fragment调用这个方法
     *
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    /**
     * 获取照片路径，在onActivityResult方法中调用
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(isFinishing()){
            return ;
        }

        final List<Photo> photoPaths = new ArrayList<>();

        switch (resultCode) {
            case Activity.RESULT_OK: {
                if (requestCode == CAMERA_WITH_DATA) {
                    String picturePath = null;
                    //照相机
                    picturePath = getCameraPicturePath();
                    addPhotoContentProvider(picturePath, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Photo photo = new Photo();
                            photo.setSelected(false);
                            photo.setPath(path);
                            photoPaths.add(photo);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (null != mUploadPictureSucceed) {
                                        mUploadPictureSucceed.onUploadPictureSucceed(photoPaths);
                                    }
                                }
                            });

                        }
                    });
                    return ;
                } else if (requestCode == PHOTO_PICKED_WITH_DATA) {
                    //图库
                    List<Photo> tmpPhotos = getGalleryPicturePath(data);
                    photoPaths.addAll(tmpPhotos);
                }
                break;
            }
            default:
                break;
        }

        if (null != mUploadPictureSucceed &&
                null != photoPaths &&
                photoPaths.size() > 0) {
            mUploadPictureSucceed.onUploadPictureSucceed(photoPaths);
        }
    }

    public void onSaveInstanceState(Bundle outState) {

        outState.putInt("photo_count_max", mPhotoCountMax);
        outState.putString("camera_pic_name", mCameraPicNameString);
        outState.putSerializable("selected_photo_list", mSelectedPhotoList);
        Log.e("save", "UploadPicOnSaveInstanceState");

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e("restore", "UploadPicOnRestoreInstanceState");
        mPhotoCountMax = savedInstanceState.getInt("photo_count_max");
        mCameraPicNameString = savedInstanceState.getString("camera_pic_name");
        mSelectedPhotoList = (ArrayList<Photo>) savedInstanceState.getSerializable("selected_photo_list");

    }

}
