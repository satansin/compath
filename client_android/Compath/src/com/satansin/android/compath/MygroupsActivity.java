package com.satansin.android.compath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.satansin.android.compath.logic.Group;
import com.satansin.android.compath.logic.MygroupsService;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MygroupsActivity extends ActionBarActivity {
	
	private static final String UI_MYGROUP_ITEM_USRNAME = "usrname";
	private static final String UI_MYGROUP_ITEM_LOCATION = "location";
	private static final String UI_MYGROUP_ITEM_TITLE = "title";
	
	private static GetMygroupsTask getMygroupsTask;
	
	private static List<HashMap<String, Object>> mygroupList;
	private static SimpleAdapter mygroupAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mygroups);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		getMygroupsTask = new GetMygroupsTask();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mygroups, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_mygroups,
					container, false);
			
			ListView listView = (ListView) rootView
					.findViewById(R.id.mygroups_list_view);
			mygroupList = new ArrayList<HashMap<String, Object>>();
			mygroupAdapter = new SimpleAdapter(getActivity(), mygroupList,
					R.layout.mygroups_item,
					new String[] { UI_MYGROUP_ITEM_USRNAME, UI_MYGROUP_ITEM_LOCATION,
							UI_MYGROUP_ITEM_TITLE }, new int[] {
							R.id.mygroups_item_usrname, R.id.mygroups_item_location,
							R.id.mygroups_item_title });
			listView.setAdapter(mygroupAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent toDiscussIntent = new Intent(getActivity(), DiscussActivity.class);
					startActivity(toDiscussIntent);
				}
			});

			getMygroupsTask.execute();
			
			return rootView;
		}
	}
	
	private class GetMygroupsTask extends AsyncTask<Void, Void, List<Group>> {
		private Exception exception;
		@Override
		protected List<Group> doInBackground(Void... params) {
			MygroupsService mygroupsService = ServiceFactory.getMygroupsService();
			ArrayList<Group> groups = new ArrayList<Group>();
			try {
				groups = (ArrayList<Group>) mygroupsService.getMygroupsList();
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
					Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if (result.size() == 0) {
				return;
			}
			mygroupList.clear();
			for (int i = 0; i < result.size(); i++) {
				Group group = result.get(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(UI_MYGROUP_ITEM_USRNAME, group.getOwnerName()); // TODO Í¼Æ¬´æ·Å
				map.put(UI_MYGROUP_ITEM_LOCATION, group.getLocation());
				map.put(UI_MYGROUP_ITEM_TITLE, group.getTitle());
				mygroupList.add(map);
			}
			mygroupAdapter.notifyDataSetChanged();
		}
	}

}
