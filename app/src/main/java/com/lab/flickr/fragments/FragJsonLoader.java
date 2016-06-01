package com.lab.flickr.fragments;

import com.lab.flickr.Util.JobRegister;
import com.lab.flickr.network.JsonLoader;
import com.lab.flickr.network.Loader;

import java.util.ArrayList;

public class FragJsonLoader extends FragLoader {

	@Override
	public void createLoaders(ArrayList<Loader> loaders) {
		loaders.add(new JsonLoader());
	}

	@Override
	public JobRegister.Job getJob() {
		return JobRegister.Job.JSON_LOADER;
	}
}
