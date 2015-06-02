package com.rabbit.imoocjson.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.rabbit.imoocjson.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Rabbit on 2015/6/1.
 */
public class ImageLoader {

    private ImageView mImageView;
    private String mUrl;
    private LruCache<String, Bitmap> mCache;
    private ListView mListView;
    private Set<BitmapAsyncTask> mTask;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl))
                mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };

    public ImageLoader(ListView listView) {
        mListView=listView;
        mTask=new HashSet<BitmapAsyncTask>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //获取最大可用内存
        int cacheSize = maxMemory / 4;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                // 每次存入内存时调用
                return value.getByteCount();
            }
        };
    }
    //获取显示在屏幕的n张图片url

  public void loadImages(int start,int end){

for (int i=start;i<end;i++){

    String url= ImoocAdapter.URLS[i];


    Bitmap bitmap = getBitmapFromCache(url);
    if (bitmap == null) {
//下载
       BitmapAsyncTask task= new BitmapAsyncTask(url);
        task.execute(url);
        mTask.add(task);

    } else {
//引用缓存的bitmap
        ImageView imageView= (ImageView) mListView.findViewWithTag(url);
        imageView.setImageBitmap(bitmap);
    }

}

  }

    public void addBitmapToCache(String url, Bitmap bitmap) {
        if (getBitmapFromCache(url) == null) {
            mCache.put(url, bitmap);

        }


    }

    public Bitmap getBitmapFromCache(String url) {
        return mCache.get(url);

    }

    public void getImageViewByThread(ImageView imageView, final String url) {
        mImageView = imageView;
        mUrl = url;
        new Thread() {


            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromURL(url);
                Message message = Message.obtain();
                message.obj = bitmap;

                mHandler.sendMessage(message);


            }
        }.start();

    }

    public Bitmap getBitmapFromURL(String urls) {
        Bitmap bitmap = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url1 = new URL(urls);

            conn = (HttpURLConnection) url1.openConnection();

            is = new BufferedInputStream(conn.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);


            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return null;
    }


    public void getImageViewByAsyncTask(ImageView imageview, String url) {
      Bitmap bitmap1 = getBitmapFromCache(url);
        //取出图片，并使用
        if (bitmap1 == null) {

            imageview.setImageResource(R.mipmap.ic_launcher);

       } else {

            imageview.setImageBitmap(bitmap1);
        }


    }

    public void cancelAllTasks() {
 if(mTask!=null){

     for(BitmapAsyncTask tasks:mTask){

         tasks.cancel(false);
     }
 }

    }

    class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

        String url1;


        BitmapAsyncTask( String url) {

            url1 = url;

        }


        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromURL(url);
            if (bitmap != null) {
                //缓存图片
                addBitmapToCache(url, bitmap);
            }

            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView= (ImageView) mListView.findViewWithTag(url1);
            if(imageView!=null&&bitmap!=null){
         //   if (imageView.getTag().equals(url1)) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
