package com.satansin.android.compath;

import java.util.Comparator;

import com.satansin.android.compath.logic.City;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.PersonalSettingsService;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CitySelectionActivity extends ActionBarActivity {
	
	public static final String EXTRA_START_FROM_PERSONAL_SETTINGS = "com.satansin.android.compath.START_FROM_PERSONAL_SETTINGS";

	public static final String EXTRA_CITY_ID = "com.satansin.android.compath.CITY_ID";
	// TODO 如果有extra选择此城市

	private MemoryService memoryService;
	
	private SetMyCityTask setMyCityTask = null;
	
	private Spinner provinceSpinner;
	private Spinner citySpinner;
	private ArrayAdapter<City> cityAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_selection);
		
		memoryService = ServiceFactory.getMemoryService(getApplicationContext());
		
		provinceSpinner = (Spinner) findViewById(R.id.spinner_province);
		String[] provinceNames = getResources().getStringArray(R.array.province_names);
		ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinceNames);
		provinceAdapter.sort(new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				return lhs.compareTo(rhs);
			}
		});
		provinceSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinceNames));
		
		citySpinner = (Spinner) findViewById(R.id.spinner_city);
		try {

			City[] citiesOfFirstProvince = memoryService.getCitiesByProvinceName(provinceNames[0]);
			
			cityAdapter = new ArrayAdapter<City>(this, android.R.layout.simple_spinner_item, citiesOfFirstProvince);
			cityAdapter.sort(City.getComparator());
			citySpinner.setAdapter(cityAdapter);
		} catch (UnknownErrorException e) {
		}
		
		provinceSpinner.setOnItemSelectedListener(
				new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						String selectedProvince = parent.getItemAtPosition(position).toString();
						try {
							City[] cities = memoryService.getCitiesByProvinceName(selectedProvince);
							Log.w("before refreshing adapter", String.valueOf(cities.length));
							cityAdapter = new ArrayAdapter<City>(CitySelectionActivity.this, android.R.layout.simple_spinner_item, cities);
							cityAdapter.sort(City.getComparator());
							citySpinner.setAdapter(cityAdapter);
						} catch (Exception e) {
						}
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
		});
		
		findViewById(R.id.finish_city_selection_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (setMyCityTask != null) {
							return;
						}
						
						City selectedCity = (City) citySpinner.getSelectedItem();
						
						setMyCityTask = new SetMyCityTask();
						setMyCityTask.execute(selectedCity);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.city_selection, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	private class SetMyCityTask extends AsyncTask<City, Void, Boolean> {
		private Exception exception;
		@Override
		protected Boolean doInBackground(City... params) {
			PersonalSettingsService personalSettingsService = ServiceFactory.getPersonalSettingsService();
			boolean citySet = false;
			try {
				citySet = personalSettingsService.setMyCity(params[0], memoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return citySet;
		}
		
		@Override
		protected void onPostExecute(final Boolean succeed) {
			setMyCityTask = null;
			
			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(), R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof NotLoginException) {
					try {
						memoryService.clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(CitySelectionActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
			
			if (succeed) {
				finishCitySelection();
			} else {
				Toast.makeText(getApplicationContext(), R.string.error_set_fail_retry, Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onCancelled() {
			setMyCityTask = null;
		}
	}
	
	private void finishCitySelection() {
		Intent intent;
		if (getIntent().hasExtra(EXTRA_START_FROM_PERSONAL_SETTINGS)) {
			intent = new Intent(this, PersonalSettingsActivity.class);
			intent.putExtra(PersonalSettingsActivity.EXTRA_PERSONAL_SETTINGS_CITY_ID, ((City) citySpinner.getSelectedItem()).getId());
		} else {
			intent = new Intent(this, LoginActivity.class);
		}
		setResult(RESULT_OK, intent);
		CitySelectionActivity.this.finish();
	}

}
