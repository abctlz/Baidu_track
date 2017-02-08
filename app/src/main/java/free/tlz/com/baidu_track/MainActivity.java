package free.tlz.com.baidu_track;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private LocationClient locationClient = null;
    private MyLocationListener myLocationListener;
    private double currentLat;//当前经度
    private double currentLng;//当前维度
    private String currentAddress;//当前地址
    private int currentTrackLineID;// 当前跟踪的线路ID
    private GeoCoder geoCoder;
    private DatabaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);
        adapter = new DatabaseAdapter(this);
        initBaiduMap();
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //菜单事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mylocation:
                mylocation();//我的位置
                break;
            case R.id.start_track:
                startTrack();//开始跟踪
                break;
            case R.id.end_track:
                endTrack();//结束跟踪
                break;
            case R.id.track_back:
                trackBack();//跟踪回放
                break;

            default:
                break;
        }
        return true;
    }

    //跟踪回放
    private void trackBack() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("跟踪路线列表");
        View view = getLayoutInflater().inflate(
                R.layout.track_line_playback_dialog, null);
        ListView playbackListView = (ListView) view
                .findViewById(R.id.listView1_play_back);

        //查询所有数据
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();


        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    //结束跟踪
    private void endTrack() {
        isTracking = false;
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(
                currentLat, currentLng)));
    }

    //开始跟踪
    private void startTrack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("开始跟踪");
        builder.setCancelable(true);
        final View view = getLayoutInflater().inflate(
                R.layout.add_track_line_dialog, null);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText ed_track_name = (EditText) view.findViewById(R.id.editText1_track_name);
                String track_name = ed_track_name.getText().toString().trim();
                if(track_name.isEmpty())
                    return;
                Toast.makeText(MainActivity.this, "路线" + track_name + "开始导航", Toast.LENGTH_SHORT).show();
                createTrack(track_name);//提交导航

            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//关闭对话框
            }
        });
        builder.show();
        System.out.println("修改");

    }

    //用于存储起始点和终点
    private ArrayList<LatLng> list = new ArrayList<LatLng>();
    boolean isTracking = false;

    //创建路线
    private void createTrack(String track_name) {
        Track track = new Track();
        track.setTrack_name(track_name);//设置路线名称
        track.setCreate_date(DateUtils.toDate(new Date()));//设置线路创建时间
        track.setStart_loc(currentAddress);//设置起点地址
        currentTrackLineID = (int) adapter.addTrack(track);//添加线路,获得线路ID
        adapter.addTrackDetail(currentTrackLineID, currentLat, currentLng);
        baiduMap.clear();
        addOverlay();
        list.add(new LatLng(currentLat, currentLng));//添加起点
        isTracking = true;//异步线程启动标记
        new Thread(new TrackThread()).start();//启动模拟路线
    }

    class TrackThread implements Runnable {

        @Override
        public void run() {
            while (isTracking) {
                getLocation();//获取当前经纬度
                adapter.addTrackDetail(currentTrackLineID, currentLat, currentLng);
                addOverlay();
                list.add(new LatLng(currentLat, currentLng));
                drawLine();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //描绘轨迹
    private void drawLine() {
        OverlayOptions line = new PolylineOptions().points(list).color(0xFFFF0000);
        baiduMap.addOverlay(line);
        list.remove(0);
    }

    private void getLocation() {
        currentLat = currentLat + Math.random() / 1000;
        currentLng = currentLng + Math.random() / 1000;
    }

    private void addOverlay() {
        baiduMap.setMyLocationEnabled(false);//关闭图层
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);
        // 构建MarkerOption，用于在地图上添加Marker
        LatLng latlng = new LatLng(currentLat, currentLng);
        OverlayOptions option = new MarkerOptions().position(latlng).icon(bitmap);
        // 在地图上添加Marker，并显示
        baiduMap.addOverlay(option);
        //把当前添加的位置作为地图的中心点
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
    }

    //我的位置
    private void mylocation() {
        Toast.makeText(MainActivity.this, "正在定位中...", Toast.LENGTH_SHORT)
                .show();
        tag = true;
        baiduMap.clear();//清除地图上自定义的图层
        baiduMap.setMyLocationEnabled(true);//启用我的位置图层
        locationClient.requestLocation();// 发起定位请求

    }

    //初始化地图
    private void initBaiduMap() {
        baiduMap = mMapView.getMap();
        locationClient = new LocationClient(getApplicationContext());
        baiduMap.setMyLocationEnabled(true);//打开定位图层
        myLocationListener = new MyLocationListener();
        locationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        locationClient.setLocOption(option);
        locationClient.start();// 启动SDK定位
        locationClient.requestLocation();// 发起定位请求

        //用于转换地理编码的监听器
        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检索到结果
                } else {
                    // 获取地理编码结果
                    // System.out.println(result.getAddress());
                    currentAddress = result.getAddress();
                    //更新线路的结束位置
                    adapter.updateEndLoc(currentAddress, currentTrackLineID);
                }
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {

            }
        });
    }


    boolean tag = true;

    //
    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null && tag) {
                tag = false;
                currentLat = bdLocation.getLatitude();
                currentLng = bdLocation.getLongitude();
                currentAddress = bdLocation.getAddrStr();
                //构造我的当前位置信息
                MyLocationData.Builder builder = new MyLocationData.Builder();
                builder.latitude(bdLocation.getLatitude());// 设置纬度
                builder.longitude(bdLocation.getLongitude());// 设置经度
                builder.accuracy(bdLocation.getRadius());// 设置精度（半径）
                builder.direction(bdLocation.getDirection());// 设置方向
                builder.speed(bdLocation.getSpeed());// 设置速度
                MyLocationData locationData = builder.build();

                //把我的位置信息设置到地图上
                baiduMap.setMyLocationData(locationData);
                //配置我的位置
                LatLng latlng = new LatLng(currentLat, currentLng);
                //设置我的位置的配置信息: 模式:跟随模式,是否要显示方向,图标
                baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING,
                        true, null));
                // 设置我的位置为地图的中心点(缩放级别为 3-20)
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(
                        latlng, 15));
            }
        }
    }

}
