package com.lab.flickr.fragments.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lab.flickr.R;

import java.util.ArrayList;

/**
 * Created by Matt on 20/02/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

	private ArrayList<Bitmap> bitmaps = new ArrayList<>();

	public RecyclerViewAdapter(ArrayList<Bitmap> data) {
		this.bitmaps = data;
	}

	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View container = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_main_recyclerview_item, parent, false);
		RecyclerViewHolder holder = new RecyclerViewHolder(container);
		return holder;
	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {
		holder.getImageView().setImageBitmap(bitmaps.get(position));
	}

	@Override
	public int getItemCount() {
		return bitmaps.size();
	}
}
