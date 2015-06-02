package com.rabbit.imoocjson.bean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rabbit.imoocjson.R;

import java.util.List;

/**
 * Created by Rabbit on 2015/6/1.
 */
public class ImoocAdapter extends ArrayAdapter<ImoocBean> implements AbsListView.OnScrollListener {
    private LayoutInflater inflater;
    private List<ImoocBean> mList;
    private ImageLoader mImageViewLoader;
    private int mStart, mEnd;
    public static String[] URLS;
    private boolean isFirstLoad = true;

    public ImoocAdapter(Context context, List<ImoocBean> list, ListView listview) {
        super(context, -1, list);
        inflater = LayoutInflater.from(context);
        mList = list;
        mImageViewLoader = new ImageLoader(listview);
        URLS = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            URLS[i] = list.get(i).picSmall;
        }
        listview.setOnScrollListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_imooc, null);
            holder = new ViewHolder();
            holder.picSmall = (ImageView) convertView.findViewById(R.id.id_item_icon);
            holder.title = (TextView) convertView.findViewById(R.id.id_item_title);
            holder.context = (TextView) convertView.findViewById(R.id.id_item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String picUrl = mList.get(position).picSmall;
        holder.picSmall.setTag(picUrl);

        //线程加载图片没有缓存，会错位
        //  mImageViewLoader.getImageViewByThread(holder.picSmall, mList.get(position).picSmall);
        //Asynctask加载图片 缓存了
        mImageViewLoader.getImageViewByAsyncTask(holder.picSmall, mList.get(position).picSmall);
        holder.title.setText(mList.get(position).title.toString());
        Log.i("TAg", mList.get(position).title.toString());
        holder.context.setText(mList.get(position).content.toString());


        return convertView;
    }

    //初始化没有调用
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            //开始加载
            mImageViewLoader.loadImages(mStart, mEnd);
        } else {
            //停止加载
            mImageViewLoader.cancelAllTasks();
        }
    }

    //初始化就调用
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //第一次显示使用
        if (isFirstLoad && visibleItemCount > 0) {
            mImageViewLoader.loadImages(mStart, mEnd);
            isFirstLoad = false;
        }
    }

    class ViewHolder {
        ImageView picSmall;
        TextView title;
        TextView context;

    }
}
