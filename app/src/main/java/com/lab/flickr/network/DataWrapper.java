package com.lab.flickr.network;

import android.graphics.Bitmap;

/**
 * Created by Matt on 18/02/2016.
 */
public class DataWrapper {

	private int index;
	private String url;
	private Bitmap bitmap;

	public DataWrapper(int index, String url) {
		this.index = index;
		this.url = url;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getIndex() {
		return index;
	}

	public String getUrl() {
		return url;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
