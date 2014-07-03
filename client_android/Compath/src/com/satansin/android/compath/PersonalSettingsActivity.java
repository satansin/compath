package com.satansin.android.compath;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;

import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.LoginService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.PersonalSettingsService;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.logic.UploadService;
import com.satansin.android.compath.qiniu.Conf;
import com.satansin.android.compath.qiniu.JSONObjectRet;
import com.satansin.android.compath.util.UITimeGenerator;

import android.app.AlertDialog.Builder;
import android.util.Log;

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
	
	// version Jin
//	private Button changeHeadBtn = null;
//	
//    /* ������*/
//    private static final int IMAGE_REQUEST_CODE = 1000;
//    private static final int CAMERA_REQUEST_CODE = 1001;
//    private static final int RESULT_REQUEST_CODE = 1002;
//    private String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };
//    /*ͷ������*/
//    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
	
	private GetMyIconUploadTokenTask uploadMyIconTask;

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
//<<<<<<< HEAD version: Jin
//				// TODO ��ת����ͼ�鿴ҳ��
//				startActivity(new Intent(PersonalSettingsActivity.this,ImageShower.class));
//=======
				Intent toImageViewIntent = new Intent(PersonalSettingsActivity.this, ImageViewActivity.class);
				iconImageView.setDrawingCacheEnabled(true);
				toImageViewIntent.putExtra(ImageViewActivity.EXTRA_THUMBNAIL, iconImageView.getDrawingCache());
				toImageViewIntent.putExtra(ImageViewActivity.EXTRA_URL, iconUrl);
				startActivity(toImageViewIntent);
			}
		});
		
		// version: Jin
//		changeHeadBtn = (Button) findViewById(R.id.change_head_btn);
//		changeHeadBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				//����ͷ��
//				showDialog();
//			}
//		});

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
			startIconUploading();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	private String capturedFileName;
	private String croppedFileName;
	
	private void generateCapturedFileName() {
		capturedFileName = new UITimeGenerator().getFormattedPhotoNameTime() + ".jpg";
	}
	
	private String getCroppedFileName(String fileName) {
		String[] split = fileName.split("\\.");
		if (split.length > 1) {
			return (split[0] + "_crp." + split[1]);
		} else {
			return (split[0] + "_crp");
		}
	}
	
	private String getCroppedFileName(Uri uri) {
		String[] split = uri.getPath().split("/");
		return getCroppedFileName(split[split.length - 1]);
	}
	
	private static final int REQUEST_CODE_CITY = 0;
	private static final int REQUEST_CODE_IMAGE = 1;
	private static final int REQUEST_CODE_CAPTURE = 2;
	private static final int REQUEST_CODE_CROP = 3;
	
	private DialogInterface.OnClickListener uploadIconDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
				galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//				galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(galleryIntent, REQUEST_CODE_IMAGE);
				break;
			case 1:
				Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				generateCapturedFileName();
				try {
					captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, memoryService.getNewCapturingUri(capturedFileName));
				} catch (UnknownErrorException e) {
					Toast.makeText(getApplicationContext(), getString(R.string.error_non_storage), Toast.LENGTH_SHORT).show();
					return;
				}
				startActivityForResult(captureIntent, REQUEST_CODE_CAPTURE);
				break;
			default:
				break;
			}
		}
	};
	
	private void startIconUploading() {
		if (uploadMyIconTask != null) {
			return;
		}
		Builder uploadIconBuilder = new Builder(this);
		uploadIconBuilder.setTitle(getString(R.string.alert_upload_icon_title));
		uploadIconBuilder.setItems(getResources().getStringArray(R.array.alert_upload_icon_list), uploadIconDialogListener);
		AlertDialog createLocAlertDialog = uploadIconBuilder.create();
		createLocAlertDialog.show();
	}

	private void startCitySelectionActivity() {
		Intent toCitySelectionIntent = new Intent(this,
				CitySelectionActivity.class);
		toCitySelectionIntent.putExtra(
				CitySelectionActivity.EXTRA_START_FROM_PERSONAL_SETTINGS, true);
		toCitySelectionIntent.putExtra(
				CitySelectionActivity.EXTRA_CITY_ID, cityId);
		startActivityForResult(toCitySelectionIntent, REQUEST_CODE_CITY);
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
	
	private void startImageCropActivity(Uri uri) {
		Log.w("uri", uri.getPath());
		croppedFileName = getCroppedFileName(uri);
		Intent toCropIntent = new Intent("com.android.camera.action.CROP");
		try {
			toCropIntent.setDataAndType(uri, "image/*");
		} catch (Exception e) {
			return;
		}
		toCropIntent.putExtra("crop", "true");
        // aspectX aspectY �ǿ�ߵı���
		toCropIntent.putExtra("aspectX", 1);
		toCropIntent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
		toCropIntent.putExtra("outputX", 320);
		toCropIntent.putExtra("outputY", 320);
		toCropIntent.putExtra("return-data", true);
        startActivityForResult(toCropIntent, REQUEST_CODE_CROP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CITY && resultCode == RESULT_OK) {
			if (data.hasExtra(EXTRA_PERSONAL_SETTINGS_CITY_ID)) {
				cityId = data.getIntExtra(EXTRA_PERSONAL_SETTINGS_CITY_ID, 0);
				try {
					((TextView) findViewById(R.id.personal_settings_city)).setText(memoryService.getCityName(cityId));
				} catch (UnknownErrorException e) {
				}
			}
		} else if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			if (uri == null) {
				return;
			}
			startImageCropActivity(uri);
		} else if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
			try {
				startImageCropActivity(memoryService.getNewCapturingUri(capturedFileName));
			} catch (UnknownErrorException e) {
				return;
			}
		} else if (requestCode == REQUEST_CODE_CROP && resultCode == RESULT_OK) {
			if (data == null) {
				return;
			}
			Bitmap bitmap = (Bitmap) data.getParcelableExtra("data");
			if (bitmap == null) {
				return;
			}
			Uri croppedUri = null;
			try {
				croppedUri = memoryService.putLocalImage(bitmap, croppedFileName, MemoryService.IMG_ALBUM);
			} catch (UnknownErrorException e) {
				return;
			}
			new GetMyIconUploadTokenTask(croppedUri).execute();
		}
	}
	
	private class GetMyIconUploadTokenTask extends AsyncTask<Void, Void, String> {
		private Exception exception;
		private Uri uri;
		public GetMyIconUploadTokenTask(Uri uri) {
			this.uri = uri;
		}
		@Override
		protected String doInBackground(Void... params) {
			String token = "";
			try {
				UploadService uploadService = ServiceFactory.getUploadService();
				token = uploadService.iconUploadToken(memoryService.getMySession());
				if (token == null || token.length() <= 0) {
					return "";
				}
				
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return token;
		}
		@Override
		protected void onPostExecute(String result) {
			uploadMyIconTask = null;
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
			
			if (result != null && result.length() != 0) {
				executeUpload(result, uri);
			} else {
				Toast.makeText(getApplicationContext(), R.string.error_update_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void executeUpload(String token, Uri uri) {
		ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
		try {
			imageService.uploadBitmap(getApplicationContext(), token, uri, new JSONObjectRet() {
				@Override
				public void onFailure(Exception ex) {
					Toast.makeText(getApplicationContext(), R.string.error_update_fail, Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onSuccess(JSONObject obj) {
					String uploadedUrl = null;
					try {
						uploadedUrl = Conf.SERVER_DOMAIN + obj.getString("hash");
					} catch (JSONException e) {
						Toast.makeText(getApplicationContext(), R.string.error_update_fail, Toast.LENGTH_SHORT).show();
					}
					if (uploadedUrl == null || uploadedUrl.length() == 0) {
						Toast.makeText(getApplicationContext(), R.string.error_update_fail, Toast.LENGTH_SHORT).show();
					}
					new UpdateIconTask().execute(uploadedUrl);
				}
			});
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.error_update_fail, Toast.LENGTH_SHORT).show();
		}
	}
	
	private class UpdateIconTask extends AsyncTask<String, Void, Boolean> {
		private Exception exception;
		private String url;
		@Override
		protected Boolean doInBackground(String... params) {
			url = params[0];
			try {
				UploadService uploadService = ServiceFactory.getUploadService();
				
				return uploadService.iconUpdate(memoryService.getMySession(), params[0]);
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return false;
		}
		@Override
		protected void onPostExecute(Boolean result) {
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
			
			if (result) {
				Toast.makeText(getApplicationContext(), R.string.success_update, Toast.LENGTH_SHORT).show();
				new GetMyIconUrlTask().execute();
				try {
					memoryService.updateUsrIcon(url);
				} catch (UnknownErrorException e) {
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.error_update_fail, Toast.LENGTH_SHORT).show();
			}
		}
		
		// by Jin
//		switch (requestCode) {
//        case IMAGE_REQUEST_CODE:
//                startPhotoZoom(data.getData());
//                break;
//        case CAMERA_REQUEST_CODE:
//                if (hasSdcard()) {
//                        File tempFile = new File(
//                                        Environment.getExternalStorageDirectory()
//                                                        + IMAGE_FILE_NAME);
//                        startPhotoZoom(Uri.fromFile(tempFile));
//                } else {
//                        Toast.makeText(PersonalSettingsActivity.this, "δ�ҵ��洢�����޷��洢��Ƭ��",
//                                        Toast.LENGTH_LONG).show();
//                }
//
//                break;
//        case RESULT_REQUEST_CODE:
//                if (data != null) {
//                        getImageToView(data);
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
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
			ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
			return imageService.getBitmap(iconUrl, ImageService.THUMB_ICON_PERSONAL_SETTINGS);
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
					try {
						ServiceFactory.getMemoryService(getApplicationContext()).clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(PersonalSettingsActivity.this,
							LoginActivity.class);
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

	// by Jin
//	private void showDialog() {
//        
//        new AlertDialog.Builder(this)
//                        .setTitle("����ͷ��")
//                        .setItems(items, new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                        switch (which) {
//                                        case 0:
//                                                Intent intentFromGallery = new Intent();
//                                                intentFromGallery.setType("image/*"); // �����ļ�����
//                                                intentFromGallery
//                                                                .setAction(Intent.ACTION_GET_CONTENT);
//                                                startActivityForResult(intentFromGallery,
//                                                                IMAGE_REQUEST_CODE);
//                                                break;
//                                        case 1:
//
//                                                Intent intentFromCapture = new Intent(
//                                                                MediaStore.ACTION_IMAGE_CAPTURE);
//                                                // �жϴ洢���Ƿ�����ã����ý��д洢
//                                                if (hasSdcard()) {
//
//                                                        intentFromCapture.putExtra(
//                                                                        MediaStore.EXTRA_OUTPUT,
//                                                                        Uri.fromFile(new File(Environment
//                                                                                        .getExternalStorageDirectory(),
//                                                                                        IMAGE_FILE_NAME)));
//                                                }
//
//                                                startActivityForResult(intentFromCapture,
//                                                                CAMERA_REQUEST_CODE);
//                                                break;
//                                        }
//                                }
//                        })
//                        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
//
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                }
//                        }).show();
//
//}
//
//
///**
// * �ü�ͼƬ����ʵ��
// * 
// * @param uri
// */
//public void startPhotoZoom(Uri uri) {
//
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        // ���òü�
//        intent.putExtra("crop", "true");
//        // aspectX aspectY �ǿ�ߵı���
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // outputX outputY �ǲü�ͼƬ���
//        intent.putExtra("outputX", 320);
//        intent.putExtra("outputY", 320);
//        intent.putExtra("return-data", true);
//        startActivityForResult(intent, 2);
//}
//
///**
// * ����ü�֮���ͼƬ����
// * 
// * @param picdata
// */
//private void getImageToView(Intent data) {
//        Bundle extras = data.getExtras();
//        if (extras != null) {
//                Bitmap photo = extras.getParcelable("data");
//                Drawable drawable = new BitmapDrawable(photo);
//                iconImageView.setImageDrawable(drawable);
//                //������Q����^�񵽱���
//        }
//}
//	
//	//�ж��Ƿ���SD��
//	public boolean hasSdcard() {
//		String state = Environment.getExternalStorageState();
//		if (state.equals(Environment.MEDIA_MOUNTED)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
}
