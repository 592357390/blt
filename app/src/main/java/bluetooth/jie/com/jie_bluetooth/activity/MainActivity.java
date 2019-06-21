package bluetooth.jie.com.jie_bluetooth.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bluetooth.jie.com.jie_bluetooth.ProgressSeekBar;
import bluetooth.jie.com.jie_bluetooth.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "蓝牙模块";
    BluetoothGatt mBluetoothGatt;
    BluetoothGattCharacteristic mCharacteristic = null;
    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        //当连接状态发生改变
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "onConnectionStateChange: " + newState + "连接状态改变");
            boolean b = gatt.discoverServices();
            Log.i(TAG, "discoverServices 开启发现服务: " + b);
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i(TAG, "onServicesDiscovered: " + status);
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                Log.i(TAG, "getServices: " + bluetoothGattService.getUuid());

                List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    int charaProp = characteristic.getProperties();
                    Log.i(TAG, "characteristic: " + characteristic.getUuid());
                    Log.i(TAG, "characteristic : " + charaProp);
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        Log.e(TAG, "characteristic:" + characteristic.getUuid());
                        gatt.setCharacteristicNotification(characteristic, true);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        Log.i(TAG, "PROPERTY_WRITE: " + characteristic.getUuid());
                        if (characteristic.getUuid().toString().contains("ffe1")) {
                            mCharacteristic = characteristic;
                        }
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        gatt.setCharacteristicNotification(
                                characteristic, true);
                    }
                }
            }


        }

        //调用mBluetoothGatt.readCharacteristic(characteristic)读取数据回调，在这里面接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead: ");
        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicWrite: " + status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);


            byte[] value = characteristic.getValue();
            Log.i(TAG, "onCharacteristicChanged: cal = " + Arrays.toString(characteristic.getValue()));
            short i = (short) ((value[4] & 0xFF) << 8 | (value[5] & 0xff));
            Log.i(TAG, "onCharacteristicChanged: " + i);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor读
            super.onDescriptorRead(gatt, descriptor, status);
            Log.i(TAG, "onDescriptorRead: ");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {//descriptor写
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.i(TAG, "onDescriptorWrite: ");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothGatt> gatts = new ArrayList<>();

    /**
     * 开始获取蓝牙适配器
     */
    private void startBle() {
        Log.i(TAG, "startBle: 开始获取蓝牙适配器");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 100);
//            boolean enable = bluetoothAdapter.enable();
//            if (enable) {
//                Log.i(TAG, "startBle: 开启手机蓝牙状态");
//            }
        } else {
            Log.i(TAG, "startBle: 蓝牙状态已经开启");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                checkBleState();
                Toast.makeText(this, "蓝牙已经打开", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "蓝牙请求取消", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void checkBleState() {
        int state = bluetoothAdapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_ON:
                Log.i(TAG, "checkBleState:正在打开蓝牙");
                break;
            case BluetoothAdapter.STATE_ON:
                Log.i(TAG, "checkBleState: 蓝牙已经打开");
                break;
            case BluetoothAdapter.STATE_OFF:
                break;

        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.beginBle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Android M Permission check
                    if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                }
                startBle();
            }
        });

        findViewById(R.id.beginSearchBle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginSearch();
            }
        });

        findViewById(R.id.write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCharacteristic != null) {
                    byte[] value = new byte[20];
                    value[0] = (byte) 0x00;
                    mCharacteristic.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    mCharacteristic.setValue("123123213".getBytes());
                    boolean b = mBluetoothGatt.writeCharacteristic(mCharacteristic);
                    Log.i(TAG, "发送数据: 发送服务" + b);
                } else {
                    Log.i(TAG, "发送数据: 无可发送服务");
                }
            }
        });

    }

    /**
     * 开始搜索蓝牙设别
     */
    private void beginSearch() {
        checkBleState();
        boolean discovery = bluetoothAdapter.startDiscovery();
        Log.i(TAG, "beginSearch: 发现状态" + discovery);
        if (bluetoothAdapter.isDiscovering()) {
            Log.i(TAG, "beginSearch: 开始获取附近蓝牙设配");
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (result.getDevice().getName() != null) {
                        Log.i(TAG, "onScanResult: 搜索结果 = " + result.getDevice().getName());
                        if (result.getDevice().getName().equals("JDY-08") || result.getDevice().getName().contains("d8a")) {
                            String address = result.getDevice().getAddress();
                            if (bluetoothAdapter.isDiscovering()) {
                                Log.i(TAG, "onScanResult: 取消搜索");
                                bluetoothAdapter.cancelDiscovery();
                            }
                            //判断设备是否配对，没有配对在配，配对了就不需要配了
                            if (result.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
                                mBluetoothGatt = result.getDevice().connectGatt(MainActivity.this, true, mBluetoothGattCallback);
                                boolean connect = mBluetoothGatt.connect();
                                Log.i(TAG, "onScanResult: 连接状态" + connect);

                            } else {
                                mBluetoothGatt = result.getDevice().connectGatt(MainActivity.this, true, mBluetoothGattCallback);
                                Log.i(TAG, "onScanResult: 连接已经配对");
                            }
                            if (mBluetoothGatt != null) {
                                gatts.add(mBluetoothGatt);
                            }
                        }
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.i(TAG, "onScanFailed: 搜索设备失败");
                }
            });
        }
    }

    public void dataSend() {
        boolean b = mBluetoothGatt.discoverServices();

    }
}


