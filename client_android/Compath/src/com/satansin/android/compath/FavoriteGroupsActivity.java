package com.satansin.android.compath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.satansin.android.compath.logic.Group;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.MygroupsService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FavoriteGroupsActivity extends ActionBarActivity {
	
	private static final String UI_GROUP_ITEM_USRNAME = "usrname";
	private static final String UI_GROUP_ITEM_LOCATION = "location";
	private static final String UI_GROUP_ITEM_TITLE = "title";
	
	private List<HashMap<String, Object>> favoriteGroupList;
	private SimpleAdapter favoriteGroupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite_groups);

		// TODO 美化这两个界面
		ListView listView = (ListView) findViewById(R.id.favorite_groups_list_view);
		favoriteGroupList = new ArrayList<HashMap<String, Object>>();
		favoriteGroupAdapter = new SimpleAdapter(this, favoriteGroupList,
				R.layout.group_item_favorite_groups1,
				new String[] { UI_GROUP_ITEM_USRNAME, UI_GROUP_ITEM_LOCATION,
						UI_GROUP_ITEM_TITLE }, new int[] {
						R.id.mygroups_item_usrname, R.id.mygroups_item_location,
						R.id.mygroups_item_title });
		listView.setAdapter(favoriteGroupAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startDiscussActivity(((Group) parent.getItemAtPosition(position)).getId());
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// remove
				return false;
			}
		});

		new GetFavoriteGroupsTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorite_groups, menu);
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
	
	private void startDiscussActivity(int groupId) {
		Intent toDiscussIntent = new Intent(this, DiscussActivity.class);
		toDiscussIntent.putExtra(DiscussActivity.EXTRA_DISCUSS_GROUP_ID, groupId);
		startActivity(toDiscussIntent);
	}
	
	private class GetFavoriteGroupsTask extends AsyncTask<Void, Void, List<Group>> {
		private Exception exception;
		@Override
		protected List<Group> doInBackground(Void... params) {
			MygroupsService mygroupsService = ServiceFactory.getMygroupsService();
			ArrayList<Group> groups = new ArrayList<Group>();
			try {
				MemoryService memoryService = ServiceFactory.getMemoryService(getApplicationContext());
				groups = (ArrayList<Group>) mygroupsService.getMyFavoriteList(memoryService.getMySession());
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
					Intent intent = new Intent(FavoriteGroupsActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
			if (result.size() == 0) {
				return;
			}
			favoriteGroupList.clear();
			for (int i = 0; i < result.size(); i++) {
				Group group = result.get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(UI_GROUP_ITEM_USRNAME, group.getOwnerName()); // TODO 图片存放
				map.put(UI_GROUP_ITEM_LOCATION, group.getLocation());
				map.put(UI_GROUP_ITEM_TITLE, group.getTitle());
				favoriteGroupList.add(map);
			}
			favoriteGroupAdapter.notifyDataSetChanged();
		}
	}

}
