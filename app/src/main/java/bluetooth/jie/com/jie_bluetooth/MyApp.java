package bluetooth.jie.com.jie_bluetooth;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import java.util.Arrays;

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        UMConfigure.setLogEnabled(true);
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, "5cfde902570df3466c000e7d", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

        String[] testDeviceInfo = getTestDeviceInfo(this);
        Log.i("UMConfigure", "onCreate: "+ Arrays.toString(testDeviceInfo));
    }

    public static String[] getTestDeviceInfo(Context context){
        String[] deviceInfo = new String[2];
        try {
            if(context != null){
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
            }
        } catch (Exception e){
            Log.i("UMConfigure", "getTestDeviceInfo: ");
        }
        return deviceInfo;
    }
}
