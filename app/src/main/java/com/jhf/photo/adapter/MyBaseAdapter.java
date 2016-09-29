package com.jhf.photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyBaseAdapter<T> extends BaseAdapter
{
    protected List<T> mList = null;
    protected LayoutInflater mInflater = null;
    protected Context mContext = null;

    public MyBaseAdapter(Context context)
    {
        super();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mList = new ArrayList<T>();
    }

    public void setDatas(List<T> list)
    {
        if (list != null)
        {
            mList.clear();
            mList.addAll(list);
        }
    }

    public void setInitDates(List<T> list){
        if(null != list){
            mList = list;
        }
    }

    @Override
    public int getCount()
    {
        if (mList != null)
        {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position)
    {
        if (mList != null && position < mList.size()) {
            return mList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }
}
