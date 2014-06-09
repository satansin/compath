package com.satansin.android.compath;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class GroupCreationActivity extends ActionBarActivity {
	
	private String locationId;
	private String locationName;
	private int locationLat;
	private int locationLon;
	
	private TextView locationTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_creation);
		
		locationId = getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_NAME);
		locationName = getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_NAME);
		locationLat = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);
		locationLon = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);

		locationTextView = (TextView) findViewById(R.id.group_creation_location_text);
		// TODO 变色
		locationTextView.setText(locationName);
		locationTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startLocationSelection();
			}
		});
	}
	
	private void startLocationSelection() {
		Intent toLocationSelectionIntent = new Intent(this, LocationSelectionActivity.class);
		toLocationSelectionIntent.putExtra(FeedActivity.EXTRA_LOCATION_ID, locationId);
		toLocationSelectionIntent.putExtra(FeedActivity.EXTRA_LOCATION_NAME, locationName);
		toLocationSelectionIntent.putExtra(FeedActivity.EXTRA_LOCATION_LON, locationLat);
		toLocationSelectionIntent.putExtra(FeedActivity.EXTRA_LOCATION_LAT, locationLon);
		startActivityForResult(toLocationSelectionIntent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			locationId = data.getStringExtra(FeedActivity.EXTRA_LOCATION_NAME);
			locationName = data.getStringExtra(FeedActivity.EXTRA_LOCATION_NAME);
			locationLat = data.getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);
			locationLon = data.getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);
			locationTextView.setText(locationName);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_creation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_finish_creation) {
			return true;
		} // TODO 返回提醒
		return super.onOptionsItemSelected(item);
	}

}
