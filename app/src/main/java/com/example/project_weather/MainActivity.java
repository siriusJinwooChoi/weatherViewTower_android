package com.example.project_weather;
/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_weather.control.ControlActivity;
import com.example.project_weather.kakao.KakaoActivity;
import com.example.project_weather.naverTalk.NaverTalkActivity;
import com.example.project_weather.sms.SMSActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {
    //위쪽 화면 뷰 구성
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    private String cityname, weathermain, total, kocityname;
    private String ipaddr, portnum, urladdr, urladdrmain;
    private int citys, whichcity, designflag = 0;

    private int weatherid, citytemp, cityhumi, citytemp_min, citytemp_max, citywind, temp, humi;
    private String citymain9, citymain12, citymain15, citymain18, citymain21;
    private int cityhumi9, citytemp9, cityid9, cityhumi12, citytemp12, cityid12, cityhumi15, citytemp15, cityid15, cityhumi18, citytemp18, cityid18, cityhumi21, citytemp21, cityid21;

    private SendComandTask sendCommandTask;

    //날짜, 시간정보 받아오기
    SimpleDateFormat CurDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    SimpleDateFormat CurTimeFormat = new SimpleDateFormat("HH시 mm분");
    SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat CurMonthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat CurDayFormat = new SimpleDateFormat("dd");
    SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
    SimpleDateFormat CurMinuteFormat = new SimpleDateFormat("mm");

    // 지정된 포맷으로 String 타입 리턴
    String strCurDate = CurDateFormat.format(date);
    String strCurTime = CurTimeFormat.format(date);
    String strCurYear = CurYearFormat.format(date);
    String strCurMonth = CurMonthFormat.format(date);
    String strCurDay = CurDayFormat.format(date);
    String strCurHour = CurHourFormat.format(date);
    String strCurMinute = CurMinuteFormat.format(date);

    TextView t_cityinfo, t_temphumi, t_weatherinfo, t_minmaxtemp, t_wind, t_time1, t_time2, t_time3, t_time4, t_time5, t_temp1, t_temp2, t_temp3, t_temp4, t_temp5;
    ImageView icon0, icon9, icon12, icon15, icon18, icon21;
    EditText ipedit, portedit;

    private static final int DIALOG_CITY_CHOICE = 1;
    private static final int DIALOG_SINGLE_CHOICE = 5;
    private static final int DIALOG_TEXT_ENTRY = 7;

    private Button voiceBtn, controlBtn, pushBtn, exitBtn;
    private int dialogflag=0;

    //2번 back누르면 종료시키도록
    private boolean isBackKeyPressed = false;             // flag
    private long currentTimeByMillis = 0;                     // calculate time interval
    private static final int MSG_TIMER_EXPIRED = 1;    // switch - key
    private static final int BACKKEY_TIMEOUT = 2;      // define interval
    private static final int MILLIS_IN_SEC = 1000;        // define millisecond

    Handler handler = new Handler() {
        /*public void handleMessage(android.os.Message msg) {
        }*/
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, SplashActivity.class));

        showDialog(DIALOG_TEXT_ENTRY);

        voiceBtn = (Button)findViewById(R.id.btn_voice);
        controlBtn = (Button)findViewById(R.id.btn_control);
        //viewBtn = (Button)findViewById(R.id.btn_view);
        pushBtn = (Button)findViewById(R.id.btn_push);
        exitBtn = (Button)findViewById(R.id.btn_exit);

        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NaverTalkActivity.class);
                intent.putExtra("address", urladdr);
                startActivity(intent);
            }
        });

        controlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                intent.putExtra("cityname", kocityname);
                intent.putExtra("citytemp", citytemp);
                intent.putExtra("cityhumi", cityhumi);
                intent.putExtra("temp", temp);
                intent.putExtra("humi", humi);
                intent.putExtra("address", urladdr);
                startActivity(intent);
            }
        });

        pushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SINGLE_CHOICE);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Weahter ViewTower 종료")
                        .setMessage("앱을 종료하시겠습니까?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "애플리케이션이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("NO", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        icon0 = (ImageView)findViewById(R.id.main_img);
        icon9 = (ImageView)findViewById(R.id.img1);
        icon12 = (ImageView)findViewById(R.id.img2);
        icon15 = (ImageView)findViewById(R.id.img3);
        icon18 = (ImageView)findViewById(R.id.img4);
        icon21 = (ImageView)findViewById(R.id.img5);


        t_cityinfo = (TextView)findViewById(R.id.txt_cityinfo);     //도시정보
        t_temphumi = (TextView)findViewById(R.id.txt_temphumi);     //온/습도
        t_weatherinfo = (TextView)findViewById(R.id.txt_weatherinfo);   //날씨정보
        t_minmaxtemp = (TextView)findViewById(R.id.txt_minmaxtemp);     //최고/최저온도 + 내부 온/습도
        t_wind = (TextView)findViewById(R.id.txt_wind);                 //풍속 + 현재 날짜+시간

        t_time1 = (TextView)findViewById(R.id.txt_time1);                 //시간별 9시
        t_time2 = (TextView)findViewById(R.id.txt_time2);                 //시간별 12시
        t_time3 = (TextView)findViewById(R.id.txt_time3);                 //시간별 15시
        t_time4 = (TextView)findViewById(R.id.txt_time4);                 //시간별 18시
        t_time5 = (TextView)findViewById(R.id.txt_time5);                 //시간별 21시

        t_temp1 = (TextView)findViewById(R.id.txt_temp1);                 //온/습도 9시
        t_temp2 = (TextView)findViewById(R.id.txt_temp2);                 //온/습도 12시
        t_temp3 = (TextView)findViewById(R.id.txt_temp3);                 //온/습도 15시
        t_temp4 = (TextView)findViewById(R.id.txt_temp4);                 //온/습도 18시
        t_temp5 = (TextView)findViewById(R.id.txt_temp5);                 //온/습도 21시
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Resources res = getResources();
        String[] titles= res.getStringArray(R.array.select_dialog_pushchoice);
        switch (id) {
            case DIALOG_CITY_CHOICE:
                return new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.select_city)
                        .setItems(R.array.select_dialog_city, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_city);
                                new AlertDialog.Builder(MainActivity.this)
                                        .setMessage("해당 도시를 선택하였습니다.")
                                        .show();

                                whichcity = which;

                                urladdrmain = urladdr + "/selcity?id="+whichcity;

                                sendCommandTask = new SendComandTask();
                                sendCommandTask.execute();

                                Log.d("urladdmainr=", urladdrmain);
                            }
                        })
                        .create();

            case DIALOG_SINGLE_CHOICE:
                return new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.dialog_kakao)
                        .setTitle(R.string.alert_dialog_single_choice)
                        .setSingleChoiceItems(R.array.select_dialog_pushchoice, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialogflag = whichButton;
                            }
                        })
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(dialogflag == 0) {
                                    Intent intent = new Intent(MainActivity.this, KakaoActivity.class);
                                    intent.putExtra("weather", weatherid);
                                    startActivity(intent);
                                }
                                else if(dialogflag == 1) {
                                    Intent intent = new Intent(MainActivity.this, SMSActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create();
            case DIALOG_TEXT_ENTRY:
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
                return new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.alert_dialog_icon)
                        .setTitle(R.string.alert_dialog_text_entry)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ipedit = (EditText)((AlertDialog)dialog).findViewById(R.id.ipaddr_edit);
                                portedit = (EditText)((AlertDialog)dialog).findViewById(R.id.port_edit);

                                ipaddr = ipedit.getText().toString();
                                portnum = portedit.getText().toString();
                                System.out.println("ipaddr="+ipaddr);
                                System.out.println("portnum="+portnum);
                                urladdr = "http://"+ipaddr+":"+portnum;

                                showDialog(DIALOG_CITY_CHOICE);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(MainActivity.this, "IP와 Port를 입력하지 않아 종료됩니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .create();
        }
        return null;
    }

    //back버튼을 두번 누르면 종료되도록 한다.
    @Override
    public void onBackPressed(){
        if ( isBackKeyPressed == false ){
            // first click
            isBackKeyPressed = true;

            currentTimeByMillis = Calendar.getInstance().getTimeInMillis();
            Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            startTimer();
        }else{
            // second click : 2초 이내면 종료! 아니면 아무것도 안한다.
            isBackKeyPressed = false;
            if ( Calendar.getInstance().getTimeInMillis() <= (currentTimeByMillis + (BACKKEY_TIMEOUT * MILLIS_IN_SEC)) ) {
                finish();
            }
        }
    }

    // startTimer : 2초의 시간적 여유를 가지게 delay 시킨다.
    private void startTimer(){
        backTimerHandler.sendEmptyMessageDelayed(MSG_TIMER_EXPIRED, BACKKEY_TIMEOUT * MILLIS_IN_SEC);
    }

    private Handler backTimerHandler = new Handler(){
        public void handleMessage(Message msg){
            switch( msg.what ){
                case MSG_TIMER_EXPIRED:{
                    isBackKeyPressed = false;
                }
                break;
            }
        }
    };

    //Asynctask로 서버 URL에 접속하여 해당 날씨 정보들을 받아오도록 한다.!
    public class SendComandTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL(urladdrmain);     //server 주소에 보내주겠다는 뜻.
                //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());

                total = br.readLine();

                try {
                    JSONObject jarray = new JSONObject(total);   // JSONArray 생성
                    cityname = jarray.getString("cityname");
                    weatherid = jarray.getInt("weatherid");
                    weathermain = jarray.getString("weathermain");
                    citytemp = jarray.getInt("citytemp");
                    cityhumi = jarray.getInt("cityhumi");
                    citytemp_min = jarray.getInt("citytemp_min");
                    citytemp_max = jarray.getInt("citytemp_max");
                    citywind = jarray.getInt("citywind");
                    temp = jarray.getInt("temp");
                    humi = jarray.getInt("humi");

                    citytemp9 = jarray.getInt("citytemp9");
                    cityhumi9 = jarray.getInt("cityhumi9");
                    citymain9 = jarray.getString("citymain9");
                    cityid9 = jarray.getInt("cityid9");

                    citymain12 = jarray.getString("citymain12");
                    cityid12 = jarray.getInt("cityid12");
                    citytemp12 = jarray.getInt("citytemp12");
                    cityhumi12 = jarray.getInt("cityhumi12");

                    citymain15 = jarray.getString("citymain15");
                    cityid15 = jarray.getInt("cityid15");
                    citytemp15 = jarray.getInt("citytemp15");
                    cityhumi15 = jarray.getInt("cityhumi15");

                    citymain18 = jarray.getString("citymain18");
                    cityid18 = jarray.getInt("cityid18");
                    citytemp18 = jarray.getInt("citytemp18");
                    cityhumi18 = jarray.getInt("cityhumi18");

                    citymain21 = jarray.getString("citymain21");
                    cityid21 = jarray.getInt("cityid21");
                    citytemp21 = jarray.getInt("citytemp21");
                    cityhumi21 = jarray.getInt("cityhumi21");

                    if(cityname.equals("Seoul")) {
                        kocityname = "서울";
                    } else if(cityname.equals("Suigen")) {
                        kocityname = "수원";
                    } else if(cityname.equals("Incheon")) {
                        kocityname = "인천";
                    } else if(cityname.equals("Daejeon")) {
                        kocityname = "대전";
                    } else if(cityname.equals("Changwon")) {
                        kocityname = "창원";
                    } else if(cityname.equals("Anseong")) {
                        kocityname = "안성";
                    } else if(cityname.equals("Wabu")) {
                        kocityname = "남양주";
                    } else if(cityname.equals("Andong")) {
                        kocityname = "안동";
                    }
                    else if(cityname.equals( "Tokyo")) {
                        kocityname = "도쿄";
                    } else if(cityname.equals("Osaka-shi")) {
                        kocityname = "오사카";
                    } else if(cityname.equals("Fukuoka-shi")) {
                        kocityname = "후쿠오카";
                    } else if(cityname.equals("Yokohama-shi")) {
                        kocityname = "요코하마";
                    } else if(cityname.equals("Kawagoe")) {
                        kocityname = "가와고에";
                    } else if(cityname.equals("Kobe-shi")) {
                        kocityname = "고베";
                    } else if(cityname.equals("Hadano")) {
                        kocityname = "하다노";
                    } else if(cityname.equals("Chiba-shi")) {
                        kocityname = "지바";
                    } else if(cityname.equals("kagoshima")) {
                        kocityname = "가고시마";
                    } else if(cityname.equals("Washington, D. C.")) {
                        kocityname = "워싱턴(미국)";
                    } else if(cityname.equals("Chicago")) {
                        kocityname = "시카고(미국)";
                    } else if(cityname.equals("Los Angeles")) {
                        kocityname = "LA(미국)";
                    } else if(cityname.equals("San Francisco")) {
                        kocityname = "샌프란시스코(미국)";
                    } else if(cityname.equals("New York")) {
                        kocityname = "뉴욕(미국)";
                    } else if(cityname.equals("Seattle")) {
                        kocityname = "시애틀(미국)";
                    } else if(cityname.equals("Boston")) {
                        kocityname = "보스턴(미국)";
                    } else if(cityname.equals("Las Vegas")) {
                        kocityname = "라스베이거스(미국)";
                    } else if(cityname.equals("Beijing")) {
                        kocityname = "베이징(중국)";
                    } else if(cityname.equals("shanghai")) {
                        kocityname = "상하이(중국)";
                    } else if(cityname.equals("Tianjin")) {
                        kocityname = "텐진(중국)";
                    } else if(cityname.equals("Hong Kong")) {
                        kocityname = "홍콩(중국)";
                    } else if(cityname.equals("Guangzhou")) {
                        kocityname = "광저우(중국)";
                    } else if(cityname.equals("Chongqing")) {
                        kocityname = "충칭(중국)";
                    } else if(cityname.equals("London")) {
                        kocityname = "런던(영국)";
                    } else if(cityname.equals("Paris")) {
                        kocityname = "파리(프랑스)";
                    } else if(cityname.equals("Roma")) {
                        kocityname = "로마(이탈리아)";
                    } else if(cityname.equals("Milano")) {
                        kocityname = "밀라노(이탈리아)";
                    } else if(cityname.equals("Venice")) {
                        kocityname = "베니스(이탈리아)";
                    } else if(cityname.equals("Madrid")) {
                        kocityname = "마드리드(스페인)";
                    } else if(cityname.equals("Istanbul")) {
                        kocityname = "이스탄불(터키)";
                    } else if(cityname.equals("Bangkok")) {
                        kocityname = "방콕(태국)";
                    }

                    //날씨코드에 따라 weathermain에 저장된 영어를 한글로 바꿔주는 작업.
                    if ((weatherid >= 700) && (weatherid <= 800)) {
                        weathermain = "맑음";
                        designflag = 700;
                    } else if ((weatherid >= 200) && (weatherid < 250)) {
                        weathermain = "천둥/번개";
                        designflag = 200;
                    } else if ((weatherid >= 300) && (weatherid < 350)) {
                        weathermain = "안개";
                        designflag = 300;
                    } else if ((weatherid >= 500) && (weatherid < 550)) {
                        weathermain = "비";
                        designflag = 500;
                    } else if ((weatherid >= 600) && (weatherid < 650)) {
                        weathermain = "눈";
                        designflag = 600;
                    } else if ((weatherid > 800) && (weatherid < 810)) {
                        weathermain = "구름";
                        designflag = 800;
                    } else if ((weatherid >= 900) && (weatherid < 910)) {
                        weathermain = "기상이변";
                        designflag = 900;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final LinearLayout designlayout = (LinearLayout)findViewById(R.id.designlayout);
            super.onPostExecute(aVoid);
            if (designflag == 700) {
                designlayout.setBackgroundResource(R.drawable.sunny);
            } else if (designflag == 800) {
                designlayout.setBackgroundResource(R.drawable.cloud);
            } else if (designflag == 200) {
                designlayout.setBackgroundResource(R.drawable.thunder);
            } else if (designflag == 300) {
                designlayout.setBackgroundResource(R.drawable.drizzle);
            } else if (designflag == 500) {
                designlayout.setBackgroundResource(R.drawable.rain);
            } else if (designflag == 600) {
                designlayout.setBackgroundResource(R.drawable.snow);
            } else if (designflag == 900) {
                designlayout.setBackgroundResource(R.drawable.extreme);
            }

            t_cityinfo.setText(kocityname);
            t_temphumi.setText(citytemp + "'C" + "/" + cityhumi + "%");
            t_weatherinfo.setText(weathermain);
            t_minmaxtemp.setText("최저/최고 온도" + "\n" + "   : " + citytemp_min + "'C" + "/" + citytemp_max + "'C" + "\n" + "내부 온/습도" + "\n" + "   : " + temp + "'C /" + humi + "%");
            t_wind.setText("풍속:" + citywind + "m/s" + "\n\n" + strCurMonth + "월 " + strCurDay + "일 " + strCurHour + "시");

            //현재 기상상태에 따라서 main 날씨 이미지를 넣어줌.
            if ((weatherid >= 700) && (weatherid <= 800)) {
                icon0.setImageResource(R.drawable.sunicon);
            } else if ((weatherid >= 200) && (weatherid < 250)) {
                icon0.setImageResource(R.drawable.thundericon);
            } else if ((weatherid >= 300) && (weatherid < 350)) {
                icon0.setImageResource(R.drawable.drizzleicon);
            } else if ((weatherid >= 500) && (weatherid < 550)) {
                icon0.setImageResource(R.drawable.raindropicon);
            } else if ((weatherid >= 600) && (weatherid < 650)) {
                icon0.setImageResource(R.drawable.snowicon);
            } else if ((weatherid > 800) && (weatherid < 810)) {
                icon0.setImageResource(R.drawable.cloudicon);
            } else if ((weatherid >= 900) && (weatherid < 910)) {
                icon0.setImageResource(R.drawable.extremeicon);
            }
            //9시의 기상 상태에 따라서 날씨 이미지를 넣어줌.
            if ((cityid9 >= 700) && (cityid9 <= 800)) {
                icon9.setImageResource(R.drawable.sunicon);
            } else if ((cityid9 >= 200) && (cityid9 < 250)) {
                icon9.setImageResource(R.drawable.thundericon);
            } else if ((cityid9 >= 300) && (cityid9 < 350)) {
                icon9.setImageResource(R.drawable.drizzleicon);
            } else if ((cityid9 >= 500) && (cityid9 < 550)) {
                icon9.setImageResource(R.drawable.raindropicon);
            } else if ((cityid9 >= 600) && (cityid9 < 650)) {
                icon9.setImageResource(R.drawable.snowicon);
            } else if ((cityid9 > 800) && (cityid9 < 810)) {
                icon9.setImageResource(R.drawable.cloudicon);
            } else if ((cityid9 >= 900) && (cityid9 < 910)) {
                icon9.setImageResource(R.drawable.extremeicon);
            }

            //12시의 기상 상태에 따라서 날씨 이미지를 넣어줌.
            if ((cityid12 >= 700) && (cityid12 <= 800)) {
                icon12.setImageResource(R.drawable.sunicon);
            } else if ((cityid12 >= 200) && (cityid12 < 250)) {
                icon12.setImageResource(R.drawable.thundericon);
            } else if ((cityid12 >= 300) && (cityid12 < 350)) {
                icon12.setImageResource(R.drawable.drizzleicon);
            } else if ((cityid12 >= 500) && (cityid12 < 550)) {
                icon12.setImageResource(R.drawable.raindropicon);
            } else if ((cityid12 >= 600) && (cityid12 < 650)) {
                icon12.setImageResource(R.drawable.snowicon);
            } else if ((cityid12 > 800) && (cityid12 < 810)) {
                icon12.setImageResource(R.drawable.cloudicon);
            } else if ((cityid12 >= 900) && (cityid12 < 910)) {
                icon12.setImageResource(R.drawable.extremeicon);
            }

            //15시의 기상 상태에 따라서 날씨 이미지를 넣어줌.
            if ((cityid15 >= 700) && (cityid15 <= 800)) {
                icon15.setImageResource(R.drawable.sunicon);
            } else if ((cityid15 >= 200) && (cityid15 < 250)) {
                icon15.setImageResource(R.drawable.thundericon);
            } else if ((cityid15 >= 300) && (cityid15 < 350)) {
                icon15.setImageResource(R.drawable.drizzleicon);
            } else if ((cityid15 >= 500) && (cityid15 < 550)) {
                icon15.setImageResource(R.drawable.raindropicon);
            } else if ((cityid15 >= 600) && (cityid15 < 650)) {
                icon15.setImageResource(R.drawable.snowicon);
            } else if ((cityid15 > 800) && (cityid15 < 810)) {
                icon15.setImageResource(R.drawable.cloudicon);
            } else if ((cityid15 >= 900) && (cityid15 < 910)) {
                icon15.setImageResource(R.drawable.extremeicon);
            }

            //18시의 기상 상태에 따라서 날씨 이미지를 넣어줌.
            if ((cityid18 >= 700) && (cityid18 <= 800)) {
                icon18.setImageResource(R.drawable.sunicon);
            } else if ((cityid18 >= 200) && (cityid18 < 250)) {
                icon18.setImageResource(R.drawable.thundericon);
            } else if ((cityid18 >= 300) && (cityid18 < 350)) {
                icon18.setImageResource(R.drawable.drizzleicon);
            } else if ((cityid18 >= 500) && (cityid18 < 550)) {
                icon18.setImageResource(R.drawable.raindropicon);
            } else if ((cityid18 >= 600) && (cityid18 < 650)) {
                icon18.setImageResource(R.drawable.snowicon);
            } else if ((cityid18 > 800) && (cityid18 < 810)) {
                icon18.setImageResource(R.drawable.cloudicon);
            } else if ((cityid18 >= 900) && (cityid18 < 910)) {
                icon18.setImageResource(R.drawable.extremeicon);
            }

            //21시의 기상 상태에 따라서 날씨 이미지를 넣어줌.
            if ((cityid21 >= 700) && (cityid21 <= 800)) {
                icon21.setImageResource(R.drawable.sunicon);
            } else if ((cityid21 >= 200) && (cityid21 < 250)) {
                icon21.setImageResource(R.drawable.thundericon);
            } else if ((cityid21 >= 300) && (cityid21 < 350)) {
                icon21.setImageResource(R.drawable.drizzleicon);
            } else if ((cityid21 >= 500) && (cityid21 < 550)) {
                icon21.setImageResource(R.drawable.raindropicon);
            } else if ((cityid21 >= 600) && (cityid21 < 650)) {
                icon21.setImageResource(R.drawable.snowicon);
            } else if ((cityid21 > 800) && (cityid21 < 810)) {
                icon21.setImageResource(R.drawable.cloudicon);
            } else if ((cityid21 >= 900) && (cityid21 < 910)) {
                icon21.setImageResource(R.drawable.extremeicon);
            }
            t_time1.setText("9시");
            t_time2.setText("12시");
            t_time3.setText("15시");
            t_time4.setText("18시");
            t_time5.setText("21시");
            t_temp1.setText(citytemp9 + "'C" + "\n" + cityhumi9 + "%");
            t_temp2.setText(citytemp12 + "'C" + "\n" + cityhumi12 + "%");
            t_temp3.setText(citytemp15 + "'C" + "\n" + cityhumi15 + "%");
            t_temp4.setText(citytemp18 + "'C" + "\n" + cityhumi18 + "%");
            t_temp5.setText(citytemp21 + "'C" + "\n" + cityhumi21 + "%");
        }
    }
}
