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
 * ??????????????????????????????????????????????????????????????????????????????RouteOverlay???TransitOverlay??????
 * ???????????????????????????????????????????????????
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
    //?????????????????????
//    private double distance;
    // ????????????????????????
    private String localcity;// ??????????????????
    Button mBtnPre = null; // ???????????????
    Button mBtnNext = null; // ???????????????
    int nodeIndex = -1; // ????????????,????????????????????????
    //private Button findroute;// poi
    private Button findroute2;// ????????????

    RouteLine route = null;   //??????
    OverlayManager routeOverlay = null; //????????????
    private Button requestLocButton;
    private LocationMode mCurrentMode;
    private Button my_back;// ????????????
    private LinearLayout edit_layout;// ??????????????????
    private LinearLayout choosemode;// ??????????????????
    private ListView search_end;// POI???????????????
    private LinearLayout guide_layout; //??????-???????????????
    private LinearLayout locationLayout;// ?????????
    private LinearLayout detailed;//????????????
    //    private TextView needDistance;//??????
//    private TextView needTime;//????????????
    BitmapDescriptor mCurrentMarker;
    boolean useDefaultIcon = false;
    private TextView popupText = null, customer_city; // ??????view
    private TextView mylocation;
    private EditText start_edit, end_edit;
    boolean isFirstLoc = true; // ??????????????????
    private Button my_login; //??????
    // ???????????????????????????MapView???MyRouteMapView???????????????touch????????????????????????
    // ???????????????touch???????????????????????????????????????MapView??????
    // ????????????
    private TextureMapView mMapView = null;
    private BaiduMap mBaiduMap;

    // ????????????
    RoutePlanSearch mSearch = null; // ???????????????????????????????????????????????????
    // ????????????
    private LinearLayout poilayout;
    private PoiSearch mPoiSearch = null;
    private List<PoiInfo> poilist = new ArrayList<PoiInfo>();
    private SuggestionSearch mSuggestionSearch = null;
    private ImageButton btn_search;

    //????????????
    private Button navigation;

    private boolean isDriver = false;
    private boolean isBike = false;
    private boolean isWalk = false;

    /**
     * ???????????????????????????
     */
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int load_Index = 0;
    public boolean isSelect = false;
    // ????????????
    LocationClient mLocClient;
    LatLng currentPt;
    public MyLocationListenner myListener = new MyLocationListenner();

    // ??????????????????
    private LinearLayout click_layout;
    private TextView endlocation;
    private Button go_end;
    private LatLng endPt;
    private GeoCoder geoCoder; //????????????

    // ????????????
    Animation slide_in_above;
    Animation slide_in_bottom;
    Animation slide_out_above;
    Animation slide_out_bottom;

    private boolean flag = false;
    //?????????
    private Toolbar toolbar;

    private MyLocationData myLocationData;

    private BroadcastReceiver mReceiver;
    private int mPageType = BNDemoUtils.NORMAL;
    //???????????????????????????
    PlanNode startPlanNode = null;
    PlanNode endPlanNode = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ?????????SDK????????????????????????context???????????????ApplicationContext
        // ?????????????????????setContentView??????????????????
        SDKInitializer.initialize(getApplicationContext());
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ????????????????????????
        setContentView(R.layout.activity_main);

        //?????????????????????
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        // ???????????????
        mMapView = (TextureMapView) findViewById(R.id.mTexturemap);
        mBaiduMap = mMapView.getMap();
        // ???????????????????????????
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // ??????????????????????????????????????????
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        requestPermission();

        //???????????????
        initMap();

        // ???????????????
        initView();
        mCurrentMode = LocationMode.COMPASS;
        requestLocButton.setText("???");
        OnClickListener btnClickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText("???");
                        mCurrentMode = LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfigeration(
                                new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
                        hideclickLayout(false);
                        break;
                    case COMPASS:
                        requestLocButton.setText("???");
                        mCurrentMode = LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfigeration(
                                new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));

                        locationLayout.startAnimation(slide_in_bottom);
                        locationLayout.setVisibility(View.VISIBLE);
                        // findroute.setVisibility(View.GONE);
                        hideclickLayout(false);
                        break;
                    case FOLLOWING:
                        requestLocButton.setText("???");
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
        //CharSequence titleLable = "??????????????????";
        //setTitle(titleLable);
        // ????????????????????????
        mBaiduMap.setOnMapClickListener(this);
        // ??????????????????????????????
        mBaiduMap.setOnMapClickListener(this);
        //???????????????
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//??????sd???????????????
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//???????????????
        }
        BaiduNaviManagerFactory.getBaiduNaviManager().init(getApplicationContext(), sdDir.toString(), "map",
                new IBaiduNaviManager.INaviInitListener() {
                    @Override
                    public void onAuthResult(int i, String s) {
                        if (i == 0) {
//                            Toast.makeText(MainActivity.this, "key????????????!", Toast.LENGTH_SHORT).show();
                        } else if (i == 1) {
                            //Toast.makeText(MainActivity.this, "key????????????, " + s, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                        // ?????????tts
                        initTTs();

                    }

                    @Override
                    public void initFailed(int i) {
                        Toast.makeText(MainActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            myLocationData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)// ????????????????????????????????????????????????
                    .direction(mCurrentDirection)// ?????????????????????????????????????????????????????????0-360
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
     * ??????????????????????????????
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        // ??????????????????
        TextView driver = findViewById(R.id.go_driver);
        TextView bus = findViewById(R.id.go_bus);
        TextView bike = findViewById(R.id.go_bike);
        TextView walk = findViewById(R.id.go_walk);
        // ?????????????????????????????????
        route = null;
        // ????????????????????????
        mBaiduMap.clear();
        // ??????????????????????????????tranist search ???????????????????????????
        geoCoder.geocode(new GeoCodeOption().city(localcity).address(end_edit.getText().toString()));
        startPlanNode = PlanNode.withLocation(currentPt);
        endPlanNode = PlanNode.withLocation(new LatLng(latitude, longitude));
        // ????????????????????????????????????????????????????????????
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
                .equals(android.os.Environment.MEDIA_MOUNTED);//??????sd???????????????
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//???????????????
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

        // ??????????????????tts????????????
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
     * ??????????????????
     * <p>
     * //* @param v
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // ??????
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        //??????WalkingRouteOverlay??????
        //WalkingRouteOverlay overlay = new WalkingRouteOverlay(mBaiduMap);


        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("????????????????????????");
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
            // ?????????????????????????????????????????????????????????????????????????????????
            // result.getSuggestAddrInfo()
            hideall();
            showguide();
            edit_layout.setVisibility(View.VISIBLE);

            return;
        }

    }

    // ??????
    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            hideall();
            showguide();
            hideDetailed();
            edit_layout.setVisibility(View.VISIBLE);
        } else {

            nodeIndex = -1;
            route = result.getRouteLines().get(0);
//            distance = route.getDistance();
            //??????TransitRouteOverlay??????
            TransitRouteOverlay overlay = new TransitRouteOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            // ??????????????????
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap(); // ?????????overlay??????????????????
            overlay.zoomToSpan();// ????????????
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // ?????????????????????????????????????????????????????????????????????????????????
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

    // ??????
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("????????????????????????");
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
            // ?????????????????????????????????????????????????????????????????????????????????
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

    // ??????
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        BikingRouteOverlay overlay = new BikingRouteOverlay(mBaiduMap);
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("????????????????????????");
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
            // ?????????????????????????????????????????????????????????????????????????????????
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
                //?????????????????????
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
        // ??????????????????????????????,???????????????,?????????????????????
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
     * ??????SDK????????????
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view ???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            String locationDescribe = location.getLocationDescribe(); // ????????????????????????
            String startLocation = locationDescribe;
            myLocationData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // ?????????????????????????????????????????????????????????0-360
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
                MyToast("?????????????????????" + locationDescribe);
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
     * ?????????????????????????????? public void searchPoiProcess(View v) {
     * <p>
     * }
     * <p>
     * // * @param v
     */

    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            MyToast("???????????????");
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

            // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            String strInfo = "???";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "????????????";
            MyToast(strInfo);
        }

    }

    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            MyToast("????????????????????????");
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
        // ??????view?????????????????????????????????????????????
        String text = search_end.getItemAtPosition(position) + "";
        end_edit.setText(text);
        if (search_end.getVisibility() == View.VISIBLE) {
            search_end.setVisibility(View.GONE);
            search_end.startAnimation(slide_out_bottom);
        }
    }

    // ????????????
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
                        // ???currentPt???????????????1000?????????????????????????????????
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
                    //????????????
                    startDriverNavi();
                    isDriver = false;
                } else if (isBike) {
                    //????????????
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
                    //????????????
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
        // ??????Maker????????? LatLng location

        // ??????Marker??????

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(a);

        // ??????MarkerOption???????????????????????????Marker

        OverlayOptions option = new MarkerOptions().position(location).icon(bitmap);

        // ??????????????????Marker????????????
        mBaiduMap.addOverlay(option);
    }

    // ???????????????
    public void initMap() {

        // ?????????????????????Logo
        mMapView.removeViewAt(1);
        // ??????????????????
        mBaiduMap.setMyLocationEnabled(true);
        // ???????????????
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // ??????gps
        option.setCoorType("bd09ll"); // ??????????????????
        option.setScanSpan(1500);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // ?????????????????????????????????????????????????????????????????????????????????
        option.setIsNeedAddress(true);// ?????????????????????????????????????????????????????????
        option.setIsNeedLocationPoiList(true);// ???????????????false?????????????????????POI??????????????????BDLocation.getPoiList?????????
        mLocClient.setLocOption(option);
        mLocClient.start();// ??????sdk
    }

    public void initView() {
        // ??????????????????????????????
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(this);
        //??????
        my_login = findViewById(R.id.my_login);
        my_login.setOnClickListener(this);
        my_back = findViewById(R.id.my_back);
        my_back.setOnClickListener(this);

//        needDistance = findViewById(R.id.distance);
//        needTime = findViewById(R.id.need_time);

        //????????????
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


        // ??????????????????
        click_layout = findViewById(R.id.click_layout);
        endlocation = findViewById(R.id.endlocation);

        findroute2.setOnClickListener(this);
        navigation.setOnClickListener(this);
        // ??????????????????????????????????????????
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        /****************** ?????? ***************/
        slide_in_above = AnimationUtils.loadAnimation(this, R.anim.slide_in_above);// ??????
        slide_out_above = AnimationUtils.loadAnimation(this, R.anim.slide_out_above);// ??????
        slide_in_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);// ??????
        slide_out_bottom = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);// ??????
        // ListView?????????????????????
        search_end.setOnItemClickListener(this);// ?????????????????????
        // *************????????????******************
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        // POI??????
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        //??????????????????
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);


        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchpoi);
        sugAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        search_end.setAdapter(sugAdapter);
        keyWorldsView.setAdapter(sugAdapter);

        /**
         * ??????????????????????????????????????????????????????
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
                 * ??????????????????????????????????????????????????????onSuggestionResult()?????????
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption()).keyword(cs.toString()).city(localcity));
            }
        });

        /**
         * ???????????????????????????
         *
         */
        end_edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() <= 0) {
                    return;
                }
                /**
                 * ??????????????????????????????????????????????????????onSuggestionResult()?????????
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
//                .equals(android.os.Environment.MEDIA_MOUNTED);//??????sd???????????????
//        if (sdCardExist) {
//            sdDir = Environment.getExternalStorageDirectory();//???????????????
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
//        // ??????????????????tts????????????
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

    //??????
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapType:
                //???????????????????????????????????????
                if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    MyToast("??????????????????");

                } else if (mBaiduMap.getMapType() == BaiduMap.MAP_TYPE_SATELLITE) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    MyToast("??????????????????");
                }
                break;
            case R.id.trafficMap:
                if (!mBaiduMap.isTrafficEnabled()) {
                    mBaiduMap.setTrafficEnabled(true);
                    MyToast("???????????????");
                } else {
                    mBaiduMap.setTrafficEnabled(false);
                    MyToast("???????????????");
                }
                break;
            case R.id.heatMap: {
                if (!mBaiduMap.isBaiduHeatMapEnabled()) {
                    //???????????????
                    mBaiduMap.setBaiduHeatMapEnabled(true);
                    MyToast("???????????????");
                } else {
                    //???????????????
                    mBaiduMap.setBaiduHeatMapEnabled(false);
                    MyToast("???????????????");
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
    // ?????????????????????
    // ???????????????

    /**
     * ??????????????????
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
                                        "????????????", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "????????????", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "????????????", Toast.LENGTH_SHORT).show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(MainActivity.this.getApplicationContext(),
                                        "??????????????????????????????", Toast.LENGTH_SHORT).show();
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
     * ??????????????????
     */
    private void startBikeNavi() {
        //Log.d(TAG, "startBikeNavi");

        BikeNavigateHelper.getInstance().initNaviEngine(MainActivity.this, new IBEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //Log.d(TAG, "BikeNavi engineInitSuccess");
                MyToast("????????????");
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
     * ??????????????????
     */
    private void startWalkNavi() {
        //Log.d(TAG, "startWalkNavi");
        WalkNavigateHelper.getInstance().initNaviEngine(this, new IWEngineInitListener() {
            @Override
            public void engineInitSuccess() {
                //  Log.d(TAG, "WalkNavi engineInitSuccess");
                MyToast("????????????");
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
     * ????????????????????????
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
     * ????????????????????????
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
                MyToast("????????????????????????????????????");
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
                    // ?????????????????????????????????.
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