package bluetooth.jie.com.jie_bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import bluetooth.jie.com.jie_bluetooth.R;
import bluetooth.jie.com.jie_bluetooth.TimesAdapter;

public class SetTimesDialog extends Dialog {

    private static final String TAG = "蓝牙模块";
    TimesAdapter.OnItemClickListener onClickListener;
    private RecyclerView recyclerView;
    private Context mContext;
    private TimesAdapter adapter;

    public SetTimesDialog(Context context, TimesAdapter.OnItemClickListener listener) {
        super(context);
        mContext = context;
        this.onClickListener = listener;
    }


    public SetTimesDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_set_times);
        recyclerView = ((RecyclerView) findViewById(R.id.timesList));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new TimesAdapter(mContext);
        adapter.setOnItemClickListener(onClickListener);
        recyclerView.setAdapter(adapter);


    }

}
