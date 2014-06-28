package com.satansin.android.compath;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.satansin.android.compath.util.UITimeGenerator;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TextView;
import android.widget.Toast;

public class DiscussActivity extends ActionBarActivity {

	public static final String EXTRA_DISCUSS_GROUP_ID = "com.satansin.android.compath.DISCUSS_ID";
	
	private static final int HISTORY_PAGE_SIZE = 20;

	private int groupId;
	
	private EditText mInputEditText;
	private ListView mListView;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private TextView headerTextView;
	private ImageView sendButtonImageView;
	private MenuItem favorMenuItem;
	
	private Button loadpicBtn = null;
	private Button cameraBtn = null;
	
	private HashMap<String, Bitmap> iconMaps;

	private List<Message> messageList;
	private MessageAdapter messageAdapter;

	private MessageService messageService = ServiceFactory.getMessageService();
	private MemoryService memoryService;
	
	private ReceivingThread receivingThread;
	
	private boolean hasFavored = false;
	private Uri outputFileUri = null; 
    private static final int IMAGE_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
	
	private CancelFavorGroupTask cancelFavorGroupTask;
	private CheckGroupFavoredTask checkGroupFavoredTask;
	private FavorGroupTask favorGroupTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		CompathApplication.getInstance().addActivity(this);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);
		
		memoryService = ServiceFactory.getMemoryService(this);

		if (!getIntent().hasExtra(EXTRA_DISCUSS_GROUP_ID)) {
			Toast.makeText(getApplicationContext(), R.string.error_unknown_retry,
					Toast.LENGTH_SHORT).show();
			DiscussActivity.this.finish();
		}
		groupId = getIntent().getIntExtra(EXTRA_DISCUSS_GROUP_ID, 0);
		
		iconMaps = new HashMap<String, Bitmap>();
		
		messageList = new ArrayList<Message>();

		mListView = (ListView) findViewById(R.id.discuss_list_view);
		View headerView = getLayoutInflater().inflate(R.layout.header_discuss, null);
		headerTextView = (TextView) headerView.findViewById(R.id.discuss_header_text);
		mListView.addHeaderView(headerView);
		
		messageAdapter = new MessageAdapter(this);
		mListView.setAdapter(messageAdapter);
		
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

		sendButtonImageView = (ImageView) findViewById(R.id.discuss_sending);
		sendButtonImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				attemptSending();
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
					sendButtonImageView.setImageResource(R.drawable.send_disabled);
				} else {
					sendButtonImageView.setImageResource(R.drawable.send);
				}
			}
		});

		loadHistoryMessages();
		
		loadpicBtn = (Button) findViewById(R.id.loadpicBtn);
		loadpicBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadpic();
			}

		});
		
		cameraBtn = (Button) findViewById(R.id.cameraBtn);
		cameraBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				camera();
			}
		});
		
		new EnterGroupTask().execute();
		
		new CheckGroupFavoredTask().execute();

		receivingThread = new ReceivingThread();
		receivingThread.start();

		mListView.setSelection(messageList.size());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discuss, menu);
		favorMenuItem = menu.findItem(R.id.action_favor);
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
			if (hasFavored) {
				setFavorMenuState(true);
				cancelFavorGroupTask = new CancelFavorGroupTask();
				cancelFavorGroupTask.execute();
			} else {
				setFavorMenuState(false);
				favorGroupTask = new FavorGroupTask();
				favorGroupTask.execute();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 
	 * @param toFavor If toFavor is true, set text into "favor" and set the icon into not-favored icon 
	 */
	private void setFavorMenuState(boolean toFavor) {
		if (toFavor) {
			favorMenuItem.setTitle(getString(R.string.action_favor));
			favorMenuItem.setIcon(R.drawable.not_favored);
		} else {
			favorMenuItem.setTitle(getString(R.string.action_cancel_favor));
			favorMenuItem.setIcon(R.drawable.favored);
		}
	}

	private boolean favorOperationsProcessing() {
		return (cancelFavorGroupTask != null || checkGroupFavoredTask != null || favorGroupTask != null);
	}

	@Override
	public void finish() {
		receivingThread.stopReceiving();
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
			int currentSize = messageList.size();
			List<Message> historyList = memoryService.
					loadHistoryMessage(groupId, currentSize + 1, currentSize + HISTORY_PAGE_SIZE);
			
			mSwipeRefreshLayout.setRefreshing(false);
			if (currentSize > 0 && historyList.size() <= 0) {
				headerTextView.setText(getString(R.string.error_non_history_msg));
			}
			for (Message message : historyList) {
				messageList.add(0, message);
				new GetUsrIconTask(message.getIconUrl()).execute();
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
					newMessages = messageService
							.receiveMessages(groupId, memoryService.getMySession());
				} catch (Exception e) {
				}
				mListView.post(new Runnable() {
					@Override
					public void run() {
						for (Message message : newMessages) {
							try {
								message = memoryService.insertReceivedMessage(message, groupId);
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
	
	private void attemptSending() {
		String text = mInputEditText.getText().toString();
		if (text.length() == 0) {
			return;
		}
		mInputEditText.setText("");
		Message message = null;
		try {
			message = memoryService.insertSendingMessage(text, groupId);
		} catch (UnknownErrorException e) {
		}
		if (message == null) {
			return;
		}
		appendMessageOnUI(message, true);

		SendMessageTask sendMessageTask = new SendMessageTask();
		sendMessageTask.execute(message);

		// TODO update sending status in SQLite
	}

	private void appendMessageOnUI(Message newMessage, boolean rollToBottom) {
		messageList.add(newMessage);
		new GetUsrIconTask(newMessage.getIconUrl()).execute();
		messageAdapter.notifyDataSetChanged();
		if (rollToBottom) {
			mListView.setSelection(mListView.getBottom());
		}
	}
	
	private class MessageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private static final int MINIMUM_SHOWING_TIMETAG_DIFF_IN_MILLIS = 3 * 60 * 1000;
		
		private ViewHolder viewHolder;

		private class ViewHolder {
			public TextView timeTextView;
			public ImageView iconImageView;
//			public TextView usrnameTextView;
			public TextView contentTextView;
		}

		public MessageAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return messageList.size();
		}

		@Override
		public Object getItem(int position) {
			return messageList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
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

			if (message.isComingMsg()) {
				convertView = showTimeTag ? inflater.inflate(
						R.layout.message_item_left_with_timetag, null)
						: inflater.inflate(
								R.layout.message_item_left_without_timetag,
								null);
			} else {
				convertView = showTimeTag ? inflater.inflate(
						R.layout.message_item_right_with_timetag, null)
						: inflater.inflate(
								R.layout.message_item_right_without_timetag,
								null);
			}
			viewHolder = new ViewHolder();
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.message_item_time);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.message_item_icon);
//			viewHolder.usrnameTextView = (TextView) convertView
//					.findViewById(R.id.message_item_usrname);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.message_item_content);
			convertView.setTag(viewHolder);

			if (showTimeTag) {
				viewHolder.timeTextView.setText(new UITimeGenerator().getFormattedMessageTime(message.getTime()));
			}
//			if (message.isComingMsg()) {
//				viewHolder.usrnameTextView.setText(message.getFrom());
//			}
			viewHolder.contentTextView.setText(message.getContent());
			
			Bitmap iconBitmap = iconMaps.get(message.getIconUrl());
			if (iconBitmap != null) {
				viewHolder.iconImageView.setImageBitmap(iconBitmap);
			}
			viewHolder.iconImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent toImageViewIntent = new Intent(DiscussActivity.this, ImageViewActivity.class);
					viewHolder.iconImageView.setDrawingCacheEnabled(true);
					toImageViewIntent.putExtra(ImageViewActivity.EXTRA_THUMBNAIL, viewHolder.iconImageView.getDrawingCache());
					toImageViewIntent.putExtra(ImageViewActivity.EXTRA_URL, ((Message) getItem(position)).getIconUrl());
					startActivity(toImageViewIntent);
				}
			});

			return convertView;
		}

	}
	
	private class GetUsrIconTask extends AsyncTask<Void, Void, Bitmap> {
		private String url;
		public GetUsrIconTask(String url) {
			this.url = url;
		}
		@Override
		protected Bitmap doInBackground(Void... params) {
			ImageService imageService = ServiceFactory.getImageService(getApplicationContext());
			return imageService.getBitmap(url, ImageService.THUMB_ICON_DISCUSS);
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				return;
			}
			iconMaps.put(url, result);
			messageAdapter.notifyDataSetChanged();
		}
	}

	private class EnterGroupTask extends AsyncTask<Void, Void, Boolean> {
		private Exception exception;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			GroupParticipationService groupParticipationService = ServiceFactory.getGroupParticipationService();
			boolean entered = false;
			try {
				entered = groupParticipationService.enter(groupId, memoryService.getMySession());
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
						memoryService.clearSession();
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
				groupParticipationService.exit(groupId, memoryService.getMySession());
			} catch (Exception e) {
			}
			return ((Void) null);
		}
	}
	
	private class SendMessageTask extends AsyncTask<Message, Void, Boolean> {
		private Exception exception;

		@Override
		protected Boolean doInBackground(Message... params) {
			boolean sent = false;
			try {
				sent = messageService.sendMessage(params[0], memoryService.getMySession());
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
				// TODO: show that the message is not sent
				if (exception instanceof NotLoginException) {
					try {
						memoryService.clearSession();
					} catch (UnknownErrorException e) {
					}
					CompathApplication.getInstance().finishAllActivities();
					Intent intent = new Intent(DiscussActivity.this, LoginActivity.class);
					startActivity(intent);
				}
			}
			// TODO: stop the animation
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
				added = mygroupsService.favorGroup(groupId, memoryService.getMySession());
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
			favorGroupTask = null;
			
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
						memoryService.clearSession();
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
				DiscussActivity.this.hasFavored = true;
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
				cancelled = mygroupsService.removeFromFavor(groupId, memoryService.getMySession());
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
			cancelFavorGroupTask = null;
			
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
						memoryService.clearSession();
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
				favorMenuItem.setTitle(getString(R.string.action_favor));
				DiscussActivity.this.hasFavored = false;
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
				hasFavored = mygroupsService.getGroupFavorStatus(groupId, memoryService.getMySession());
			} catch (Exception e) {
			}
			return hasFavored;
		}

		@Override
		protected void onPostExecute(final Boolean hasFavored) {
			checkGroupFavoredTask = null;
			
			if (hasFavored) {
				setFavorMenuState(false);
			}
			DiscussActivity.this.hasFavored = hasFavored;
		}
	}

	
	private void loadpic(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
        intent.addCategory(Intent.CATEGORY_OPENABLE);  
        intent.setType("image/*");  
        startActivityForResult(Intent.createChooser(intent, "选择图片"), 1000);  
	}
	
	private void camera(){
		File file = new File(Environment.getExternalStorageDirectory(), "textphoto.jpg");  
        outputFileUri = Uri.fromFile(file);  

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);  
        startActivityForResult(intent, 1001); 
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
//    	private ImageView iv;  
    	Bitmap bmp = null;  

    	
        if (requestCode == IMAGE_REQUEST_CODE) {  
              
            if(data == null){  
                return;  
            }  
              
            Uri uri = data.getData();  
            String[] proj = { MediaStore.Images.Media.DATA };  
            @SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri, proj, // Which  
                                                                    // columns  
                                                                    // to return  
                    null, // WHERE clause; which rows to return (all rows)  
                    null, // WHERE clause selection arguments (none)  
                    null); // Order-by clause (ascending by name)  
  
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
            cursor.moveToFirst();  
  
            String path = cursor.getString(column_index);  
  
            if (bmp != null)// 如果不释放的话，不断取图片，将会内存不够  
                bmp.recycle();  
  
            bmp = BitmapFactory.decodeFile(path);  
            //TODO发送图片消息
  
        } else if (requestCode == CAMERA_REQUEST_CODE) {  
            bmp = BitmapFactory.decodeFile(outputFileUri.getPath());  
            //TODO发送图片消息
        } else {  
            Toast.makeText(this, "请重新选择图片", Toast.LENGTH_SHORT).show();  
        }  
  
    }  
	
	
}
