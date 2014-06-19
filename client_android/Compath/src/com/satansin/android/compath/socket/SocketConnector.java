package com.satansin.android.compath.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONException;

import com.satansin.android.compath.logic.UnknownErrorException;

import android.util.Log;

public class SocketConnector {
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	
	private boolean connectionFailed = false;
	
	private static final int TIME_INTERVAL = 20;
	
	public SocketConnector() {
		try {
			socket = new Socket("192.110.165.234", 9527);
//			socket = new Socket("172.17.186.180", 9527);
//			socket = new Socket("10.0.0.16", 9527);
//			socket = new Socket("192.168.159.3", 9527);
//			socket = new Socket("192.168.159.1", 9527);
//			socket = new Socket("10.0.2.2", 9527);
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream(), "utf-8"));
			writer = new BufferedWriter(new OutputStreamWriter(socket
					.getOutputStream(), "utf-8"));
		} catch (Exception e) {
			connectionFailed = true;
		}
	}
	
	public SocketMsg send(SocketMsg messageSent, int timeout) throws UnknownErrorException {
		Log.w("socket_send", messageSent.toString());
		if (connectionFailed) {
			return null;
		}
		try {
			writer.write(messageSent.toString() + "\n");
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SocketMsg messageReceived = null;
		int t = 0;
		while (t < timeout) {
			try {
				messageReceived = new SocketMsg(reader.readLine());
				Log.w("socket_receive", messageReceived.toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
				throw new UnknownErrorException();
			}
			if (messageReceived != null) {
				break;
			}
			
			t += TIME_INTERVAL;
			try {
				Thread.sleep(TIME_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			reader.close();
			writer.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return messageReceived;
	}
	
}
