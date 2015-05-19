package ro.pub.cs.systems.pdsd.practicaltest02var04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

public class ClientThread extends Thread{

	private String   address;
	private int      port;
	private String   url;
	private WebView webView;
	
	private Socket   socket;
	
	public ClientThread(
			String address,
			int port,
			String url,
			WebView webView) {
		this.address                 = address;
		this.port                    = port;
		this.url        = url;
		this.webView = webView;
	}
	
	@Override
	public void run() {
		try {
			socket = new Socket(address, port);
			if (socket == null) {
				Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
			}
			
			BufferedReader bufferedReader = Utilities.getReader(socket);
			PrintWriter    printWriter    = Utilities.getWriter(socket);
			String result="";
			if (bufferedReader != null && printWriter != null) {
				printWriter.println(url);
				printWriter.flush();
				String line;
				
				while ((line = bufferedReader.readLine()) != null) {
					result += line + "\n";
				}
				Log.e(Constants.TAG, "got result:\n"+result);
				final String endResult = result;
				
				this.webView.post(new Runnable() {
							@Override
							public void run() {
								webView.loadDataWithBaseURL(url, endResult, "text/html", "UTF8", null);
							}
						});
			} else {
				Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
			}
			socket.close();
		} catch (IOException ioException) {
			Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
		}
	}
}
