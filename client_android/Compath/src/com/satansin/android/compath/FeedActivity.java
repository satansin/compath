package com.satansin.android.compath;

import java.util.ArrayList;
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
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NonLocationException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.util.UITimeGenerator;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FeedActivity extends ActionBarActivity {

	/**
	 * Extra: id of the selected location.
	 */
	public static final String EXTRA_LOCATION_ID = "com.satansin.android.compath.EXTRA_FEED_LOCATION_ID";
	/**
	 * Extra: name of the selected location.
	 */
	public static final String EXTRA_LOCATION_NAME = "com.satansin.android.compath.EXTRA_FEED_LOCATION_NAME";
	/**
	 * Extra: latitude of the selected location.
	 */
	public static final String EXTRA_LOCATION_LAT = "com.satansin.android.compath.EXTRA_FEED_LOCATION_LAT";
	/**
	 * Extra: longitude of the selected location.
	 */
	public static final String EXTRA_LOCATION_LON = "com.satansin.android.compath.EXTRA_FEED_LOCATION_LON";
	
	/**
	 * Request code for group creation.
	 */
	private static final int REQUEST_CODE_GROUP_CREATION = 0;
	/**
	 * Request code for location selection.
	 */
	private static final int REQUEST_CODE_LOCATION_SELECTION = 1;
	/**
	 * Request code for personal settings.
	 */
	private static final int REQUEST_CODE_PERSONAL_SETTINGS = 2;
	/**
	 * Request code for location creation.
	 */
	private static final int REQUEST_CODE_LOCATION_CREATION = 3;
	/**
	 * Request code for entering a group right after it is created.
	 */
	private static final int REQUEST_CODE_GROUP_ENTERING = 4;

	/**
	 * Current location the user are at.<br>
	 * Id 0 shows that the there's no location info now, usually the location includes a city's info.
	 */
	private Location mLocation = new Location();

//	private List<FeedItem> feedList;
	private List<Group> mFeedList;
	private FeedItemAdapter mFeedAdapter;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private TextView nonFeedReminderTextView;
	
	private GetFeedTask mGetFeedTask;
	
	// 定位相关
	private LocationClient mLocClient;
	private LocationData locData = null;
	private MyLocationListenner myListener = new MyLocationListenner();
	private boolean isFirstLoc = false;
	private static final int MAX_LOCATING_TIMEOUT = 8000;
	
	/**
	 * Location listener.<br>
	 * You should call the startTiming method to start a timing, the location operation will stop after MAX_LOCATING_TIMEOUT.
	 * When a location is received, update the locData, set isFirstLoc true, and stop the mLocClient.<br>
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
			int timeInterval = 200;
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
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// add this activity to application activity list
		CompathApplication.getInstance().addActivity(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

		// initiate the list and adapter
//		feedList = new ArrayList<FeedItem>();
		mFeedList = new ArrayList<Group>();
		mFeedAdapter = new FeedItemAdapter(this);

		mListView = (ListView) findViewById(R.id.feed_list_view);
		mListView.setAdapter(mFeedAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Group selectedGroup = ((FeedItem)parent.getItemAtPosition(position)).group;
				Group selectedGroup = (Group) parent.getItemAtPosition(position);
				int selectedGroupId = selectedGroup.getId();
		
				Intent toDiscussIntent = new Intent(FeedActivity.this, DiscussActivity.class);
				toDiscussIntent.putExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, selectedGroupId);
				startActivity(toDiscussIntent);
			}
		});
		
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		// 顶部刷新的样式 
		mSwipeRefreshLayout.setColorScheme(R.color.holo_red_light, R.color.holo_green_light,  
                R.color.holo_blue_bright, R.color.holo_orange_light);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				executeRefresh(false, false);
			}
		});
		
		nonFeedReminderTextView = (TextView) findViewById(R.id.feed_non_feed_text);
		
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
		
		executeRefresh(true, true);
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
			startPersonalSettings();
			return true;
		} else if (id == R.id.action_location_selection) {
			startLocationSelection();
			return true;
		} else if (id == R.id.action_create_group) {
			startGroupSelection();
			return true;
		} else if (id == R.id.action_create_location) {
			startLocationCreation();
			return true;
		} else if (id == R.id.action_refresh) {
			executeRefresh(true, true);
			return true;
		} else if (id == R.id.action_exit) {
			exitApp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			exitApp();
//		}
//		return true;
//	}
	
	private void startPersonalSettings() {
		Intent toPersonalSettingsIntent = new Intent(this, PersonalSettingsActivity.class);
		startActivityForResult(toPersonalSettingsIntent, REQUEST_CODE_PERSONAL_SETTINGS);
	}
	
	private void startLocationSelection() {
		Intent toLocationSelectionIntent = new Intent(this, LocationSelectionActivity.class);
		toLocationSelectionIntent = putLocationExtras(toLocationSelectionIntent);
		startActivityForResult(toLocationSelectionIntent, REQUEST_CODE_LOCATION_SELECTION);
	}
	
	private void startGroupSelection() {
		Intent toGroupCreationIntent = new Intent(this, GroupCreationActivity.class);
		toGroupCreationIntent = putLocationExtras(toGroupCreationIntent);
		startActivityForResult(toGroupCreationIntent, REQUEST_CODE_GROUP_CREATION);
	}
	
	private void startLocationCreation() {
		Builder createLocBuilder = new Builder(this);
		createLocBuilder.setTitle(getString(R.string.alert_create_loc_title));
		createLocBuilder.setItems(getResources().getStringArray(R.array.alert_create_loc_list), createLocDialogListener);
		AlertDialog createLocAlertDialog = createLocBuilder.create();
		createLocAlertDialog.show();
	}
	
	private void exitApp() {
//		AlertDialog exitAlertDialog = new AlertDialog.Builder(this).create();
//		exitAlertDialog.setTitle(getString(R.string.alert_exit_title));
//		exitAlertDialog.setMessage(getString(R.string.alert_exit_message));
//		exitAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.alert_positive), exitDialogListener);
//		exitAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.alert_negative), exitDialogListener);
//		exitAlertDialog.show();
		System.exit(0);
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	private DialogInterface.OnClickListener createLocDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				Intent toLocationCreationIntent = new Intent(FeedActivity.this, LocationCreationActivity.class);
				startActivityForResult(toLocationCreationIntent, REQUEST_CODE_LOCATION_CREATION);
				break;
			case 1:
				Intent toMapSelectionIntent = new Intent(FeedActivity.this, MapSelectionActivity.class);
				toMapSelectionIntent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, mLocation.getLatitude());
				toMapSelectionIntent.putExtra(FeedActivity.EXTRA_LOCATION_LON, mLocation.getLongitude());
				startActivityForResult(toMapSelectionIntent, REQUEST_CODE_LOCATION_CREATION);
				break;
			default:
				break;
			}
		}
	};
	
//	private DialogInterface.OnClickListener exitDialogListener = new DialogInterface.OnClickListener() {
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			if (which == DialogInterface.BUTTON_POSITIVE) {
//				System.exit(0);
//			}
//		}
//	};
	
	private Intent putLocationExtras(Intent intent) {
		intent.putExtra(EXTRA_LOCATION_ID, mLocation.getId());
		intent.putExtra(EXTRA_LOCATION_NAME, mLocation.getName());
		intent.putExtra(EXTRA_LOCATION_LON, mLocation.getLongitude());
		intent.putExtra(EXTRA_LOCATION_LAT, mLocation.getLatitude());
		return intent;
	}
	
	private void refreshWithLocationFromIntent(Intent data) {
		int locationId = data.getIntExtra(EXTRA_LOCATION_ID, 0);
		String locationName = data.getStringExtra(EXTRA_LOCATION_NAME);
		int locationLat = data.getIntExtra(EXTRA_LOCATION_LAT, 0);
		int locationLon = data.getIntExtra(EXTRA_LOCATION_LON, 0);
		mLocation = new Location(locationId, locationName, locationLat, locationLon);
		setTitle(locationName);

		executeRefresh(true, false);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GROUP_CREATION && resultCode == RESULT_OK) {
			Intent toDiscussIntent = new Intent(this, DiscussActivity.class);
			toDiscussIntent.putExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, data.getIntExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, 0));
			startActivityForResult(toDiscussIntent, REQUEST_CODE_GROUP_ENTERING);
		} else if (requestCode == REQUEST_CODE_LOCATION_SELECTION && resultCode == RESULT_OK) {
			refreshWithLocationFromIntent(data);
		} else if (requestCode == REQUEST_CODE_PERSONAL_SETTINGS && resultCode == RESULT_OK) {
			boolean isLogout = data.getBooleanExtra(PersonalSettingsActivity.EXTRA_LOGOUT, false);
			if (isLogout) {
				Intent toLoginIntent = new Intent(this, LoginActivity.class);
				startActivity(toLoginIntent);
				FeedActivity.this.finish();
			}
		} else if (requestCode == REQUEST_CODE_LOCATION_CREATION && resultCode == RESULT_OK) {
			refreshWithLocationFromIntent(data);
		} else if (requestCode == REQUEST_CODE_GROUP_ENTERING && resultCode == RESULT_OK) {
			executeRefresh(true, false);
		}
	}
	
	/**
	 * 
	 * @param Boolean参数表示是否更新当前位置
	 *
	 */
	private class GetFeedTask extends AsyncTask<Boolean, Void, List<Group>> {
		private FeedService feedService;
		private Exception exception;
		@Override
		protected List<Group> doInBackground(Boolean... params) {
			feedService = ServiceFactory.getFeedService();
			ArrayList<Group> groups = new ArrayList<Group>();
			try {
				if (params[0]) {
					isFirstLoc = false;
					mLocClient.start();
					myListener.startTiming();
					
					Log.w("loc?", String.valueOf(isFirstLoc));
					if (isFirstLoc) {
						LocationService locationService = ServiceFactory.getLocationService();
						try {
							mLocation = locationService.getLocationByPoint((int) (locData.latitude * 1e6), (int) (locData.longitude * 1e6));
						} catch (NonLocationException e) {
						}
					}
				}
				if (mLocation.getId() <= 0) {
					MemoryService memoryService = ServiceFactory.getMemoryService(getApplicationContext());
					groups = (ArrayList<Group>) feedService.getFeedByMycity(memoryService.getMySession());
					mLocation = feedService.getUpdatedLocation();
				} else {
					groups = (ArrayList<Group>) feedService.getFeedByLocationId(mLocation.getId());
				}
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return groups;
		}
		
		@Override
		protected void onPostExecute(List<Group> result) {
			mSwipeRefreshLayout.setRefreshing(false);
			mGetFeedTask = null;
			
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
					Intent intent = new Intent(FeedActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
			
			// TODO setTitle╮(╯▽╰)╭
			FeedActivity.this.setTitle(mLocation.getName());
			
			if (result.size() == 0) {
				mListView.setVisibility(View.GONE);
				nonFeedReminderTextView.setVisibility(View.VISIBLE);
				return;
			}
			
			mListView.setVisibility(View.VISIBLE);
			nonFeedReminderTextView.setVisibility(View.GONE);
			
			mFeedList.clear();
			for (int i = 0; i < result.size(); i++) {
				Group group = result.get(i);
//				feedList.add(new FeedItem(group, null));
				mFeedList.add(group);
				
//				new GetUsrIconTask(i, group.getIconUrl()).execute();
			}
			mFeedAdapter.notifyDataSetChanged();
		}
		
	}
	
	private void executeRefresh(boolean manuallyShowHeader, boolean relocation) {
		if (mGetFeedTask != null) {
			return;
		}
		if (manuallyShowHeader) {
			mSwipeRefreshLayout.setRefreshing(true);
		}
		mGetFeedTask = new GetFeedTask();
		mGetFeedTask.execute(relocation);
	}
	
//	private class FeedItem {
//		Group group;
//		Bitmap bitmap;
//		public FeedItem(Group group, Bitmap bitmap) {
//			this.group = group;
//			this.bitmap = bitmap;
//		}
//	}
	
	private class FeedItemAdapter extends BaseAdapter {
		private Context context;
		private ViewHolder viewHolder = new ViewHolder();
		
		class ViewHolder {
//			public ImageView iconImageView;
			public TextView timeTextView;
			public TextView usrnameTextView;
			public TextView titleTextView;
			public TextView numberTextView;
		}

		public FeedItemAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return mFeedList.size();
		}

		@Override
		public Object getItem(int position) {
			return mFeedList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			FeedItem item = (FeedItem) getItem(position);
//			Group group = item.group;
//			Bitmap bitmap = item.bitmap;
			Group group = (Group) getItem(position);
			
			convertView = LayoutInflater.from(context).inflate(R.layout.group_item_feed, null);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.feed_item_time);
//			viewHolder.iconImageView = (ImageView) convertView
//					.findViewById(R.id.feed_item_icon);
			viewHolder.usrnameTextView = (TextView) convertView
					.findViewById(R.id.feed_item_usrname);
			viewHolder.titleTextView = (TextView) convertView
					.findViewById(R.id.feed_item_title);
			viewHolder.numberTextView = (TextView) convertView
					.findViewById(R.id.feed_item_number_of_members);
			convertView.setTag(viewHolder);

			// TODO 点击头像发消息或者查看大图
			viewHolder.timeTextView.setText(new UITimeGenerator().getFormattedFeedTime(group.getLastActiveTime()));
			viewHolder.usrnameTextView.setText(group.getOwnerName());
			viewHolder.titleTextView.setText(group.getTitle());
			viewHolder.numberTextView.setText(String.valueOf(group.getNumberOfMembers()));
//			if (bitmap != null) {
//				viewHolder.iconImageView.setImageBitmap(bitmap);
//			}
			
			return convertView;
		}
	}
	
//	private class GetUsrIconTask extends AsyncTask<Void, Void, Bitmap> {
//		private int position;
//		private String url;
//		public GetUsrIconTask(int position, String url) {
//			this.position = position;
//			this.url = url;
//		}
//		@Override
//		protected Bitmap doInBackground(Void... params) {
//			ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
//			return imageService.getBitmap(url, ImageService.THUMB_ICON);
//		}
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			if (result == null) {
//				return;
//			}
//			feedList.get(position).bitmap = result;
//			feedAdapter.notifyDataSetChanged();
//		}
//	}

}
