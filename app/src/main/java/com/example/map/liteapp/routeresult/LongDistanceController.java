package com.example.map.liteapp.routeresult;

import android.view.View;

import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.example.map.R;

/**
 * Author: v_duanpeifeng
 * Time: 2020-05-19
 * Description:
 */
public class LongDistanceController implements View.OnClickListener {

    // 记录按钮的点击状态
    private boolean isClickCity;
    private boolean isClickRoute;
    private boolean isClickService;
    private boolean isClickCheckpoint;
    private boolean isClickWeather;

    public LongDistanceController(View rootView) {
        rootView.findViewById(R.id.city).setOnClickListener(this);
        rootView.findViewById(R.id.route).setOnClickListener(this);
        rootView.findViewById(R.id.service).setOnClickListener(this);
        rootView.findViewById(R.id.checkpoint).setOnClickListener(this);
        rootView.findViewById(R.id.weather).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city:
                BaiduNaviManagerFactory.getRouteResultManager().handleCityClick(!isClickCity);
                isClickCity = !isClickCity;
                isClickRoute = false;
                isClickService = false;
                isClickCheckpoint = false;
                isClickWeather = false;
                break;
            case R.id.route:
                BaiduNaviManagerFactory.getRouteResultManager().handleRouteClick(!isClickRoute);
                isClickRoute = !isClickRoute;
                isClickCity = false;
                isClickService = false;
                isClickCheckpoint = false;
                isClickWeather = false;
                break;
            case R.id.service:
                BaiduNaviManagerFactory.getRouteResultManager().handleServiceClick(!isClickService);
                isClickService = !isClickService;
                isClickCity = false;
                isClickRoute = false;
                isClickCheckpoint = false;
                isClickWeather = false;
                break;
            case R.id.checkpoint:
                BaiduNaviManagerFactory.getRouteResultManager()
                        .handleCheckpointClick(!isClickCheckpoint);
                isClickCheckpoint = !isClickCheckpoint;
                isClickCity = false;
                isClickRoute = false;
                isClickService = false;
                isClickWeather = false;
                break;
            case R.id.weather:
                BaiduNaviManagerFactory.getRouteResultManager().handleWeatherClick(!isClickWeather);
                isClickWeather = !isClickWeather;
                isClickCity = false;
                isClickRoute = false;
                isClickService = false;
                isClickCheckpoint = false;
                break;
            default:
                break;
        }
    }
}
