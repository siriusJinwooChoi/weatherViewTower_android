package com.example.project_weather.control;
/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering,  201003629
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.project_weather.R;

import java.util.ArrayList;

public class ControlActivity extends Activity {
    private int citytemp, cityhumi, temp, humi;
    private String cityname, urladdress;

    ArrayList<Control> weathers = new ArrayList<Control>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        Intent intent = getIntent();

        cityname = intent.getStringExtra("cityname");
        citytemp = intent.getIntExtra("citytemp", 0);
        cityhumi = intent.getIntExtra("cityhumi", 0);
        temp = intent.getIntExtra("temp", 0);
        humi = intent.getIntExtra("humi", 0);
        urladdress = intent.getStringExtra("address");

        weathers.add(new Control("sun", R.drawable.sunny, "맑음", cityname, "맑은 날씨에 대한 뷰 타워 설정", citytemp, cityhumi, temp, humi));
        weathers.add(new Control("clo", R.drawable.cloud, "흐림", cityname, "흐린 날씨에 대한 뷰 타워 설정", citytemp, cityhumi, temp, humi));
        weathers.add(new Control("rai", R.drawable.rain, "비", cityname, "비 오는 날씨에 대한 뷰 타워 설정", citytemp, cityhumi, temp, humi));
        weathers.add(new Control("sno", R.drawable.snow, "눈", cityname, "눈 오는 날씨에 대한 뷰 타워 설정", citytemp, cityhumi, temp, humi));
        weathers.add(new Control("thu", R.drawable.thunder, "천둥/번개", cityname, "천둥/번개 에 대한 뷰 타워 설정", citytemp, cityhumi, temp, humi));
        weathers.add(new Control("dri", R.drawable.drizzle, "안개", cityname, "안개에 대한 뷰 타워 설정", citytemp, cityhumi, temp, humi));
        weathers.add(new Control("ext", R.drawable.extreme, "기상이변", cityname, "기상 이변이 일어났을 때 뷰 타워 설정", citytemp, cityhumi, temp, humi));

        Log.d("univ", weathers.toString());

        ListView list = (ListView)findViewById(R.id.list);      //list뷰를 불러옴.
        ControlAdapter adt = new ControlAdapter(ControlActivity.this, weathers, R.layout.weatherlist);
        list.setAdapter(adt);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (Control c : weathers) {
                    if (c.weather == id) {
                        Intent intent = new Intent(ControlActivity.this, SubControlActivity.class);
                        intent.putExtra("code", c.code);
                        intent.putExtra("weather", c.weather);
                        intent.putExtra("name", c.name);
                        intent.putExtra("cityname", c.cityname);
                        intent.putExtra("intro", c.intro);
                        intent.putExtra("temp", c.temp);
                        intent.putExtra("humidity", c.humi);
                        intent.putExtra("inner_temp", c.intemp);
                        intent.putExtra("inner_humidity", c.inhumi);
                        intent.putExtra("position", position);
                        intent.putExtra("urladdress", urladdress);

                        startActivity(intent);
                        break;
                    }
                }
            }
        });
    }
}