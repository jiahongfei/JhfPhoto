package com.jhf.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhf.photo.adapter.BigPhotoAdapter;
import com.jhf.photo.entity.Photo;
import com.jhf.photo.view.PhotoViewPager;

import java.util.ArrayList;

/**
 * 查看大图页面
 */
public class BigPhotoActivity extends Activity implements View.OnClickListener {

    public static final String INTENT_PHOTO_LIST = "photo_list";
    public static final String INTENT_PHOTO_INDEX = "photo_index";
    public static final String INTENT_SELECTED_PHOTO_LIST = "seleccted_photo_list";
    public static final String INTENT_PHOTO_COUNT_MAX = "photo_count_max";
    public static final String INTENT_FLAG = "flag";

    public static final String RESULT_TYPE = "type";
    public static final String RESULT_PHOTO_INDEX_LIST = "photo_index_list";

    private Context mContext;

    protected ImageView mCancelImageView;
    protected ImageView mSelectedImageView;
    protected TextView mSelectedCountTextView;
    protected ImageView mImageDelImageView;
    protected ViewGroup mPhotoDoneLayout;
    protected TextView mDoneTextView;
    protected ViewGroup mBottomWidgetLayout;
    protected TextView mPhotoIndexTextView;

    protected PhotoViewPager mPhotoViewPager;
    private BigPhotoAdapter mAdapter;
    private ArrayList<Photo> mPhotoList = new ArrayList<>();
    private ArrayList<Photo> mSelectedPhotoList = new ArrayList<>();
    /**删除索引列表*/
    private ArrayList<Integer> mDeletePhotoIndexList = new ArrayList<>();
    private int mPhotoIndex = 0;
    /**最大选择照片的数量*/
    private int mPhotoCountMax = 9;
    /**0选择图片，1上传完成图片展示，2普通的展示*/
    private int mFlag = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_photo);

        mContext = this;

        mCancelImageView = (ImageView) findViewById(R.id.iv_cancel);
        mCancelImageView.setOnClickListener(this);
        mSelectedImageView = (ImageView)findViewById(R.id.iv_selected);
        mSelectedImageView.setOnClickListener(this);
        mSelectedCountTextView = (TextView) findViewById(R.id.tv_selected_count);
        mImageDelImageView = (ImageView) findViewById(R.id.iv_delete);
        mImageDelImageView.setOnClickListener(this);
        mPhotoDoneLayout = (ViewGroup) findViewById(R.id.ll_photo_done);
        mPhotoDoneLayout.setOnClickListener(this);
        mDoneTextView = (TextView) findViewById(R.id.tv_done);
        mBottomWidgetLayout = (ViewGroup) findViewById(R.id.rl_bottom_widget);
        mPhotoIndexTextView = (TextView) findViewById(R.id.tv_photo_index);

        mPhotoViewPager = (PhotoViewPager) findViewById(R.id.photoViewPager);

        getIntentData();

        updateBottomViewState();

        initView();

        setUIModel();

    }

    private void initView(){
        if (mPhotoIndex < mPhotoList.size()) {
            updateTopViewState(mPhotoIndex);
        }

        mAdapter = new BigPhotoAdapter(mContext, mPhotoList);
        mPhotoViewPager.setAdapter(mAdapter);
        //设置当前需要显示的图片
        mPhotoViewPager.setCurrentItem(mPhotoIndex);
    }

    private void getIntentData(){
        mPhotoList = (ArrayList<Photo>) getIntent().getSerializableExtra(INTENT_PHOTO_LIST);
        if (null == mPhotoList) {
            mPhotoList = new ArrayList<>();
        }
        mSelectedPhotoList = (ArrayList<Photo>) (ArrayList<Photo>) getIntent().getSerializableExtra(INTENT_SELECTED_PHOTO_LIST);
        if (null == mSelectedPhotoList) {
            mSelectedPhotoList = new ArrayList<>();
        }
        mPhotoIndex = getIntent().getIntExtra(INTENT_PHOTO_INDEX, 0);

        mPhotoCountMax = getIntent().getIntExtra(INTENT_PHOTO_COUNT_MAX, mPhotoCountMax);

        mFlag = getIntent().getIntExtra(INTENT_FLAG,0);
    }

    /**
     * 通过flag来设置UI模式
     */
    private void setUIModel(){
        if(1 == mFlag){
            //从上传完成图片进入
            mBottomWidgetLayout.setVisibility(View.GONE);
            mSelectedImageView.setVisibility(View.GONE);
            mImageDelImageView.setVisibility(View.VISIBLE);
        }else if(0 == mFlag){
            //相册，选择图片进入
            mBottomWidgetLayout.setVisibility(View.VISIBLE);
            mSelectedImageView.setVisibility(View.VISIBLE);
            mImageDelImageView.setVisibility(View.GONE);
        }else if(2 == mFlag){
            //普通的看大图
            mBottomWidgetLayout.setVisibility(View.GONE);
            mSelectedImageView.setVisibility(View.GONE);
            mImageDelImageView.setVisibility(View.GONE);
        }
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            updateTopViewState(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void updateBottomViewState() {
        if (mSelectedPhotoList.size() > 0) {
            mDoneTextView.setEnabled(true);
            mSelectedCountTextView.setVisibility(View.VISIBLE);
            mSelectedCountTextView.setText(mSelectedPhotoList.size() + "");
        } else {
            mDoneTextView.setEnabled(false);
            mSelectedCountTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void updateTopViewState(int position) {

        mSelectedImageView.setSelected(mPhotoList.get(position).isSelected());

        mPhotoIndexTextView.setText((position + 1) + "/" + mPhotoList.size());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoViewPager.addOnPageChangeListener(onPageChangeListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mPhotoViewPager) {
            mPhotoViewPager.removeOnPageChangeListener(onPageChangeListener);
        }
    }

    @Override
    public void onBackPressed() {
        finishResult(0, mSelectedPhotoList);
    }

    private void finishResult(int type, ArrayList<Photo> selected) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_TYPE, type);
        intent.putExtra(INTENT_SELECTED_PHOTO_LIST, selected);
        intent.putExtra(RESULT_PHOTO_INDEX_LIST, mDeletePhotoIndexList);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void selectFinish() {
        finishResult(1, mSelectedPhotoList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_photo_done: {
                selectFinish();
                break;
            }
            case R.id.iv_cancel: {
                onBackPressed();
                break;
            }
            case R.id.iv_selected: {
                if (mPhotoViewPager.getCurrentItem() < mPhotoList.size()) {
                    Photo photo = mPhotoList.get(mPhotoViewPager.getCurrentItem());
                    photo.setSelected(!photo.isSelected());
                    if (photo.isSelected()) {
                        if (mSelectedPhotoList.size() >= mPhotoCountMax) {
                            Toast.makeText(mContext, "最多选择" + mPhotoCountMax + "张图片", Toast.LENGTH_SHORT).show();
                            photo.setSelected(!photo.isSelected());
                            return;
                        }
                        mSelectedPhotoList.add(photo);
                    } else {
                        mSelectedPhotoList.remove(photo);
                    }
                    updateTopViewState(mPhotoViewPager.getCurrentItem());
                    updateBottomViewState();
                }
                break;
            }
            case R.id.iv_delete:{
                if (mPhotoViewPager.getCurrentItem() < mPhotoList.size()){
                    mPhotoList.remove(mPhotoViewPager.getCurrentItem());
                    mSelectedPhotoList.remove(mPhotoViewPager.getCurrentItem());
                    mDeletePhotoIndexList.add(mPhotoViewPager.getCurrentItem());
                    mAdapter.notifyDataSetChanged();
                    if(0 == mPhotoList.size()){
                        finishResult(0, mSelectedPhotoList);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ImageUtils.initImageLoader(mContext);
    }
}
