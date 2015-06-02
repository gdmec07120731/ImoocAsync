package com.rabbit.imoocjson;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.rabbit.imoocjson.bean.ImoocAdapter;
import com.rabbit.imoocjson.bean.ImoocBean;
import com.rabbit.imoocjson.bean.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
private ListView listview;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        listview= (ListView) findViewById(R.id.id_layout_lv);
        new ImoocAsynctask().execute(Util.IMOOC_URL);
    }

    private List<ImoocBean> getJosnData(String url) {
        List<ImoocBean> imoocBeanList = new ArrayList<ImoocBean>();
        try {
            String jsonString = readStream(new URL(url).openStream());
            JsontoString(jsonString,imoocBeanList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imoocBeanList;

    }

    private void JsontoString(String jsonString,List<ImoocBean> imoocBeanList) {

        JSONObject jsonObject;
        ImoocBean imoocBean;
        try {
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = (JSONArray) jsonObject.get("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                imoocBean = new ImoocBean();
                imoocBean.picSmall = jsonObject.getString("picSmall");
                imoocBean.title = jsonObject.getString("name");
                imoocBean.content=jsonObject.getString("description");
                imoocBeanList.add(imoocBean);

            }
            Log.i("TAG",imoocBeanList.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String readStream(InputStream is) {

        InputStreamReader isr;
        String result = "";

        try {
            String line = "";
            isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            try {
                while ((line = br.readLine()) != null) {

                    result += line;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    class ImoocAsynctask extends AsyncTask<String, Void, List<ImoocBean>> {


        @Override
        protected List<ImoocBean> doInBackground(String... params) {


            return getJosnData(params[0]);
        }

        @Override
        protected void onPostExecute(List<ImoocBean> imoocBeans) {
            super.onPostExecute(imoocBeans);
            ImoocAdapter imoocAdapter=new ImoocAdapter(mContext,imoocBeans,listview);
            listview.setAdapter(imoocAdapter);


        }
    }
}






