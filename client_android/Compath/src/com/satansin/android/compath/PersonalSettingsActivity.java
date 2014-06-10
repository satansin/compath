package com.satansin.android.compath;

import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.ServiceFactory;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonalSettingsActivity extends ActionBarActivity {

	public static final String EXTRA_PERSONAL_SETTINGS_USRNAME = "com.satansin.android.compath.MSG_PERSONAL_SETTINGS_USRNAME";
	public static final String EXTRA_PERSONAL_SETTINGS_CITY = "com.satansin.android.compath.MSG_PERSONAL_SETTINGS_CITY";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_settings);

		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_PERSONAL_SETTINGS_USRNAME)) {
			((TextView) findViewById(R.id.personal_settings_usrname)).setText(intent.getStringExtra(EXTRA_PERSONAL_SETTINGS_USRNAME));
		}
		if (intent.hasExtra(EXTRA_PERSONAL_SETTINGS_CITY)) {
			((TextView) findViewById(R.id.personal_settings_city)).setText(intent.getStringExtra(EXTRA_PERSONAL_SETTINGS_CITY));
		}

		// TODO get data from server
		MemoryService memoryService = ServiceFactory.getMemoryService();
		TextView usrnameTextView = (TextView) findViewById(R.id.personal_settings_usrname);
		usrnameTextView.setText(memoryService.getMyUsrname());
		TextView cityTextView = (TextView) findViewById(R.id.personal_settings_city);
		cityTextView.setText(memoryService.getMyCity());

		cityTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCitySelectionActivity();
			}
		});

		RelativeLayout mygroupsRelativeLayout = (RelativeLayout) findViewById(R.id.mygroups_relative_layout);
		mygroupsRelativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startMygroupsActivity();
			}
		});
		
		RelativeLayout favoriteGroupsRelativeLayout = (RelativeLayout) findViewById(R.id.favorite_groups_relative_layout);
		favoriteGroupsRelativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startFavoriteGroupsActivity();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_settings, menu);
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

	private void startCitySelectionActivity() {
		Intent toCitySelectionIntent = new Intent(this,
				CitySelectionActivity.class);
		toCitySelectionIntent.putExtra(
				CitySelectionActivity.EXTRA_START_FROM_PERSONAL_SETTINGS, true);
		startActivityForResult(toCitySelectionIntent, 0);
	}

	private void startMygroupsActivity() {
		Intent toMygroupsIntent = new Intent(this, MygroupsActivity.class);
		startActivity(toMygroupsIntent);
	}
	
	private void startFavoriteGroupsActivity() {
		Intent toFavoriteGroupsIntent = new Intent(this, FavoriteGroupsActivity.class);
		startActivity(toFavoriteGroupsIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data.hasExtra(EXTRA_PERSONAL_SETTINGS_CITY)) {
				((TextView) findViewById(R.id.personal_settings_city)).setText(data.getStringExtra(EXTRA_PERSONAL_SETTINGS_CITY));
			}
		}
	}

}
