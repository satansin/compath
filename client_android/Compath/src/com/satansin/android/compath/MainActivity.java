package com.satansin.android.compath;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	// 定位相关
	LocationClient mLocClient;
	LocationData locData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	boolean isFirstLoc = false;

	// 搜索相关
	MKSearch mSearch = null;
	boolean isFirstReverseCode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		/**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        CompathApplication app = (CompathApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(new CompathApplication.MyGeneralListener());
        }

		// 定位初始化
		mLocClient = new LocationClient(this);
		locData = new LocationData();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("错误号：%d", error);
					Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				
				if (isFirstReverseCode)
					return;
				
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
					//反地理编码：通过坐标点检索详细地址及周边poi
					String strInfo = res.strAddr;

					// 更新定位数据
					TextView provinceTextView = (TextView) findViewById(R.id.provinceTextView);
					TextView cityTextView = (TextView) findViewById(R.id.cityTextView);
					TextView streetTextView = (TextView) findViewById(R.id.streetTextView);
					
					provinceTextView.setText(strInfo);
				}
			}
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
			}

        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			
			if (isFirstLoc)
				return;

			locData.latitude = location.getLatitude();
			locData.longitude = location.getLongitude();
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			locData.accuracy = 0;
			// 此处可以设置 locData的方向信息, 如果定位 SDK 未返回方向信息，用户可以自己实现罗盘功能添加方向信息。
			//locData.direction = location.getDerect();
			
			GeoPoint ptCenter = new GeoPoint((int)(locData.latitude*1e6), (int)(locData.longitude*1e6));
			//反Geo搜索
			mSearch.reverseGeocode(ptCenter);
			
			// 首次定位完成
			isFirstLoc = false;
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

}
