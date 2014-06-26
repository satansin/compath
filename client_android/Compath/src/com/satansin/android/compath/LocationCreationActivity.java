package com.satansin.android.compath;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
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
import com.satansin.android.compath.logic.LocationService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class LocationCreationActivity extends ActionBarActivity {
	
	private static final String EXTRA_START_FROM_MAP = "com.satansin.android.compath.EXTRA_START_FROM_MAP";
	
	private int latitude = 0;
	private int longitude = 0;
	private String locationName = "";
	private boolean hasLocation = false;
	
	private int cityId = 0;
	
	private EditText locationNameEditText;
	
	private CreateLocationTask createLocationTask;
	
	// 定位相关
	private LocationClient mLocClient;
	private MyLocationListenner myListener = new MyLocationListenner();
	//搜索相关
	private MKSearch mSearch = null;
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			
			latitude = (int)(location.getLatitude() * 1e6);
			longitude = (int)(location.getLongitude() * 1e6);
			
			// 首次定位完成
			hasLocation = true;
			mLocClient.stop();
			
			mSearch.reverseGeocode(new GeoPoint(latitude, longitude));
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_creation);
		
		locationNameEditText = (EditText) findViewById(R.id.edit_location_name);
		
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
        
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener() {
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
					String city = res.addressComponents.city;
					Log.w("city_get", city);
					try {
						cityId = ServiceFactory.getMemoryService(getApplicationContext()).getCityIdByName(city);
					} catch (UnknownErrorException e) {
					}
				}
			}
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			}
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			}
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			}
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
			}
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			}
        });
		
		if (getIntent().hasExtra(FeedActivity.EXTRA_LOCATION_LAT) &&
			getIntent().hasExtra(FeedActivity.EXTRA_LOCATION_LON)) {
			hasLocation = true;
			latitude = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LAT, 0);
			longitude = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);
			
			mSearch.reverseGeocode(new GeoPoint(latitude, longitude));
		} else {
			// 定位初始化
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);
			mLocClient.setLocOption(option);
			mLocClient.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_creation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_finish_creation) {
			attemptCreate();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	private DialogInterface.OnClickListener cancelDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				setResult(RESULT_CANCELED);
				LocationCreationActivity.this.finish();
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog cancelAlertDialog = new AlertDialog.Builder(this).create();
			cancelAlertDialog.setTitle(getString(R.string.alert_cancel_title));
			cancelAlertDialog.setMessage(getString(R.string.alert_cancel_message_create));
			cancelAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.alert_positive), cancelDialogListener);
			cancelAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.alert_negative), cancelDialogListener);
			cancelAlertDialog.show();
		}
		return true;
	}
	
	private void attemptCreate() {
		if (createLocationTask != null) {
			return;
		}
		
		locationNameEditText.setError(null);
		locationName = locationNameEditText.getText().toString();
		
		if (TextUtils.isEmpty(locationName)) {
			locationNameEditText.setError(getString(R.string.error_title_required));
			locationNameEditText.requestFocus();
			return;
		} else if (locationName.length() > 15) {
			locationNameEditText.setError(getString(R.string.error_title_too_long));
			locationNameEditText.requestFocus();
			return;
		}
		
		if (!hasLocation) {
			Toast.makeText(this, getString(R.string.error_non_loc), Toast.LENGTH_SHORT).show();
		}
		
		if (cityId <= 0) {
			Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
		}

		createLocationTask = new CreateLocationTask();
		createLocationTask.execute((Void) null);
	}
	
	private class CreateLocationTask extends AsyncTask<Void, Void, Boolean> {
		private Exception exception;
		private LocationService locationService;
		@Override
		protected Boolean doInBackground(Void... params) {
			locationService = ServiceFactory.getLocationService();
			boolean created = false;
			try {
				MemoryService memoryService = ServiceFactory.getMemoryService(getApplicationContext());
				created = locationService.createLocation(locationName, latitude, longitude, cityId, memoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return created;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			createLocationTask = null;
			
			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(), R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof NotLoginException) {
					try {
						ServiceFactory.getMemoryService(getApplicationContext()).clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(LocationCreationActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}

			if (success) {
				Toast.makeText(getApplicationContext(), R.string.success_create, Toast.LENGTH_SHORT).show();
				finishLocationCreation(locationService.getNewCreatedLocationId());
			} else {
				Toast.makeText(getApplicationContext(), R.string.error_create_fail_retry, Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			createLocationTask = null;
		}
		
	}
	
	private void finishLocationCreation(int newCreatedLocationId) {
		boolean startFromMap = getIntent().getBooleanExtra(EXTRA_START_FROM_MAP, false);
		Intent intent;
		if (startFromMap) {
			intent = new Intent(this, MapSelectionActivity.class);
		} else {
			intent = new Intent(this, FeedActivity.class);
		}
		intent.putExtra(FeedActivity.EXTRA_LOCATION_ID, newCreatedLocationId);
		intent.putExtra(FeedActivity.EXTRA_LOCATION_NAME, locationName);
		intent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, latitude);
		intent.putExtra(FeedActivity.EXTRA_LOCATION_LON, longitude);
		setResult(RESULT_OK, intent);
		LocationCreationActivity.this.finish();
	}

}
