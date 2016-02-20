package com.lab.flickr.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.LoaderListener;

/**
 * Created by Matt on 18/02/2016.
 */
public abstract class FragLoader extends Fragment {

	DataWrapper dataWrapper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			this.dataWrapper = bundle.getParcelable(DataWrapper.PARCEL_KEY);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		performLoadingTask(this.dataWrapper);
	}

	public abstract void setLoaderListener(LoaderListener listener);
	public abstract void cancel(boolean interrupt);
	public abstract void performLoadingTask(DataWrapper wrapper);
}
