package com.satansin.android.compath;

import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.LoginService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.PersonalSettingsService;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalSettingsActivity extends ActionBarActivity {

	public static final String EXTRA_PERSONAL_SETTINGS_CITY_ID = "com.satansin.android.compath.MSG_PERSONAL_SETTINGS_CITY";
	public static final String EXTRA_LOGOUT = "com.satansin.android.compath.MSG_LOGOUT";
	
	private int cityId = 0;
	private String iconUrl = "";
	
	private MemoryService memoryService;
	
	private TextView cityTextView;
	private ImageView iconImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_settings);

		memoryService = ServiceFactory.getMemoryService(this);
		TextView usrnameTextView = (TextView) findViewById(R.id.personal_settings_usrname);
		usrnameTextView.setText(memoryService.getMyUsrname());
		
		cityTextView = (TextView) findViewById(R.id.personal_settings_city);
		cityTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCitySelectionActivity();
			}
		});
		
		new GetMycityTask().execute();
		
		iconImageView = (ImageView) findViewById(R.id.personal_settings_icon);
		iconImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO ��ת����ͼ�鿴ҳ��
				
			}
		});
		
		new GetMyIconUrlTask().execute();

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
		if (id == R.id.action_logout) {
			new LogoutTask().execute();
			
			Intent toFeedIntent = new Intent(PersonalSettingsActivity.this, FeedActivity.class);
			toFeedIntent.putExtra(EXTRA_LOGOUT, true);
			setResult(RESULT_OK, toFeedIntent);
			PersonalSettingsActivity.this.finish();
			return true;
		} else if (id == R.id.action_upload_icon) {
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	private void startCitySelectionActivity() {
		Intent toCitySelectionIntent = new Intent(this,
				CitySelectionActivity.class);
		toCitySelectionIntent.putExtra(
				CitySelectionActivity.EXTRA_START_FROM_PERSONAL_SETTINGS, true);
		toCitySelectionIntent.putExtra(
				CitySelectionActivity.EXTRA_CITY_ID, cityId);
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
			if (data.hasExtra(EXTRA_PERSONAL_SETTINGS_CITY_ID)) {
				cityId = data.getIntExtra(EXTRA_PERSONAL_SETTINGS_CITY_ID, 0);
				try {
					((TextView) findViewById(R.id.personal_settings_city)).setText(memoryService.getCityName(cityId));
				} catch (UnknownErrorException e) {
				}
			}
		}
	}
	
	private class GetMyIconUrlTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			PersonalSettingsService personalSettingsService = ServiceFactory.getPersonalSettingsService();
			try {
				iconUrl = personalSettingsService.getMyIconUrl(memoryService.getMySession());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (Void)null;
		}
		@Override
		protected void onPostExecute(Void result) {
			new GetMyIconThumbTask().execute();
		}
	}
	
	private class GetMyIconThumbTask extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... params) {
			ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
			try {
				return imageService.getBitmap(iconUrl, ImageService.THUMB_ICON);
			} catch (UnknownErrorException e) {
				return null;
			}
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				iconImageView.setImageBitmap(result);
			}
		}
	}
	
	private class GetMycityTask extends AsyncTask<Void, Void, Integer> {
		private Exception exception;
		@Override
		protected Integer doInBackground(Void... params) {
			PersonalSettingsService personalSettingsService = ServiceFactory.getPersonalSettingsService();
			int cityId = 0;
			try {
				cityId = personalSettingsService.getMyCityId(memoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return cityId;
		}
		@Override
		protected void onPostExecute(Integer result) {
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
					Intent intent = new Intent(PersonalSettingsActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}

			PersonalSettingsActivity.this.cityId = result;
			try {
				cityTextView.setText(memoryService.getCityName(result));
			} catch (UnknownErrorException e) {
			}
		}
	}
	
	private class LogoutTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				LoginService loginService = ServiceFactory.getLoginService();
				loginService.logout(memoryService.getMySession());
				memoryService.clearSession();
			} catch (Exception e) {
			}
			return (Void) null;
		}
	}

}
