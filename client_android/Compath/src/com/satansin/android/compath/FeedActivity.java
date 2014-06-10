package com.satansin.android.compath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.satansin.android.compath.logic.FeedService;
import com.satansin.android.compath.logic.Group;
import com.satansin.android.compath.logic.Location;
import com.satansin.android.compath.logic.LocationService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.util.UITimeGenerator;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FeedActivity extends ActionBarActivity {

	public static final String EXTRA_LOCATION_ID = "com.satansin.android.compath.EXTRA_FEED_LOCATION_ID";
	public static final String EXTRA_LOCATION_NAME = "com.satansin.android.compath.EXTRA_FEED_LOCATION_NAME";
	public static final String EXTRA_LOCATION_LAT = "com.satansin.android.compath.EXTRA_FEED_LOCATION_LAT";
	public static final String EXTRA_LOCATION_LON = "com.satansin.android.compath.EXTRA_FEED_LOCATION_LON";

	private static final String UI_FEED_ITEM_ICON = "icon";
	private static final String UI_FEED_ITEM_USRNAME = "usrname";
	private static final String UI_FEED_ITEM_TIME = "time";
	private static final String UI_FEED_ITEM_TITLE = "title";
	private static final String UI_FEED_ITEM_NUMBER_OF_MEMBERS = "numberOfMembers";

	private static Location location = new Location();

	private static List<HashMap<String, Object>> feedList;
	private static SimpleAdapter feedAdapter;
	
	// 定位相关
	private LocationClient mLocClient;
	private LocationData locData = null;
	private MyLocationListenner myListener = new MyLocationListenner();
	private boolean isFirstLoc = false;
	private static final int MAX_LOCATING_TIMEOUT = 8000;
	
	private static final int REQUEST_CODE_GROUP_CREATION = 0;
	private static final int REQUEST_CODE_LOCATION_SELECTION = 1;
	
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
			
			GetLocationTask getLocationTask = new GetLocationTask();
			getLocationTask.execute((int)(locData.latitude * 1e6), (int)(locData.longitude * 1e6));
			
			// 首次定位完成
			isFirstLoc = true;
			mLocClient.stop();
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}

		public void startTiming() {
			final int timeInterval = 200;
			new Thread(new Runnable() {
				@Override
				public void run() {
					int duration = 0;
					while (duration < MAX_LOCATING_TIMEOUT) {
						if (isFirstLoc) {
							break;
						}
						duration += timeInterval;
						try {
							Thread.sleep(timeInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					// 若定位失败，直接获取当前位置的feed
					if (!isFirstLoc) {
						mLocClient.stop();
						Toast.makeText(getApplicationContext(), R.string.error_locating_timeout, Toast.LENGTH_SHORT).show();
					}
				}
			}).start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

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
		
		startLocating();
	}
	
	private void startLocating() {
		isFirstLoc = false;
		mLocClient.start();
		myListener.startTiming();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_personal_settings) {
			Intent toPersonalSettingsIntent = new Intent(this, PersonalSettingsActivity.class);
			startActivity(toPersonalSettingsIntent);
			return true;
		} else if (id == R.id.action_location_selection) {
			Intent toLocationSelectionIntent = new Intent(this, LocationSelectionActivity.class);
			toLocationSelectionIntent = putLocationExtras(toLocationSelectionIntent);
			startActivityForResult(toLocationSelectionIntent, REQUEST_CODE_LOCATION_SELECTION);
			return true;
		} else if (id == R.id.action_create_group) {
			Intent toGroupCreationIntent = new Intent(this, GroupCreationActivity.class);
			toGroupCreationIntent = putLocationExtras(toGroupCreationIntent);
			startActivityForResult(toGroupCreationIntent, REQUEST_CODE_GROUP_CREATION);
			return true;
		} else if (id == R.id.action_refresh) {
			startLocating();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private Intent putLocationExtras(Intent intent) {
		intent.putExtra(EXTRA_LOCATION_ID, location.getId());
		intent.putExtra(EXTRA_LOCATION_NAME, location.getName());
		intent.putExtra(EXTRA_LOCATION_LON, location.getLongitude());
		intent.putExtra(EXTRA_LOCATION_LAT, location.getLatitude());
		return intent;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GROUP_CREATION && resultCode == RESULT_OK) {
			Intent toDiscussIntent = new Intent(this, DiscussActivity.class);
			toDiscussIntent.putExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, data.getIntExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, 0));
			startActivity(toDiscussIntent);
		} else if (requestCode == REQUEST_CODE_LOCATION_SELECTION && resultCode == RESULT_OK) {
			String locationId = data.getStringExtra(EXTRA_LOCATION_ID);
			String locationName = data.getStringExtra(EXTRA_LOCATION_NAME);
			int locationLat = data.getIntExtra(EXTRA_LOCATION_LAT, 0);
			int locationLon = data.getIntExtra(EXTRA_LOCATION_LON, 0);
			location = new Location(locationId, locationName, locationLat, locationLon);
			setTitle(locationName);

			GetFeedTask getFeedTask = new GetFeedTask();
			getFeedTask.execute();
		}
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
			View rootView = inflater.inflate(R.layout.fragment_feed, container,
					false);

			ListView listView = (ListView) rootView
					.findViewById(R.id.feed_list_view);
			feedList = new ArrayList<HashMap<String, Object>>();
			feedAdapter = new SimpleAdapter(getActivity(), feedList,
					R.layout.group_item_feed,
					new String[] { UI_FEED_ITEM_ICON, UI_FEED_ITEM_USRNAME,
							UI_FEED_ITEM_TIME, UI_FEED_ITEM_TITLE,
							UI_FEED_ITEM_NUMBER_OF_MEMBERS }, new int[] {
							R.id.feed_item_icon, R.id.feed_item_usrname,
							R.id.feed_item_time, R.id.feed_item_title,
							R.id.feed_item_number_of_members });
			listView.setAdapter(feedAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Group selectedGroup = (Group) parent.getItemAtPosition(position);
					if (selectedGroup == null) {
						return;
					}
					
					Intent toDiscussIntent = new Intent(getActivity(), DiscussActivity.class);
					toDiscussIntent.putExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, selectedGroup.getId());
					startActivity(toDiscussIntent);
				}
			});

			return rootView;
		}
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
					Toast.makeText(getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(), R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
					return;
				}
			}
			
			location = result;
			setTitle(result.getName());

			GetFeedTask getFeedTask = new GetFeedTask();
			getFeedTask.execute();
		}
		
	}

	private class GetFeedTask extends AsyncTask<Void, Void, List<Group>> {
		private Exception exception;
		@Override
		protected List<Group> doInBackground(Void... params) {
			FeedService feedService = ServiceFactory.getFeedService();
			ArrayList<Group> groups = new ArrayList<Group>();
			try {
				groups = (ArrayList<Group>) feedService.getGroupListByLocationId(location.getId());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			}
			return groups;
		}

		@Override
		protected void onPostExecute(List<Group> result) {
			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(), R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (result.size() == 0) {
				return;
			}
			feedList.clear();
			for (int i = 0; i < result.size(); i++) {
				Group group = result.get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(UI_FEED_ITEM_ICON, R.drawable.test_icon); // TODO 图片存放
				map.put(UI_FEED_ITEM_USRNAME, group.getOwnerName());
				map.put(UI_FEED_ITEM_TIME, new UITimeGenerator().getFormattedFeedTime(group.getLastActiveTime()));
				map.put(UI_FEED_ITEM_TITLE, group.getTitle());
				map.put(UI_FEED_ITEM_NUMBER_OF_MEMBERS, group.getNumberOfMembers());
				feedList.add(map);
			}
			feedAdapter.notifyDataSetChanged();
		}
	}

}
