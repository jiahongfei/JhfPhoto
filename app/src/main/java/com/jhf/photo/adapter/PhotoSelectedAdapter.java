package com.jhf.photo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jhf.photo.R;
import com.jhf.photo.entity.Photo;
import com.jhf.photo.util.ImageLoaderUtil;
import com.jhf.photo.util.ScreenUtils;

import java.io.File;

public class PhotoSelectedAdapter extends MyBaseAdapter<Photo> {

    public static final int FLAG_SELECTED = 0;
    public static final int FLAG_SHOW = 1;

    private View.OnClickListener mSelectedOnClickListener;
    private int mBounds = 0;
    private int mFlag = 0;

    /**
     * 展示图片GridView
     *
     * @param context
     * @param flag
     */
    public PhotoSelectedAdapter(Context context, int flag) {
        super(context);
        int[] bounds = ScreenUtils.getScreenBounds(mContext);
        mBounds = (bounds[0] - ScreenUtils.dipToPx(mContext, 10)) / 4;
        mFlag = flag;
    }

    public PhotoSelectedAdapter(Context context) {
        this(context, FLAG_SELECTED);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_photo_selected, null);
        }

        final Photo picture = mList.get(position);

        final ImageView selectImageView = (ImageView) convertView
                .findViewById(R.id.selectImageView);
        selectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mSelectedOnClickListener) {
                    v.setTag(position);
                    mSelectedOnClickListener.onClick(v);
                }
            }
        });
        selectImageView.setSelected(picture.isSelected());

        ImageView photoImageView = (ImageView) convertView
                .findViewById(R.id.photoImageView);
        photoImageView.setLayoutParams(new RelativeLayout.LayoutParams(mBounds,
                mBounds));

        String photoUri = picture.getThumbPath();
        if (TextUtils.isEmpty(photoUri) || !new File(photoUri).exists()) {
            // 没有缩略图
            photoUri = picture.getNativePicturePath();

        } else {
            // 有缩略图
            photoUri = picture.getNativeThumbPath();
        }
        //设置图片
        ImageLoaderUtil.setThumbnailPhoto(photoUri, photoImageView, R.drawable.iv_product_detail_top);

        ViewGroup itemLayout = (ViewGroup) convertView
                .findViewById(R.id.itemLayout);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(mBounds,
                mBounds));

        if(FLAG_SHOW == mFlag){
            selectImageView.setVisibility(View.GONE);
        }else{
            selectImageView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void setSelectedOnClickListener(View.OnClickListener onClickListener) {
        mSelectedOnClickListener = onClickListener;
    }

    public void clearAllSelected() {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelected(false);
        }
    }

}
