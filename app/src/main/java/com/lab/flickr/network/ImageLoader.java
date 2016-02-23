package com.lab.flickr.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageLoader extends AsyncTask<DataWrapper, Integer, DataWrapper> {

	public interface ImageLoaderListener extends LoaderListener {
		void onRequestFinished(DataWrapper dataWrapper);
	}

	private ImageLoaderListener listener;

	public void setJsonLoaderListener(ImageLoaderListener listener) {
		this.listener = listener;
	}

	@Override
	protected DataWrapper doInBackground(DataWrapper... params) {
		return load(params[0]);
	}

	@Override
	protected void onPostExecute(DataWrapper dataWrapper) {
		if (listener != null) {
			listener.onRequestFinished(dataWrapper);
		}
	}

	@Override
	protected void onCancelled() {
		listener = null;
	}

	private DataWrapper load(DataWrapper wrapper) {
		if (wrapper == null) {
			return wrapper;
		}
		try {
			URL url = new URL(wrapper.getUrl());
			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			wrapper.setBitmap(bmp);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wrapper;
	}
}
