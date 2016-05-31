package com.lab.flickr.fragments;

import com.lab.flickr.Util.JobRegister;
import com.lab.flickr.network.JsonLoader;
import com.lab.flickr.network.Loader;

import java.util.ArrayList;

public class FragJsonLoader extends FragLoader {

	@Override
	public ArrayList<Loader> createLoaders() {
		ArrayList<Loader> loaders = new ArrayList<>();
		loaders.add(new JsonLoader());
		return loaders;
	}

	@Override
	public JobRegister.Job getJob() {
		return JobRegister.Job.JSON_LOADER;
	}
}
