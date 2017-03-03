package de.uni.madnClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class MADNSocket extends Thread {

	private Queue<String> replies;
	private Socket socket;
	private PrintStream outStream;
	private BufferedReader inStream;
	private boolean isActive;

	public MADNSocket() {
		this.isActive = true;
		this.replies = new LinkedBlockingDeque<String>();
		try {
			this.socket = new Socket("192.168.43.66", 5005);
			this.outStream = new PrintStream(socket.getOutputStream(), true);
			this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			this.isActive = false;
		} catch (ConnectException e) {
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			this.isActive = false;
		}

	}

	public void close() {
		try {
			sendString("quit");
			socket.close();
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendString(String toSend) {
		outStream.print(toSend);
	}

	public String getReply() {
		return this.replies.poll();
	}

	public boolean hasReplies() {
		return replies.size() > 0;

	}

	private void updateReplies() {
		try {
			String line;
			while ((line = inStream.readLine()) != null) {
				replies.add(line.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (Arguments.running) {
			updateReplies();
		}

	}

}
