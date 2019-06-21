package bluetooth.jie.com.jie_bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import bluetooth.jie.com.jie_bluetooth.R;

public class ConnectTipsDialog extends Dialog {

    private static final String TAG = "蓝牙模块";
    public Context mContext;

    public ConnectTipsDialog(Context context) {
        super(context);
    }

    public ConnectTipsDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_connect_tips);
        findViewById(R.id.rootLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
