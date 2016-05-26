package com.lab.flickr.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonLoader extends Loader {

	@Override
	protected DataWrapper doInBackground(DataWrapper... params) {
		try {
			return load(params[0]);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return params[0];
	}

	@Override
	protected void onCancelled() {
		listener = null;
	}

	@Override
	public LoaderType getType() {
		return LoaderType.JsonLoader;
	}

	private DataWrapper load(DataWrapper wrapper) throws IOException, JSONException {
		String urlString = wrapper.getValue(DataWrapper.Key.URL).toString();
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
			wrapper.setValue(DataWrapper.Key.JSON, new JSONObject(sb.toString()));
			return wrapper;
		}
		return null;
	}
}
