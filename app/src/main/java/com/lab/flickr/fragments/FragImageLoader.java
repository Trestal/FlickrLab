package com.lab.flickr.fragments;

import android.os.AsyncTask;
import android.util.Log;

import com.lab.flickr.Util.JobRegister;
import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.ImageLoader;
import com.lab.flickr.network.Loader.LoaderListener;

public class FragImageLoader extends FragLoader {

	private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

	private ImageLoader[] imageLoaders = new ImageLoader[NUM_THREADS];

	public FragImageLoader() {
		Log.d(this.getClass().getSimpleName(), "Number of threads for image loading : " + NUM_THREADS);
		for (int i = 0; i < NUM_THREADS; i++) {
			imageLoaders[i] = new ImageLoader(JobRegister.Job.MAIN_IMAGES);
		}
	}

	@Override
	public void onStart() {
		for (ImageLoader loader : imageLoaders) {
			loader.setContext(getActivity().getBaseContext());
		}
		super.onStart();
	}

	@Override
	public void onStop() {
		JobRegister.removeJob(JobRegister.Job.MAIN_IMAGES);
		for (ImageLoader loader : imageLoaders) {
			loader.cancel(true);
		}
		super.onStop();
	}

	@Override
	public void setLoaderListener(LoaderListener listener) {
		for (ImageLoader loader : imageLoaders) {
			loader.setLoaderListener(listener);
		}
	}

	@Override
	public void cancel(boolean interrupt) {
		for (ImageLoader task : imageLoaders) {
			task.cancel(interrupt);
		}
	}

	@Override
	public void performLoadingTask(DataWrapper wrapper) {
		for (ImageLoader task : imageLoaders) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, wrapper);
		}
	}
}
