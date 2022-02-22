package com.example.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
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
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.map.entity.User;
import com.example.map.overlayutil.BikingRouteOverlay;
import com.example.map.overlayutil.DrivingRouteOverlay;
import com.example.map.overlayutil.OverlayManager;
import com.example.map.overlayutil.PoiOverlay;
import com.example.map.overlayutil.TransitRouteOverlay;
import com.example.map.overlayutil.WalkingRouteOverlay;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.litepal.crud.DataSupport;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class MainActivity extends AppCompatActivity implements OnMapClickListener, OnGetRoutePlanResultListener,
        OnClickListener, OnGetSuggestionResultListener, OnGetPoiSearchResultListener, OnItemClickListener, SensorEventListener {
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private double endLat = 0.0;
    private double endLon = 0.0;
    //两地之间的距离
    private double distance;
    // 浏览路线节点相关
    private String localcity;// 记录当前城市
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    //private Button findroute;// poi
    private Button findroute2;// 路线规划
    private Button navigation;//导航
    RouteLine route = null;   //路线
    OverlayManager routeOverlay = null; //路线叠加
    private Button requestLocButton;
    private LocationMode mCurrentMode;
    private Button my_back;// 返回按钮
    private LinearLayout edit_layout;// 底部目的地栏
    private LinearLayout choosemode;// 选择导航方式
    private ListView search_end;// 推荐目的地
    private LinearLayout guide_layout; //起点-终点搜索框
    private LinearLayout locationLayout;// 定位框
    private LinearLayout detailed;//详细说明
    private TextView needDistance;//距离
    private TextView needTime;//需要时间
    BitmapDescriptor mCurrentMarker;
    boolean useDefaultIcon = false;
    private TextView popupText = null, customer_city; // 泡泡view
    private TextView mylocation;
    private EditText start_edit, end_edit;
    boolean isFirstLoc = true; // 是否首次定位
    private Button my_login; //登录
    // 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    // 如果不处理touch事件，则无需继承，直接使用MapView即可
    // 地图控件
    private TextureMapView mMapView = null;
    private BaiduMap mBaiduMap;

    // 搜索相关
    RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    // 搜索周边
    private LinearLayout poilayout;
    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private ImageButton customer_find_btn;

    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;

    // 定位相关
    LocationClient mLocClient;
    LatLng currentPt;
    public MyLocationListenner myListener = new MyLocationListenner();

    // 点击地图事件
    private LinearLayout click_layout;
    private TextView endlocation;
    private Button go_end;
    private LatLng endPt;
    private GeoCoder geoCoder;

    // 动画效果
    Animation slide_in_above;
    Animation slide_in_bottom;
    Animation slide_out_above;
    Animation slide_out_bottom;

    private boolean flag = false;
    //导航栏
    private Toolbar toolbar;

    private MyLocationData myLocationData;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置标题栏不可用
        setContentView(R.layout.activity_main);

        //设置主页导航栏
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // 地图初始化
        mMapView = (TextureMapView) findViewById(R.id.mTexturemap);
        mBaiduMap = mMapView.getMap();
        // 获取传感器管理服务
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        List<String> permissionList = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            //初始化定位
            initMap();
        }
        // 初始化控件
        initView();
        mCurrentMode = LocationMode.COMPASS;
        requestLocButton.setText("罗");
        OnClickListener btnClickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText("跟");
                        mCurrentMode = LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfigeration(
                                new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                        hideclickLayout(false);
                        break;
                    case COMPASS:
                        requestLocButton.setText("普");
                        mCurrentMode = LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfigeration(
                                new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));

                        locationLayout.startAnimation(slide_in_bottom);
                        locationLayout.setVisibility(View.VISIBLE);
                        // findroute.setVisibility(View.GONE);
                        hideclickLayout(false);
                        break;
                    case FOLLOWING:
                        requestLocButton.setText("罗");
                        mCurrentMode = LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfigeration(
                                new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));

                        locationLayout.startAnimation(slide_in_bottom);
                        locationLayout.setVisibility(View.VISIBLE);
                        //.setVisibility(View.GONE);
                        hideclickLayout(false);
                        break;
                    default:
                        break;
                }
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);
        //CharSequence titleLable = "路线规划功能";
        //setTitle(titleLable);

        // 地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);

        // 点击地图获取点的坐标
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapPoiClick(MapPoi arg0) {
                if (locationLayout.getVisibility() == View.VISIBLE) {
                    locationLayout.setVisibility(View.GONE);
                    locationLayout.startAnimation(slide_out_bottom);
                }
                hideclickLayout(true);
                my_login.setVisibility(View.GONE);
                my_back.setVisibility(View.VISIBLE);
                //findroute.setVisibility(View.GONE);
                end_edit.setText(arg0.getName());
                endlocation.setText(arg0.getName());
                endPt = arg0.getPosition();
                mBaiduMap.clear();
                mydraw(arg0.getPosition(), R.drawable.icon_en);

            }

            @Override
            public void onMapClick(LatLng Ll) {
                if (locationLayout.getVisibility() == View.VISIBLE) {
                    locationLayout.setVisibility(View.GONE);
                    locationLayout.startAnimation(slide_out_bottom);
                }
                //findroute.setVisibility(View.GONE);
                hideclickLayout(true);
                my_login.setVisibility(View.GONE);
                my_back.setVisibility(View.VISIBLE);
                //终点的坐标
                endPt = Ll;
                endLat = Ll.latitude;  //纬度
                endLon = Ll.longitude;//经度
                mBaiduMap.clear();
                mydraw(endPt, R.drawable.icon_en);
                // 创建地理编码检索实例
                geoCoder = GeoCoder.newInstance();
                // 设置反地理经纬度坐标,请求位置时,需要一个经纬度
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(endPt));
                // 设置地址或经纬度反编译后的监听,这里有两个回调方法,
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                        // addre = "地址："+reverseGeoCodeResult.getAddress();
                        // Log.i(TAG, "onGetReverseGeoCodeResult: "+reverseGeoCodeResult.getAddress());
                    }

                    /**
                     *
                     * @param reverseGeoCodeResult
                     */
                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                        if (reverseGeoCodeResult == null
                                || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {

                        } else {
                            end_edit.setText(reverseGeoCodeResult.getAddress());
                            endlocation.setText(reverseGeoCodeResult.getAddress());
                        }
                    }
                });
            }
        });
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

    //菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites:
                Intent favorite = new Intent(MainActivity.this, Favorite.class);
                startActivity(favorite);
                break;
            case R.id.mapType:
                //地图显示类型（普通、卫星）
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    MyToast("开启卫星图层");

                } else if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    MyToast("关闭文星图层");
                }
                break;
            case R.id.trafficMap:
                if (!mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(true);
                    MyToast("开启路况图");
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                    MyToast("关闭路况图");
                }
                break;
            case R.id.heatMap: {
                if (!mBaiduMap.isBaiduHeatMapEnabled()) {
                    //开启热力图
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                    MyToast("开启热力图");
                } else {
                    //关闭热力图
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                    MyToast("关闭热力图");
                }
            }
            default:
        }
        return true;
    }

    // 地图初始化
    public void initMap() {

        // 不显示百度地图Logo
        mMapView.removeViewAt(1);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1500);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocClient.setLocOption(option);
        mLocClient.start();// 启动sdk
    }

    public void initView() {
        needDistance = findViewById(R.id.distance);
        needTime = findViewById(R.id.need_time);
        my_login = findViewById(R.id.my_login);
        start_edit = findViewById(R.id.start);
        end_edit = findViewById(R.id.end);
        customer_city = findViewById(R.id.customer_city);
        my_back = findViewById(R.id.my_back);
        mylocation = findViewById(R.id.mylocation);
        requestLocButton = findViewById(R.id.change);
        //  findroute = (Button) findViewById(R.id.findroute);
        findroute2 = findViewById(R.id.findroute2);
        navigation = findViewById(R.id.navigation);
        detailed = findViewById(R.id.detailed);
        guide_layout = findViewById(R.id.guide_layout);
        edit_layout = findViewById(R.id.edit_layout);
        search_end = findViewById(R.id.search_end);
        locationLayout = findViewById(R.id.locationLayout);
        poilayout = findViewById(R.id.poilayout);
        choosemode = findViewById(R.id.choosemode);


        // 地图点击事件
        click_layout = findViewById(R.id.click_layout);
        endlocation = findViewById(R.id.endlocation);
        my_login.setOnClickListener(this);
        my_back.setOnClickListener(this);
        // findroute.setOnClickListener(this);
        findroute2.setOnClickListener(this);
        navigation.setOnClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        /****************** 动画 ***************/
        slide_in_above = AnimationUtils.loadAnimation(this, R.anim.slide_in_above);// 显示
        slide_out_above = AnimationUtils.loadAnimation(this, R.anim.slide_out_above);// 消失
        slide_in_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);// 显示
        slide_out_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);// 消失

        // ListView中推荐地址选择
        search_end.setOnItemClickListener(this);// 推荐地址的监听

        // *************搜索周边******************
        customer_find_btn = (ImageButton) findViewById(R.id.customer_find_btn);
        customer_find_btn.setOnClickListener(this);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchpoi);
        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        search_end.setAdapter(sugAdapter);
        keyWorldsView.setAdapter(sugAdapter);

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city(localcity));
            }
        });

        /**
         * 目的地关键字变化时
         *
         */
        end_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption()).keyword(s.toString()).city(localcity));

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }
//    public void initOverlay(){
//        LatLng llA = new LatLng(39.963175, 116.400244);
//        LatLng llB = new LatLng(39.906965, 116.401394);
//
//        MarkerOptions ooA = new MarkerOptions().position(llA).icon(mBitmapA).zIndex(9).draggable(true);
//        mMarkerA = (Marker)mBaiduMap.addOverlay(ooA);
//
//        MarkerOptions ooB = new MarkerOptions().position(llB).icon(mBitmapB).zIndex(5).draggable(true);
//        mMarkerB = (Marker) (mBaiduMap.addOverlay(ooB));
//    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        // 选择导航方式
        TextView driver = findViewById(R.id.go_driver);
        TextView bus = findViewById(R.id.go_bus);
        TextView bike = findViewById(R.id.go_bike);
        TextView walk = findViewById(R.id.go_walk);
        // 重置浏览节点的路线数据
        route = null;
        mBaiduMap.clear();
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(localcity, start_edit.getText().toString().trim());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(localcity, end_edit.getText().toString().trim());

        // 清除之前的覆盖物
        mBaiduMap.clear();
        // 实际使用中请对起点终点城市进行正确的设定
        switch (v.getId()) {
            case R.id.go_driver:
                bus.setSelected(false);
                bike.setSelected(false);
                walk.setSelected(false);
                driver.setSelected(true);
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
                hideguide();
                showDetailed();
                needDistance.setText("距离：" + distance / 1000 + " 公里");
                needTime.setText("大约需要：" + Math.round((distance / 1000) / 25 * 60) + " 分钟");

                my_back.setVisibility(View.VISIBLE);
                my_login.setVisibility(View.GONE);
                break;
            case R.id.go_bus:
                bus.setSelected(true);
                bike.setSelected(false);
                walk.setSelected(false);
                driver.setSelected(false);
                mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode).city(localcity).to(enNode));
                hideguide();
                needDistance.setText("距离：" + distance / 1000 + " 公里");
                needTime.setText("大约需要：" + Math.round((distance / 1000) / 20 * 60) + " 分钟");
                my_back.setVisibility(View.VISIBLE);
                my_login.setVisibility(View.GONE);
                break;
            case R.id.go_bike:
                bus.setSelected(false);
                bike.setSelected(true);
                walk.setSelected(false);
                driver.setSelected(false);
                mSearch.bikingSearch((new BikingRoutePlanOption()).from(stNode).to(enNode));
                hideguide();
                needDistance.setText("距离：" + distance / 1000 + " 公里");
                needTime.setText("大约需要：" + Math.round((distance / 1000) / 15 * 60) + " 分钟");
                my_back.setVisibility(View.VISIBLE);
                my_login.setVisibility(View.GONE);
                showDetailed();
                break;
            case R.id.go_walk:
                bus.setSelected(false);
                bike.setSelected(false);
                walk.setSelected(true);
                driver.setSelected(false);
                mSearch.walkingSearch((new WalkingRoutePlanOption().from(stNode)).to(enNode));
                needDistance.setText("距离：" + distance / 1000 + " 公里");

                needTime.setText("大约需要：" + Math.round((distance / 1000) / 10 * 60) + " 分钟");
                hideguide();
                showDetailed();
                my_login.setVisibility(View.GONE);
                my_back.setVisibility(View.VISIBLE);
                break;
            case R.id.go_end:
                click_layout.setVisibility(View.GONE);
                bus.setSelected(false);
                bike.setSelected(false);
                walk.setSelected(true);
                driver.setSelected(false);
                PlanNode startPlanNode = PlanNode.withLocation(currentPt); // lat long
                PlanNode endPlanNode = PlanNode.withLocation(endPt);

                mSearch.walkingSearch(new WalkingRoutePlanOption().from(startPlanNode).to(endPlanNode));
                distance = DistanceUtil.getDistance(currentPt, endPt);
                needDistance.setText("距离：" + Math.round(distance) + " 米");
                needTime.setText("大约需要：" + Math.round(distance / 60) + " 分钟");
                showDetailed();
                hideall();
                showguide();
                my_back.setVisibility(View.VISIBLE);
                search_end.setVisibility(View.GONE);
                edit_layout.setVisibility(View.GONE);
                choosemode.setVisibility(View.VISIBLE);
                //hideDetailed();
                break;
        }

    }

    /**
     * 节点浏览示例
     * <p>
     * //* @param v
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // 步行
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        //创建WalkingRouteOverlay实例
        //WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);


        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("抱歉，未找到结果");
            hideall();
            showguide();
            hideDetailed();
            edit_layout.setVisibility(View.VISIBLE);
        } else {

            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            distance = route.getDistance();

            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            hideall();
            showguide();
            edit_layout.setVisibility(View.VISIBLE);

            return;
        }

    }

    // 公交
    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
            hideall();
            showguide();
            hideDetailed();
            edit_layout.setVisibility(View.VISIBLE);
        } else {

            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            distance = route.getDistance();
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            // 设置路线数据
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap(); // 将所有overlay添加到地图中
            overlay.zoomToSpan();// 缩放地图
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            hideall();
            showguide();
            edit_layout.setVisibility(View.VISIBLE);
            return;
        }

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    // 驾车
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("抱歉，未找到结果");
            hideall();
            showguide();
            hideDetailed();
            edit_layout.setVisibility(View.VISIBLE);
        } else {

            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            distance = route.getDistance();
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();

        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            hideall();
            showguide();
            edit_layout.setVisibility(View.VISIBLE);
            return;
        }

    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    // 骑行
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("抱歉，未找到结果");
            hideall();
            showguide();
            hideDetailed();
            edit_layout.setVisibility(View.VISIBLE);
        } else {

            nodeIndex = -1;
            route = result.getRouteLines().get(0);
            distance = route.getDistance();
            BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
            routeOverlay = overlay;
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            hideall();
            showguide();
            edit_layout.setVisibility(View.VISIBLE);
            return;
        }

    }



    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }

    }


    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public void onMapPoiClick(MapPoi poi) {

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }

    public void MyToast(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            String locationDescribe = location.getLocationDescribe(); // 获取位置描述信息
            String startLocation = locationDescribe;
            myLocationData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(myLocationData);
            if (isFirstLoc) {
                isFirstLoc = false;
                currentPt = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(currentPt).zoom(17.5f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                start_edit.setText(startLocation);
                MyToast("当前所在位置：" + locationDescribe);
                mylocation.setText(locationDescribe);
                localcity = location.getDistrict();
                customer_city.setText(location.getDistrict());
                String mm = "customer " + "location " + location.getLatitude() + " " + location.getLongitude() + "\n";

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 影响搜索按钮点击事件 public void searchPoiProcess(View v) {
     * <p>
     * }
     * <p>
     * // * @param v
     */

    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            MyToast("未找到结果");
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            MyToast(strInfo);
        }
    }

    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("抱歉，未找到结果");
        } else {
            endPt = result.getLocation();
            endlocation.setText(result.getName() + ": " + result.getAddress());

        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        sugAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null)
                sugAdapter.add(info.key);
        }
        sugAdapter.notifyDataSetChanged();
    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
            if (locationLayout.getVisibility() == View.VISIBLE) {
                locationLayout.setVisibility(View.GONE);
                locationLayout.startAnimation(slide_out_bottom);
            }
            // findroute.setVisibility(View.GONE);
            hideclickLayout(true);
            // }
            return true;
        }
    }

    // ----------------------------------------------------------
    public void mydraw(LatLng location, int a) {
        // 定义Maker坐标点 LatLng location

        // 构建Marker图标

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(a);

        // 构建MarkerOption，用于在地图上添加Marker

        OverlayOptions option = new MarkerOptions().position(location).icon(bitmap);

        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.findroute2:
                locationLayout.setVisibility(View.GONE);
                poilayout.setVisibility(View.GONE);

                requestLocButton.setVisibility(View.GONE);
                showguide();
                break;
            case R.id.customer_find_btn:
                EditText editSearchKey = (EditText) findViewById(R.id.searchpoi);
                mPoiSearch.searchNearby(new PoiNearbySearchOption().location(currentPt)
                        .keyword(editSearchKey.getText().toString()).radius(3000).pageNum(15)
                        // 以currentPt为搜索中心1000米半径范围内的自行车点
                        .pageNum(load_Index));
                break;
            case R.id.my_back:
                hideguide();
                requestLocButton.setVisibility(View.VISIBLE);
                poilayout.setVisibility(View.VISIBLE);
                edit_layout.setVisibility(View.VISIBLE);
                guide_layout.setVisibility(View.GONE);
                locationLayout.setVisibility(View.VISIBLE);
                click_layout.setVisibility(View.GONE);
                break;
            case R.id.my_login:
                List<User> users = DataSupport.select("name", "phone", "password", "status")
                        .where("status = ?", "1")
                        .find(User.class);
                if (users.size() > 0) {
                    Intent my = new Intent(MainActivity.this, MyActivity.class);
                    startActivity(my);
                } else {
                    Intent login = new Intent(MainActivity.this, Login.class);
                    startActivity(login);
                }

        }
    }

    // ListView中点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 通过view获取其内部的组件，进而进行操作
        String text = search_end.getItemAtPosition(position) + "";
        end_edit.setText(text);

        if (search_end.getVisibility() == View.VISIBLE) {
            search_end.setVisibility(View.GONE);
            search_end.startAnimation(slide_out_bottom);
        }
    }

    private void showguide() {
        my_login.setVisibility(View.GONE);
        my_back.setVisibility(View.VISIBLE);
        if (guide_layout.getVisibility() == View.GONE) {
            guide_layout.setVisibility(View.VISIBLE);
            guide_layout.startAnimation(slide_in_above);
        }
        if (search_end.getVisibility() == View.GONE) {
            search_end.setVisibility(View.VISIBLE);
            search_end.startAnimation(slide_in_bottom);
        }


    }

    private void hideguide() {
        detailed.setVisibility(View.GONE);
        my_login.setVisibility(View.VISIBLE);
        my_back.setVisibility(View.GONE);
        mBaiduMap.clear();
        if (edit_layout.getVisibility() == View.VISIBLE) {
            edit_layout.setVisibility(View.GONE);
            edit_layout.startAnimation(slide_out_above);
        }
        if (search_end.getVisibility() == View.VISIBLE) {
            search_end.setVisibility(View.GONE);
            search_end.startAnimation(slide_out_bottom);
        }

    }

    private void hideclickLayout(boolean flag) {
        if (flag) {
            if (click_layout.getVisibility() == View.GONE) {
                click_layout.setVisibility(View.VISIBLE);
                click_layout.startAnimation(slide_in_bottom);
                hideDetailed();
            }

        } else {
            if (click_layout.getVisibility() == View.VISIBLE) {
                click_layout.setVisibility(View.GONE);
                click_layout.startAnimation(slide_out_bottom);

            }
        }
    }

    private void showDetailed() {
        if (detailed.getVisibility() == View.GONE) {
            detailed.setVisibility(View.VISIBLE);
            detailed.startAnimation(slide_in_bottom);
        }
    }

    private void hideDetailed() {
        if (detailed.getVisibility() == View.VISIBLE) {
            detailed.setVisibility(View.GONE);
            detailed.startAnimation(slide_out_bottom);
        }
    }

    private void hideall() {
        edit_layout.setVisibility(View.VISIBLE);
        requestLocButton.setVisibility(View.GONE);
        poilayout.setVisibility(View.GONE);
    }

}
