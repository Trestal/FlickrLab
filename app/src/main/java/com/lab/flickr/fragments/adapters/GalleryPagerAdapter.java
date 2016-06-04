package com.lab.flickr.fragments.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab.flickr.R;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class GalleryPagerAdapter extends PagerAdapter {

	private ArrayList<View> views = new ArrayList<>();

	public GalleryPagerAdapter(Context context, ArrayList<Bitmap> images) {
		for (int i = 0, ii = images.size(); i < ii; i++) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.frag_main_viewpager_item, null);
			addView(view);
			updateViewBitmap(images.get(i), i);
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = views.get(position);
		container.addView(view);
		return view;
	}

	@Override
	public int getItemPosition(Object object) {
		int index = views.indexOf(object);
		return index == -1 ? POSITION_NONE : index;
	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(views.get(position));
	}

	public int addView(View view) {
		return addView(view, views.size());
	}

	public int addView(View view, int position) {
		views.add(position, view);
		return position;
	}

	public void updateViewBitmap(Bitmap bitmap, int position) {
		View view = views.get(position);
		ImageViewTouch iv = (ImageViewTouch) view.findViewById(R.id.frag_main_viewpager_item_imageView);
		iv.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
		iv.setImageBitmap(bitmap);
	}
}
