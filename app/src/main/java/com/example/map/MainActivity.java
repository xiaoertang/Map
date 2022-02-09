package com.example.map;


import android.app.Application;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.map.overlayutil.WalkingRouteOverlay;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private Button btn_change;
    private Button btn_btn_traffic;
    private Button btn_opt;
    private Button btn_loc;
    // 定位图层显示方式
    public MyLocationConfiguration.LocationMode mCurrentMode;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;

    // 定位相关
    private LocationClient mLocClient;
    private MyLocationListener myListener = new MyLocationListener();

    //定位模式
    private TextView mTextView = null;
    private boolean isFirstLoc = true;  //是否首次定位
    private LocationClient mLocationClient = null;

    private MyLocationData myLocationData;
    //    -----------------------------------------------------------------------------------
    //搜索模块
    private EditText start_edit, end_edit;
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    RoutePlanSearch mSearch = null;
    RouteLine route = null;
    private String loaclcity = null;
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private TextView popupText = null, driver_city; // 泡泡view
    boolean useDefaultIcon = false;
    //    -----------------------------------------------------------------------------------

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = findViewById(R.id.bmapView);
        //地图对象
        mBaiduMap = mMapView.getMap();
        //设置主页导航栏
       // toolbar = findViewById(R.id.toolbar_main);


        Application application = getApplication();

        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        // 定位初始化
        initLocation();
        initView();

        //按钮的监听器
        btn_change.setOnClickListener(clickListener);
        btn_btn_traffic.setOnClickListener(clickListener);
        btn_opt.setOnClickListener(clickListener);
        //btn_loc.setOnClickListener(clickListener);


    }

    /**
     * 定位初始化
     */
    public void initLocation() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        //刷新时间
        option.setScanSpan(1000);
        //获取地理文职
        option.setIsNeedAddress(true);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置是否需要设备方向结果
        option.setNeedDeviceDirect(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        option.setIsNeedAltitude(true);
        // 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void initView() {
        btn_change = findViewById(R.id.btn_change);
        btn_btn_traffic = findViewById(R.id.btn_traffic);
        btn_opt = findViewById(R.id.more);
        btn_loc = findViewById(R.id.btn_loc);
//        mTextView = findViewById(R.id.locmsg);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(mCurrentLat)
                    .longitude(mCurrentLon)
                    .build();
            mBaiduMap.setMyLocationData(myLocationData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_change) {
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    btn_change.setText("打开普通地图");
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    btn_change.setText("打开卫星地图");
                }
            } else if (v.getId() == R.id.btn_traffic) {
                if (!mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(true);
                    btn_btn_traffic.setText("关闭路况");
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                    btn_btn_traffic.setText("打开路况");
                }
            } else if (v.getId() == R.id.more) {
                //添加滑动窗口函数，在activity_main.xml中由于添加了NavigationView组件，
                // 看书到底是要添加什么依赖。
                //其中添加了相应的图片  和nav_menu.xml和nav_header.xml
                //而且之前的标注北京图片因此被取消
                //点击more  出现弹出框，将另外两个页面都加载到此项目上。
                Intent intent = new Intent(MainActivity.this, CustomerMenu.class);
                startActivity(intent);
            } else if (v.getId() == R.id.btn_loc) {
                //开启地图定位图层
                mLocationClient.start();
                mTextView.setVisibility(View.VISIBLE);
            }
        }
    };

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            mBaiduMap.setMyLocationEnabled(true);
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
//            mCurrentCity = location.getCity();
//            mCurrentDistrict.setText(location.getDistrict());//获取当前城市
            mCurrentAccracy = location.getRadius();
            myLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
                    .direction(mCurrentDirection)// 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();

            mBaiduMap.setMyLocationData(myLocationData);
            //判断是否是第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(16.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            StringBuffer sb = new StringBuffer(256);
            sb.append("time:");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                //GPS定位
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight:");
                sb.append(location.getAltitude());
                sb.append("\ndirection:");
                sb.append(location.getDirection());
                sb.append("\naddr:");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe:");
                sb.append("GPS定位成功！");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\noperationer:");
                sb.append(location.getOperators());
                sb.append("\ndescribe:");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                //离线定位
                sb.append("\ndescribe:");
                sb.append("离线定位成功");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe:");
                sb.append("server定位失败，没有对应的位置信息");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe:");
                sb.append("网络连接失败");
            }
            mTextView.setText(sb);
        }
    }


    private void addOpt() {
        LatLng point = new LatLng(mCurrentLat, mCurrentLon);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.region);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
        mBaiduMap.addOverlay(option);
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
