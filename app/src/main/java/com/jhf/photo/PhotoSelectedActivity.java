package com.jhf.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhf.photo.adapter.PhotoSelectedAdapter;
import com.jhf.photo.entity.Photo;
import com.jhf.photo.util.PhotoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 相片选择界面
 *
 * @author jiahongfei
 */
public class PhotoSelectedActivity extends Activity implements OnClickListener{

    /**
     * 查看大图界面
     */
    public static final int REQUEST_CODE_BIG_PHOTO = 100;

    public static final String SELECTED_PHOTO_LIST = "seleccted_photo_list";
    public static final String PHOTO_COUNT_MAX = "photo_count_max";

    private Context mContext;

    protected TextView mPreViewPhotoTextView;
    protected TextView mSelectedCountTextView;
    protected TextView mDoneTextView;
    protected ViewGroup mPhotoDoneLayout;

    protected GridView mGridView;
    private PhotoSelectedAdapter mPictureSelectedAdapter;
    private ViewGroup mNoDataLayout;

    private ArrayList<Photo> mPictures = new ArrayList<Photo>();
    private ArrayList<Photo> mPictureSelectedList = new ArrayList<Photo>();

    private int mPhotoCountMax = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selected);
        mContext = this;
        init();
    }

    protected void init() {

        mPhotoCountMax = getIntent().getIntExtra(PHOTO_COUNT_MAX, mPhotoCountMax);

        List<Photo> photos = (List<Photo>) getIntent().getSerializableExtra(SELECTED_PHOTO_LIST);
        if (null == photos || 0 == photos.size()) {
            photos = new ArrayList<>();
        }
        mPictureSelectedList.clear();
        mPictureSelectedList.addAll(photos);

        mPreViewPhotoTextView = (TextView) findViewById(R.id.tv_preview_photo);
        mPreViewPhotoTextView.setOnClickListener(this);
        mSelectedCountTextView = (TextView) findViewById(R.id.tv_selected_count);
        mDoneTextView = (TextView) findViewById(R.id.tv_done);
        mPhotoDoneLayout = (ViewGroup) findViewById(R.id.ll_photo_done);
        mPhotoDoneLayout.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridView);
        mNoDataLayout = (ViewGroup) findViewById(R.id.noDataLayout);

        mGridView = (GridView) findViewById(R.id.gridView);
        mPictureSelectedAdapter = new PhotoSelectedAdapter(mContext);
        mPictureSelectedAdapter.setInitDates(mPictures);
        mGridView.setAdapter(mPictureSelectedAdapter);
//        mGridView.setOnScrollListener(ImageUtils.pauseOnScrollListener());

        setTitleNavBar();
        setListener();

        updateBottomViewState();

        PhotoUtil.getInstance().getPhotoList(PhotoApplication.getInstance(), new PhotoUtil.OnScanPhotoListener() {
            @Override
            public void onStartScanPhotoListener() {
                if(isFinishing()){
                    return;
                }
                //		正在加载图片请等待...
//                showLoadingView();
            }

            @Override
            public void onScanPhotoSuccessListener(List<Photo> photos) {
                if(isFinishing()){
                    return;
                }
                photos = getPhotoSelected(photos, mPictureSelectedList);
                mPictures.clear();
                mPictures.addAll(photos);
                mPictureSelectedAdapter.notifyDataSetChanged();
//                dismissLoadingView();
                if (0 == mPictures.size()) {
                    mNoDataLayout.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                } else {
                    mNoDataLayout.setVisibility(View.GONE);
                    mGridView.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private List<Photo> getPhotoSelected(List<Photo> photos, List<Photo> selecteds) {
        List<Photo> resultPhotoList = new ArrayList<>();
        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            photo.setSelected(false);
            resultPhotoList.add(photo);
            for (int j = 0; j < selecteds.size(); j++) {
                if (photo.getPath().equals(selecteds.get(j).getPath())) {
                    photo.setSelected(true);
                }
            }
        }
        photos.clear();
        return resultPhotoList;
    }

    protected void setListener() {

        mPictureSelectedAdapter.setSelectedOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                Photo picture = (Photo) mPictureSelectedAdapter.getItem(position);
                picture.setSelected(!picture.isSelected());

                if (picture.isSelected()) {

                    if (mPictureSelectedList.size() >= mPhotoCountMax) {
                        Toast.makeText(mContext,"最多只能添加" + mPhotoCountMax + "张图片", Toast.LENGTH_SHORT).show();
                        picture.setSelected(!picture.isSelected());
                        return;
                    }

                    mPictureSelectedList.add(picture);
                } else {
                    mPictureSelectedList.remove(picture);
                }
                v.setSelected(picture.isSelected());
                updateBottomViewState();
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                goBigPhotoActivity(arg2, mPictureSelectedList, mPictures);
            }
        });
        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                return false;
            }
        });
    }

    protected void setTitleNavBar() {
        ((TextView)findViewById(R.id.titleTextView)).setText(R.string.title_selected_upload_photo);
//        decodeSystemTitle(R.string.title_selected_upload_photo, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancel();
//            }
//        });
    }

    private void selectFinish() {
        finishResult(mPictureSelectedList);
    }

    private void updateBottomViewState() {
        if (mPictureSelectedList.size() > 0) {
            mPreViewPhotoTextView.setEnabled(true);
            mDoneTextView.setEnabled(true);
            mSelectedCountTextView.setVisibility(View.VISIBLE);
            mSelectedCountTextView.setText(mPictureSelectedList.size() + "");
        } else {
            mPreViewPhotoTextView.setEnabled(false);
            mDoneTextView.setEnabled(false);
            mSelectedCountTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_photo_done: {
                selectFinish();
                break;
            }
            case R.id.tv_preview_photo: {

                goBigPhotoActivity(0, mPictureSelectedList, mPictureSelectedList);

                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_BIG_PHOTO: {
                switch (resultCode) {
                    case RESULT_OK: {
                        int type = data.getIntExtra(BigPhotoActivity.RESULT_TYPE, 0);
                        if (0 == type) {
                            ArrayList<Photo> selectedList = (ArrayList<Photo>) data.getSerializableExtra(BigPhotoActivity.INTENT_SELECTED_PHOTO_LIST);
                            mPictureSelectedList.clear();
                            mPictureSelectedList.addAll(selectedList);
                            List<Photo> photos = getPhotoSelected(mPictures, mPictureSelectedList);
                            mPictures.clear();
                            mPictures.addAll(photos);
                            mPictureSelectedAdapter.notifyDataSetChanged();
                            updateBottomViewState();
                        } else if (1 == type) {
                            ArrayList<Photo> selectedList = (ArrayList<Photo>) data.getSerializableExtra(BigPhotoActivity.INTENT_SELECTED_PHOTO_LIST);
                            finishResult(selectedList);
                        }
                        break;
                    }
                    case RESULT_CANCELED: {
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
    }

    private void finishResult(ArrayList<Photo> selected) {
        Intent intent = new Intent();
        intent.putExtra(SELECTED_PHOTO_LIST, selected);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void goBigPhotoActivity(int position, ArrayList<Photo> photoSelectedList, ArrayList<Photo> photoList) {
        Intent intent = new Intent(mContext, BigPhotoActivity.class);
        intent.putExtra(BigPhotoActivity.INTENT_SELECTED_PHOTO_LIST, photoSelectedList);
        intent.putExtra(BigPhotoActivity.INTENT_PHOTO_LIST, photoList);
        intent.putExtra(BigPhotoActivity.INTENT_PHOTO_INDEX, position);
        intent.putExtra(BigPhotoActivity.INTENT_PHOTO_COUNT_MAX,mPhotoCountMax);
        startActivityForResult(intent,REQUEST_CODE_BIG_PHOTO);
    }

//    @Override
//    public void onEventMainThread(Object event) {
//        super.onEventMainThread(event);
//        if (event instanceof PhotoEvent) {
//            PhotoEvent photoEvent = (PhotoEvent) event;
//            int type = photoEvent.mType;
//            if (0 == type) {
//                List<Photo> selectedList = (List<Photo>) photoEvent.mMoInfoModel;
//                mPictureSelectedList.clear();
//                mPictureSelectedList.addAll(selectedList);
//                List<Photo> photos = getPhotoSelected(mPictures, mPictureSelectedList);
//                mPictures.clear();
//                mPictures.addAll(photos);
//                mPictureSelectedAdapter.notifyDataSetChanged();
//                updateBottomViewState();
//            } else if (1 == type) {
//                finish();
//            }
//        }
//    }
}
