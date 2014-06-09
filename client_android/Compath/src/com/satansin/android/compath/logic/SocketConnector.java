package com.satansin.android.compath.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnector {
	
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	
	private boolean connectionFailed = false;
	
	private static final int TIME_INTERVAL = 20;
	
	public SocketConnector() {
		try {
//			socket = new Socket("192.110.165.234", 9527);
//			socket = new Socket("172.17.186.180", 9527);
//			socket = new Socket("10.0.0.21", 9527);
			socket = new Socket("192.168.159.248", 9527);
			InputStreamReader streamReader = new InputStreamReader(socket
					.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(socket.getOutputStream());
		} catch (Exception e) {
			connectionFailed = true;
		}
	}
	
	public String send(String messageSent, int timeout) {
		if (connectionFailed) {
			return null;
		}
		try {
			writer.println(messageSent);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String messageReceived = null;
		int t = 0;
		while (t < timeout) {
			try {
				messageReceived = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
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
