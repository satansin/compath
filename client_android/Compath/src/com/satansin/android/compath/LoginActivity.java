package com.satansin.android.compath;

import com.satansin.android.compath.logic.LoginService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends ActionBarActivity {

	/**
	 * The default user name to populate the user name field with.
	 */
	public static final String EXTRA_LOGIN_USRNAME = "com.satansin.android.compath.MSG_LOGIN_USRNAME";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for user name and password at the time of the login attempt.
	private String mUsrname;
	private String mPassword;

	// UI references.
	private EditText mUsrnameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUsrname = getIntent().getStringExtra(EXTRA_LOGIN_USRNAME);
		mUsrnameView = (EditText) findViewById(R.id.usrname);
		mUsrnameView.setText(mUsrname);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		if (mUsrname != null && mUsrname.length() > 0) {
			mPasswordView.requestFocus();
		}

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startRegisterActivity();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// int id = item.getItemId();
		// if (id == R.id.action_settings) {
		// return true;
		// } else if (id == R.id.action_forgot_password) {
		// return true;
		// }
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}

	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsrnameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsrname = mUsrnameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mUsrname)) {
			mUsrnameView.setError(getString(R.string.error_field_required));
			focusView = mUsrnameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, String> {
		private Exception exception = null;
		private LoginService loginService = ServiceFactory.getLoginService();

		@Override
		protected String doInBackground(Void... params) {
			String session = "";
			try {
				session = loginService.authenticate(mUsrname, mPassword);
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			}
			return session;
		}

		@Override
		protected void onPostExecute(final String session) {
			mAuthTask = null;
			showProgress(false);

			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(),
							R.string.error_network_timeout, Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(),
							R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if (!TextUtils.isEmpty(session)) {
				try {
					MemoryService memoryService = ServiceFactory.getMemoryService(getApplicationContext());
					memoryService.clearSession();
					boolean sessionWritten = memoryService.writeSession(mUsrname,
							session, loginService.getIconUrl());
					if (!sessionWritten) {
						Toast.makeText(getApplicationContext(),
								R.string.error_unknown_retry, Toast.LENGTH_SHORT).show();
						return;
					}
				} catch (UnknownErrorException e) {
				}
				if (loginService.isFirstLogin()) {
					startCitySelectionActivity();
				} else {
					startFeedActivity();
				}
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_usrname_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	private static final int REQUEST_CODE_REGISTER = 0;
	private static final int REQUEST_CODE_CITY_SELECTION = 1;

	private void startRegisterActivity() {
		Intent toRegisterIntent = new Intent(this, RegisterActivity.class);
		String usrname = mUsrnameView.getText().toString();
		if (!TextUtils.isEmpty(usrname)) {
			toRegisterIntent.putExtra(RegisterActivity.EXTRA_REGISTER_USRNAME, usrname);
		}
		String password = mPasswordView.getText().toString();
		if (!TextUtils.isEmpty(password)) {
			toRegisterIntent.putExtra(RegisterActivity.EXTRA_REGISTER_PASSWORD, password);
		}
		startActivityForResult(toRegisterIntent, REQUEST_CODE_REGISTER);
	}

	private void startCitySelectionActivity() {
		Intent toCitySelectionIntent = new Intent(this, CitySelectionActivity.class);
		startActivityForResult(toCitySelectionIntent, REQUEST_CODE_CITY_SELECTION);
	}

	private void startFeedActivity() {
		Intent toFeedIntent = new Intent(this, FeedActivity.class);
		startActivity(toFeedIntent);
		LoginActivity.this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
			if (data.hasExtra(EXTRA_LOGIN_USRNAME)) {
				mUsrnameView.setText(data.getStringExtra(EXTRA_LOGIN_USRNAME));
				mPasswordView.requestFocus();
			}
		} else if (requestCode == REQUEST_CODE_CITY_SELECTION && resultCode == RESULT_OK) {
			startFeedActivity();
		}
	}
	
}
