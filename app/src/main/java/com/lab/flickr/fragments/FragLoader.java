package com.lab.flickr.fragments;

import android.app.Fragment;
import android.os.Bundle;

import com.lab.flickr.network.DataWrapper;
import com.lab.flickr.network.Loader.LoaderListener;

public abstract class FragLoader extends Fragment {

	private DataWrapper dataWrapper;

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
		performLoadingTask(this.dataWrapper);
	}

	public abstract void setLoaderListener(LoaderListener listener);

	public abstract void cancel(boolean interrupt);

	public abstract void performLoadingTask(DataWrapper wrapper);
}
