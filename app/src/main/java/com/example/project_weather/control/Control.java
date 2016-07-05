package com.example.project_weather.control;
/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering,  201003629
 */
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by edu on 2016-04-06.
 */
public class Control extends BaseAdapter {
    String code, name, cityname, intro;
    int weather;        //이미지 값은 int로 저장되어 있음.
    double temp, humi, intemp, inhumi;

    public Control(String code, int weather, String name, String cityname, String intro, double temp, double humi, double intemp, double inhumi) {
        super();
        this.code = code;
        this.weather = weather;
        this.name = name;
        this.cityname = cityname;
        this.intro = intro;
        this.temp = temp;
        this.humi = humi;
        this.intemp = intemp;
        this.inhumi = inhumi;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}