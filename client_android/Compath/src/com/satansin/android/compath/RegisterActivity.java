package com.satansin.android.compath;

import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.RegisterService;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class RegisterActivity extends ActionBarActivity {

	public static final String EXTRA_REGISTER_USRNAME = "com.satansin.android.compath.EXTRA_REGISTER_USRNAME";
	public static final String EXTRA_REGISTER_PASSWORD = "com.satansin.android.compath.EXTRA_REGISTER_PASSWORD";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserRegisterTask mRegisterTask = null;

	private String mUsrname;
	private String mPassword;

	// UI references.
	private EditText mUsrnameView;
	private EditText mPasswordView;
	private View mRegisterFormView;
	private View mRegisterStatusView;
	private TextView mRegisterStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register);
		setupActionBar();

		mUsrnameView = (EditText) findViewById(R.id.usrname);
		if (getIntent().hasExtra(EXTRA_REGISTER_USRNAME)) {
			mUsrnameView.setText(getIntent().getStringExtra(EXTRA_REGISTER_USRNAME));
		}

		mPasswordView = (EditText) findViewById(R.id.password);
		if (getIntent().hasExtra(EXTRA_REGISTER_PASSWORD)) {
			mPasswordView.setText(getIntent().getStringExtra(EXTRA_REGISTER_PASSWORD));
		}
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.register || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});

		mRegisterFormView = findViewById(R.id.register_form);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegisterStatusMessageView = (TextView) findViewById(R.id.register_status_message);

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		if (mRegisterTask != null) {
			return;
		}

		// Reset errors.
		mUsrnameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the register attempt.
		mUsrname = mUsrnameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 8 || mPassword.length() > 16 || TextUtils.isDigitsOnly(mPassword)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsrname)) {
			mUsrnameView.setError(getString(R.string.error_field_required));
			focusView = mUsrnameView;
			cancel = true;
		} else if (mUsrname.length() < 4 || mUsrname.length() > 16 || !mUsrname.matches("[a-zA-Z0-9]*")) {
			mUsrnameView.setError(getString(R.string.error_invalid_usrname));
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
			mRegisterStatusMessageView.setText(R.string.register_progress_registering);
			showProgress(true);
			mRegisterTask = new UserRegisterTask();
			mRegisterTask.execute((Void) null);
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

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
		private Exception exception;
		@Override
		protected Boolean doInBackground(Void... params) {
			RegisterService registerService = ServiceFactory.getRegisterService();
			boolean registered = false;
			try {
				registered = registerService.register(mUsrname, mPassword);
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			}
			return registered;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegisterTask = null;
			showProgress(false);
			
			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(), R.string.error_network_timeout, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_SHORT).show();
					return;
				}
			}

			if (success) {
				Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
				finishRegister();
			} else {
				mUsrnameView
						.setError(getString(R.string.error_repeating_usrname));
				mUsrnameView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mRegisterTask = null;
			showProgress(false);
		}
	}
	
	private void finishRegister() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(LoginActivity.EXTRA_LOGIN_USRNAME, mUsrname);
		setResult(RESULT_OK, intent);
		RegisterActivity.this.finish();
	}
}
