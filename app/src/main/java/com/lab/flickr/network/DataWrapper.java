package com.lab.flickr.network;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matt on 18/02/2016.
 */
public class DataWrapper implements Parcelable {

	public static final String PARCEL_KEY = "DataWrapperKey";

	private int index;
	private String url;
	private Bitmap bitmap;

	public DataWrapper(int index, String url) {
		this.index = index;
		this.url = url;
	}

	public DataWrapper(Parcel in) {
		index = in.readInt();
		url = in.readString();
		bitmap = in.readParcelable(Bitmap.class.getClassLoader());
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(index);
		dest.writeString(url);
		dest.writeValue(bitmap);
	}

	public static final Parcelable.Creator<DataWrapper> CREATOR = new Parcelable.Creator<DataWrapper>() {

		@Override
		public DataWrapper createFromParcel(Parcel in) {
			return new DataWrapper(in);
		}

		@Override
		public DataWrapper[] newArray(int size) {
			return new DataWrapper[0];
		}
	};
}
