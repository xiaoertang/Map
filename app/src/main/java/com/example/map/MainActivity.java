package com.example.map;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
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
import com.baidu.mapapi.search.geocode.GeoCodeOption;
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
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.navisdk.adapter.struct.BNTTsInitConfig;
import com.example.map.entity.User;
import com.example.map.liteapp.BNDemoUtils;
import com.example.map.liteapp.activity.DemoGuideActivity;
import com.example.map.overlayutil.BikingRouteOverlay;
import com.example.map.overlayutil.DrivingRouteOverlay;
import com.example.map.overlayutil.OverlayManager;
import com.example.map.overlayutil.PoiOverlay;
import com.example.map.overlayutil.TransitRouteOverlay;
import com.example.map.overlayutil.WalkingRouteOverlay;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class MainActivity extends AppCompatActivity implements OnMapClickListener,
        OnGetRoutePlanResultListener, OnClickListener, OnGetSuggestionResultListener,
        OnGetPoiSearchResultListener, SensorEventListener, OnItemClickListener,
        OnGetGeoCoderResultListener {
    private BikeNaviLaunchParam bikeParam;
    private WalkNaviLaunchParam walkParam;
    private static boolean isPermissionRequested = false;
    private boolean isPOIClick = false;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private double endLat = 0.0;
    private double endLon = 0.0;
    //两地之间的距离
//    private double distance;
    // 浏览路线节点相关
    private String localcity;// 记录当前城市
    Button mBtnPre = null; // 上一个节点
    Button mBtnNext = null; // 下一个节点
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    //private Button findroute;// poi
    private Button findroute2;// 路线规划

    RouteLine route = null;   //路线
    OverlayManager routeOverlay = null; //路线叠加
    private Button requestLocButton;
    private LocationMode mCurrentMode;
    private Button my_back;// 返回按钮
    private LinearLayout edit_layout;// 底部目的地栏
    private LinearLayout choosemode;// 选择导航方式
    private ListView search_end;// POI推荐目的地
    private LinearLayout guide_layout; //起点-终点搜索框
    private LinearLayout locationLayout;// 定位框
    private LinearLayout detailed;//详细说明
    //    private TextView needDistance;//距离
//    private TextView needTime;//需要时间
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
    private List<PoiInfo> poilist = new ArrayList<PoiInfo>();
    private SuggestionSearch mSuggestionSearch = null;
    private ImageButton btn_search;

    //导航按钮
    private Button navigation;

    private boolean isDriver = false;
    private boolean isBike = false;
    private boolean isWalk = false;

    /**
     * 搜索关键字输入窗口
     */
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;
    public boolean isSelect = false;
    // 定位相关
    LocationClient mLocClient;
    LatLng currentPt;
    public MyLocationListenner myListener = new MyLocationListenner();

    // 点击地图事件
    private LinearLayout click_layout;
    private TextView endlocation;
    private Button go_end;
    private LatLng endPt;
    private GeoCoder geoCoder; //地理编码

    // 动画效果
    Animation slide_in_above;
    Animation slide_in_bottom;
    Animation slide_out_above;
    Animation slide_out_bottom;

    private boolean flag = false;
    //导航栏
    private Toolbar toolbar;

    private MyLocationData myLocationData;

    private BroadcastReceiver mReceiver;
    private int mPageType = BNDemoUtils.NORMAL;
    //骑行、步行起点终点
    PlanNode startPlanNode = null;
    PlanNode endPlanNode = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置标题栏不可用
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
        requestPermission();

        //初始化定位
        initMap();

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
        mBaiduMap.setOnMapClickListener(this);
        //导航初始化
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        BaiduNaviManagerFactory.getBaiduNaviManager().init(getApplicationContext(), sdDir.toString(), "map",
                new IBaiduNaviManager.INaviInitListener() {
                    @Override
                    public void onAuthResult(int i, String s) {
                        if (i == 0) {
//                            Toast.makeText(MainActivity.this, "key校验成功!", Toast.LENGTH_SHORT).show();
                        } else if (i == 1) {
                            //Toast.makeText(MainActivity.this, "key校验失败, " + s, Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //Toast.makeText(MainActivity.this, "authinfo", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void initStart() {

                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        // 初始化tts
                        initTTs();

                    }

                    @Override
                    public void initFailed(int i) {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();

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
        // 清除之前的覆盖物
        mBaiduMap.clear();
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        geoCoder.geocode(new GeoCodeOption().city(localcity).address(end_edit.getText().toString()));
        startPlanNode = PlanNode.withLocation(currentPt);
        endPlanNode = PlanNode.withLocation(new LatLng(latitude, longitude));
        // 实际使用中请对起点终点城市进行正确的设定
        switch (v.getId()) {
            case R.id.go_driver:
                PlanNode stNode = PlanNode.withCityNameAndPlaceName(localcity,start_edit.getText().toString());
                PlanNode enNode = PlanNode.withCityNameAndPlaceName(localcity,end_edit.getText().toString());
                isDriver = true;
                bus.setSelected(false);
                bike.setSelected(false);
                walk.setSelected(false);
                driver.setSelected(true);
                mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));
                hideguide();
                showDetailed();
                my_back.setVisibility(View.VISIBLE);
                my_login.setVisibility(View.GONE);
                break;
            case R.id.go_bus:
                bus.setSelected(true);
                bike.setSelected(false);
                walk.setSelected(false);
                driver.setSelected(false);
                mSearch.transitSearch((new TransitRoutePlanOption()).from(startPlanNode).city(localcity).to(endPlanNode));
                hideguide();
                my_back.setVisibility(View.VISIBLE);
                my_login.setVisibility(View.GONE);
                break;
            case R.id.go_bike:
                isBike = true;
                bus.setSelected(false);
                bike.setSelected(true);
                walk.setSelected(false);
                driver.setSelected(false);
                mSearch.bikingSearch((new BikingRoutePlanOption()).from(startPlanNode).to(endPlanNode).ridingType(0));
                hideguide();
                my_back.setVisibility(View.VISIBLE);
                my_login.setVisibility(View.GONE);
                showDetailed();
                break;
            case R.id.go_walk:
                isWalk = true;
                bus.setSelected(false);
                bike.setSelected(false);
                walk.setSelected(true);
                driver.setSelected(false);
                mSearch.walkingSearch((new WalkingRoutePlanOption().from(startPlanNode)).to(endPlanNode));
                hideguide();
                showDetailed();
                my_login.setVisibility(View.GONE);
                my_back.setVisibility(View.VISIBLE);
                break;
            case R.id.go_end:
                isWalk = true;
                click_layout.setVisibility(View.GONE);
                bus.setSelected(false);
                bike.setSelected(false);
                walk.setSelected(true);
                driver.setSelected(false);
//                endPt = new LatLng(latitude,longitude);
                startPlanNode = PlanNode.withLocation(currentPt); // lat long
                endPlanNode = PlanNode.withLocation(endPt);
                mSearch.walkingSearch(new WalkingRoutePlanOption().from(startPlanNode).to(endPlanNode));
//
                showDetailed();
                hideall();
                showguide();
                end_edit.setText("");
                my_back.setVisibility(View.VISIBLE);
                search_end.setVisibility(View.GONE);
                edit_layout.setVisibility(View.GONE);
                choosemode.setVisibility(View.VISIBLE);
                //hideDetailed();
                break;
            default:
                break;
        }
        endPlanNode = null;
        startPlanNode = null;
    }

    private void initTTs() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        BNTTsInitConfig builder = new BNTTsInitConfig.Builder()
                .context(getApplicationContext())
                .sdcardRootPath(sdDir.toString())
                .appFolderName("map")
                .appId("25677896")
                .appKey("MdDVNTMa8UkXEeNB6a8evHLOPwBdjCHW")
                .secretKey("cwLrQSgeWOzyyWkQr5c8m8Hr7RZznhx9")
                .build();
        BaiduNaviManagerFactory.getTTSManager().initTTS(builder);

        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {

                    }

                    @Override
                    public void onPlayEnd(String speechId) {

                    }

                    @Override
                    public void onPlayError(int code, String message) {

                    }
                }
        );
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
//            distance = route.getDistance();

            WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);
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
//            distance = route.getDistance();
            //创建TransitRouteOverlay实例
            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
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
//            distance = route.getDistance();
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
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
        BikingRouteOverlay overlay = new BikingRouteOverlay(mBaiduMap);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("抱歉，未找到结果");
            hideall();
            showguide();
            hideDetailed();
            edit_layout.setVisibility(View.VISIBLE);
        } else {

            nodeIndex = -1;
            route = result.getRouteLines().get(0);
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
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (null != geoCodeResult && geoCodeResult.getLocation() != null) {
            if (geoCodeResult == null || geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                return;
            } else {
                latitude = geoCodeResult.getLocation().latitude;
                longitude = geoCodeResult.getLocation().longitude;

            }
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null
                || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {

        } else {
            end_edit.setText(reverseGeoCodeResult.getAddress());
            endlocation.setText(reverseGeoCodeResult.getAddress());
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
        if (my_login.getVisibility() == View.VISIBLE) {
            my_login.setVisibility(View.GONE);
            my_back.setVisibility(View.VISIBLE);
        }
        if (locationLayout.getVisibility() == View.VISIBLE) {
            locationLayout.setVisibility(View.GONE);
            locationLayout.startAnimation(slide_out_bottom);
        }
        hideclickLayout(true);
        endPt = point;
        mBaiduMap.clear();
        mydraw(endPt, R.drawable.icon_en);
        latitude = point.latitude;
        longitude = point.longitude;
        // 设置反地理经纬度坐标,请求位置时,需要一个经纬度
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption()
                .location(point)
                .newVersion(1)
                .radius(500));

    }


    @Override
    public void onMapPoiClick(MapPoi poi) {
        isPOIClick = true;
        if (locationLayout.getVisibility() == View.VISIBLE) {
            locationLayout.setVisibility(View.GONE);
            locationLayout.startAnimation(slide_out_bottom);
        }
        hideclickLayout(true);
        end_edit.setText(poi.getName());
        endlocation.setText(poi.getName());
        //geoCoder.geocode(new GeoCodeOption().city(localcity).address(poi.getName()));
        endPt = poi.getPosition();
        mBaiduMap.clear();
        mydraw(poi.getPosition(), R.drawable.icon_en);
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
        geoCoder.destroy();
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
                int len = startLocation.length();
                start_edit.setText(startLocation.substring(1, len - 2));
                MyToast("当前所在位置：" + locationDescribe);
                mylocation.setText(locationDescribe);
                localcity = location.getCity();
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
            hideclickLayout(true);
            // }
            return true;
        }
    }

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
            case R.id.btn_search:
                String key = keyWorldsView.getText().toString();
                mPoiSearch.searchNearby(new PoiNearbySearchOption().location(currentPt)
                        .keyword(key).radius(3000).pageNum(15)
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
                break;
            case R.id.navigation:
                geoCoder.geocode(new GeoCodeOption()
                        .city(localcity)
                        .address(end_edit.getText().toString()));

                if (isDriver) {
                    //驾车导航
                    startDriverNavi();
                    isDriver = false;
                } else if (isBike) {
                    //骑行导航
                    endPt = new LatLng(latitude, longitude);
                    BikeRouteNodeInfo bikeStartNode = new BikeRouteNodeInfo();
                    bikeStartNode.setLocation(currentPt);
                    BikeRouteNodeInfo bikeEndNode = new BikeRouteNodeInfo();
                    bikeEndNode.setLocation(endPt);
                    bikeParam = new BikeNaviLaunchParam().stPt(currentPt).endPt(endPt).vehicle(0);
//                    bikeParam = new BikeNaviLaunchParam().stPt(currentPt).endPt(endPt).vehicle(0);
                    startBikeNavi();
                    isBike = false;
                } else if (isWalk) {
                    //步行导航
                    if (isPOIClick) {
                        geoCoder.geocode(new GeoCodeOption()
                                .city(customer_city.toString())
                                .address(end_edit.getText().toString()));
                        isPOIClick = false;
                    } else {
                        endPt = new LatLng(latitude, longitude);
                    }
                    WalkRouteNodeInfo walkStartNode = new WalkRouteNodeInfo();
                    walkStartNode.setLocation(currentPt);
                    WalkRouteNodeInfo walkEndNode = new WalkRouteNodeInfo();
                    walkEndNode.setLocation(endPt);
                    walkParam = new WalkNaviLaunchParam().stPt(currentPt).endPt(endPt);
                    startWalkNavi();
                    isWalk = false;

                }
                break;
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
        // 创建地理编码检索实例
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        //登录
        my_login = findViewById(R.id.my_login);
        my_login.setOnClickListener(this);
        my_back = findViewById(R.id.my_back);
        my_back.setOnClickListener(this);

//        needDistance = findViewById(R.id.distance);
//        needTime = findViewById(R.id.need_time);

        //起点终点
        start_edit = findViewById(R.id.start);
        end_edit = findViewById(R.id.end);
        customer_city = findViewById(R.id.customer_city);

        mylocation = findViewById(R.id.mylocation);
        requestLocButton = findViewById(R.id.change);
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
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        // POI搜索
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        //在线建议查询
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

//    private void initTTs() {
//        File sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState()
//                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
//        if (sdCardExist) {
//            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
//        }
//        BNTTsInitConfig  builder = new BNTTsInitConfig.Builder()
//                .context(getApplicationContext())
//                .sdcardRootPath(sdDir.toString())
//                .appFolderName("map")
//                .appId("25677896")
//                .appKey("MdDVNTMa8UkXEeNB6a8evHLOPwBdjCHW")
//                .secretKey("cwLrQSgeWOzyyWkQr5c8m8Hr7RZznhx9")
//                .build();
//        BaiduNaviManagerFactory.getTTSManager().initTTS(builder);
//
//        // 注册同步内置tts状态回调
//        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
//                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
//                    @Override
//                    public void onPlayStart() {
//
//                    }
//
//                    @Override
//                    public void onPlayEnd(String speechId) {
//
//                    }
//
//                    @Override
//                    public void onPlayError(int code, String message) {
//
//                    }
//                }
//        );
//    }

    //菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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

//    public void setEndPt(LatLng Ll) {
//        this.endPt = Ll;
//    }
//
//    public LatLng getEndPt() {
//        return endPt;
//    }
    // 获取导航控制类
    // 引擎初始化

    /**
     * 开始驾车导航
     */
    private void startDriverNavi() {

        BNRoutePlanNode sNode = new BNRoutePlanNode.Builder()
                .latitude(currentPt.latitude)
                .longitude(currentPt.longitude)
                .name(start_edit.getText().toString())
                .description(start_edit.getText().toString())
                .build();
        BNRoutePlanNode eNode = new BNRoutePlanNode.Builder()
                .latitude(latitude)
                .longitude(longitude)
                .name(end_edit.getText().toString())
                .description(end_edit.getText().toString())
                .build();
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(sNode);
        list.add(eNode);
        BaiduNaviManagerFactory.getRoutePlanManager().routePlanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "算路开始", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "算路成功", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "算路失败", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "算路成功准备进入导航", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,
                                        DemoGuideActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    /**
     * 开始骑行导航
     */
    private void startBikeNavi() {
        //Log.d(TAG, "startBikeNavi");

        BikeNavigateHelper.getInstance().initNaviEngine(MainActivity.this, new IBEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //Log.d(TAG, "BikeNavi engineInitSuccess");
                MyToast("骑行导航");
                routePlanWithBikeParam();
            }

            @Override
            public void engineInitFail() {
                //Log.d(TAG, "BikeNavi engineInitFail");
                BikeNavigateHelper.getInstance().unInitNaviEngine();
            }
        });
    }

    /**
     * 开始步行导航
     */
    private void startWalkNavi() {
        //Log.d(TAG, "startWalkNavi");
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //  Log.d(TAG, "WalkNavi engineInitSuccess");
                MyToast("步行导航");
                routePlanWithWalkParam();
            }

            @Override
            public void engineInitFail() {
                //   Log.d(TAG, "WalkNavi engineInitFail");
                WalkNavigateHelper.getInstance().unInitNaviEngine();
            }
        });

    }

    /**
     * 发起骑行导航算路
     */
    private void routePlanWithBikeParam() {
        BikeNavigateHelper.getInstance().routePlanWithRouteNode(bikeParam, new IBRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {

            }

            @Override
            public void onRoutePlanSuccess() {
                //   Log.d(TAG, "BikeNavi onRoutePlanSuccess");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, BNaviGuideActivity.class);
                startActivity(intent);
            }

            @Override
            public void onRoutePlanFail(BikeRoutePlanError error) {
                //   Log.d(TAG, "BikeNavi onRoutePlanFail");
            }

        });
    }

    /**
     * 发起步行导航算路
     */
    private void routePlanWithWalkParam() {
        WalkNavigateHelper.getInstance().routePlanWithRouteNode(walkParam, new IWRoutePlanListener() {
            @Override
            public void onRoutePlanStart() {
                //    Log.d(TAG, "WalkNavi onRoutePlanStart");
            }

            @Override
            public void onRoutePlanSuccess() {

                //    Log.d(TAG, "onRoutePlanSuccess");
                MyToast("步行——————————");
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WNaviGuideActivity.class);
                startActivity(intent);

            }

            @Override
            public void onRoutePlanFail(WalkRoutePlanError error) {
                //     Log.d(TAG, "WalkNavi onRoutePlanFail");
            }

        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {

            isPermissionRequested = true;

            ArrayList<String> permissionsList = new ArrayList<>();

            String[] permissions = {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.MODIFY_AUDIO_SETTINGS,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE


            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (permissionsList.isEmpty()) {
                return;
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), 0);
            }
        }
    }
}