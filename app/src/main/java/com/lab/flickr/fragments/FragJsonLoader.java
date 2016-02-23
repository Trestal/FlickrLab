package com.lab.flickr.fragments;

import com.lab.flickr.R;
import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.JsonLoader;
import com.lab.flickr.network.LoaderListener;

public class FragJsonLoader extends FragLoader {

	private JsonLoader jsonLoader;

	public FragJsonLoader() {
		jsonLoader = new JsonLoader();
	}

	@Override
	public void setLoaderListener(LoaderListener listener) {
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
		jsonLoader.execute(getResources().getString(R.string.frag_json_loader_url));
	}
}
