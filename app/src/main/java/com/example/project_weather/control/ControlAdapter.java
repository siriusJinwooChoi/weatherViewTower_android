package com.example.project_weather.control;
/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering,  201003629
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project_weather.R;

import java.util.ArrayList;

/**
 * Created by edu on 2016-04-06.
 */
public class ControlAdapter extends BaseAdapter {
    Context context;
    ArrayList<Control> data;
    int layout;

    ControlAdapter(Context context, ArrayList<Control> data, int layout) {
        this.context = context;
        this.data = data;
        this.layout = layout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linear = null;
        ImageView weather = null;
        TextView name = null;
        TextView intro = null;

        if(convertView != null) {           //linear layout이 넘어올떄,(넘어온다는 뜻)
            linear = (LinearLayout)convertView;
            weather = (ImageView)linear.findViewById(R.id.weather);
            name = (TextView)linear.findViewById(R.id.name);
            intro = (TextView)linear.findViewById(R.id.intro);
        } else {
            linear = (LinearLayout)View.inflate(context, layout, null);
            weather = (ImageView)linear.findViewById(R.id.weather);
            name = (TextView)linear.findViewById(R.id.name);
            intro = (TextView)linear.findViewById(R.id.intro);
        }
        Control c = data.get(position);
        weather.setImageResource(c.weather);
        name.setText(c.name);
        intro.setText(c.intro);

        return linear;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /*@Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }*/

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).weather;             //국기(long타입이므로)를 얻어옴
    }
}