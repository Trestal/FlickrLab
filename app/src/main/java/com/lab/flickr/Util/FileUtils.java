package com.lab.flickr.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

	public static final String INTERNAL_PATH = "/tempImages/";

	public static void saveJpegToFile(Context context, Bitmap bitmap, String fileName) {
		FileOutputStream out = null;
		try {
			File dir = new File(context.getFilesDir().getAbsolutePath() + INTERNAL_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}
			if (!fileName.contains(".jpg")) {
				fileName = fileName + ".jpg";
			}
			File f = new File(dir, fileName);
			out = new FileOutputStream(f);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//TODO add checking for filepath and extension for image types (png, jpg etc)
	public static Bitmap loadBitmapFromFile(Context context, String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			return BitmapFactory.decodeFile(filePath);
		}
		return null;
	}

	public static void deleteDirectoryContents(String pathToDir) {
		File dir = new File(pathToDir);
		if (dir.exists()) {
			for (File file : dir.listFiles()) {
				if (!file.isDirectory()) {
					file.delete();
				}
			}
		}
	}
}
