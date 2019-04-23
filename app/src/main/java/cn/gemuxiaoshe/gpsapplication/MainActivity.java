package cn.gemuxiaoshe.gpsapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView = null;
    private RadioButton rbtn_1, rbtn_2;
    private CheckBox cb_1, cb_2;
    private boolean isFirstLoc = true;
    private LocationClient locationClient;

    public MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // 获取地图控件的引用
        mapView = (MapView) findViewById(R.id.mMapView);
        // 开启定位图层
        mapView.getMap().setMyLocationEnabled(true);
        //定位初始化
         locationClient = new LocationClient(this);

        locationClient.registerLocationListener(myListener);

        //定位配置信息
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd0911");
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        locationClient.start();
        initView();

    }



    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mapView.getMap().setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    private void initView() {
        rbtn_1 =(RadioButton)findViewById(R.id.Rbtn_1);
        rbtn_2 =(RadioButton)findViewById(R.id.Rbtn_2);

        cb_1 = (CheckBox) findViewById(R.id.cb_1);
        cb_2 = (CheckBox) findViewById(R.id.cb_2);


        rbtn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 设置为普通图
                mapView = (MapView) findViewById(R.id.mMapView);
                mapView.getMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);
            }
        });
        rbtn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 设置为卫星图
                mapView = (MapView) findViewById(R.id.mMapView);
                mapView.getMap().setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            }
        });
            // 设置复选框监听事件,选中与取消选中的两种事件.
        cb_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mapView.getMap().setTrafficEnabled(true);
                    // 可以自定义设置路况显示的颜色,四个参数,分别代表严重拥堵,拥堵,缓行,畅通
                    mapView.getMap().setCustomTrafficColor("#ffba0101", "#fff33131", "#ffff9e19", "#00000000");
                    // 对地图状态做更新,否则可能不会触发渲染.造成自定义无效.
                    MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(13);
                    mapView.getMap().animateMapStatus(u);
                }else
                    mapView.getMap().setTrafficEnabled(false);
            }
        });

        cb_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mapView.getMap().setBaiduHeatMapEnabled(true);
                }else
                    mapView.getMap().setBaiduHeatMapEnabled(false);
            }
        });
    }

    // 重写父类方法,管理各部分生命周期.
    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();

    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        mapView.getMap().setMyLocationEnabled(false);
        mapView.onDestroy();
        super.onDestroy();
    }
}


