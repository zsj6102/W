package per.lijuan.meituan.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

import per.lijuan.meituan.Bean.BottomCom;
import per.lijuan.meituan.HttpThread.SendText;
import per.lijuan.meituan.HttpThread.SendWav;
import per.lijuan.meituan.Interface.ErrorCallBack;
import per.lijuan.meituan.Interface.PostCallBack;
import per.lijuan.meituan.R;
import per.lijuan.meituan.View.DailDialog;
import per.lijuan.meituan.View.SmsDialog;
import per.lijuan.meituan.View.VoiceLineView;
import static android.content.ContentValues.TAG;
import static per.lijuan.meituan.Util.AndroidUtil.isNetworkAvailable;

/**
 * Created by admin on 2017/3/24.
 */

public class ShowActivity extends CheckPermissionsActivity implements SpeechSynthesizerListener,View.OnClickListener {

    private int count = 0;
    String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
    Context mContext = null;
    private Button btn_control;
    private Button btn_voice;
    private Button btn_text;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn4;
    private Button btn5;

    private LinearLayout layout1;
    private LinearLayout layout2;
    private LinearLayout layout3;
    private LinearLayout layout4;
    private LinearLayout layout5;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv_text;
    private TextView tv_control;
    private List<BottomCom> list;
    private Realm realm;
    Recorder recorder;
    private RelativeLayout content;
    private VoiceLineView voiceLineView;
    private long exitTime = 0;
    SmsDialog smsDialog ;
    Map<Integer, Integer> resmap;
    private SurfaceView sv;
    private SurfaceHolder holder ;
    private Rect dst ;

    //资源
    private Resources resources;
    private SpeechSynthesizer mSpeechSynthesizer;//百度语音合成客户端
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license_2016-04-05";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private static final String APP_ID = "9481964";//请更换为自己创建的应用
    private static final String API_KEY = "2M6bo6iQY1how9R9WgaEkkSH";//请更换为自己创建的应用
    private static final String SECRET_KEY = "4fa5b98feb83836128b8f51da92220d8";//请更换为自己创建的应用
    private int[] res = {R.mipmap.bottom0, R.mipmap.bottom1, R.mipmap.bottom2, R.mipmap.bottom3, R.mipmap.bottom4, R.mipmap.bottom5, R.mipmap.bottom6,
            R.mipmap.bottom7, R.mipmap.bottom8};
    private int[] first = {R.mipmap.first0,R.mipmap.first1,R.mipmap.first2,R.mipmap.first3,R.mipmap.first4,R.mipmap.first5,R.mipmap.first6,R.mipmap.first7,R.mipmap.first8};
    private int[] fb = {R.mipmap.fb0,R.mipmap.fb1,R.mipmap.fb2,R.mipmap.fb3,R.mipmap.fb4,R.mipmap.fb5,R.mipmap.fb6,R.mipmap.fb7,R.mipmap.fb8};
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Map<String, String> map = new HashMap<>();
                    map = (Map<String, String>) msg.obj;
                    if (map.get("status").equals("true")) {
                        mSpeechSynthesizer.speak(map.get("info"));

                    }else{
                        Toast.makeText(ShowActivity.this,"控制失败",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 10:
                    Toast.makeText(ShowActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        initialTts();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上手机
//            requestPermission();
//        }else{
            setupRecorder();
//        }


        setContentView(R.layout.show_layout);
        mContext =  this;
      smsDialog   = new SmsDialog(ShowActivity.this,R.style.mydialog);
        registerReceiver(sendMessage, new IntentFilter(SENT_SMS_ACTION));
        registerReceiver(receiver, new IntentFilter(DELIVERED_SMS_ACTION));
        sv = (SurfaceView)findViewById(R.id.music_record);
        sv.setZOrderOnTop(true);
        holder = sv.getHolder();

        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(callback);
        findView();
        resmap = new HashMap<>();
        for (int i = 0; i < 9; i++) {

            resmap.put(res[i], first[i]);
        }
        RealmResults<BottomCom> bottom = realm.where(BottomCom.class).findAll();
        list= realm.copyFromRealm(bottom);
        Log.e("size",list.size()+"");
        if(list.size()==0){
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);
            layout4.setVisibility(View.GONE);
            layout5.setVisibility(View.GONE);
        }
        if(list!=null){
            if(list.size()==1){
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(0).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(0).getRes() == res[i]){
                                btn3.setBackgroundResource(fb[i]);
                            }
                        }

                    }
                });
                if (resmap.get(list.get(0).getRes()) != null){
                    btn3.setBackgroundResource(resmap.get(list.get(0).getRes()));
                }

                tv3.setText(list.get(0).getName());
                layout4.setVisibility(View.GONE);
                layout5.setVisibility(View.GONE);

            }
            if(list.size()==2){
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.VISIBLE);
                layout5.setVisibility(View.GONE);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(0).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {

                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Log.e(TAG, jsonObject.get("status")+ "=======");
                                    Log.e(TAG, jsonObject.get("info")+ "=======");
                                    mSpeechSynthesizer.speak((String)jsonObject.get("info"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(0).getRes() == res[i]){
                                btn2.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(1).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                    }
                });
                if (resmap.get(list.get(0).getRes()) != null){
                    btn2.setBackgroundResource(resmap.get(list.get(0).getRes()));
                }

                tv2.setText(list.get(0).getName());
                btn4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(1).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(1).getRes() == res[i]){
                                btn4.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                    }
                });
                if (resmap.get(list.get(1).getRes()) != null){
                    btn4.setBackgroundResource(resmap.get(list.get(1).getRes()));
                }

                tv4.setText(list.get(1).getName());

            }
            if(list.size()==3){
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.VISIBLE);
                layout5.setVisibility(View.GONE);

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(0).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(0).getRes() == res[i]){
                                btn2.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(1).getRes()) != null){
                            btn3.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                        if (resmap.get(list.get(2).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                    }
                });
                if (resmap.get(list.get(0).getRes()) != null){
                    btn2.setBackgroundResource(resmap.get(list.get(0).getRes()));
                }
                tv2.setText(list.get(0).getName());
                if (resmap.get(list.get(1).getRes()) != null){
                    btn3.setBackgroundResource(resmap.get(list.get(1).getRes()));
                }

                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(1).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(1).getRes() == res[i]){
                                btn3.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }
                        if (resmap.get(list.get(2).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }

                    }
                });
                tv3.setText(list.get(1).getName());
                if (resmap.get(list.get(2).getRes()) != null){
                    btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                }
                btn4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(2).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(2).getRes() == res[i]){
                                btn4.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }
                        if (resmap.get(list.get(1).getRes()) != null){
                            btn3.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                    }
                });
                tv4.setText(list.get(2).getName());
            }
            if(list.size()==4){
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                layout4.setVisibility(View.VISIBLE);
                layout5.setVisibility(View.VISIBLE);

                if (resmap.get(list.get(0).getRes()) != null){
                    btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                }
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isNetworkAvailable(ShowActivity.this)){
                            Toast.makeText(ShowActivity.this,"网络不可用，请检查！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SendText.send(list.get(0).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    CheckNet();
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(0).getRes() == res[i]){
                                btn1.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }

                        if (resmap.get(list.get(2).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }
                    }
                });
                tv1.setText(list.get(0).getName());
                if (resmap.get(list.get(1).getRes()) != null){
                    btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                }
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(1).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(1).getRes() == res[i]){
                                btn2.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(2).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }
                    }
                });
                tv2.setText(list.get(1).getName());
                if (resmap.get(list.get(2).getRes()) != null){
                    btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                }

                btn4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(2).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(2).getRes() == res[i]){
                                btn4.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }
                    }
                });
                tv4.setText(list.get(2).getName());

                if (resmap.get(list.get(3).getRes()) != null){
                    btn5.setBackgroundResource(resmap.get(list.get(3).getRes()));
                }
                btn5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(3).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(3).getRes() == res[i]){
                                btn5.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                        if (resmap.get(list.get(2).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }

                    }
                });
                tv5.setText(list.get(3).getName());
            }
            if(list.size()==5){
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);
                layout4.setVisibility(View.VISIBLE);
                layout5.setVisibility(View.VISIBLE);
                if (resmap.get(list.get(0).getRes()) != null){
                    btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                }

                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(0).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(0).getRes() == res[i]){
                                btn1.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }

                        if (resmap.get(list.get(2).getRes()) != null){
                            btn3.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                        if (resmap.get(list.get(4).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(4).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }

                    }
                });
                tv1.setText(list.get(0).getName());
                if (resmap.get(list.get(1).getRes()) != null){
                    btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                }

                tv2.setText(list.get(1).getName());
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(1).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });

                        for(int i= 0;i<res.length;i++){
                            if(list.get(1).getRes() == res[i]){
                                btn2.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(2).getRes()) != null){
                            btn3.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                        if (resmap.get(list.get(4).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(4).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }

                    }
                });
                if (resmap.get(list.get(2).getRes()) != null){
                    btn3.setBackgroundResource(resmap.get(list.get(2).getRes()));
                }

                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(2).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(2).getRes() == res[i]){
                                btn3.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                        if (resmap.get(list.get(4).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(4).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }

                    }
                });
                tv3.setText(list.get(2).getName());
                if (resmap.get(list.get(3).getRes()) != null){
                    btn4.setBackgroundResource(resmap.get(list.get(3).getRes()));
                }

                tv4.setText(list.get(3).getName());
                btn4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(3).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(3).getRes() == res[i]){
                                btn4.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                        if (resmap.get(list.get(2).getRes()) != null){
                            btn3.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                        if (resmap.get(list.get(4).getRes()) != null){
                            btn5.setBackgroundResource(resmap.get(list.get(4).getRes()));
                        }

                    }
                });
                if (resmap.get(list.get(4).getRes()) != null){
                    btn5.setBackgroundResource(resmap.get(list.get(4).getRes()));
                }

                tv5.setText(list.get(4).getName());
                btn5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckNet();
                        SendText.send(list.get(4).getCom(), new PostCallBack() {
                            @Override
                            public void excute(String str) {
                                try {

                                    JSONObject jsonObject = new JSONObject(str);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("status", jsonObject.get("status").toString());
                                    if (jsonObject.get("status").toString().equals("true")) {
                                        map.put("info", jsonObject.get("info").toString());
                                    }
                                    msg.obj = map;
                                    ShowActivity.this.handler.sendMessage(msg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new ErrorCallBack() {
                            @Override
                            public void excute() {
                                Message msg = new Message();
                                msg.what = 10;
                                ShowActivity.this.handler.sendMessage(msg);
                            }
                        });
                        for(int i= 0;i<res.length;i++){
                            if(list.get(4).getRes() == res[i]){
                                btn5.setBackgroundResource(fb[i]);
                            }
                        }
                        if (resmap.get(list.get(0).getRes()) != null){
                            btn1.setBackgroundResource(resmap.get(list.get(0).getRes()));
                        }

                        if (resmap.get(list.get(1).getRes()) != null){
                            btn2.setBackgroundResource(resmap.get(list.get(1).getRes()));
                        }
                        if (resmap.get(list.get(2).getRes()) != null){
                            btn3.setBackgroundResource(resmap.get(list.get(2).getRes()));
                        }
                        if (resmap.get(list.get(3).getRes()) != null){
                            btn4.setBackgroundResource(resmap.get(list.get(3).getRes()));
                        }
                    }
                });
            }
        }
    }

    private void findView(){
        content = (RelativeLayout)findViewById(R.id.content);
        btn_control = (Button)findViewById(R.id.btn_control);
        btn_voice = (Button)findViewById(R.id.btn_voice);
        btn_text = (Button)findViewById(R.id.btn_text);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        btn5 = (Button)findViewById(R.id.btn5);
        layout1 = (LinearLayout)findViewById(R.id.layout1);
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        layout3 = (LinearLayout)findViewById(R.id.layout3);
        layout4 = (LinearLayout)findViewById(R.id.layout4);
        layout5 = (LinearLayout)findViewById(R.id.layout5);
       tv_control = (TextView)findViewById(R.id.tv_control);
        tv_text = (TextView)findViewById(R.id.tv_text);
        tv1 = (TextView)findViewById(R.id.tv1);
        tv2 = (TextView)findViewById(R.id.tv2);
        tv3 = (TextView)findViewById(R.id.tv3);
        tv4 = (TextView)findViewById(R.id.tv4);
        tv5 = (TextView)findViewById(R.id.tv5);
        voiceLineView = (VoiceLineView) findViewById(R.id.voicLine);

        btn_voice.setOnTouchListener(new View.OnTouchListener() {
            long start;
            long end;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case  MotionEvent.ACTION_DOWN:
                        btn_voice.setBackgroundResource(R.mipmap.btn_voice);
                        start=System.currentTimeMillis();
//                        Log.i("midon",System.currentTimeMillis()+"");
//                        startVoice();
                        if(recorder!=null){
                            recorder.startRecording();
                        }

                        voiceLineView.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        end = System.currentTimeMillis();
                        Log.i("miup",System.currentTimeMillis()+"");
                        btn_voice.setBackgroundResource(R.mipmap.btn_voice0);
                        if(recorder!=null){
                            recorder.stopRecording();
                        }

                        voiceLineView.setVisibility(View.GONE);

                        Log.i("dif",(end-start)+"");

                        if((end-start)>500){
                            CheckNet();
                            File file = new File(Environment.getExternalStorageDirectory()+"/"+"zsj.wav");
                            Log.e("file",file.getName());
                                SendWav.send(file, new PostCallBack() {
                                    @Override
                                    public void excute(String str) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(str);
                                            Message msg = new Message();
                                            msg.what = 0;
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("status", jsonObject.get("status").toString());
                                            if (jsonObject.get("status").toString().equals("true")) {
                                                map.put("info", jsonObject.get("info").toString());
                                            }
                                            msg.obj = map;
                                            ShowActivity.this.handler.sendMessage(msg);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new ErrorCallBack() {
                                    @Override
                                    public void excute() {
                                        Message msg = new Message();
                                        msg.what = 10;
                                        ShowActivity.this.handler.sendMessage(msg);
                                    }
                                });


                        }else{
                            Toast.makeText(ShowActivity.this,"时间太短",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        btn_text.setOnClickListener(listenertext);
        tv_text.setOnClickListener(this);
        btn_control.setOnClickListener(listenercontrol);
        tv_control.setOnClickListener(this);
    }
    private View.OnClickListener listenercontrol = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_control.setBackgroundResource(R.mipmap.btn_control);
            Intent intent = new Intent(ShowActivity.this,MainActivity.class);
            startActivity(intent);
            ShowActivity.this.finish();
        }
    };
   private View.OnClickListener listenertext = new View.OnClickListener() {
       @Override
       public void onClick(View v) {

           count++;
           if(count%2==0){
               final DailDialog dialog = new DailDialog(ShowActivity.this,R.style.mydialog);
               dialog.show();

               dialog.setUser("郑少杰");
               dialog.setPhone("5554");
               dialog.setCancelButtonListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog.dismiss();

                   }
               });
               dialog.setSureButtonListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       callPhone();

                   }
               });
           }else{

               smsDialog.show();

               smsDialog.setUser("郑少杰");
               smsDialog.setPhone("5554");
               smsDialog.setCancelButtonListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       smsDialog.dismiss();

                   }
               });
               smsDialog.setSureButtonListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String num= smsDialog.getPhone();
                       String content= smsDialog.getEditContent();
                       Log.e("num",num);
                       Log.e("content",content);
                       if( !content.trim().equals("")){
                           sendSms(num,content);
                       }else{
                           Toast.makeText(ShowActivity.this,"短信内容不能为空",Toast.LENGTH_SHORT).show();
                       }

                   }
               });
           }

       }
   };
    public void callPhone()
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + "5554");
        intent.setData(data);
        startActivity(intent);
    }
    private  void sendSms(String num,String content){


            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            PendingIntent sentPI = PendingIntent.getBroadcast(ShowActivity.this, 0, sentIntent,
                    0);

            // create the deilverIntent parameter
            Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
            PendingIntent deliverPI = PendingIntent.getBroadcast(ShowActivity.this, 0,
                    deliverIntent, 0);
            SmsManager sm=SmsManager.getDefault();
            //2、切割短信,把长短信切割成多条小短信
            ArrayList<String> smss=sm.divideMessage(content);
            //3、把多条短信发送出去
            if(content.length()>70){
                List<String> contents = sm.divideMessage(content);
                for(String c:contents){

                    sm.sendTextMessage(num, null, c, sentPI, deliverPI);
                }
            }else{
                sm.sendTextMessage(num, null, content, sentPI, deliverPI);
            }

    }
//    private void requestPermission(){
//       if (ContextCompat.checkSelfPermission(ShowActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
//
//       }else{
//           setupRecorder();
//       }
//
//
//    }
    private void setupRecorder() {
        Log.e("efi",file().getName());
        if(file()!=null){
            recorder = OmRecorder.wav(
                    new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                        @Override
                        public void onAudioChunkPulled(AudioChunk audioChunk) {
                            animateVoice((float) (audioChunk.maxAmplitude())/2);
                        }
                    }), file());
        }

    }

    private void animateVoice(final float maxPeak) {
        Log.e("dp",maxPeak+"");
//        btn_voice.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
        voiceLineView.setVolume((int)maxPeak);
        if(((int)maxPeak)>28){
            voiceLineView.setVolume((int)maxPeak*2);
        }else{
//            if(maxPeak<0){
                voiceLineView.setVolume(0);
//            }
//            voiceLineView.setVolume((int)maxPeak/3);
        }
    }
    private void CheckNet(){
        if(!isNetworkAvailable(ShowActivity.this)){
            Toast.makeText(ShowActivity.this,"网络不可用，请检查！",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private AudioSource mic() {
        return new AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100);
    }

    @NonNull
    private File file() {
        return new File(Environment.getExternalStorageDirectory(), "zsj.wav");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private BroadcastReceiver sendMessage = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断短信是否发送成功
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(mContext, "短信发送成功", Toast.LENGTH_SHORT).show();
                    smsDialog.dismiss();

                    break;
                default:
                    Toast.makeText(mContext, "发送失败", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //表示对方成功收到短信
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(mContext, "对方接收成功",Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(mContext, "未送达",Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };
    private void initialTts() {
        //获取语音合成对象实例
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        //设置Context
        this.mSpeechSynthesizer.setContext(this);
        //设置语音合成状态监听
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        //文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        //声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        //本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，
        //仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，
        //不需要设置该参数，建议将该行代码删除（离线引擎）
//        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
//                + LICENSE_FILE_NAME);
        //请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId(APP_ID);
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey(API_KEY, SECRET_KEY);
        //发音人（在线引擎），可用参数为0,1,2,3。。。
        //（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
            Log.i(TAG, ">>>auth success.");
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            Log.i(TAG, ">>>auth failed errorMsg: " + errorMsg);
        }
        // 引擎初始化tts接口
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
        int result =
                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        Log.i(TAG, ">>>loadEnglishModel result: " + result);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            if(event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0){
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    private void exitApp(){
        if((System.currentTimeMillis() - exitTime)>2000){
            Toast.makeText(ShowActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            finish();
        }
    }

    @Override
    public void onSynthesizeStart(String s) {

    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

    }

    @Override
    public void onSynthesizeFinish(String s) {

    }

    @Override
    public void onSpeechStart(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {

    }

    @Override
    public void onError(String s, SpeechError speechError) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(sendMessage);
    }
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        /**
         * 通知 surface 已经创建完了
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i("tag","======begin set surfaceView =========");


            //获取surfaceView 的宽高
            int svWidth= sv.getWidth();
            int svHeight = sv.getHeight();
            Log.i("tag",svWidth+"======surfaceCreated========="+svHeight);
            //目标矩形 画布上的矩形
            dst = new Rect(0,0,svWidth,svHeight);

            //拿到图片     getRescources 是获取应用资源 包括res 下所有的资源
            resources= getResources();
            drawBitmap(R.mipmap.people);

            Log.e("tag","============over=============");

        }

        /**
         * 通知 surface 发生了改变  尺寸发生变化时
         * @param holder
         * @param format
         * @param width 宽度
         * @param height 高度
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        /**
         * 通知 surface已经销毁了
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };
    private void drawBitmap(int id ) {
        //锁定并拿到画布
        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Bitmap bmp = getBitmap(id);

        //获取位图的像素宽高
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        //创建矩形 要绘制的位图
        Rect src = new Rect(0,0,bmpWidth,bmpHeight);

        /**
         * 画一个位图 图片对应到程序中就是位图
         * 参数
         *  bmp 图片位图
         *  src，要绘制的矩形 位图矩形
         *  dst  目标矩形 画布中的
         *  null 画笔  现在不需要
         */
        canvas.drawBitmap(bmp,src,dst,null);

        //绘制完毕 接触画布的锁定 并且 寄送画布
        holder.unlockCanvasAndPost(canvas);
    }
    private Bitmap getBitmap(int id ){
        //获取到资源中的可绘制资源(图片) 参数 资源id
        Drawable drawable= resources.getDrawable(id);

        //子类型； Drawable 有多种类型的 例如 颜色绘制的图
        BitmapDrawable bmpDaw = (BitmapDrawable) drawable;

        //获取可绘制资源中的位图
        Bitmap bmp = bmpDaw.getBitmap();
        return bmp;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_control:
                btn_control.setPressed(true);
                Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                startActivity(intent);
                ShowActivity.this.finish();
                break;
            case R.id.tv_text:
                btn_text.setPressed(true);
                count++;
                if(count%2==0){
                    final DailDialog dialog = new DailDialog(ShowActivity.this,R.style.mydialog);
                    dialog.show();

                    dialog.setUser("郑少杰");
                    dialog.setPhone("5554");
                    dialog.setCancelButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    dialog.setSureButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callPhone();

                        }
                    });
                }else{

                    smsDialog.show();

                    smsDialog.setUser("郑少杰");
                    smsDialog.setPhone("5554");
                    smsDialog.setCancelButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            smsDialog.dismiss();

                        }
                    });
                    smsDialog.setSureButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String num= smsDialog.getPhone();
                            String content= smsDialog.getEditContent();
                            Log.e("num",num);
                            Log.e("content",content);
                            if(!content.trim().equals("")){
                                sendSms(num,content);
                            }else{
                                Toast.makeText(ShowActivity.this,"短信内容不能为空",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
              break;

        }
    }
}
