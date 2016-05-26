package com.lab.flickr.network;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Matt on 22/05/2016.
 */
public abstract class Loader extends AsyncTask<DataWrapper, Integer, DataWrapper> {

	private static final AtomicInteger nextID = new AtomicInteger(0);
	protected final String id = "" + nextID.incrementAndGet();

	protected LoaderListener listener;
	protected Context context;

	public void setLoaderListener(LoaderListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("LoaderListener cannot be null.");
		}
		this.listener = listener;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		int size = values.length;
		listener.reportProgress(getType(), values[size - 1]); //assumes the highest index has the most recent update
	}

	@Override
	protected void onPostExecute(DataWrapper dataWrapper) {
		listener.onRequestFinished(getType(), dataWrapper);
	}

	public String getID() {
		return getType().name() + id;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public abstract LoaderType getType();

	public interface LoaderListener {

		void reportProgress(LoaderType type, Integer progress);
		void onRequestFinished(LoaderType type, DataWrapper result);

	}

	/**
	 * Add an enum for each subclass so that the LoaderListener can pass the type of caller
	 */
	public enum LoaderType {
		JsonLoader,
		ImageLoader;
	}

}
