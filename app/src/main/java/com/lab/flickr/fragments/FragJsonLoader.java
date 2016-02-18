package com.lab.flickr.fragments;

import android.util.Log;

import com.lab.flickr.R;
import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.JsonLoader;
import com.lab.flickr.network.LoaderListener;

/**
 * Created by Matt on 18/02/2016.
 */
public class FragJsonLoader extends FragLoader {

	private JsonLoader jsonLoader;

	public FragJsonLoader() {
		jsonLoader = new JsonLoader();
	}

	@Override
	public void setLoaderListener(LoaderListener listener) {
		Log.d("FragJsonLoader","setLoaderListener. Listener null ? " + (listener == null));
		if (listener instanceof JsonLoader.JsonLoaderListener) {
			jsonLoader.setJsonLoaderListener((JsonLoader.JsonLoaderListener) listener);
		}
	}

	@Override
	public void cancel(boolean interrupt) {
		jsonLoader.cancel(interrupt);
	}

	@Override
	public void performLoadingTask(DataWrapper wrapper) {
		Log.d("FragJson", "performLoadingTask() - Starting AsyncTask.execute");
		jsonLoader.execute(new String[] {getResources().getString(R.string.frag_json_loader_url)});
	}
}
