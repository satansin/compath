package com.satansin.android.compath;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.satansin.android.compath.logic.Location;
import com.satansin.android.compath.logic.LocationService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationSelectionActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_selection);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		private View rootView;
		
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
		
		private GetLocationTask getLocationTask;
		
		private Location currentLocation = null;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(
					R.layout.fragment_location_selection, container, false);

			CompathApplication app = (CompathApplication)this.getActivity().getApplication();
	        if (app.mBMapManager == null) {
	            app.mBMapManager = new BMapManager(getActivity().getApplicationContext());
	            /**
	             * 如果BMapManager没有初始化则初始化BMapManager
	             */
	            app.mBMapManager.init(new CompathApplication.MyGeneralListener());
	        }
			mMapView = (MapView)rootView.findViewById(R.id.bmapView);
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
	        mMapController.setZoom(12);
	        
	        /**
	         * 设置地图点击事件监听 
	         */
	        mapTouchListener = new MKMapTouchListener(){
				@Override
				public void onMapClick(GeoPoint point) {
					int lat = point.getLatitudeE6();
					int lon = point.getLongitudeE6();
					getLocationTask = new GetLocationTask();
					getLocationTask.execute(lat, lon);
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
					getActivity().getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_ID),
					getActivity().getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_NAME),
					getActivity().getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LAT, (int)(32.056774 * 1E6)),
					getActivity().getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, (int)(118.780659 * 1E6)));
	        
	        mMapController.setCenter(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
	        
	        RelativeLayout locationSelectionLayout = (RelativeLayout) rootView.findViewById(R.id.location_selection_layout);
	        locationSelectionLayout.setOnClickListener(
	        		new OnClickListener() {
	        			@Override
	        			public void onClick(View v) {
	        				Intent toFeedIntent = new Intent(getActivity(), FeedActivity.class);
	        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_ID, currentLocation.getId());
	        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_NAME, currentLocation.getName());
	        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, currentLocation.getLongitude());
	        				toFeedIntent.putExtra(FeedActivity.EXTRA_LOCATION_LON, currentLocation.getLongitude());
	        				getActivity().setResult(RESULT_OK, toFeedIntent);
	        				getActivity().finish();
	        			}
	        		});
	        
	        ((TextView) rootView.findViewById(R.id.location_selection_text)).setText(currentLocation.getName());
	        
			return rootView;
		}
		
		public void updateMapState(int lat, int lon, String locationName) {
			mMapController.setCenter(new GeoPoint(lat, lon));
			TextView locationNameTextView = (TextView) rootView.findViewById(R.id.location_selection_text);
			locationNameTextView.setText(locationName);
		}
		
		private class GetLocationTask extends AsyncTask<Integer, Void, Location> {
			private Exception exception;
			@Override
			protected Location doInBackground(Integer... params) {
				LocationService locationService = ServiceFactory.getLocationService();
				Location location = new Location();
				try {
					location = locationService.getLocationByPoint(params[0], params[1]);
				} catch (NetworkTimeoutException e) {
					exception = (NetworkTimeoutException) e;
				} catch (UnknownErrorException e) {
					exception = (UnknownErrorException) e;
				}
				return location;
			}
			
			@Override
			protected void onPostExecute(Location result) {
				if (exception != null) {
					if (exception instanceof NetworkTimeoutException) {
						Toast.makeText(getActivity().getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
						return;
					} else if (exception instanceof UnknownErrorException) {
						Toast.makeText(getActivity().getApplicationContext(), R.string.error_unknown, Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				updateMapState(result.getLatitude(), result.getLongitude(), result.getName());
				currentLocation = result;
			}
			
		}
	}

}
