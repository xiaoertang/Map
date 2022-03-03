package com.example.map.liteapp;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.struct.BNMotorInfo;
import com.baidu.navisdk.adapter.struct.BNTruckInfo;
import com.baidu.navisdk.adapter.struct.VehicleConstant;

/**
 * Author: v_duanpeifeng
 * Time: 2020-05-22
 * Description:
 */
public class BNDemoFactory {

    private BNRoutePlanNode startNode;

    private BNRoutePlanNode endNode;

    private BNDemoFactory() {
    }

    private static class Holder {
        private static BNDemoFactory INSTANCE = new BNDemoFactory();
    }

    public static BNDemoFactory getInstance() {
        return Holder.INSTANCE;
    }

    public void initCarInfo() {
        // 驾车车牌设置
        // BaiduNaviManagerFactory.getCommonSettingManager().setCarNum("京A88888");

        // 货车信息
        BNTruckInfo truckInfo = new BNTruckInfo.Builder()
                .plate("京A88888")
                .axlesNumber(2)
                .axlesWeight(1)
                .emissionLimit(VehicleConstant.EmissionStandard.S3)
                .length(5)
                .weight(2)
                .loadWeight(1)
                .oilCost("40000")
                .plateType(VehicleConstant.PlateType.BLUE)
                .powerType(VehicleConstant.PowerType.OIL)
                .truckType(VehicleConstant.TruckType.HEAVY)
                .height(2)
                .width(2.5f)
                .build();
        // 该接口会做本地持久化，在应用中设置一次即可
        BaiduNaviManagerFactory.getCommonSettingManager().setTruckInfo(truckInfo);

        // 摩托车信息
        BNMotorInfo motorInfo = new BNMotorInfo.Builder()
                .plate("京A88888")
                .plateType(VehicleConstant.PlateType.BLUE)
                .motorType(VehicleConstant.MotorType.OIL)
                .displacement("")
                .build();
        // 该接口会做本地持久化，在应用中设置一次即可
        BaiduNaviManagerFactory.getCommonSettingManager().setMotorInfo(motorInfo);

        // BaiduNaviManagerFactory.getCommonSettingManager().setTestEnvironment(false);
        BaiduNaviManagerFactory.getCommonSettingManager().setNodeClick(true);
    }

    public void initRoutePlanNode() {
        startNode = new BNRoutePlanNode.Builder()
                .latitude(40.041690)
                .longitude(116.306333)
                .name("百度大厦")
                .description("百度大厦")
                .build();
        endNode = new BNRoutePlanNode.Builder()
                .latitude(39.908560)
                .longitude(116.397609)
                .name("北京天安门")
                .description("北京天安门")
                .build();
    }

    public BNRoutePlanNode getStartNode(Context context) {
        String start = BNDemoUtils.getString(context, "start_node");
        if (!TextUtils.isEmpty(start)) {
            String[] node = start.split(",");
            startNode = new BNRoutePlanNode.Builder()
                    .longitude(Double.parseDouble(node[0]))
                    .latitude(Double.parseDouble(node[1]))
                    .build();
        }
        return startNode;
    }

    public void setStartNode(Context context, String value) {
        BNDemoUtils.setString(context, "start_node", value);
    }

    public BNRoutePlanNode getEndNode(Context context) {
        String end = BNDemoUtils.getString(context, "end_node");
        if (!TextUtils.isEmpty(end)) {
            String[] node = end.split(",");
            endNode = new BNRoutePlanNode.Builder()
                    .longitude(Double.parseDouble(node[0]))
                    .latitude(Double.parseDouble(node[1]))
                    .build();
        }
        return endNode;
    }

    public void setEndNode(Context context, String value) {
        BNDemoUtils.setString(context, "end_node", value);
    }
}
