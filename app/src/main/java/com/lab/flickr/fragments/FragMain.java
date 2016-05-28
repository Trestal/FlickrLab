package com.lab.flickr.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.lab.flickr.R;
import com.lab.flickr.Util.FileUtils;
import com.lab.flickr.fragments.adapters.GalleryPagerAdapter;
import com.lab.flickr.fragments.adapters.RecyclerViewAdapter;
import com.lab.flickr.fragments.interfaces.RecyclerViewOnItemClickListener;

import java.io.File;
import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

public class FragMain extends Fragment implements ViewPager.OnPageChangeListener, RecyclerViewOnItemClickListener, OnChildAttachStateChangeListener {

	private ViewPager viewPager;
	private RecyclerView recyclerView;

	private RecyclerViewAdapter recyclerViewAdapter;

	private LinearLayoutManager recyclerLayoutManager;

	private ArrayList<Bitmap> data = new ArrayList<>();

	private int currentItemPos = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		File dir = new File(getActivity().getFilesDir().getAbsolutePath() + FileUtils.INTERNAL_PATH);
		if (dir.exists()) {
			for (File file : dir.listFiles()) {
				if (file.getName().contains(".jpg")) {
					data.add(FileUtils.loadBitmapFromFile(file.getAbsolutePath()));
				}
			}
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initViewPager(view);
		initRecyclerView(view);
	}

	private void initRecyclerView(View view) {
		recyclerView = (RecyclerView) view.findViewById(R.id.frag_main_recyclerView);
		//TODO Convert data to thumbnails so that each thumbnail is the same height/width
		recyclerViewAdapter = new RecyclerViewAdapter(data, this);
		recyclerViewAdapter.setHasStableIds(true);
		recyclerView.setAdapter(recyclerViewAdapter);
		recyclerView.addOnChildAttachStateChangeListener(this);
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			recyclerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
		} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			recyclerLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
		}
		recyclerView.setLayoutManager(recyclerLayoutManager);
	}

	private void initViewPager(View view) {
		viewPager = (ViewPager) view.findViewById(R.id.frag_main_viewPager);
		GalleryPagerAdapter galleryPagerAdapter = new GalleryPagerAdapter(getActivity(), data);
		viewPager.setAdapter(galleryPagerAdapter);
		viewPager.addOnPageChangeListener(this);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		int previousItemPos = currentItemPos;
		currentItemPos = position;
		int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) { //port
			portraitScroll(position);
		} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) { //land
			landscapeScroll(position);
		}
		changeItem(position, previousItemPos);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onClick(View view, int position) {
		viewPager.setCurrentItem(position);
	}

	@Override
	public void onChildViewAttachedToWindow(View view) {
		int childPosition = recyclerView.getChildAdapterPosition(view);
		if (childPosition == currentItemPos) {
			highlightItem(view);
		}
	}

	@Override
	public void onChildViewDetachedFromWindow(View view) {
		view.setBackground(null);
	}

	private void portraitScroll(int position) {
		scroll(position, true);
	}

	private void landscapeScroll(int position) {
		scroll(position, false);
	}

	private void scroll(int position, boolean isPortrait) {
		View view = recyclerView.getChildAt(0); //Only used to get the width of a view. They are all the same so this is safe
		if (view != null) {
			int width = isPortrait ? view.getWidth() : view.getHeight();
			float pos = isPortrait
					? recyclerView.computeHorizontalScrollOffset()
					: recyclerView.computeVerticalScrollOffset();
			float targetPos = position * width;
			float delta = (pos - targetPos) * -1;
			int x = isPortrait ? (int) delta : 0;
			int y = isPortrait ? 0 : (int) delta;
			recyclerView.smoothScrollBy(x, y);
		}
	}

	private void changeItem(int newItem, int oldItem) {
		ViewHolder holder = recyclerView.findViewHolderForItemId(recyclerViewAdapter.getItemId(newItem));
		if (holder instanceof RecyclerViewAdapter.RecyclerViewHolder) {
			highlightItem(((RecyclerViewAdapter.RecyclerViewHolder) holder).getViewHolderContainer());
		}
		ViewHolder oldHolder = recyclerView.findViewHolderForItemId(recyclerViewAdapter.getItemId(oldItem));
		if (oldHolder instanceof RecyclerViewAdapter.RecyclerViewHolder) {
			unHighlightItem(((RecyclerViewAdapter.RecyclerViewHolder) oldHolder).getViewHolderContainer());
		}
	}

	private void highlightItem(View view) {
		GradientDrawable border = new GradientDrawable();
		int c1 = getResources().getColor(R.color.recycler_highlight);
		int c2 = getResources().getColor(R.color.recycler_highlight_stroke);
		int c3 = c1;
		border.setColors(new int[] {c1, c2, c3});
		border.setCornerRadius(10.0f);
		view.setBackground(border);
	}

	private void unHighlightItem(View view) {
		view.setBackground(null);
	}
}
