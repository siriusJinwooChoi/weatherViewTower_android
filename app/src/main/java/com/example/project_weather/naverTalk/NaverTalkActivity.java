package com.example.project_weather.naverTalk;
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
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.project_weather.R;
import com.example.project_weather.util.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class NaverTalkActivity extends Activity {
    private int num, viewnum;
    private String urladdress;
    private static final int DIALOG_SUNNY = 1;
    private static final int DIALOG_CLOUDY = 2;
    private static final int DIALOG_RAIN = 3;
    private static final int DIALOG_SNOW = 4;
    private static final int DIALOG_THUNDER = 5;
    private static final int DIALOG_DRIZZLE = 6;
    private static final int DIALOG_EXTREME = 7;
    private static final int DIALOG_DIRECT = 8;
    private static final int DIALOG_VIEW = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        Intent intent = getIntent();
        urladdress = intent.getStringExtra("address");
        Log.d("addr", urladdress);
        /* Display a list of items */
        Button sunnyButton = (Button) findViewById(R.id.cmdsunny);
        sunnyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SUNNY);
            }
        });
        Button cloudyButton = (Button) findViewById(R.id.cmdcloudy);
        cloudyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_CLOUDY);
            }
        });
        Button selectButton = (Button) findViewById(R.id.cmdsunny);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SUNNY);
            }
        });
        Button rainButton = (Button) findViewById(R.id.cmdrain);
        rainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_RAIN);
            }
        });
        Button snowButton = (Button) findViewById(R.id.cmdsnow);
        snowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SNOW);
            }
        });
        Button thunderButton = (Button) findViewById(R.id.cmdthunder);
        thunderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_THUNDER);
            }
        });
        Button drizzleButton = (Button) findViewById(R.id.cmddrizzle);
        drizzleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_DRIZZLE);
            }
        });
        Button extremeButton = (Button) findViewById(R.id.cmdextreme);
        extremeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_EXTREME);
            }
        });


        //음성 인식 하는 버튼에 대한 부분.(클릭 시)
        txtResult = (TextView) findViewById(R.id.txt_voiceresult);
        btnStart = (Button) findViewById(R.id.btn_voicestart);

        handler = new RecognitionHandler(NaverTalkActivity.this);
        naverRecognizer = new NaverRecognizer(NaverTalkActivity.this, handler, CLIENT_ID, SPEECH_CONFIG);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("인식 중...");
                    btnStart.setText(R.string.str_listening);
                    isRunning = true;

                    naverRecognizer.recognize();
                } else {
                    // This flow is occurred by pushing start button again
                    // when SpeechRecognizer is running.
                    // Because it means that a user wants to cancel speech
                    // recognition commonly, so call stop().
                    btnStart.setEnabled(false);

                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });


        //임시확인 / 수동 제어 버튼 클릭하는 부분
        Button directButton = (Button)findViewById(R.id.btn_direct);
        directButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DIRECT);
            }
        });
        Button viewButton = (Button)findViewById(R.id.btn_view);
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_VIEW);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SUNNY:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_sunny)
                        .setItems(R.array.select_dialog_suncmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_suncmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_CLOUDY:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_cloudy)
                        .setItems(R.array.select_dialog_cloudcmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_cloudcmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_RAIN:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_rain)
                        .setItems(R.array.select_dialog_raincmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_raincmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_SNOW:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_snow)
                        .setItems(R.array.select_dialog_snowcmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_snowcmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_THUNDER:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_thunder)
                        .setItems(R.array.select_dialog_thundercmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_thundercmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_DRIZZLE:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_drizzle)
                        .setItems(R.array.select_dialog_drizzlecmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_drizzlecmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_EXTREME:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_extreme)
                        .setItems(R.array.select_dialog_extremecmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_extremecmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("당신은 " + (which+1) + "번째 명령을 선택하였습니다. 아래 VOICE COMMAND 버튼을 누르고 해당 명령을 말해주세요 ->" + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_DIRECT:

                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_direct)
                        .setItems(R.array.select_dialog_directcmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_directcmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("해당 날씨를 뷰 타워에서 임시 확인합니다.")
                                        .show();
                                num = which;
                                Thread t = new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            URL url = new URL(urladdress + "/" + "direct?num="+num);     //server 주소에 보내주겠다는 뜻.
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
                            }
                        })
                        .create();

            case DIALOG_VIEW:
                return new AlertDialog.Builder(NaverTalkActivity.this)
                        .setTitle(R.string.select_view)
                        .setItems(R.array.select_dialog_viewcmd, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                        /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_viewcmd);
                                new AlertDialog.Builder(NaverTalkActivity.this)
                                        .setMessage("해당 날씨를 뷰 타워에서 구현합니다.")
                                        .show();
                                viewnum = which;
                                Thread t = new Thread(){
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            System.out.println("urladdress="+urladdress);
                                            URL url = new URL(urladdress + "/view?viewnum="+viewnum);     //server 주소에 보내주겠다는 뜻.
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
                            }
                        })
                        .create();
        }
        return null;
    }

    String result="";
    private static final String CLIENT_ID = "4XhT5raObbDCL74KlOlP"; // "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    private static final SpeechConfig SPEECH_CONFIG = SpeechConfig.OPENAPI_KR; // or SpeechConfig.OPENAPI_EN

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

    private TextView txtResult;
    private Button btnStart;
    private String mResult;

    private AudioWriterPCM writer;

    private boolean isRunning;

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                // Now an user can speak.
                txtResult.setText("명령해주세요");
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                mResult = (String) (msg.obj);
                txtResult.setText(mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.
                String[] results = (String[]) msg.obj;
                mResult = results[0];
                txtResult.setText(mResult);
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                isRunning = false;
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }

                btnStart.setText(R.string.str_start);
                btnStart.setEnabled(true);
                isRunning = false;
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // initialize() must be called on resume time.
        naverRecognizer.getSpeechRecognizer().initialize();

        mResult = "";
        txtResult.setText("");
        btnStart.setText(R.string.str_start);
        btnStart.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // release() must be called on pause time.
        naverRecognizer.getSpeechRecognizer().stopImmediately();
        naverRecognizer.getSpeechRecognizer().release();
        isRunning = false;
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<NaverTalkActivity> mActivity;

        RecognitionHandler(NaverTalkActivity activity) {
            mActivity = new WeakReference<NaverTalkActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NaverTalkActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
