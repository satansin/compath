package com.satansin.android.compath;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MapSelectionActivity extends ActionBarActivity {
	
	private int latitude = 0;
	private int longitude = 0;
	
	private boolean selected = false;
	
	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 * 用于截获屏坐标
	 */
	private MKMapTouchListener mapTouchListener = null;
	/**
	 * 覆盖物图层
	 */
	private ItemizedOverlay<OverlayItem> mOverlay = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_selection);

		CompathApplication app = (CompathApplication) this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getApplicationContext());
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(new CompathApplication.MyGeneralListener());
        }
		mMapView = (MapView) findViewById(R.id.bmapView);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(16);
        
        mOverlay = new ItemizedOverlay<OverlayItem>(getResources().getDrawable(R.drawable.icon_gcoding), mMapView);
        mMapView.getOverlays().add(mOverlay);
        
        /**
         * 设置地图点击事件监听 
         */
        mapTouchListener = new MKMapTouchListener(){
			@Override
			public void onMapClick(GeoPoint point) {
				latitude = point.getLatitudeE6();
				longitude = point.getLongitudeE6();
				Log.w("lat_clicked", String.valueOf(latitude));
				Log.w("lon_clicked", String.valueOf(longitude));
				
				selected = true;
				// 在地图上标注marker
				mOverlay.removeAll();
				OverlayItem item = new OverlayItem(point, "", "");
				mOverlay.addItem(item);
				mMapView.refresh();
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
			}
			@Override
			public void onMapLongClick(GeoPoint point) {
			}
        };
        mMapView.regMapTouchListner(mapTouchListener);
        
        latitude = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LAT, 0);
        longitude = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);
        
        if (latitude != 0 && longitude != 0) {
        	mMapController.setCenter(new GeoPoint(latitude, longitude));
        } else {
        	mMapController.setZoom(12);
        	mMapController.setCenter(new GeoPoint((int)(32.056774 * 1E6), (int)(118.780659 * 1E6)));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_selection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_finish_creation) {
			if (!selected) {
				return true;
			}
			startLocationCreation();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startLocationCreation() {
		Intent intent = new Intent(this, LocationCreationActivity.class);
		intent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, latitude);
		intent.putExtra(FeedActivity.EXTRA_LOCATION_LON, longitude);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			Intent intent = new Intent(this, FeedActivity.class);
			intent.putExtra(FeedActivity.EXTRA_LOCATION_ID, data.getIntExtra(FeedActivity.EXTRA_LOCATION_ID, 0));
			intent.putExtra(FeedActivity.EXTRA_LOCATION_NAME, data.getStringExtra(FeedActivity.EXTRA_LOCATION_NAME));
			intent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, data.getIntExtra(FeedActivity.EXTRA_LOCATION_LAT, 0));
			intent.putExtra(FeedActivity.EXTRA_LOCATION_LON, data.getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0));
			setResult(RESULT_OK, intent);
			MapSelectionActivity.this.finish();
		} else if (requestCode == 0 && resultCode == RESULT_CANCELED) {
			MapSelectionActivity.this.finish();
		}
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	

}
