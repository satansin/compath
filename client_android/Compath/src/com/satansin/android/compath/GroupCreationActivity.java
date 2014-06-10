package com.satansin.android.compath;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class GroupCreationActivity extends ActionBarActivity {
	
	private String locationId;
	private String locationName;
	private int locationLat;
	private int locationLon;
	
	private EditText groupTitleEditText;
	private TextView locationTextView;
	
	private CreateGroupTask createGroupTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_creation);
		
		groupTitleEditText = (EditText) findViewById(R.id.edit_group_title);
		
		locationId = getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_NAME);
		locationName = getIntent().getStringExtra(FeedActivity.EXTRA_LOCATION_NAME);
		locationLat = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);
		locationLon = getIntent().getIntExtra(FeedActivity.EXTRA_LOCATION_LON, 0);

		locationTextView = (TextView) findViewById(R.id.group_creation_location_text);
		// TODO ±äÉ«
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	private DialogInterface.OnClickListener cancelDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				GroupCreationActivity.this.finish();
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog cancelAlertDialog = new AlertDialog.Builder(getApplicationContext()).create();
			cancelAlertDialog.setTitle(getString(R.string.alert_title));
			cancelAlertDialog.setMessage(getString(R.string.alert_message));
			cancelAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.alert_positive), cancelDialogListener);
			cancelAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.alert_negative), cancelDialogListener);
		}
		return false;
	}
	
	private void attemptCreate() {
		
	}
	
	private class CreateGroupTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}
