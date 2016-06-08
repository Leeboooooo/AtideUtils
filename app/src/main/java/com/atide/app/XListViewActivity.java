package com.atide.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.atide.ui.XListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * XListView demo
 *
 * from @author markmjw
 * @date 2013-10-08
 */
public class XListViewActivity extends Activity
        implements XListView.IXListViewListener {
    private XListView mListView;

    private DataAdapter mAdapter;
    private Handler mHandler;


    private ArrayList<ClassItem> dataProvider=null, sourceList=null;

    public static void launch(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, XListViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_act_list_view);

        geneItems();
        init();
    }

    private void init() {
        mHandler = new Handler();

        mListView = (XListView) findViewById(R.id.list_view);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(getTime());

        mAdapter = new DataAdapter();
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mListView.autoRefresh();
        }
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mIndex = ++mRefreshIndex;
                dataProvider.clear();
                geneItems();
                mAdapter = new DataAdapter();
                mListView.setAdapter(mAdapter);
                onLoad();
            }
        }, 2500);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                geneItems();
                mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2500);
    }

    private void geneItems() {
        if(null == dataProvider)
            dataProvider = new ArrayList<ClassItem>();
        if(null == sourceList) {
            sourceList = new ArrayList<ClassItem>();
            for(int i=0; i<13; i++){
                ClassItem item = new ClassItem("neo", "man", "New York", "Lanxiang Vocational School");
                item.setNum(i+1);
                sourceList.add(item);
            }
        }

        int pageSize = 4;
        int dSize = dataProvider.size();
        int sSize = sourceList.size();
        for(int i=0; i<pageSize; i++){
            if(dSize < sSize){
                if(dSize+i < sSize){
                    dataProvider.add(sourceList.get(dSize+i));
                }else{
                    return;
                }
            }else{
                return;
            }
        }
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(getTime());
    }

    private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }

    private class ClassItem{
        private String num;
        private String name;
        private String city;
        private String sex;
        private String school;

        public ClassItem(String name, String sex,
                         String city, String school){
            this.name = name;
            this.sex = sex;
            this.city = city;
            this.school = school;
        }

        public void setNum(int i){
            this.num = i+"";
        }
    }

    private class DataAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return dataProvider.size();
        }

        @Override
        public Object getItem(int i) {
            return dataProvider.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null){
                view = LayoutInflater.from(XListViewActivity.this)
                        .inflate(R.layout.test_view_refresh_list_item, null);
                holder = new ViewHolder();
                holder.numtxt = (TextView) view.findViewById(R.id.num_txt);
                holder.nameTxt = (TextView) view.findViewById(R.id.name_txt);
                holder.sexTxt = (TextView) view.findViewById(R.id.sex_txt);
                holder.cityTxt = (TextView) view.findViewById(R.id.city_txt);
                holder.schoolTxt = (TextView) view.findViewById(R.id.school_txt);

                view.setTag(holder);
            }else{
                holder = (ViewHolder) view.getTag();
            }
            ClassItem item = dataProvider.get(i);
            holder.numtxt.setText(item.num);
            holder.nameTxt.setText(item.name);
            holder.sexTxt.setText(item.sex);
            holder.cityTxt.setText(item.city);
            holder.schoolTxt.setText(item.school);
            return view;
        }

        private class ViewHolder{
            public TextView numtxt;
            public TextView nameTxt;
            public TextView sexTxt;
            public TextView cityTxt;
            public TextView schoolTxt;

        }
    }
}
