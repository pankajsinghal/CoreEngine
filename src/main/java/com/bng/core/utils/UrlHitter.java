package com.bng.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class UrlHitter {

	private LinkedBlockingQueue<String> urls = new LinkedBlockingQueue<String>();

	public void init() {
		UrlHitterThread thread = new UrlHitterThread();
		thread.start();
	}

	public void hitUrl(String url) {
		try {
			urls.put(url);
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
	}

	protected String getUrl() {
		try {
			return urls.take();
		} catch (InterruptedException e) {
			Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
		}
		return null;
	}

	class UrlHitterThread extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					hitUrl(getUrl());
					Thread.sleep(10);
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					Logger.sysLog(LogValues.error, this.getClass().getName(), com.bng.core.exception.coreException.GetStack(e));
				}
			}

		}

		void hitUrl(String url) throws IOException {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();


			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			if(response.toString()!=null)
				Logger.sysLog(LogValues.info, this.getClass().getName(),"url : "+url +"response : "+response.toString());
		}
	}

}
