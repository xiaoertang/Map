/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.map.liteapp;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.struct.BNTTsInitConfig;
import com.example.map.liteapp.controlwindow.ControlBoardWindow;

import java.io.File;

public class ONApplication extends Application implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = "BNSDKSimpleDemo";
    public static final String APP_FOLDER_NAME = TAG;
    public int appCount;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.GCJ02);
        initNavi();
        registerActivityLifecycleCallbacks(this);
    }

    private void initNavi() {
        // 针对单次有效的地图设置项 - DemoNaviSettingActivity
        BNDemoUtils.setBoolean(this, BNDemoUtils.KEY_gb_routeSort, true);
        BNDemoUtils.setBoolean(this, BNDemoUtils.KEY_gb_routeSearch, true);
        BNDemoUtils.setBoolean(this, BNDemoUtils.KEY_gb_moreSettings, true);

        if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
            return;
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(this,
                getExternalFilesDir(null).getPath(),
                APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {

                    @Override
                    public void onAuthResult(int status, String msg) {
                        String result;
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Log.e(TAG, result);
                    }

                    @Override
                    public void initStart() {
                        Log.e(TAG, "initStart");
                    }

                    @Override
                    public void initSuccess() {
                        Log.e(TAG, "initSuccess");
                        BaiduNaviManagerFactory.getBaiduNaviManager().enableOutLog(true);
                        String cuid = BaiduNaviManagerFactory.getBaiduNaviManager().getCUID();
                        Log.e(TAG, "cuid = " + cuid);
                        // 初始化tts
                        initTTS();
                        sendBroadcast(new Intent("com.navi.ready"));
                    }

                    @Override
                    public void initFailed(int errCode) {
                        Log.e(TAG, "initFailed-" + errCode);
                    }
                });
    }

    private void initTTS() {
        // 使用内置TTS
        BNTTsInitConfig config = new BNTTsInitConfig.Builder()
                .context(getApplicationContext())
                .sdcardRootPath(getSdcardDir())
                .appFolderName("map")
                .appId("25677896")
                .appKey("MdDVNTMa8UkXEeNB6a8evHLOPwBdjCHW")
                .secretKey("cwLrQSgeWOzyyWkQr5c8m8Hr7RZznhx9")
                .build();
        BaiduNaviManagerFactory.getTTSManager().initTTS(config);
    }

    private String getSdcardDir() {
        if (Build.VERSION.SDK_INT >= 29) {
            // 如果外部储存可用 ,获得外部存储路径
            File file = getExternalFilesDir(null);
            if (file != null && file.exists()) {
                return file.getPath();
            } else {
                return getFilesDir().getPath();
            }
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        appCount++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        appCount--;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                if (appCount == 0) {
                    if (ControlBoardWindow.getInstance().isShown) {
                        ControlBoardWindow.getInstance().hidePopupWindow();
                    }
                } else {
                    if (!ControlBoardWindow.getInstance().isShown) {
                        ControlBoardWindow.getInstance().showPopupWindow(this);
                    }
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
