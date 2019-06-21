package bluetooth.jie.com.jie_bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import bluetooth.jie.com.jie_bluetooth.R;

public class SetTimesTipsDialog extends Dialog {

    private static final String TAG = "蓝牙模块";
    public Context mContext;
    View.OnClickListener onClickListener;

    public SetTimesTipsDialog(Context context) {
        super(context);
    }

    public SetTimesTipsDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        this.onClickListener = onClickListener;
    }

    public SetTimesTipsDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_times_tips);
        findViewById(R.id.rootLayout).setOnClickListener(onClickListener);
    }

}
