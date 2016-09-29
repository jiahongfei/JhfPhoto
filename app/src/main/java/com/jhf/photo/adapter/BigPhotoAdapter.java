package com.jhf.photo.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.jhf.photo.R;
import com.jhf.photo.entity.Photo;
import com.jhf.photo.util.ImageLoaderUtil;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jiahongfei on 16/8/30.
 */
public class BigPhotoAdapter extends PagerAdapter implements PhotoViewAttacher.OnViewTapListener{

    private List<Photo> mPhotoList;
    private Context mContext;
    private PhotoViewAttacher mAttacher;
    private List<ImageView> imageViews;

    public BigPhotoAdapter(Context context, List<Photo> photos) {
        mContext = context;
        mPhotoList = photos;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = container.inflate(mContext,
                R.layout.item_photo_view,null);
        Photo photo = mPhotoList.get(position);

        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo);
        String url = "";
        if(TextUtils.isEmpty(photo.getImageUrl())){
            url = photo.getNativePicturePath();
        }else{
            url = photo.getImageUrl();
        }
        ImageLoaderUtil.setBigPhoto(url,photoView,R.drawable.iv_product_detail_top);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mAttacher = null;
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onViewTap(View view, float x, float y) {

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
