package com.satansin.android.compath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.satansin.android.compath.logic.Location;
import com.satansin.android.compath.logic.LocationService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NonLocationException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationSelectionActivity extends ActionBarActivity {
	
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
	
	private TextView locationNameTextView;
	
	private GetLocationTask getLocationTask;
	
	private Location currentLocation = null;
	
	private HashMap<OverlayItem ,Location> locItemMap = new HashMap<OverlayItem ,Location>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_selection);
		
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
        if (getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_ID, 0) <= 0) {
            mMapController.setZoom(12);
		}
        mMapController.setZoom(16);
        
        /**
         * 设置地图点击事件监听 
         */
        mapTouchListener = new MKMapTouchListener(){
			@Override
			public void onMapClick(GeoPoint point) {
				if (getLocationTask != null) {
					return;
				}
				int lat = point.getLatitudeE6();
				int lon = point.getLongitudeE6();
				Log.w("lat_clicked", String.valueOf(lat));
				Log.w("lon_clicked", String.valueOf(lon));
				getLocationTask = new GetLocationTask();
				getLocationTask.execute(lat, lon);
				
				if (locationNameTextView != null) {
					locationNameTextView.setText(getString(R.string.location_selection_loading));
				}
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
			}
			@Override
			public void onMapLongClick(GeoPoint point) {
			}
        };
        mMapView.regMapTouchListner(mapTouchListener);
        
        // 传来空值时设置地图的显示
        currentLocation = new Location(
				getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_ID, 0),
				getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_NAME),
				getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LAT, 0),
				getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0));
        
        if (currentLocation.getLatitude() != 0 && currentLocation.getLongitude() != 0) {
            mMapController.setCenter(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else {
        	// 设置默认位置为天安门
        	mMapController.setCenter(new GeoPoint((int)(39.909604 * 1E6), (int)(116.397228 * 1E6)));
		}
        
        RelativeLayout locationSelectionLayout = (RelativeLayout) findViewById(R.id.location_selection_layout);
        locationSelectionLayout.setOnClickListener(
        		new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				Intent toFeedIntent = new Intent(LocationSelectionActivity.this, FeedActivity.class);
        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_ID, currentLocation.getId());
        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_NAME, currentLocation.getName());
        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, currentLocation.getLatitude());
        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_LON, currentLocation.getLongitude());
        				setResult(RESULT_OK, toFeedIntent);
        				LocationSelectionActivity.this.finish();
        			}
        		});
        
        mOverlay = new ItemizedOverlay<OverlayItem>(getResources().getDrawable(R.drawable.icon_gcoding), mMapView) {
        	@Override
    		public boolean onTap(int index){
    			OverlayItem item = getItem(index);
    			Location selectedLocation = locItemMap.get(item);
    			if (selectedLocation != null) {
        			currentLocation = locItemMap.get(item);
				}
    			locationNameTextView = (TextView) findViewById(R.id.location_selection_text);
    			locationNameTextView.setText(currentLocation.getName());
    			
    			return true;
    		}
        };
        mMapView.getOverlays().add(mOverlay);
        
        locationNameTextView = (TextView) findViewById(R.id.location_selection_text);
        if (currentLocation.getId() == 0) {
			locationNameTextView.setText(getString(R.string.location_selection_error_non_loc_tap));
		} else {
			locationNameTextView.setText(currentLocation.getName());
			
			GeoPoint point = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
			OverlayItem item = new OverlayItem(point, "", "");
			mOverlay.addItem(item);
			mMapView.refresh();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_selection, menu);
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
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	private void updateMapState(List<Location> result) {
		locItemMap.clear();
		mOverlay.removeAll();
		for (Location location : result) {
			OverlayItem item = new OverlayItem(new GeoPoint(location.getLatitude(), location.getLongitude()), "", "");
			locItemMap.put(item, location);
			mOverlay.addItem(item);
		}
		mMapView.refresh();
		
		locationNameTextView.setText(getString(R.string.location_selection_tap_pops));
	}
	
//	public void updateMapState(int lat, int lon, String locationName) {
////		mMapController.setCenter(new GeoPoint(lat, lon));
//		// 在地图上标注marker
//		mOverlay.removeAll();
//		GeoPoint point = new GeoPoint(lat, lon);
//		OverlayItem item = new OverlayItem(point, "", "");
//		mOverlay.addItem(item);
//		mMapView.refresh();
//		
//		TextView locationNameTextView = (TextView) findViewById(R.id.location_selection_text);
//		locationNameTextView.setText(locationName);
//	}
	
	private class GetLocationTask extends AsyncTask<Integer, Void, List<Location>> {
		private Exception exception;
		private LocationService locationService;
		@Override
		protected List<Location> doInBackground(Integer... params) {
			locationService = ServiceFactory.getLocationService();
			try {
				return locationService.getLocationsByPoint(params[0], params[1]);
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NonLocationException e) {
				exception = (NonLocationException) e;
			}
			return new ArrayList<Location>();
		}
		
		@Override
		protected void onPostExecute(List<Location> result) {
			getLocationTask = null;
			
			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(), R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof NonLocationException) {
					locationNameTextView.setText(getString(R.string.location_selection_error_non_loc_tap));
					return;
				}
			}
			
			if (result.size() <= 0) {
				locationNameTextView.setText(getString(R.string.location_selection_error_non_loc_tap));
				return;
			}
			updateMapState(result);
//			updateMapState(result.getLatitude(), result.getLongitude(), result.getName());
//			currentLocation = result;
		}
		
	}

}
