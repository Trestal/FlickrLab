package com.lab.flickr.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Matt on 18/02/2016.
 */
public class JsonLoader extends AsyncTask<String, Void, JSONObject> {

	public interface JsonLoaderListener extends LoaderListener {
		void onRequestFinished(JSONObject jsonObject);
	}

	private JsonLoaderListener listener;

	public void setJsonLoaderListener(JsonLoaderListener listener) {
		this.listener = listener;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		return load(params[0]);
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		Log.d("ActMain/onPostExecute", "onPostExecute() Method - Retrieved Json. Is null ? : " + (jsonObject == null));
		Log.d("ActMain/onPostExecute", "onPostExecute() Method - listener is null ? : " + (listener == null));
		if (listener != null) {
			listener.onRequestFinished(jsonObject);
		}
	}

	private JSONObject load(String urlString) {
		Log.d("jsonLoader/load", "load() Method - Attempting to get json");
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = new BufferedInputStream(conn.getInputStream());
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					sb.append(line);
				}
				conn.disconnect();
				return new JSONObject(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		listener = null;
	}
}
