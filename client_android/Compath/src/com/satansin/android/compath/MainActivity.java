package com.satansin.android.compath;

import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.ServiceFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;

public class MainActivity extends Activity {
	
	private boolean isLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new WaitTask().execute();
		
		MemoryService memoryService = ServiceFactory.getMemoryService(this);
		String session = memoryService.getMySession();
		if (session == null || session.length() == 0) {
			isLogin = false;
		} else {
			isLogin = true;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0);
		}
		return true;
	}
	
	private class WaitTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (isLogin) {
				Intent toFeedIntent = new Intent(MainActivity.this, FeedActivity.class);
				startActivity(toFeedIntent);
			} else {
				Intent toLoginIntent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(toLoginIntent);
			}

			MainActivity.this.finish();
		}
		
	}

}
