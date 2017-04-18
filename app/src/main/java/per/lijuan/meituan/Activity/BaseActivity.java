package per.lijuan.meituan.Activity;


import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.blankj.utilcode.utils.ToastUtils;

import com.tbruyelle.rxpermissions2.RxPermissions;

import per.lijuan.meituan.R;

/**
 * Created by admin on 2017/4/17.
 */

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //6.0权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions
                    .requestEach(
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.SEND_SMS,
                            Manifest.permission.RECORD_AUDIO)
                    .subscribe(permission -> {
                        if (permission.granted) {
                            // `permission.name` is granted !
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                            ToastUtils.showShortToast(getString(R.string.error_grant));
                        }

                    });
        }
        ;
    }
}