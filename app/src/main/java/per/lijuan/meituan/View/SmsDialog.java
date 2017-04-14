package per.lijuan.meituan.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import per.lijuan.meituan.R;

/**
 * Created by admin on 2017/4/7.
 */

public class SmsDialog extends Dialog{
    private Context context;

    private TextView tv_name;
    private TextView tv_phone;
    private Button btn_cancel;
    private Button btn_sure;
    private EditText editText;
    public SmsDialog(Context context,int theme) {
        super(context,theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.sms_layout);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_sure = (Button)findViewById(R.id.btn_phone);
        tv_name = (TextView)findViewById(R.id.name);
        tv_phone = (TextView)findViewById(R.id.phone);
        editText = (EditText)findViewById(R.id.edt_edt);
    }
    public void setSureButtonListener(View.OnClickListener listener) {
        btn_sure.setOnClickListener(listener);
    }
    public void setCancelButtonListener(View.OnClickListener listener){
        btn_cancel.setOnClickListener(listener);
    }
    public void setUser(String str){
        tv_name.setText(str);
    }
    public void setPhone(String str){
        tv_phone.setText(str);
    }
    public String getEditContent(){
        return editText.getText().toString();
    }
    public String getPhone(){
        return tv_phone.getText().toString();
    }

}
