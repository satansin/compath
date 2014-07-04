package com.satansin.android.compath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.satansin.android.compath.logic.GroupParticipationService;
import com.satansin.android.compath.logic.ImageService;
import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.Message;
import com.satansin.android.compath.logic.MessageService;
import com.satansin.android.compath.logic.MygroupsService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.NotLoginException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.logic.UploadService;
import com.satansin.android.compath.qiniu.Conf;
import com.satansin.android.compath.qiniu.JSONObjectRet;
import com.satansin.android.compath.util.UITimeGenerator;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DiscussActivity extends ActionBarActivity {

	public static final String EXTRA_DISCUSS_GROUP_ID = "com.satansin.android.compath.DISCUSS_ID";
	
	private static final int HISTORY_PAGE_SIZE = 20;

	private int mGroupId;
	
	private EditText mInputEditText;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private TextView mHeaderTextView;
	private ImageView mSendButtonImageView;
	private MenuItem mFavorMenuItem;
	
	private Button loadpicBtn = null;
	private Button cameraBtn = null;
	
	private HashMap<String, Bitmap> mImageMaps;

	private List<Message> mMessageList;
	private MessageAdapter mMessageAdapter;

	private MessageService mMessageService = ServiceFactory.getMessageService();
	private MemoryService mMemoryService;
	
	private ReceivingThread mReceivingThread;

	private boolean mHasFavored = false;
	private CancelFavorGroupTask mCancelFavorGroupTask;
	private CheckGroupFavoredTask mCheckGroupFavoredTask;
	private FavorGroupTask mFavorGroupTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);
		
		mMemoryService = ServiceFactory.getMemoryService(this);

		if (!getIntent().hasExtra(EXTRA_DISCUSS_GROUP_ID)) {
			Toast.makeText(getApplicationContext(), R.string.error_unknown_retry,
					Toast.LENGTH_SHORT).show();
			DiscussActivity.this.finish();
		}
		mGroupId = getIntent().getIntExtra(EXTRA_DISCUSS_GROUP_ID, 0);
		
		mImageMaps = new HashMap<String, Bitmap>();
		
		mMessageList = new ArrayList<Message>();

		mListView = (ListView) findViewById(R.id.discuss_list_view);
		View headerView = getLayoutInflater().inflate(R.layout.header_discuss, null);
		mHeaderTextView = (TextView) headerView.findViewById(R.id.discuss_header_text);
		mListView.addHeaderView(headerView);
		
		mMessageAdapter = new MessageAdapter(this);
		mListView.setAdapter(mMessageAdapter);
		
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		// 顶部刷新的样式 
		mSwipeRefreshLayout.setColorScheme(R.color.holo_red_light, R.color.holo_green_light,  
                R.color.holo_blue_bright, R.color.holo_orange_light);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadHistoryMessages();
			}
		});

		mSendButtonImageView = (ImageView) findViewById(R.id.discuss_sending);
		mSendButtonImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSendingText();
			}
		});

		mInputEditText = (EditText) findViewById(R.id.discuss_input);
		mInputEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				String text = mInputEditText.getText().toString();
				if (text.length() == 0) {
					mSendButtonImageView.setImageResource(R.drawable.send_disabled);
				} else {
					mSendButtonImageView.setImageResource(R.drawable.send);
				}
			}
		});

		loadHistoryMessages();
		
		loadpicBtn = (Button) findViewById(R.id.loadpicBtn);
		loadpicBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loadpic();
			}
		});
		
		cameraBtn = (Button) findViewById(R.id.cameraBtn);
		cameraBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				camera();
			}
		});
		
		new EnterGroupTask().execute();
		
		new CheckGroupFavoredTask().execute();

		mReceivingThread = new ReceivingThread();
		mReceivingThread.start();

		mListView.setSelection(mMessageList.size());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discuss, menu);
		mFavorMenuItem = menu.findItem(R.id.action_favor);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_favor) {
			if (favorOperationsProcessing()) {
				return true;
			}
			if (mHasFavored) {
				setFavorMenuState(true);
				mCancelFavorGroupTask = new CancelFavorGroupTask();
				mCancelFavorGroupTask.execute();
			} else {
				setFavorMenuState(false);
				mFavorGroupTask = new FavorGroupTask();
				mFavorGroupTask.execute();
			}
			return true;
		} else if (id == R.id.action_view_pics) {
			// TODO
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 
	 * @param toFavor If toFavor is true, set text into "favor" and set the icon into not-favored icon 
	 */
	private void setFavorMenuState(boolean toFavor) {
		if (toFavor) {
			mFavorMenuItem.setTitle(getString(R.string.action_favor));
			mFavorMenuItem.setIcon(R.drawable.not_favored);
		} else {
			mFavorMenuItem.setTitle(getString(R.string.action_cancel_favor));
			mFavorMenuItem.setIcon(R.drawable.favored);
		}
	}

	private boolean favorOperationsProcessing() {
		return (mCancelFavorGroupTask != null || mCheckGroupFavoredTask != null || mFavorGroupTask != null);
	}

	@Override
	public void finish() {
		mReceivingThread.stopReceiving();
		new ExitGroupTask().execute();
		setResult(RESULT_OK);
		super.finish();
	}
	
	@Override
	public void onDestroy() {
		CompathApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	private void loadHistoryMessages() {
		try {
			int currentSize = mMessageList.size();
			List<Message> historyList = mMemoryService.
					loadHistoryMessage(mGroupId, currentSize + 1, currentSize + HISTORY_PAGE_SIZE);
			
			mSwipeRefreshLayout.setRefreshing(false);
			if (currentSize > 0 && historyList.size() <= 0) {
				mHeaderTextView.setText(getString(R.string.error_non_history_msg));
			}
			for (Message message : historyList) {
				mMessageList.add(0, message);
				new GetImageTask(message.getIconUrl(), ImageService.THUMB_ICON_DISCUSS).execute();
				if (message.getType() == Message.TYPE_PIC) {
					new GetImageTask(message.getContent(), ImageService.THUMB_PIC_DISCUSS).execute();
				}
			}
		} catch (UnknownErrorException e) {
		}
	}
	
	private class ReceivingThread extends Thread {
		private boolean isReceiving = true;
		ArrayList<Message> newMessages = new ArrayList<Message>();
		
		public void run() {
			while (true) {
				if (!isReceiving) {
					break;
				}
				try {
					newMessages = mMessageService
							.receiveMessages(mGroupId, mMemoryService.getMySession());
				} catch (Exception e) {
				}
				mListView.post(new Runnable() {
					@Override
					public void run() {
						for (Message message : newMessages) {
							try {
								message = mMemoryService.insertReceivedMessage(message, mGroupId);
							} catch (UnknownErrorException e) {
							}
							if (message == null) {
								continue;
							}
							message.setComingMsg(true);
							appendMessageOnUI(message, mListView
									.getLastVisiblePosition() == mListView
									.getCount() - 1);
						}
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopReceiving() {
			isReceiving = false;
		}
	}

	private void appendMessageOnUI(Message newMessage, boolean rollToBottom) {
		mMessageList.add(newMessage);
		new GetImageTask(newMessage.getIconUrl(), ImageService.THUMB_ICON_DISCUSS).execute();
		if (newMessage.getType() == Message.TYPE_PIC) {
			new GetImageTask(newMessage.getContent(), ImageService.THUMB_PIC_DISCUSS).execute();
		}
		mMessageAdapter.notifyDataSetChanged();
		if (rollToBottom) {
			mListView.setSelection(mListView.getBottom());
		}
	}
	
	private void updateMessageOnUI(int messageId, boolean messageSent) {
		for (int i = mMessageList.size() - 1; i >= 0; i--) {
			if (mMessageList.get(i).getId() == messageId) {
				if (messageSent) {
					mMessageList.get(i).setSendingState(Message.STATE_SENT);
				} else {
					mMessageList.get(i).setSendingState(Message.STATE_FAILED);
				}
			}
		}
		mMessageAdapter.notifyDataSetChanged();
	}
	
	private void updateMessageOnUI(int messageId, boolean messageSent, String url) {
		for (int i = mMessageList.size() - 1; i >= 0; i--) {
			if (mMessageList.get(i).getId() == messageId) {
				if (messageSent) {
					mMessageList.get(i).setSendingState(Message.STATE_SENT);
					mMessageList.get(i).setContent(url);
				} else {
					mMessageList.get(i).setSendingState(Message.STATE_FAILED);
				}
			}
		}
		mMessageAdapter.notifyDataSetChanged();
	}
	
	private void attemptSendingText() {
		// get the text in edit field
		String text = mInputEditText.getText().toString();
		if (text.length() == 0) {
			return;
		}
		mInputEditText.setText("");
		
		// insert into file and get message object
		Message message = null;
		try {
			message = mMemoryService.insertSendingMessage(text, mGroupId, Message.TYPE_TEXT);
		} catch (UnknownErrorException e) {
		}
		if (message == null) {
			return;
		}
		
		// show the message on ui
		appendMessageOnUI(message, true);

		// start message sending task
		SendTextMessageTask sendMessageTask = new SendTextMessageTask();
		sendMessageTask.execute(message);
	}
	
	private class MessageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private static final int MINIMUM_SHOWING_TIMETAG_DIFF_IN_MILLIS = 3 * 60 * 1000;
		
		private ViewHolder viewHolder;

		private class ViewHolder {
			public TextView timeTextView;
			public ImageView iconImageView;
			public TextView contentTextView;
			public ProgressBar progressBar;
			public ImageView picImageView;
		}

		public MessageAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mMessageList.size();
		}

		@Override
		public Object getItem(int position) {
			return mMessageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		private int getConvertView(Message msg, boolean showTimeTag) {
			int[] layoutIds = new int[] {
					R.layout.message_item_left_with_timetag,
					R.layout.message_item_left_without_timetag,
					R.layout.message_item_right_with_timetag,
					R.layout.message_item_right_without_timetag,
					R.layout.pic_message_item_left_with_timetag,
					R.layout.pic_message_item_left_without_timetag,
					R.layout.pic_message_item_right_with_timetag,
					R.layout.pic_message_item_right_without_timetag
			};
			switch (msg.getType()) {
			case Message.TYPE_TEXT:
				if (msg.isComingMsg()) {
					return showTimeTag ? layoutIds[0] : layoutIds[1];
				} else {
					return showTimeTag ? layoutIds[2] : layoutIds[3];
				}
			case Message.TYPE_PIC:
				if (msg.isComingMsg()) {
					return showTimeTag ? layoutIds[4] : layoutIds[5];
				} else {
					return showTimeTag ? layoutIds[6] : layoutIds[7];
				}
			default:
				return 0;
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Message message = (Message) getItem(position);
			boolean showTimeTag = true;
			if (position > 0) {
				Message previousMessage = (Message) getItem(position - 1);
				if (message.getTime() - previousMessage.getTime() < MINIMUM_SHOWING_TIMETAG_DIFF_IN_MILLIS) {
					showTimeTag = false;
				}
			}

			int viewId = getConvertView(message, showTimeTag);
			if (viewId == 0) {
				return convertView;
			}
			
			convertView = inflater.inflate(getConvertView(message, showTimeTag), null);
			viewHolder = new ViewHolder();
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.message_item_time);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.message_item_icon);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.message_item_content);
			viewHolder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.message_item_progress_bar);
			viewHolder.picImageView = (ImageView) convertView
					.findViewById(R.id.message_item_pic);
			convertView.setTag(viewHolder);

			if (showTimeTag) {
				viewHolder.timeTextView.setText(new UITimeGenerator().getFormattedMessageTime(message.getTime()));
			}
			
			if (message.getType() == Message.TYPE_TEXT) {
				viewHolder.contentTextView.setText(message.getContent());
				if (message.isComingMsg() == true && message.getSendingState() == Message.STATE_SENDING) {
					viewHolder.progressBar.setVisibility(View.VISIBLE);
				} else {
					viewHolder.progressBar.setVisibility(View.GONE);
				}
			} else if (message.getType() == Message.TYPE_PIC) {
				final Bitmap picBitmap = mImageMaps.get(message.getContent());
				if (picBitmap != null) {
					viewHolder.picImageView.setImageBitmap(picBitmap);
					viewHolder.picImageView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startImageView(picBitmap, ((Message) getItem(position)).getContent());
						}
					});
					if (message.isComingMsg() == true && message.getSendingState() == Message.STATE_SENDING) {
						viewHolder.progressBar.setVisibility(View.VISIBLE);
					} else {
						viewHolder.progressBar.setVisibility(View.GONE);
					}
				} else {
					viewHolder.progressBar.setVisibility(View.VISIBLE);
				}
			}
			
			final Bitmap iconBitmap = mImageMaps.get(message.getIconUrl());
			if (iconBitmap != null) {
				viewHolder.iconImageView.setImageBitmap(iconBitmap);
				viewHolder.iconImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startImageView(iconBitmap, ((Message) getItem(position)).getIconUrl());
					}
				});
			}

			return convertView;
		}

	}
	
	private void startImageView(Bitmap thumb, String url) {
		Intent toImageViewIntent = new Intent(DiscussActivity.this, ImageViewActivity.class);
		toImageViewIntent.putExtra(ImageViewActivity.EXTRA_THUMBNAIL, thumb);
		toImageViewIntent.putExtra(ImageViewActivity.EXTRA_URL, url);
		startActivity(toImageViewIntent);
	}
	
	private class GetImageTask extends AsyncTask<Void, Void, Bitmap> {
		private String url;
		private int imageType;
		public GetImageTask(String url, int imageType) {
			this.url = url;
			this.imageType = imageType;
		}
		@Override
		protected Bitmap doInBackground(Void... params) {
			ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
			return imageService.getBitmap(url, imageType);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				return;
			}
			mImageMaps.put(url, result);
			mMessageAdapter.notifyDataSetChanged();
		}
	}

	private class EnterGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Exception exception;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			GroupParticipationService groupParticipationService = ServiceFactory.getGroupParticipationService();
			boolean entered = false;
			try {
				entered = groupParticipationService.enter(mGroupId, mMemoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return entered;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if (exception != null) {
				if (exception instanceof NetworkTimeoutException) {
					Toast.makeText(getApplicationContext(),
							R.string.error_network_timeout, Toast.LENGTH_SHORT)
							.show();
					return;
				} else if (exception instanceof UnknownErrorException) {
					Toast.makeText(getApplicationContext(),
							R.string.error_unknown, Toast.LENGTH_SHORT).show();
					return;
				} else if (exception instanceof NotLoginException) {
					try {
						mMemoryService.clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
		}
		
	}
	
	private class ExitGroupTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			GroupParticipationService groupParticipationService = ServiceFactory.getGroupParticipationService();
			try {
				groupParticipationService.exit(mGroupId, mMemoryService.getMySession());
			} catch (Exception e) {
			}
			return ((Void) null);
		}
	}
	
	private class SendTextMessageTask extends AsyncTask<Message, Void, Boolean> {
		private Exception exception;
		private int messageId;

		@Override
		protected Boolean doInBackground(Message... params) {
			messageId = params[0].getId();
			boolean sent = false;
			try {
				sent = mMessageService.sendMessage(params[0], mMemoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return sent;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (exception != null) {
				if (exception instanceof NotLoginException) {
					try {
						mMemoryService.clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				} else {
					updateMessageOnUI(messageId, false);
				}
			}

			try {
				mMemoryService.setMessageSent(messageId, true, "");
			} catch (UnknownErrorException e) {
				updateMessageOnUI(messageId, false);
			}
			updateMessageOnUI(messageId, success);
		}
	}

	private class FavorGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Exception exception;

		@Override
		protected Boolean doInBackground(Void... params) {
			MygroupsService mygroupsService = ServiceFactory
					.getMygroupsService();
			boolean added = false;
			try {
				added = mygroupsService.favorGroup(mGroupId, mMemoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return added;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mFavorGroupTask = null;
			
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
				} else if (exception instanceof NotLoginException) {
					try {
						mMemoryService.clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}

			if (success) {
				Toast.makeText(getApplicationContext(), R.string.success_favor,
						Toast.LENGTH_SHORT).show();
				setFavorMenuState(false);
				DiscussActivity.this.mHasFavored = true;
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.error_favor_fail_retry, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class CancelFavorGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Exception exception;

		@Override
		protected Boolean doInBackground(Void... params) {
			MygroupsService mygroupsService = ServiceFactory
					.getMygroupsService();
			boolean cancelled = false;
			try {
				cancelled = mygroupsService.removeFromFavor(mGroupId, mMemoryService.getMySession());
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			} catch (NotLoginException e) {
				exception = (NotLoginException) e;
			}
			return cancelled;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mCancelFavorGroupTask = null;
			
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
				} else if (exception instanceof NotLoginException) {
					try {
						mMemoryService.clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}

			if (success) {
				Toast.makeText(getApplicationContext(), R.string.success_cancel,
						Toast.LENGTH_SHORT).show();
				mFavorMenuItem.setTitle(getString(R.string.action_favor));
				DiscussActivity.this.mHasFavored = false;
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.error_cancel_fail_retry, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class CheckGroupFavoredTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			MygroupsService mygroupsService = ServiceFactory.getMygroupsService();
			boolean hasFavored = false;
			try {
				hasFavored = mygroupsService.getGroupFavorStatus(mGroupId, mMemoryService.getMySession());
			} catch (Exception e) {
			}
			return hasFavored;
		}

		@Override
		protected void onPostExecute(final Boolean hasFavored) {
			mCheckGroupFavoredTask = null;
			
			if (hasFavored) {
				setFavorMenuState(false);
			}
			DiscussActivity.this.mHasFavored = hasFavored;
		}
	}
	
	private String capturedFileName;
	
	private void generateCapturedFileName() {
		capturedFileName = new UITimeGenerator().getFormattedPhotoNameTime() + ".jpg";
	}

	private static final int REQUEST_CODE_IMAGE = 1;
	private static final int REQUEST_CODE_CAPTURE = 2;
	
	private void loadpic(){
		Intent galleryIntent = new Intent(Intent.ACTION_PICK, null);
		galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(galleryIntent, REQUEST_CODE_IMAGE);
	}
	
	private void camera(){
		Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		generateCapturedFileName();
		try {
			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMemoryService.getNewCapturingUri(capturedFileName));
		} catch (UnknownErrorException e) {
			Toast.makeText(getApplicationContext(), getString(R.string.error_non_storage), Toast.LENGTH_SHORT).show();
			return;
		}
		startActivityForResult(captureIntent, REQUEST_CODE_CAPTURE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			if (uri == null) {
				return;
			}
			attemptSendingPic(uri);
		} else if (requestCode == REQUEST_CODE_CAPTURE && resultCode == RESULT_OK) {
			try {
				attemptSendingPic(mMemoryService.getNewCapturingUri(capturedFileName));
			} catch (UnknownErrorException e) {
				return;
			}
		}
	}
	
	private void attemptSendingPic(Uri uri) {
		// insert into file and get message object
		Message message = null;
		try {
			message = mMemoryService.insertSendingMessage("", mGroupId, Message.TYPE_PIC);
		} catch (UnknownErrorException e) {
		}
		if (message == null) {
			return;
		}
		
		// refresh ui
		appendMessageOnUI(message, true);
		
		new GetPhotoUploadTokenTask(uri, message.getId()).execute();
	}
	
	private class GetPhotoUploadTokenTask extends AsyncTask<Void, Void, String> {
		private Exception exception;
		private Uri uri;
		private int messageId;
		public GetPhotoUploadTokenTask(Uri uri, int messageId) {
			this.uri = uri;
			this.messageId = messageId;
		}
		@Override
		protected String doInBackground(Void... params) {
			String token = "";
			try {
				UploadService uploadService = ServiceFactory.getUploadService();
				token = uploadService.photoUploadToken(mMemoryService.getMySession(), mGroupId);
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
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
			
			if (result != null && result.length() != 0) {
				executeUpload(result, uri, messageId);
			} else {
				Toast.makeText(getApplicationContext(), R.string.error_send_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void executeUpload(String token, Uri uri, final int messageId) {
		ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
		try {
			imageService.uploadBitmap(getApplicationContext(), token, uri, new JSONObjectRet() {
				@Override
				public void onFailure(Exception ex) {
					Toast.makeText(getApplicationContext(), R.string.error_send_fail, Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onSuccess(JSONObject obj) {
					String uploadedUrl = null;
					try {
						uploadedUrl = Conf.SERVER_DOMAIN + obj.getString("hash");
					} catch (JSONException e) {
						Toast.makeText(getApplicationContext(), R.string.error_send_fail, Toast.LENGTH_SHORT).show();
					}
					if (uploadedUrl == null || uploadedUrl.length() == 0) {
						Toast.makeText(getApplicationContext(), R.string.error_send_fail, Toast.LENGTH_SHORT).show();
					}
					new UpdatePicTask(messageId).execute(uploadedUrl);
				}
			});
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.error_send_fail, Toast.LENGTH_SHORT).show();
		}
	}
	
	private class UpdatePicTask extends AsyncTask<String, Void, Boolean> {
		private Exception exception;
		private int messageId;
		private String picUrl;
		public UpdatePicTask(int messageId) {
			this.messageId = messageId;
		}
		@Override
		protected Boolean doInBackground(String... params) {
			this.picUrl = params[0];
			try {
				UploadService uploadService = ServiceFactory.getUploadService();
				
				return uploadService.photoUpdate(mMemoryService.getMySession(), mGroupId, picUrl);
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
				if (exception instanceof NotLoginException) {
					try {
						ServiceFactory.getMemoryService(getApplicationContext()).clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				} else {
					updateMessageOnUI(messageId, false);
				}
			}
			
			try {
				mMemoryService.setMessageSent(messageId, true, picUrl);
			} catch (UnknownErrorException e) {
				updateMessageOnUI(messageId, false);
			}
			updateMessageOnUI(messageId, result, picUrl);
			new GetImageTask(picUrl, ImageService.THUMB_PIC_DISCUSS).execute();
		}
	}
	
}
