package com.satansin.android.compath;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.LoginService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.PersonalSettingsService;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;

public class PersonalSettingsActivity extends ActionBarActivity {

	public static final String EXTRA_PERSONAL_SETTINGS_CITY_ID = "com.satansin.android.compath.MSG_PERSONAL_SETTINGS_CITY";
	public static final String EXTRA_LOGOUT = "com.satansin.android.compath.MSG_LOGOUT";

	private int cityId = 0;
	private String iconUrl = "";

	private MemoryService memoryService;

	private TextView cityTextView;
	private ImageView iconImageView;
	
	private Button changeHeadBtn = null;
	
    /* 请求码*/
    private static final int IMAGE_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int RESULT_REQUEST_CODE = 1002;
    private String[] items = new String[] { "选择本地图片", "拍照" };
    /*头像名称*/
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";

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
				// TODO 跳转到大图查看页面
				startActivity(new Intent(PersonalSettingsActivity.this,ImageShower.class));
			}
		});
		
		changeHeadBtn = (Button) findViewById(R.id.change_head_btn);
		changeHeadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//更换头像
				showDialog();
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

			Intent toFeedIntent = new Intent(PersonalSettingsActivity.this,
					FeedActivity.class);
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
		toCitySelectionIntent.putExtra(CitySelectionActivity.EXTRA_CITY_ID,
				cityId);
		startActivityForResult(toCitySelectionIntent, 0);
	}

	private void startMygroupsActivity() {
		Intent toMygroupsIntent = new Intent(this, MygroupsActivity.class);
		startActivity(toMygroupsIntent);
	}

	private void startFavoriteGroupsActivity() {
		Intent toFavoriteGroupsIntent = new Intent(this,
				FavoriteGroupsActivity.class);
		startActivity(toFavoriteGroupsIntent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK) {
			if (data.hasExtra(EXTRA_PERSONAL_SETTINGS_CITY_ID)) {
				cityId = data.getIntExtra(EXTRA_PERSONAL_SETTINGS_CITY_ID, 0);
				((TextView) findViewById(R.id.personal_settings_city))
						.setText(memoryService.getCityName(cityId));
			}
		}
		
		switch (requestCode) {
        case IMAGE_REQUEST_CODE:
                startPhotoZoom(data.getData());
                break;
        case CAMERA_REQUEST_CODE:
                if (hasSdcard()) {
                        File tempFile = new File(
                                        Environment.getExternalStorageDirectory()
                                                        + IMAGE_FILE_NAME);
                        startPhotoZoom(Uri.fromFile(tempFile));
                } else {
                        Toast.makeText(PersonalSettingsActivity.this, "未找到存储卡，无法存储照片！",
                                        Toast.LENGTH_LONG).show();
                }

                break;
        case RESULT_REQUEST_CODE:
                if (data != null) {
                        getImageToView(data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
	}

	private class GetMyIconUrlTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			PersonalSettingsService personalSettingsService = ServiceFactory
					.getPersonalSettingsService();
			try {
				iconUrl = personalSettingsService.getMyIconUrl(memoryService
						.getMySession());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return (Void) null;
		}

		@Override
		protected void onPostExecute(Void result) {
			new GetMyIconThumbTask().execute();
		}
	}

	private class GetMyIconThumbTask extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... params) {
			ImageService imageService = ServiceFactory
					.getImageService(getApplicationContext());
			return imageService.getBitmap(iconUrl, ImageService.THUMB_ICON);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			iconImageView.setImageBitmap(result);
		}
	}

	private class GetMycityTask extends AsyncTask<Void, Void, Integer> {
		private Exception exception;

		@Override
		protected Integer doInBackground(Void... params) {
			PersonalSettingsService personalSettingsService = ServiceFactory
					.getPersonalSettingsService();
			int cityId = 0;
			try {
				cityId = personalSettingsService.getMyCityId(memoryService
						.getMySession());
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
					Toast.makeText(getApplicationContext(),
							R.string.error_network_timeout, Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(),
							R.string.error_unknown_retry, Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (exception instanceof NotLoginException) {
					ServiceFactory.getMemoryService(getApplicationContext())
							.clearSession();
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(PersonalSettingsActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
			}

			PersonalSettingsActivity.this.cityId = result;
			cityTextView.setText(memoryService.getCityName(result));
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

	private void showDialog() {
        
        new AlertDialog.Builder(this)
                        .setTitle("设置头像")
                        .setItems(items, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                        case 0:
                                                Intent intentFromGallery = new Intent();
                                                intentFromGallery.setType("image/*"); // 设置文件类型
                                                intentFromGallery
                                                                .setAction(Intent.ACTION_GET_CONTENT);
                                                startActivityForResult(intentFromGallery,
                                                                IMAGE_REQUEST_CODE);
                                                break;
                                        case 1:

                                                Intent intentFromCapture = new Intent(
                                                                MediaStore.ACTION_IMAGE_CAPTURE);
                                                // 判断存储卡是否可以用，可用进行存储
                                                if (hasSdcard()) {

                                                        intentFromCapture.putExtra(
                                                                        MediaStore.EXTRA_OUTPUT,
                                                                        Uri.fromFile(new File(Environment
                                                                                        .getExternalStorageDirectory(),
                                                                                        IMAGE_FILE_NAME)));
                                                }

                                                startActivityForResult(intentFromCapture,
                                                                CAMERA_REQUEST_CODE);
                                                break;
                                        }
                                }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                }
                        }).show();

}


/**
 * 裁剪图片方法实现
 * 
 * @param uri
 */
public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
}

/**
 * 保存裁剪之后的图片数据
 * 
 * @param picdata
 */
private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
                Bitmap photo = extras.getParcelable("data");
                Drawable drawable = new BitmapDrawable(photo);
                iconImageView.setImageDrawable(drawable);
                //保存更換後的頭像到本地
        }
}
	
	//判断是否有SD卡
	public boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

}
