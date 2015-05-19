package ro.pub.cs.systems.pdsd.practicaltest02var04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class ServerThread extends Thread {
	
	private int          port         = 0;
	private ServerSocket serverSocket = null;
	
	private HashMap<String, String> webPages = null;
	private BufferedReader reader=null;
	private PrintWriter writer=null;
	
	
	public ServerThread(int port) {
		this.port = port;
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException ioException) {
			Log.e("Colocviu2", "An exception has occurred: " + ioException.getMessage());
			ioException.printStackTrace();
		}
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setServerSocker(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	
	@Override
	public void run() {
		try {		
			while (!Thread.currentThread().isInterrupted()) {
				Log.i(Constants.TAG, "[SERVER] Waiting for a connection...");
				Socket socket = serverSocket.accept();
				Log.e(Constants.TAG, "connection accepted");
				this.reader = Utilities.getReader(socket);
				this.writer = Utilities.getWriter(socket);
				
				String url = this.reader.readLine();
				Log.e(Constants.TAG, "read " + url);
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				Log.e(Constants.TAG, "trying to execute");
				String content = httpClient.execute(httpGet, responseHandler);
				Log.e(Constants.TAG, "got page content");
				this.writer.println(content);
				this.writer.flush();
				Log.e(Constants.TAG, "sent page content; closing");
				socket.close();
				
				Log.i(Constants.TAG, "[SERVER] Connection handled succesfully");
			}			
			
			
		} catch (ClientProtocolException clientProtocolException) {
			Log.e(Constants.TAG, "An exception has occurred: " + clientProtocolException.getMessage());
			
		} catch (IOException ioException) {
			Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
		}
	}
	
	public void stopThread() {
		if (serverSocket != null) {
			interrupt();
			try {
				serverSocket.close();
			} catch (IOException ioException) {
				Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
			
			}
		}
	}


}