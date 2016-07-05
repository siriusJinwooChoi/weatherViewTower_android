package com.example.project_weather.control;
/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering,  201003629
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_weather.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SubControlActivity extends Activity {
    private int choice, identity, i=0;
    private String urlad;

    private boolean [] sunarr, cloudarr, rainarr, snowarr, thunderarr, drizzlearr, extremearr;

    Button setBtn;

    private static final int DIALOG_SUNNY_CHOICE = 1;
    private static final int DIALOG_CLOUDY_CHOICE = 2;
    private static final int DIALOG_RAIN_CHOICE = 3;
    private static final int DIALOG_SNOW_CHOICE = 4;
    private static final int DIALOG_THUNDER_CHOICE = 5;
    private static final int DIALOG_DRIZZLE_CHOICE = 6;
    private static final int DIALOG_EXTREME_CHOICE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_control);

        sunarr = new boolean[]{true, true, true, false, false, false};
        cloudarr = new boolean[]{false, true, false, false, false, true};
        rainarr = new boolean[]{false, false, true, true, true, true};
        snowarr = new boolean[]{true, false, true, false, true, false};
        thunderarr = new boolean[]{true, true, false, true, true, false};
        drizzlearr = new boolean[]{false, false, false, false, false, true};
        extremearr = new boolean[]{true, false, false, true, true, false};

        setBtn = (Button)findViewById(R.id.settingtower);

        Intent intent = getIntent();        //intent안에 있는 데이터를 불러와줌.

        ImageView weather = (ImageView)findViewById(R.id.weather);
        TextView name = (TextView)findViewById(R.id.name);
        TextView city = (TextView)findViewById(R.id.city);
        TextView intro = (TextView)findViewById(R.id.intro);
        TextView temphumi = (TextView)findViewById(R.id.temp_humi);
        TextView intemp_inhumi = (TextView)findViewById(R.id.intemp_inhumi);

        weather.setImageResource(intent.getIntExtra("weather", 0));
        name.setText(intent.getStringExtra("name"));
        city.setText(intent.getStringExtra("cityname"));
        intro.setText(intent.getStringExtra("intro"));
        temphumi.setText(intent.getDoubleExtra("temp", 0) + "" + "/" + intent.getDoubleExtra("humidity", 0) + "");
        intemp_inhumi.setText(intent.getDoubleExtra("inner_temp", 0) +"" + "/" + intent.getDoubleExtra("inner_humidity", 0) +"");

        choice = intent.getIntExtra("position", 0);
        urlad = intent.getStringExtra("urladdress");
        System.out.println("choice=" + choice);

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choice == 0) {       //맑음을 눌렀을 때,
                    showDialog(DIALOG_SUNNY_CHOICE);
                } else if(choice == 1) {        //흐림을 눌렀을 때,
                    showDialog(DIALOG_CLOUDY_CHOICE);
                } else if(choice == 2) {        //비 를 눌렀을 때,
                    showDialog(DIALOG_RAIN_CHOICE);
                } else if(choice == 3) {        //눈을 눌렀을 때,
                    showDialog(DIALOG_SNOW_CHOICE);
                } else if(choice == 4) {        //천둥/번개를 눌렀을 때,
                    showDialog(DIALOG_THUNDER_CHOICE);
                } else if(choice == 5) {        //안개를 눌렀을 때,
                    showDialog(DIALOG_DRIZZLE_CHOICE);
                } else if(choice == 6) {        //기상이변을 눌렀을 때,
                    showDialog(DIALOG_EXTREME_CHOICE);
                }
            }
        });
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SUNNY_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctlsun)
                        .setTitle(R.string.alert_dialog_sun_choice)
                        .setMultiChoiceItems(R.array.select_dialog_suntower,
                                sunarr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        sunarr[whichButton] = isChecked;
                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        Thread t = new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red=" + sunarr[0] + "&green=" + sunarr[1] + "&blue=" + sunarr[2] + "&water=" + sunarr[3] + "&windy=" + sunarr[4] + "&drizzle=" + sunarr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:" + buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                        Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                                    }
                        /* User clicked Yes so do some stuff */
                            })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
            case DIALOG_CLOUDY_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctlcloud)
                        .setTitle(R.string.alert_dialog_cloud_choice)
                        .setMultiChoiceItems(R.array.select_dialog_cloudtower,
                                cloudarr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        cloudarr[whichButton] = isChecked;
                                        //기본값으로 초기화

                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        Thread t = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red="+ cloudarr[0] + "&green="+cloudarr[1] + "&blue="+cloudarr[2] + "&water=" + cloudarr[3] + "&windy="+cloudarr[4] + "&drizzle="+cloudarr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:"+buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        /* User clicked Yes so do some stuff */
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
            case DIALOG_RAIN_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctlrain)
                        .setTitle(R.string.alert_dialog_rain_choice)
                        .setMultiChoiceItems(R.array.select_dialog_raintower,
                                rainarr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        rainarr[whichButton] = isChecked;
                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        System.out.println("isTrue");
                                        Thread t = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red="+ rainarr[0] + "&green="+rainarr[1] + "&blue="+rainarr[2] + "&water=" + rainarr[3] + "&windy="+rainarr[4] + "&drizzle="+rainarr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    System.out.println("id="+identity);
                                                    System.out.println("red="+rainarr[0]);
                                                    System.out.println("green="+rainarr[1]);
                                                    System.out.println("blue="+rainarr[2]);
                                                    System.out.println("water="+rainarr[3]);
                                                    System.out.println("windy="+rainarr[4]);
                                                    System.out.println("drizzle="+rainarr[5]);
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:"+buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                        /* User clicked Yes so do some stuff */
                            })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
            case DIALOG_SNOW_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctlsnow)
                        .setTitle(R.string.alert_dialog_snow_choice)
                        .setMultiChoiceItems(R.array.select_dialog_snowtower,
                                snowarr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        snowarr[whichButton] = isChecked;
                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        Thread t = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red="+ snowarr[0] + "&green="+snowarr[1] + "&blue="+snowarr[2] + "&water=" + snowarr[3] + "&windy="+snowarr[4] + "&drizzle="+snowarr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:"+buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                        /* User clicked Yes so do some stuff */
                            })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
            case DIALOG_THUNDER_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctlthunder)
                        .setTitle(R.string.alert_dialog_thunder_choice)
                        .setMultiChoiceItems(R.array.select_dialog_thundertower,
                                thunderarr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        thunderarr[whichButton] = isChecked;
                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        Thread t = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red="+ thunderarr[0] + "&green="+thunderarr[1] + "&blue="+thunderarr[2] + "&water=" + thunderarr[3] + "&windy="+thunderarr[4] + "&drizzle="+thunderarr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:"+buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                        /* User clicked Yes so do some stuff */
                            })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
            case DIALOG_DRIZZLE_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctldrizzle)
                        .setTitle(R.string.alert_dialog_drizzle_choice)
                        .setMultiChoiceItems(R.array.select_dialog_drizzletower,
                                drizzlearr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        drizzlearr[whichButton] = isChecked;
                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        Thread t = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red="+ drizzlearr[0] + "&green="+drizzlearr[1] + "&blue="+drizzlearr[2] + "&water=" + drizzlearr[3] + "&windy="+drizzlearr[4] + "&drizzle="+drizzlearr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:"+buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                        /* User clicked Yes so do some stuff */
                            })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
            case DIALOG_EXTREME_CHOICE:
                identity = id;
                return new AlertDialog.Builder(SubControlActivity.this)
                        .setIcon(R.drawable.ctlextreme)
                        .setTitle(R.string.alert_dialog_extreme_choice)
                        .setMultiChoiceItems(R.array.select_dialog_extremetower,
                                extremearr,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton,
                                                        boolean isChecked) {
                                        extremearr[whichButton] = isChecked;
                                /* User clicked on a check box do some stuff */
                                    }
                                })
                        .setPositiveButton(R.string.alert_dialog_setting, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                        Thread t = new Thread(){
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    URL url = new URL(urlad+"/"+"dbupdate?id=" + identity + "&red="+ extremearr[0] + "&green="+extremearr[1] + "&blue="+extremearr[2] + "&water=" + extremearr[3] + "&windy="+extremearr[4] + "&drizzle="+extremearr[5]);     //server 주소에 보내주겠다는 뜻.
                                                    //해당 주소의 페이지로 접속을 하고, 단일 HTTP 접속을 하기위해 캐스트한다
                                                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                    // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
                                                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), urlConnection.getContentLength());
                                                    String buf;

                                                    // 표준출력으로 한 라인씩 출력
                                                    while ((buf = br.readLine()) != null) {
                                                        System.out.println("buffer:"+buf);
                                                    }
                                                    // 스트림을 닫는다.
                                                    br.close();
                                                }catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        t.start();
                                Toast.makeText(SubControlActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                        /* User clicked Yes so do some stuff */
                            })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                        /* User clicked No so do some stuff */
                            }
                        })
                        .create();
        }
        return null;
    }
}
