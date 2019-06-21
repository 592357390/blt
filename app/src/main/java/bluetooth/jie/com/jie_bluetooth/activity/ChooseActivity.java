package bluetooth.jie.com.jie_bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import bluetooth.jie.com.jie_bluetooth.BaseActivity;
import bluetooth.jie.com.jie_bluetooth.BleListAdapter;
import bluetooth.jie.com.jie_bluetooth.BluetoothLeService;
import bluetooth.jie.com.jie_bluetooth.R;
import bluetooth.jie.com.jie_bluetooth.dialog.SetTimesTipsDialog;

public class ChooseActivity extends BaseActivity {

    private static final String TAG = "蓝牙模块";

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;
    SetTimesTipsDialog setTimesTipsDialog;
    private BleListAdapter listAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listAdapter.setData(device);
                        }
                    });
                }
            };
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void bleInitSuccess() {
        super.bleInitSuccess();
        mBluetoothAdapter = mBluetoothLeService.getmBluetoothAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        scanLeDevice(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        mHandler = new Handler();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refreshLayout = ((SwipeRefreshLayout) findViewById(R.id.refreshLayout));
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listAdapter.getData().clear();
                listAdapter.notifyDataSetChanged();
                scanLeDevice(true);
            }
        });

        recyclerView = ((RecyclerView) findViewById(R.id.btlList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

//        new ConnectTipsDialog(this, R.style.dialog).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initializes list view adapter.
        listAdapter = new BleListAdapter(this);
        listAdapter.setOnItemClickListener(new BleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!isConnectting) {
                    mBluetoothLeService.connect(listAdapter.getData().get(position).getAddress());
                }
            }
        });
        recyclerView.setAdapter(listAdapter);


        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }

    @Override
    public void onConnected(String address) {
        Log.i(TAG, "onConnected: " + address);
        isConnectting = false;
        listAdapter.setConnectAdds(address);
        listAdapter.notifyDataSetChanged();
        Toast.makeText(this, "蓝牙已链接", Toast.LENGTH_SHORT).show();
//        if (listAdapter.getConnectAdds().size() == 2) {
//        setTimesTipsDialog = new SetTimesTipsDialog(this, new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setTimesTipsDialog.dismiss();
//                finish();
//
//            }
//        });
//        setTimesTipsDialog.show();
//        }
        finish();
    }

    boolean isConnectting = false;

    @Override
    public void onBackPressed() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            refreshLayout.setRefreshing(true);

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            refreshLayout.setRefreshing(true);

        }
    }
}
