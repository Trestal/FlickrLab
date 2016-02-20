package com.lab.flickr.Util;

import android.os.Bundle;

import java.util.Objects;

/**
 * Created by Matt on 16/02/2016.
 */
public class BundleUtils {


	/**
	 * This method assumes relationship between key and value is one-to-one. This will return the first key that matches the value
	 */
	public static String getKeyByValue(Bundle bundle, Object value) {
		for (String key : bundle.keySet()) {
			if (Objects.equals(value, bundle.get(key))) {
				return key;
			}
		}
		return null;
	}
}
