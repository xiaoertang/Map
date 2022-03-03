package com.example.map.liteapp.routeresult;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviCommonParams;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNRouteResultManager;
import com.baidu.navisdk.adapter.struct.BNRouteDetail;
import com.baidu.navisdk.adapter.struct.BNRoutePlanItem;
import com.example.map.R;
import com.example.map.liteapp.BNDemoFactory;
import com.example.map.liteapp.BNDemoUtils;
import com.example.map.liteapp.custom.BNRecyclerView;
import com.example.map.liteapp.custom.BNScrollLayout;
import com.example.map.liteapp.custom.BNScrollView;

import java.util.ArrayList;
import java.util.List;

public class DemoRouteResultFragment extends Fragment implements View.OnClickListener,
        PreferItemsAdapter.ClickPreferListener {

    private static final String TAG = "DemoRouteResultFragment";

    private LinearLayout mLayout_tab0;
    private LinearLayout mLayout_tab1;
    private LinearLayout mLayout_tab2;
    private RelativeLayout mRl_button;
    private FrameLayout mFl_retry;
    private LinearLayout mLDLayout;
    private Button mPreferBtn;
    private RouteResultAdapter mResultAdapter;
    private BNRecyclerView mRecyclerView;
    private RecyclerView mPreferRecyclerView;
    private PreferItemsAdapter mItemsAdapter;
    private PopupWindow mPopWindow;
    private ArrayList<RouteSortModel> mRouteSortList;
    private ArrayList<BNRoutePlanItem> mRoutePlanItems;
    private ArrayList<BNRouteDetail> mRouteList = new ArrayList<>();
    private Bundle mRouteDetails = new Bundle();
    private Bundle mRoutePoints = new Bundle();
    private ArrayList<String> mLimitInfos = new ArrayList<>();
    private View mRootView;
    private int currentPrefer = IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                    Toast.makeText(getContext(), "算路开始", Toast.LENGTH_SHORT).show();
                    mFl_retry.setVisibility(View.GONE);
                    break;
                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                    Toast.makeText(getContext(), "算路成功", Toast.LENGTH_SHORT).show();
                    mFl_retry.setVisibility(View.GONE);
                    mRl_button.setVisibility(View.VISIBLE);
                    updateBtnText(currentPrefer);
                    initData();
                    break;
                case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                    Toast.makeText(getContext(), "算路失败", Toast.LENGTH_SHORT).show();
                    mFl_retry.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        BaiduNaviManagerFactory.getRouteResultManager().onCreate(getActivity());
        mRootView = inflater.inflate(R.layout.fragment_route_result, container, false);
        mLayout_tab0 = mRootView.findViewById(R.id.route_0);
        mLayout_tab0.setOnClickListener(this);
        mLayout_tab1 = mRootView.findViewById(R.id.route_1);
        mLayout_tab1.setOnClickListener(this);
        mLayout_tab2 = mRootView.findViewById(R.id.route_2);
        mLayout_tab2.setOnClickListener(this);
        mRl_button = mRootView.findViewById(R.id.rl_button);
        mRecyclerView = mRootView.findViewById(R.id.rv);
        mPreferBtn = mRootView.findViewById(R.id.btn_prefer);
        mPreferBtn.setOnClickListener(this);
        mLDLayout = mRootView.findViewById(R.id.ld_container);
        mFl_retry = mRootView.findViewById(R.id.fl_retry);
        mRootView.findViewById(R.id.btn_road).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_fullView).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_start_navi).setOnClickListener(this);

        new LongDistanceController(mRootView);
        BaiduNaviManagerFactory.getRouteResultSettingManager().setRouteMargin(
                100, 100, 100, 500);
        BaiduNaviManagerFactory.getRouteResultManager().setRouteClickedListener(
                new IBNRouteResultManager.IRouteClickedListener() {
                    @Override
                    public void routeClicked(int index) {
                        BaiduNaviManagerFactory.getRouteGuideManager().selectRoute(index);
                    }
                });
        initData();
        initPreferPopWindow();
        initListener();
        routePlan();
        return mRootView;
    }

    private void initListener() {
        BaiduNaviManagerFactory.getRouteResultManager().setCalcRouteByViaListener(
                new IBNRouteResultManager.ICalcRouteByViaListener() {
                    @Override
                    public void onStart() {
                        Log.e(TAG, "开始算路");
                    }

                    @Override
                    public void onSuccess() {
                        mFl_retry.setVisibility(View.GONE);
                        mRl_button.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailed(int errorCode) {
                        mFl_retry.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initPreferPopWindow() {
        View popView =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_pop_prefer, null, false);
        mPreferRecyclerView = popView.findViewById(R.id.nsdk_route_sort_gv);
        initPreferView();
        mPopWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setTouchable(true);
    }

    private void initPreferView() {
        initRouteSortList();
        mPreferRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mPreferRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (mItemsAdapter == null) {
            mItemsAdapter = new PreferItemsAdapter(getContext(), mRouteSortList);
            mItemsAdapter.setClickPreferListener(this);
        }
        mPreferRecyclerView.setAdapter(mItemsAdapter);
    }

    private void initRouteSortList() {
        mRouteSortList = new ArrayList<>();
        mRouteSortList.add(new RouteSortModel("智能推荐", IBNRoutePlanManager.RoutePlanPreference
                .ROUTE_PLAN_PREFERENCE_DEFAULT));
        mRouteSortList.add(new RouteSortModel("时间优先", IBNRoutePlanManager.RoutePlanPreference
                .ROUTE_PLAN_PREFERENCE_TIME_FIRST));
        mRouteSortList.add(new RouteSortModel("少收费", IBNRoutePlanManager.RoutePlanPreference
                .ROUTE_PLAN_PREFERENCE_NOTOLL));
        mRouteSortList.add(new RouteSortModel("躲避拥堵", IBNRoutePlanManager.RoutePlanPreference
                .ROUTE_PLAN_PREFERENCE_AVOID_TRAFFIC_JAM));
        mRouteSortList.add(new RouteSortModel("不走高速", IBNRoutePlanManager.RoutePlanPreference
                .ROUTE_PLAN_PREFERENCE_NOHIGHWAY));
        mRouteSortList.add(new RouteSortModel("高速优先", IBNRoutePlanManager.RoutePlanPreference
                .ROUTE_PLAN_PREFERENCE_ROAD_FIRST));
    }

    private void initView() {
        BNScrollView scrollView = mRootView.findViewById(R.id.content_scroll);
        scrollView.setVerticalScrollBarEnabled(false);
        final LinearLayout layoutTab = mRootView.findViewById(R.id.layout_3tab);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BNScrollLayout scrollLayout = mRootView.findViewById(R.id.layout_scroll);
                float dipValue = 10f;
                if (BNDemoUtils.checkDeviceHasNavigationBar(getActivity())) {
                    scrollLayout.setMaxOffset(layoutTab.getMeasuredHeight()
                            + BNDemoUtils.dip2px(getActivity(), dipValue)
                            + BNDemoUtils.getNavigationBarHeight(getActivity()));
                } else {
                    scrollLayout.setMaxOffset(layoutTab.getMeasuredHeight()
                            + BNDemoUtils.dip2px(getActivity(), dipValue));
                }
                scrollLayout.setToOpen();

                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mRl_button.getLayoutParams();
                layoutParams.bottomMargin =
                        layoutTab.getMeasuredHeight() + BNDemoUtils.dip2px(getActivity(), 10);
                mRl_button.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initData() {
        Bundle bundle = BaiduNaviManagerFactory.getRouteResultManager().getRouteInfo();
        if (bundle == null) {
            return;
        }
        // 3Tab信息
        mRoutePlanItems = bundle.getParcelableArrayList(BNaviCommonParams.BNRouteInfoKey.INFO_TAB);
        // 每条路线的详细信息
        mRouteDetails = bundle.getBundle(BNaviCommonParams.BNRouteInfoKey.INFO_ROUTE_DETAIL);
        // 每条路线的限行信息
        mLimitInfos =
                bundle.getStringArrayList(BNaviCommonParams.BNRouteInfoKey.TRAFFIC_LIMIT_INFO);
        // 每条路线的点坐标
        mRoutePoints = bundle.getBundle(BNaviCommonParams.BNRouteInfoKey.INFO_ROUTE_POINT);
        if (mRoutePoints != null) {
            ArrayList<BNRoutePlanNode> nodes = mRoutePoints.getParcelableArrayList("0");
        }
        if (mLimitInfos != null) {
            for (int i = 0; i < mLimitInfos.size(); i++) {
                String[] arr = mLimitInfos.get(i).split(",");
                Log.e(TAG, "第" + arr[0] + "条路线限行消息：" + arr[1]);
            }
        }
        if (mRoutePlanItems != null) {
            if (mRoutePlanItems.size() > 0 && mRoutePlanItems.get(0) != null) {
                initTabView(mLayout_tab0, mRoutePlanItems.get(0));
            }

            if (mRoutePlanItems.size() > 1 && mRoutePlanItems.get(1) != null) {
                initTabView(mLayout_tab1, mRoutePlanItems.get(1));
            } else {
                mLayout_tab1.setVisibility(View.GONE);
            }

            if (mRoutePlanItems.size() > 2 && mRoutePlanItems.get(2) != null) {
                initTabView(mLayout_tab2, mRoutePlanItems.get(2));
            } else {
                mLayout_tab2.setVisibility(View.GONE);
            }
        }
        mLayout_tab0.setSelected(true);

        mRouteList.clear();
        mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("0"));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mResultAdapter = new RouteResultAdapter(mRouteList);
        mRecyclerView.setAdapter(mResultAdapter);
        // 展示长途信息
        if (BaiduNaviManagerFactory.getRouteResultManager().isLongDistance()) {
            mLDLayout.setVisibility(View.VISIBLE);
        }
    }

    private void initTabView(LinearLayout layout_tab, BNRoutePlanItem bnRoutePlanItem) {
        TextView prefer = layout_tab.findViewById(R.id.prefer);
        prefer.setText(bnRoutePlanItem.getPusLabelName());
        TextView time = layout_tab.findViewById(R.id.time);
        time.setText((int) bnRoutePlanItem.getPassTime() / 60 + "分钟");
        TextView distance = layout_tab.findViewById(R.id.distance);
        distance.setText((int) bnRoutePlanItem.getLength() / 1000 + "公里");
        TextView traffic_light = layout_tab.findViewById(R.id.traffic_light);
        traffic_light.setText(String.valueOf(bnRoutePlanItem.getLights()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mFl_retry.getVisibility() == View.GONE) {
            initView();
        }
        BaiduNaviManagerFactory.getRouteResultManager().onResume();
        BaiduNaviManagerFactory.getMapManager().onResume();
        FrameLayout ybContainer = mRootView.findViewById(R.id.yb_container);
        BaiduNaviManagerFactory.getRouteResultManager().addYellowTipsToContainer(ybContainer);
    }

    @Override
    public void onPause() {
        super.onPause();
        BaiduNaviManagerFactory.getRouteResultManager().onPause();
        BaiduNaviManagerFactory.getMapManager().onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mItemsAdapter.onDestroy();
        BaiduNaviManagerFactory.getRouteResultManager().onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_0:
                mLayout_tab0.setSelected(true);
                mLayout_tab1.setSelected(false);
                mLayout_tab2.setSelected(false);
                BaiduNaviManagerFactory.getRouteResultManager().selectRoute(0);
                mRouteList.clear();
                mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("0"));
                mResultAdapter.notifyDataSetChanged();
                break;
            case R.id.route_1:
                mLayout_tab0.setSelected(false);
                mLayout_tab1.setSelected(true);
                mLayout_tab2.setSelected(false);
                BaiduNaviManagerFactory.getRouteResultManager().selectRoute(1);
                mRouteList.clear();
                mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("1"));
                mResultAdapter.notifyDataSetChanged();
                break;
            case R.id.route_2:
                if (mRoutePlanItems.size() < 3) {
                    return;
                }
                mLayout_tab0.setSelected(false);
                mLayout_tab1.setSelected(false);
                mLayout_tab2.setSelected(true);
                BaiduNaviManagerFactory.getRouteResultManager().selectRoute(2);
                mRouteList.clear();
                mRouteList.addAll(mRouteDetails.<BNRouteDetail>getParcelableArrayList("2"));
                mResultAdapter.notifyDataSetChanged();
                break;
            case R.id.btn_fullView:
                BaiduNaviManagerFactory.getRouteResultManager().fullView();
                break;
            case R.id.btn_road:
                BaiduNaviManagerFactory.getRouteResultSettingManager()
                        .setRealRoadCondition(getActivity());
                break;
            case R.id.btn_prefer:
                mPopWindow.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.btn_start_navi:
                BaiduNaviManagerFactory.getRouteResultManager().startNavi();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction tx = fm.beginTransaction();
                DemoGuideFragment fragment = new DemoGuideFragment();
                tx.replace(R.id.fragment_content, fragment, "DemoGuide");
                tx.addToBackStack(null);
                tx.commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickPrefer(int clickPrefer) {
        currentPrefer = clickPrefer;
        mItemsAdapter.updatePrefer(clickPrefer);
        mItemsAdapter.notifyDataSetChanged();
        mPopWindow.dismiss();
        mRl_button.setVisibility(View.GONE);
        routePlan();
    }

    private void updateBtnText(int clickPrefer) {
        switch (clickPrefer) {
            case IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT:
                mPreferBtn.setText("智能推荐");
                break;
            case IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_TIME_FIRST:
                mPreferBtn.setText("时间优先");
                break;
            case IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_NOTOLL:
                mPreferBtn.setText("少收费");
                break;
            case IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_AVOID_TRAFFIC_JAM:
                mPreferBtn.setText("躲避拥堵");
                break;
            case IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_NOHIGHWAY:
                mPreferBtn.setText("不走高速");
                break;
            case IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_ROAD_FIRST:
                mPreferBtn.setText("高速优先");
                break;
            default:
                break;
        }
    }

    private void routePlan() {
        List<BNRoutePlanNode> list = new ArrayList<>();
        list.add(BNDemoFactory.getInstance().getStartNode(getActivity()));
        list.add(BNDemoFactory.getInstance().getEndNode(getActivity()));

        // 关闭电子狗
        if (BaiduNaviManagerFactory.getCruiserManager().isCruiserStarted()) {
            BaiduNaviManagerFactory.getCruiserManager().stopCruise();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(BNaviCommonParams.RoutePlanKey.VEHICLE_TYPE, IBNRoutePlanManager.Vehicle.TRUCK);
        BaiduNaviManagerFactory.getRoutePlanManager().routePlan(list, currentPrefer, bundle,
                handler);
    }
}
