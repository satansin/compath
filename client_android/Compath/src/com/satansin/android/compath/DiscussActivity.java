package com.satansin.android.compath;

import java.util.ArrayList;
import java.util.List;

import com.satansin.android.compath.logic.MemoryService;
import com.satansin.android.compath.logic.Message;
import com.satansin.android.compath.logic.MessageService;
import com.satansin.android.compath.logic.MygroupsService;
import com.satansin.android.compath.logic.NetworkTimeoutException;
import com.satansin.android.compath.logic.ServiceFactory;
import com.satansin.android.compath.logic.UnknownErrorException;
import com.satansin.android.compath.util.UITimeGenerator;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiscussActivity extends ActionBarActivity {

	public static final String EXTRA_DISCUSS_GROUP_ID = "com.satansin.android.compath.DISCUSS_ID";

	private int groupId;

	private EditText inputEditText;
	private ListView listView;

	private List<Message> messageList;
	private MessageAdapter messageAdapter;

	private MessageService messageService = ServiceFactory
			.getMessageService();
	private MemoryService memoryService = ServiceFactory
			.getMemoryService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);

		if (!getIntent().hasExtra(EXTRA_DISCUSS_GROUP_ID)) {
			Toast.makeText(getApplicationContext(), R.string.error_unknown,
					Toast.LENGTH_SHORT).show();
			DiscussActivity.this.finish();
		}
		groupId = getIntent().getIntExtra(EXTRA_DISCUSS_GROUP_ID, 0);
		messageList = memoryService.loadHistoryMessage(groupId);

		listView = (ListView) findViewById(R.id.discuss_list_view);
		messageAdapter = new MessageAdapter(this);
		listView.setAdapter(messageAdapter);
		listView.setSelection(listView.getBottom());

		inputEditText = (EditText) findViewById(R.id.discuss_input);

		findViewById(R.id.discuss_sending).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						attemptSending();
					}
				});

		new Thread() {
			@Override
			public void run() {
				while (true) {
					final ArrayList<Message> newMessages = messageService
							.receiveMessages(groupId);
					listView.post(new Runnable() {
						@Override
						public void run() {
							for (Message message : newMessages) {
								appendMessage(message, listView
										.getLastVisiblePosition() == listView
										.getCount() - 1);
								memoryService.insertMessage(message, false);
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
		}.start();
		
		// TODO no-reply: entering the group

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.discuss, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_favor) {
			new FavorGroupTask().execute();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void attemptSending() {
		String text = inputEditText.getText().toString();
		if (text.length() == 0) {
			return;
		}
		inputEditText.setText("");
		Message message = memoryService.insertMessage(text, false);
		appendMessage(message, true);

		SendMessageTask sendMessageTask = new SendMessageTask();
		sendMessageTask.execute(message);

		// TODO update sending status in SQLite
	}

	private void appendMessage(Message newMessage, boolean rollToBottom) {
		messageList.add(newMessage);
		messageAdapter.notifyDataSetChanged();
		if (rollToBottom) {
			listView.setSelection(listView.getBottom());
		}
	}

	private class MessageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private static final int MINIMUM_SHOWING_TIMETAG_DIFF_IN_MILLIS = 60 * 1000;

		class ViewHolder {
			public TextView timeTextView;
			public ImageView iconImageView;
			public TextView usrnameTextView;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			Message message = (Message) getItem(position);
			boolean showTimeTag = true;
			if (position > 0) {
				Message previousMessage = (Message) getItem(position);
				if (message.getTime().getTimeInMillis()
						- previousMessage.getTime().getTimeInMillis() < MINIMUM_SHOWING_TIMETAG_DIFF_IN_MILLIS) {
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
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.message_item_time);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.message_item_icon);
			viewHolder.usrnameTextView = (TextView) convertView
					.findViewById(R.id.message_item_usrname);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.message_item_content);
			convertView.setTag(viewHolder);

			if (showTimeTag) {
				viewHolder.timeTextView.setText(new UITimeGenerator()
						.getUITime(message.getTime()));
			}
			if (message.isComingMsg()) {
				viewHolder.usrnameTextView.setText(message.getFrom());
			}
			viewHolder.contentTextView.setText(message.getContent());

			return convertView;
		}

	}

	private class EnterGroupTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class ExitGroupTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	private class SendMessageTask extends AsyncTask<Message, Void, Boolean> {
		private Exception exception;

		@Override
		protected Boolean doInBackground(Message... params) {
			boolean sent = false;
			try {
				sent = messageService.sendMessage(params[0]);
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			}
			return sent;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (exception != null) {
				// TODO: show that the message is not sent
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
				added = mygroupsService.favorGroup(groupId);
			} catch (NetworkTimeoutException e) {
				exception = (NetworkTimeoutException) e;
			} catch (UnknownErrorException e) {
				exception = (UnknownErrorException) e;
			}
			return added;
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
				}
			}

			if (success) {
				Toast.makeText(getApplicationContext(), R.string.success_favor,
						Toast.LENGTH_SHORT).show();
				// TODO change the text of favor into cancel favor
			} else {
				Toast.makeText(getApplicationContext(),
						R.string.error_favor_fail, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
