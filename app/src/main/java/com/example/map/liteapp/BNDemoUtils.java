/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.example.map.liteapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import com.example.map.liteapp.activity.DemoGuideActivity;


public class BNDemoUtils {

    public static final int NORMAL = 1 << 1;
    public static final int ANALOG = 1 << 2;
    public static final int EXTGPS = 1 << 3;

    public static final String KEY_gb_iconset = "gb_iconset"; // 用于sp储存 定制icon 开关状态
    public static final String KEY_gb_iconshow = "gb_iconshow"; // icon显示 开关状态
    public static final String KEY_gb_carnum = "gb_carnum"; // 设置车牌 开关状态
    public static final String KEY_gb_carnumtxt = "gb_carnumtxt"; // 设置的车牌
    public static final String KEY_gb_cariconoffset = "gb_cariconoffset"; // 车标偏移 开关状态
    public static final String KEY_gb_cariconoffset_x = "gb_cariconoffset_x"; // 车标偏移x坐标
    public static final String KEY_gb_cariconoffset_y = "gb_cariconoffset_y"; // 车标偏移y坐标
    public static final String KEY_gb_margin = "gb_margin"; // 屏幕边距 开关状态
    public static final String KEY_gb_routeSort = "gb_routeSort"; // 路线偏好 开关状态
    public static final String KEY_gb_routeSearch = "gb_routeSearch"; // 沿途检索 开关状态
    public static final String KEY_gb_moreSettings = "gb_moreSettings"; // 更多设置 开关状态
    public static final String KEY_gb_seeall = "gb_seeall"; // 用于sp储存 定制icon 开关状态


    public static void gotoNavi(Activity activity) {
        Intent it = new Intent(activity, DemoGuideActivity.class);
        activity.startActivity(it);
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static boolean checkDeviceHasNavigationBar(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = context.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(context)
                    .hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap
                    .deviceHasKey(KeyEvent.KEYCODE_BACK);
            return hasMenuKey || hasBackKey;
        }
    }

    public static int getNavigationBarHeight(Activity context) {
        int result = 0;
        if (checkDeviceHasNavigationBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height",
                    "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp =
                context.getSharedPreferences(ONApplication.APP_FOLDER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp =
                context.getSharedPreferences(ONApplication.APP_FOLDER_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp =
                context.getSharedPreferences(ONApplication.APP_FOLDER_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences sp =
                context.getSharedPreferences(ONApplication.APP_FOLDER_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static Boolean getBoolean(Context context, String key, boolean defaultreturn) {
        SharedPreferences sp =
                context.getSharedPreferences(ONApplication.APP_FOLDER_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultreturn);
    }

}
