package com.proton.card.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.proton.ecgcard.connector.EcgCardManager;
import com.proton.ecgcard.connector.callback.DataListener;
import com.proton.ecgcard.connector.utils.CardFirmwareOperator;
import com.proton.ecgcard.connector.utils.UpdateFailType;
import com.wms.ble.bean.ScanResult;
import com.wms.ble.callback.OnConnectListener;
import com.wms.ble.callback.OnScanListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity--->";
    public static final String localFirmwareFilePath = "/storage/emulated/0/Android/data/com.proton.carepatchecg/files/fireware/5/V1.0.3";
    public static final String updateMac = "D0:2E:AB:62:4C:B8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EcgCardManager.init(this);

        //需要获取定位权限，否则搜索不到设备
        PermissionUtils.getLocationPermission(this);
        PermissionUtils.getReadAndWritePermission(this);

        findViewById(R.id.btnConnect).setOnClickListener(v -> {
            testCardConnect();
        });

        findViewById(R.id.btnUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCard();
            }
        });
    }

    private void scanCardDevice() {
        EcgCardManager.scanDevice(new OnScanListener() {
            @Override
            public void onScanStart() {
                super.onScanStart();
                Log.e(TAG, "onScanStart()");
            }

            @Override
            public void onDeviceFound(ScanResult scanResult) {
                super.onDeviceFound(scanResult);
                Log.e(TAG, "onDeviceFound(),mac:" + scanResult.getMacaddress());
            }

            @Override
            public void onScanStopped() {
                super.onScanStopped();
                Log.e(TAG, "onScanStopped()");
            }

            @Override
            public void onScanCanceled() {
                super.onScanCanceled();
                Log.e(TAG, "onScanCanceled()");
            }
        });
    }

    private void testCardConnect() {

        EcgCardManager ecgCardManager = EcgCardManager.getInstance("0C:61:CF:C1:2D:52");
        ecgCardManager.setDataListener(new DataListener() {
            @Override
            public void receiveEcgRawData(byte[] data) {
                //Log.e(TAG, "蓝牙数据:" + data.length);
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
            public void onConnectSuccess(boolean isNewUUID) {
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

    private void updateCard() {
        CardFirmwareOperator firmwareOperator = CardFirmwareOperator.getInstance();
        firmwareOperator.update(this, localFirmwareFilePath, updateMac, new CardFirmwareOperator.UpdateCallback() {
            @Override
            public void notFoundDevice() {
                Log.d(TAG, "notFoundDevice: 没有找到搜索的设备");
            }

            @Override
            public void onConnectSuccess() {
                Log.d(TAG, "onConnectSuccess: 连接成功");
            }

            @Override
            public void onConnectFail() {
                Log.d(TAG, "onConnectFail: 连接失败");
            }

            @Override
            public void onVerifyOad() {
                Log.d(TAG, "onVerifyOad: 正在校验oad...");//R2F心电卡升级过程中需要，因为耗时40s左右
            }

            @Override
            public void onSuccess(String mac) {
                Log.d(TAG, "onSuccess: mac==" + mac);
            }

            @Override
            public void onFail(String msg, UpdateFailType type) {
                Log.d(TAG, "onFail: " + msg);
            }

            @Override
            public void onProgress(float progress) {
                Log.d(TAG, "onProgress: " + progress);
            }
        });
    }
}