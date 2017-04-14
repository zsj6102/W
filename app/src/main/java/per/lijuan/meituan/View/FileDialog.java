package per.lijuan.meituan.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import per.lijuan.meituan.R;

/**
 * Created by admin on 2017/4/5.
 */

public class FileDialog extends Dialog {

    private EditText edt_name;
    private Button btn_sure;
    private Context context;
    public FileDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.filedialog_layout);
        btn_sure = (Button) findViewById(R.id.btn_complete);
        edt_name = (EditText)findViewById(R.id.edt_name);
    }
    public void setSureButtonListener(View.OnClickListener listener) {
        btn_sure.setOnClickListener(listener);
    }
    public String getName() {
        return edt_name.getText().toString();
    }
}
