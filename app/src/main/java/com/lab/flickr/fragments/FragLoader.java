package com.lab.flickr.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.lab.flickr.Util.JobRegister;
import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.ImageLoader;
import com.lab.flickr.network.Loader;
import com.lab.flickr.network.Loader.LoaderListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class FragLoader extends Fragment {

	public static final String TAG = FragLoader.class.getSimpleName();

	private DataWrapper dataWrapper;

	protected ArrayList<Loader> loaders;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		loaders = createLoaders();
		try {
			for (Loader loader : loaders) {
				loader.setLoaderListener((LoaderListener) context);
				loader.setContext(context);
				JobRegister.registerTask(getJob(), loader.getID());
			}
		} catch (ClassCastException e) {
			Log.wtf(TAG, "Context used for fragment creation must implement LoaderListener.", e);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			this.dataWrapper = bundle.getParcelable(DataWrapper.Key.PARCEL_KEY.name());
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		if (this.dataWrapper == null) {
			throw new IllegalArgumentException("Cannot perform loading task without a DataWrapper");
		}
		for (Loader loader : loaders) {
			loader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, dataWrapper);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		JobRegister.removeJob(JobRegister.Job.MAIN_IMAGES);
		for (Loader loader : loaders) {
			loader.cancel(true);
		}
	}

	public void cancel(boolean interrupt) {
		for (Loader loader : loaders) {
			loader.cancel(interrupt);
		}
	}

	public abstract ArrayList<Loader> createLoaders();

	public abstract JobRegister.Job getJob();

}
