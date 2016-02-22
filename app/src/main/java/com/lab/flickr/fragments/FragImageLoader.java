package com.lab.flickr.fragments;

import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.ImageLoader;
import com.lab.flickr.network.LoaderListener;

/**
 * Created by Matt on 18/02/2016.
 */
public class FragImageLoader extends FragLoader {

	private ImageLoader imageLoader;

	public FragImageLoader() {
		this.imageLoader = new ImageLoader();
	}

	@Override
	public void setLoaderListener(LoaderListener listener) {
		if (listener instanceof ImageLoader.ImageLoaderListener) {
			imageLoader.setJsonLoaderListener((ImageLoader.ImageLoaderListener) listener);
		}
	}

	@Override
	public void cancel(boolean interrupt) {
		imageLoader.cancel(interrupt);
	}

	@Override
	public void performLoadingTask(DataWrapper wrapper) {
		imageLoader.execute(wrapper);
	}
}
