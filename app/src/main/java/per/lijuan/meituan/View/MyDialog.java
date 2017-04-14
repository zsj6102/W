package per.lijuan.meituan.View;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import per.lijuan.meituan.Adapter.DialogGridAdapter;
import per.lijuan.meituan.R;
public class MyDialog extends Dialog {
    private Button btn_sure;
    private GridView gridView;
    private LinearLayout addComlayout;
    private LinearLayout addFilelayout;
    private LinearLayout  layoutLeft;
    private LinearLayout  layoutRight;
    private EditText edt_name;
    private EditText edt_com;
    private EditText edt_file;
    private Button btn_voice;
    private Context context;
    private TextView tv_com;
    private TextView tv_file;
    private List<Integer> list;
    private ImageView iv_com;
    private ImageView iv_file;
    private DialogGridAdapter adapter;
    private  boolean isFolder = false;
    private boolean isFile  ;
    private boolean isInEdit;
    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_layout);
        iv_com = (ImageView)findViewById(R.id.iv_com);
        iv_file = (ImageView)findViewById(R.id.iv_file);
        tv_com = (TextView) findViewById(R.id.tv_com);
        tv_file = (TextView) findViewById(R.id.tv_file);
        edt_com = (EditText) findViewById(R.id.edt_com);
        edt_name = (EditText)findViewById(R.id.edt_name);
        edt_file = (EditText)findViewById(R.id.edt_file);
        btn_sure = (Button) findViewById(R.id.btn_complete);
        gridView = (GridView) findViewById(R.id.gridview);
        addComlayout = (LinearLayout) findViewById(R.id.layout_addcom);
        addFilelayout = (LinearLayout) findViewById(R.id.layout_addfile);
        layoutLeft = (LinearLayout)findViewById(R.id.layoutleft);
        layoutRight = (LinearLayout)findViewById(R.id.layoutright);
        btn_voice = (Button) findViewById(R.id.btn_voice);
        list = new ArrayList<>();
//        btn_voice.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        btn_voice.setBackgroundResource(R.mipmap.dialog_voice);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        btn_voice.setBackgroundResource(R.mipmap.undialog_voice);
//                        break;
//                }
//                return false;
//            }
//        });
        for (int i = 0; i < 9; i++) {
            list.add(i);
        }
//        tv_com.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tv_com.setTextColor(Color.BLACK);
//            }
//        });
        if(isInEdit){
            tv_com.setText("编辑按钮");
        }
        addComlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 tv_com.setTextColor(Color.parseColor("#5dbdf5"));
                 tv_file.setTextColor(Color.WHITE);
                iv_com.setBackgroundResource(R.mipmap.add_btn);
                 iv_file.setBackgroundResource(R.mipmap.un_dialogfile);
                layoutLeft.setVisibility(View.VISIBLE);
                layoutRight.setVisibility(View.GONE);
                isFolder =false;
            }
        });
        addFilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_com.setTextColor(Color.WHITE);
                tv_file.setTextColor(Color.parseColor("#5dbdf5"));
                iv_com.setBackgroundResource(R.mipmap.unadd_btn);
                iv_file.setBackgroundResource(R.mipmap.dialog_file);
                layoutLeft.setVisibility(View.GONE);
                layoutRight.setVisibility(View.VISIBLE);
                isFolder = true;
            }
        });

        if(isFile){
            addFilelayout.setVisibility(View.GONE);
        }
        adapter = new DialogGridAdapter(context, list);
        gridView.setAdapter(adapter);

    }

    public void setEdtNameClick(View.OnClickListener listener){
        edt_name.setOnClickListener(listener);
    }

    public void setVoiceTouchListener(View.OnTouchListener listener) {
        btn_voice.setOnTouchListener(listener);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        gridView.setOnItemClickListener(listener);
    }

    public void setSureButtonListener(View.OnClickListener listener) {
        btn_sure.setOnClickListener(listener);
    }
    public DialogGridAdapter getAdapter() {
        return adapter;
    }

    public String getName() {
        return edt_name.getText().toString();
    }
    public EditText getEdt_com(){
        return edt_com;
    }
    public boolean isFolder(){
        return isFolder;
    }
    public String getFolderName(){
        return edt_file.getText().toString();
    }
    public void SetFile(boolean isFile){
        this.isFile = isFile;
    }
     public void setInEdit(boolean is){
         this.isInEdit = is;
     }
    public boolean getIsFile(){
        return isFile;
    }
    public String getCom() {
        return edt_com.getText().toString();
    }
    public EditText getEdt_name(){ return edt_name;}
    public void setCom(String str){
        edt_com.setText(str);
    }
    public void setName(String str){edt_name.setText(str);}

}
