package com.example.project_weather.naverTalk;

/*
Made by : Jinwoo Choi,
Location : Suwon, Korea.
Date    : 2016.04.12
Major   : Hankuk University of Foreign Studies, ICE(Information Communication Engineering)
 */

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.project_weather.R;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class NaverRecognizer implements SpeechRecognitionListener {
    String cmd="";

    private final static String TAG = NaverRecognizer.class.getSimpleName();

    private Handler mHandler;
    private SpeechRecognizer mRecognizer;

    public NaverRecognizer(Activity activity, Handler handler, String clientId, SpeechConfig config) {
        this.mHandler = handler;

        try {
            mRecognizer = new SpeechRecognizer(activity, clientId, config);
        } catch (SpeechRecognitionException e) {
            // 예외가 발생하는 경우는 아래와 같습니다.
            //   1. activity 파라미터가 올바른 MainActivity의 인스턴스가 아닙니다.
            //   2. AndroidManifest.xml에서 package를 올바르게 등록하지 않았습니다.
            //   3. package를 올바르게 등록했지만 과도하게 긴 경우, 256바이트 이하면 좋습니다.
            //   4. clientId가 null인 경우
            //
            // 개발하면서 예외가 발생하지 않았다면 실서비스에서도 예외는 발생하지 않습니다.
            // 개발 초기에만 주의하시면 됩니다.
            e.printStackTrace();
        }

        mRecognizer.setSpeechRecognitionListener(this);

        // If you want to recognize audio file, please add codes as follows.
        // mRecognizer.setAudioFileInput(true);
        // mRecognizer.setAudioFilePath(Environment.getExternalStorageDirectory().getAbsolutePath()
        // + "/NaverSpeechTest/TestInput.pcm");
    }

    public SpeechRecognizer getSpeechRecognizer() {
        return mRecognizer;
    }

    public void recognize() {
        try {
            mRecognizer.recognize();
        } catch (SpeechRecognitionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInactive() {
        Log.d(TAG, "Event occurred : Inactive");
        Message msg = Message.obtain(mHandler, R.id.clientInactive);
        msg.sendToTarget();
    }

    @Override
    public void onReady() {
        Log.d(TAG, "Event occurred : Ready");
        Message msg = Message.obtain(mHandler, R.id.clientReady);
        msg.sendToTarget();
    }

    @Override
    public void onRecord(short[] speech) {
        Log.d(TAG, "Event occurred : Record");
        Message msg = Message.obtain(mHandler, R.id.audioRecording, speech);
        msg.sendToTarget();
    }

    @Override
    public void onPartitialResult(String result) {
        //result값이 음성 인식 된 값임!
        Log.d(TAG, "Partial Result!! (" + result + ")");
        Message msg = Message.obtain(mHandler, R.id.partialResult, result);
        msg.sendToTarget();
    }

    @Override
    public void onEndPointDetected() {
        Log.d(TAG, "Event occurred : EndPointDetected");
    }

    @Override
    public void onResult(Object[] result) {
        //result값이 음성 인식 된 값임!
        Log.d(TAG, "Final Result!! (" + result[0] + ")");

        if(result[0].equals("맑은 날씨 보여줘") || result[0].equals("맑음") || result[0].equals("맑은 날씨")) {
            cmd="sunny";
            System.out.println("sunny");
        }
        else if(result[0].equals("흐린날씨보여줘") || result[0].equals("흐린 날씨보여줘") || result[0].equals("흐림") || result[0].equals("흐린 날씨")) {
            cmd="cloudy";
            System.out.println("cloudy");
        }
        else if(result[0].equals("비오는날씨보여줘") || result[0].equals("비오는날씨 보여줘") || result[0].equals("비") || result[0].equals("p") || result[0].equals("비오는 날씨")) {
            cmd="rain";
        }
        else if(result[0].equals("눈오는날씨보여줘") || result[0].equals("눈") || result[0].equals("눈오는 날씨")) {
            cmd="snow";
        }
        else if(result[0].equals("안개날씨보여줘") || result[0].equals("안개") || result[0].equals("안개낀날씨보여줘") || result[0].equals("안개낀 날씨")) {
            cmd="drizzle";
        }
        else if(result[0].equals("천둥번개 보여줘") || result[0].equals("천둥번개") || result[0].equals("천둥") || result[0].equals("번개") || result[0].equals("번개 치는날씨") || result[0].equals("천둥치는날씨") || result[0].equals("번개치는날씨") || result[0].equals("천둥 치는날씨")) {
            cmd="thunder";
        }
        else if(result[0].equals("기상이변") || result[0].equals("기상 이변") || result[0].equals("기상이변 보여줘") || result[0].equals("기상이변 날씨보여줘") ||result[0].equals("기상이변 날씨") || result[0].equals("기상이변보여줘") || result[0].equals("기상이변날씨보여줘")) {
            cmd="extreme";
        }
        else if(result[0].equals("안녕") || result[0].equals("안녕하세요") || result[0].equals("하이") || result[0].equals("hi") ||result[0].equals("헬로") || result[0].equals("hello") || result[0].equals("이유") || result[0].equals("유") || result[0].equals("you")) {
            cmd="hi";
        }
        else if(result[0].equals("누구야") || result[0].equals("누구세요") || result[0].equals("who") || result[0].equals("후") ||result[0].equals("후얼유") || result[0].equals("후아유")) {
            cmd="who";
        }
        else if(result[0].equals("은행") || result[0].equals("은헤") || result[0].equals("은혜") || result[0].equals("내가좋아하는사람") || result[0].equals("내가 좋아하는 사람") || result[0].equals("내가 좋아하는사람") ||result[0].equals("내가좋아하는 사람") || result[0].equals("사랑") || result[0].equals("love") || result[0].equals("러브") || result[0].equals("아이러브유")) {
            cmd="love";
        }
        else if(result[0].equals("현재날씨") || result[0].equals("지금날씨") || result[0].equals("지금 날씨") || result[0].equals("현재 날씨") || result[0].equals("현재 날씨 어때요") || result[0].equals("오늘날씨") || result[0].equals("오늘 날씨")) {
            cmd="weather";
        }
        else {
            cmd="not";
        }

        Message msg = Message.obtain(mHandler, R.id.finalResult, result);
        msg.sendToTarget();

        Thread t = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL("http://(Your Server IP Address)/vcontrol?command="+cmd);     //server 주소에 보내주겠다는 뜻.
                    //URL url = new URL(urladdr +"/vcontrol?command="+cmd);     //server 주소에 보내주겠다는 뜻.
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
    @Override
    public void onError(int errorCode) {
        Message msg = Message.obtain(mHandler, R.id.recognitionError, errorCode);
        msg.sendToTarget();
    }
}
