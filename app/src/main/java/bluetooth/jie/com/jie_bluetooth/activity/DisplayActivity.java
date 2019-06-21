package bluetooth.jie.com.jie_bluetooth.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bluetooth.jie.com.jie_bluetooth.BaseActivity;
import bluetooth.jie.com.jie_bluetooth.BluetoothLeService;
import bluetooth.jie.com.jie_bluetooth.ProgressSeekBar;
import bluetooth.jie.com.jie_bluetooth.R;
import bluetooth.jie.com.jie_bluetooth.dialog.ScanDeviceDialog;
import bluetooth.jie.com.jie_bluetooth.utils.AutoUtils;
import bluetooth.jie.com.jie_bluetooth.utils.RxBus;
import io.reactivex.functions.Consumer;

public class DisplayActivity extends BaseActivity {

    private static final String TAG = "蓝牙模块";
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isConnect = false;
    private ImageView leftConnectStateImg;
    private ImageView rightConnectStateImg;
    private TextView leftNum;
    private TextView rightNum;
    private ImageView leftLevelState;
    private ImageView rightLevelState;
    private Switch leftBtnState;
    private Switch rightBthState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AutoUtils.setSize(this, false, 1024, 768);
        setContentView(R.layout.activity_display);
        AutoUtils.auto(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        RxBus.getDefault().toObservable(String.class).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (s.equals("timesSetting")) {
//                    setTimesDialog = new SetTimesDialog(DisplayActivity.this, new TimesAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(int data) {
//                            writeTimesGatt(data);
//                        }
//                    });
//                    setTimesDialog.show();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });

        findViewById(R.id.beginMeasure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnect) {
                    new ScanDeviceDialog(DisplayActivity.this, R.style.dialog).show();
                }
            }
        });

        findViewById(R.id.exitApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        leftConnectStateImg = ((ImageView) findViewById(R.id.left_connect_state));
        rightConnectStateImg = ((ImageView) findViewById(R.id.right_connect_state));
        leftNum = ((TextView) findViewById(R.id.leftNum));
        rightNum = ((TextView) findViewById(R.id.rightNum));
        rightLevelState = ((ImageView) findViewById(R.id.right_level_state));
        leftLevelState = ((ImageView) findViewById(R.id.left_level_state));
        leftBtnState = ((Switch) findViewById(R.id.left_bth_state));
        rightBthState = ((Switch) findViewById(R.id.right_bth_state));

        ((ProgressSeekBar) findViewById(R.id.leftSeekBar)).setLocation(5);
        ((ProgressSeekBar) findViewById(R.id.rightSeekBar)).setLocation(5);

        TimerTask timerTask = new TimerTask() {
            int i=-10;
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        i++;
                        Log.i(TAG, "run: "+i);

                        ((ProgressSeekBar) findViewById(R.id.leftSeekBar)).setLocation(i);
                        ((ProgressSeekBar) findViewById(R.id.rightSeekBar)).setLocation(i);
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,1000,2000);


        leftBtnState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new ScanDeviceDialog(DisplayActivity.this, R.style.dialog).show();
                    leftBtnState.setChecked(false);
                    isChooseLeft = true;
                    isChooseRight = false;
                } else {
                    for (BluetoothGatt bluetoothGatt : mBluetoothLeService.getGattArrayList()) {
                        if (bluetoothGatt.getDevice().getAddress().equals(leftAddress)) {
                            bluetoothGatt.disconnect();
                            break;
                        }
                    }
                }
            }
        });

        rightBthState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    new ScanDeviceDialog(DisplayActivity.this, R.style.dialog).show();
                    rightBthState.setChecked(false);
                    leftBtnState.setChecked(false);
                    isChooseLeft = true;
                    isChooseRight = false;
                } else {
                    for (BluetoothGatt bluetoothGatt : mBluetoothLeService.getGattArrayList()) {
                        if (bluetoothGatt.getDevice().getAddress().equals(rightAddress)) {
                            bluetoothGatt.disconnect();
                            break;
                        }
                    }
                }
            }
        });


        checkPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initConnectState();

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "蓝牙所需权限尚未打开，请前去设置中开启权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void writeTimesGatt(Integer data) {
        int i = 0;
        boolean leftWriteState = false;
        boolean rightWriteState = false;
//        if (mBluetoothLeService.getWriteGatt().size() != 2) {
//            Toast.makeText(this, "蓝牙写入服务不存在，请检查后重新连接蓝牙设备", Toast.LENGTH_SHORT).show();
//            return;
//        }
        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : mBluetoothLeService.getWriteGatt()) {
            if (bluetoothGattCharacteristic != null) {
                byte[] value = new byte[20];
                value[0] = (byte) 0x00;
                bluetoothGattCharacteristic.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                bluetoothGattCharacteristic.setValue(data.toString().getBytes());
                if (i == 0) {
                    BluetoothGatt gatt = mBluetoothLeService.getGattArrayList().get(i);
                    leftWriteState = gatt.writeCharacteristic(bluetoothGattCharacteristic);
                }
                if (i == 1 && mBluetoothLeService.getGattArrayList().size() > 1) {
                    BluetoothGatt gatt = mBluetoothLeService.getGattArrayList().get(i);
                    rightWriteState = gatt.writeCharacteristic(bluetoothGattCharacteristic);
                }
                Log.i(TAG, "发送数据: 发送服务左侧写入状态 = " + leftWriteState + " ---右侧写入状态:" + rightWriteState);
                i++;
            } else {
                Log.i(TAG, "发送数据: 无可发送服务");
            }
        }

        if (leftWriteState) {
            Toast.makeText(this, "左侧设备写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "左侧设备写入error", Toast.LENGTH_SHORT).show();
        }

        if (rightWriteState) {
            Toast.makeText(this, "右侧设备写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "右侧设备写入error", Toast.LENGTH_SHORT).show();
        }


//        if (leftWriteState && rightWriteState) {
//            Toast.makeText(this, "更新速率设置成功", Toast.LENGTH_SHORT).show();
//            updateTimes.setText("当前数据更新速率:    " + data + "s/次");
//            setTimesDialog.dismiss();
//        } else {
//            Toast.makeText(this, "更新速率设置失败请检查后重试", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void bleInitSuccess() {
        super.bleInitSuccess();
        mBluetoothAdapter = mBluetoothLeService.getmBluetoothAdapter();
    }

    boolean isChooseLeft = false;
    boolean isChooseRight = false;

    String leftAddress = "";
    String rightAddress = "";

    @Override
    public void onConnected(String address) {
        super.onConnected(address);
        initConnectState();

        if (isChooseLeft) {
            leftAddress = address;
            leftBtnState.setChecked(true);
        }

        if (isChooseRight) {
            rightAddress = address;
            rightBthState.setChecked(true);
        }

        isChooseLeft = false;
        isChooseRight = false;
    }

    @Override
    public void onBleServicesDeal(List<BluetoothGattService> supportedGattServices) {
        super.onBleServicesDeal(supportedGattServices);

    }


    private void initConnectState() {
        if (mBluetoothLeService != null) {
            if (mBluetoothLeService.getAddress().size() == 0) {
                leftConnectStateImg.setImageResource(R.mipmap.ic_un_plane);
                rightConnectStateImg.setImageResource(R.mipmap.ic_un_plane);
                isConnect = false;
            }
            if (mBluetoothLeService.getAddress().size() == 1) {
                leftConnectStateImg.setImageResource(R.mipmap.ic_plane);
                rightConnectStateImg.setImageResource(R.mipmap.ic_un_plane);
                isConnect = false;
            }
            if (mBluetoothLeService.getAddress().size() == 2) {
                leftConnectStateImg.setImageResource(R.mipmap.ic_plane);
                rightConnectStateImg.setImageResource(R.mipmap.ic_plane);
                isConnect = true;
            }
        } else {
            leftConnectStateImg.setImageResource(R.mipmap.ic_un_plane);
            rightConnectStateImg.setImageResource(R.mipmap.ic_un_plane);
            isConnect = false;
        }

    }

    int j = 0;

    @Override
    public void onBaleReceiveData(byte[] data, String address) {
        super.onBaleReceiveData(data, address);

        Log.i(TAG, "onBaleReceiveData: " + Arrays.toString(data));


        Log.i(TAG, "onBaleReceiveData: " + (data[0] & 0xff));


        if (data.length <= 2 || (data[0] & 0xff) != 170 || (data[1] & 0xff) != 251) {
            Toast.makeText(this, "数据格式错误，请校验后重新发送！", Toast.LENGTH_SHORT).show();
            return;
        }
        short i = (short) ((data[4] & 0xFF) << 8 | (data[5] & 0xff));

        double height = i / 10.0;

        if (mBluetoothLeService.getAddress() == null || mBluetoothLeService.getAddress().size() == 0) {
            return;
        }

        if (address.equals(leftAddress)) {
            // 左侧数据
            leftNum.setText(height + "");
            Log.i(TAG, "左侧数据: " + height);
            if (height <= 2.0 && height >= -2.0) {
                leftLevelState.setImageResource(R.mipmap.ic_plane);
            } else {
                leftLevelState.setImageResource(R.mipmap.ic_un_plane);
            }
        }
        if (address.equals(rightAddress)) {
            //右侧数据
            rightNum.setText(height + "");
            Log.i(TAG, "右侧数据: " + height);
            if (height <= 2.0 && height >= -2.0) {
                rightLevelState.setImageResource(R.mipmap.ic_plane);
            } else {
                rightLevelState.setImageResource(R.mipmap.ic_un_plane);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeService.close();
        unbindService(mServiceConnection);
        unregisterReceiver(mGattUpdateReceiver);
    }
}
