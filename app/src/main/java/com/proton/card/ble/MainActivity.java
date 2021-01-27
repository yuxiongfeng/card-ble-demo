package com.proton.card.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.proton.ecgcard.connector.EcgCardManager;
import com.proton.ecgcard.connector.callback.DataListener;
import com.wms.ble.callback.OnConnectListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //需要获取定位权限，否则搜索不到设备
        PermissionUtils.getLocationPermission(this);

        findViewById(android.R.id.content).setOnClickListener(v -> {
            testCardConnect();
        });
    }

    private void testCardConnect() {
        EcgCardManager.init(this);
        EcgCardManager ecgCardManager = EcgCardManager.getInstance("0C:61:CF:C7:E3:C8");
        ecgCardManager.setDataListener(new DataListener() {
            @Override
            public void receiveEcgRawData(byte[] data) {
                Log.e(TAG, "蓝牙数据:" + data.length);
            }

            @Override
            public void receivePackageNum(int packageNum) {
                Log.e(TAG, "包序: " + packageNum);
            }

            @Override
            public void receiveHardVersion(String version) {
                Log.e(TAG, "固件版本: " + version);
            }

            @Override
            public void receiveBattery(Integer battery) {
                Log.e(TAG, "电量: " + battery);
            }

            @Override
            public void receiveSerial(String serial) {
                Log.e(TAG, "序列号: " + serial);
            }

            @Override
            public void receiveTouchMode(int mode) {
                //0 未双手触摸，1双手触摸
                Log.e(TAG, "触摸模式: " + mode);
            }
        });
        ecgCardManager.connectEcgCard(new OnConnectListener() {
            @Override
            public void onConnectSuccess() {
                Log.e(TAG, "连接成功");
            }

            @Override
            public void onConnectFaild() {
                Log.e(TAG, "连接失败");
            }

            @Override
            public void onDisconnect(boolean b) {
                Log.e(TAG, "断开连接，是否手动断开:" + b);
            }
        });
    }
}