package bluetooth.jie.com.jie_bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import bluetooth.jie.com.jie_bluetooth.R;
import bluetooth.jie.com.jie_bluetooth.activity.ChooseActivity;
import bluetooth.jie.com.jie_bluetooth.activity.ScanActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;

public class ScanDeviceDialog extends Dialog implements QRCodeView.Delegate {

    private static final String TAG = "蓝牙模块";
    public Context mContext;

    public ScanDeviceDialog(Context context) {
        super(context);
    }

    public ScanDeviceDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scan);
        findViewById(R.id.scanBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ScanActivity.class));
                dismiss();
            }
        });

        findViewById(R.id.txtBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ChooseActivity.class));
                dismiss();
            }
        });
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "onScanQRCodeSuccess: " + result);
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        Log.i(TAG, "onCameraAmbientBrightnessChanged: " + isDark);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }
}
