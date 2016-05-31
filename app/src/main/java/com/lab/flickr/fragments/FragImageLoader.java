package com.lab.flickr.fragments;

import android.util.Log;

import com.lab.flickr.Util.JobRegister;
import com.lab.flickr.network.ImageLoader;
import com.lab.flickr.network.Loader;

import java.util.ArrayList;

public class FragImageLoader extends FragLoader {

	private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

	@Override
	public ArrayList<Loader> createLoaders() {
		Log.d(this.getClass().getSimpleName(), "Number of threads for image loading : " + NUM_THREADS);
		ArrayList<Loader> loaders = new ArrayList<>();
		for (int i = 0; i < NUM_THREADS; i++) {
			loaders.add(new ImageLoader(JobRegister.Job.MAIN_IMAGES));
		}
		return loaders;
	}

	@Override
	public JobRegister.Job getJob() {
		return JobRegister.Job.MAIN_IMAGES;
	}
}
