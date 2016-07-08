package com.example.project_weather.kakao;
/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering)
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

public class KakaoActivity extends Activity {
    private int weatherid;
    private String total, msg;
    //kakaAPI
    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        weatherid = intent.getIntExtra("weather", 0);

        //Kkao API 이용하여 보내주는 부분(링크 전송)
        try {
            if((weatherid >= 700) && (weatherid <= 800)) {
                msg="현재 날씨가 아주 맑습니다. 즐거운 하루 되세요:)";
            } else if((weatherid >= 200) && (weatherid < 250)) {
                msg="현재 천둥/번개가 치고있습니다. 벼락에 주의하세요!";
            } else if((weatherid >= 300) && (weatherid < 350)) {
                msg="현재 안개가 뿌옇습니다. 운전길 주의하세요.";
            } else if((weatherid >= 500) && (weatherid < 550)) {
                msg="현재 비가 내리고 있습니다. 우산을 꼭 챙겨가세요!";
            } else if((weatherid >= 600) && (weatherid < 650)) {
                msg="현재 눈이 내리고 있습니다. 눈길 조심하세요!";
            } else if((weatherid > 800) && (weatherid < 810)) {
                msg="현재 구름이 다소 있습니다. 좋은 하루 되세요!";
            }  else if((weatherid >= 900) && (weatherid < 910)) {
                msg="현재 기상상황이 매우 좋지 않습니다. 주의하세요!!";
            }
            //kakao 객체를 초기화
            kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            //실제 메세지를 전송
            kakaoTalkLinkMessageBuilder.addText(msg);
            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

        } catch (KakaoParameterException e) {
            Log.e("error", e.getMessage());
        }
        finish();
    }
}
