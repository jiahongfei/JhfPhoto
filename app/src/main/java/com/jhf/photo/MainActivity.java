package com.jhf.photo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.jhf.photo.adapter.PhotoSelectedAdapter;
import com.jhf.photo.entity.Photo;
import com.jhf.photo.upload.UploadPic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PHOTO_MAX = 9;

    private Context mContext;
    private ViewGroup mMainLayout;
    private GridView mGridView;
    private PhotoSelectedAdapter mAdapter;
    private UploadPic mUploadPic;
    private List<Photo> mSelectedPhotoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mMainLayout = (ViewGroup) findViewById(R.id.mainLayout);
        findViewById(R.id.selectedButton).setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridView);
        mAdapter = new PhotoSelectedAdapter(mContext, PhotoSelectedAdapter.FLAG_SHOW);
        mAdapter.setInitDates(mSelectedPhotoList);
        mGridView.setAdapter(mAdapter);

        mUploadPic = new UploadPic((Activity) mContext);
        mUploadPic.setUploadPictureSucceed(new UploadPic.UploadPictureSucceed() {
            @Override
            public void onUploadPictureSucceed(List<Photo> photoList) {
                mSelectedPhotoList.clear();
                mSelectedPhotoList.addAll(photoList);
                mAdapter.notifyDataSetChanged();
            }
        });

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.selectedButton:{
                mUploadPic.doPickPhotoAction(mSelectedPhotoList, PHOTO_MAX);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(null != mUploadPic){
            mUploadPic.onActivityResult(requestCode,resultCode, data);
        }
    }
}
