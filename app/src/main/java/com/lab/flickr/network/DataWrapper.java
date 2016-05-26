package com.lab.flickr.network;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class DataWrapper implements Parcelable {

	private Map<Key, Object> values = new HashMap<>();

	public DataWrapper() {}

	public DataWrapper(Parcel in) {
		this.values = in.readHashMap(HashMap.class.getClassLoader());
	}

	public synchronized Object getValue(Key key) {
		return values.get(key);
	}

	public synchronized void setValue(Key key, Object object) {
		values.put(key, object);
	}

	public synchronized boolean contains(Key key) {
		return values.containsKey(key);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeMap(values);
	}

	public static final Parcelable.Creator<DataWrapper> CREATOR = new Parcelable.Creator<DataWrapper>() {

		@Override
		public DataWrapper createFromParcel(Parcel in) {
			return new DataWrapper(in);
		}

		@Override
		public DataWrapper[] newArray(int size) {
			return new DataWrapper[size];
		}
	};

	/**
	 * Contains all the keys that may be used with DataWrapper
	 */
	public enum Key {
		/**
		 * <b>Type : </b> String <br>
		 * Used to reference the datawrapper in parcelable format
		 */
		PARCEL_KEY,
		/**
		 * <b>Type : </b> BlockingQueue<DataWrapper> <br>
		 */
		QUEUE,
		URL,
		INDEX,
		BITMAP,
		JSON,
		/**
		 * <b>Type : </b> Object <br>
		 * Used by ImageLoader to signal finishing callbacks.
		 */
		POISON_PILL;
	}
}
