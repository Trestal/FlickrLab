package com.lab.flickr.fragments.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lab.flickr.R;
import com.lab.flickr.fragments.interfaces.RecyclerViewOnItemClickListener;

import java.util.ArrayList;

/**
 * Created by Matt on 20/02/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

	private ArrayList<Bitmap> bitmaps = new ArrayList<>();
	private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

	public RecyclerViewAdapter(ArrayList<Bitmap> data, RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
		this.bitmaps = data;
		this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
	}

	@Override
	public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View container = LayoutInflater.from(parent.getContext()).inflate(R.layout.frag_main_recyclerview_item, parent, false);
		RecyclerViewHolder holder = new RecyclerViewHolder(container, recyclerViewOnItemClickListener);
		return holder;

	}

	@Override
	public void onBindViewHolder(RecyclerViewHolder holder, int position) {
		holder.getImageView().setImageBitmap(bitmaps.get(position));
	}

	@Override
	public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
		super.registerAdapterDataObserver(observer);
	}

	@Override
	public int getItemCount() {
		return bitmaps.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private View container;
		private ImageView imageView;
		private RecyclerViewOnItemClickListener listener;

		public RecyclerViewHolder(View itemView, RecyclerViewOnItemClickListener listener) {
			super(itemView);
			this.container = itemView;
			this.imageView = (ImageView) itemView.findViewById(R.id.frag_main_recyclerView_imageView);
			this.listener = listener;
			this.imageView.setOnClickListener(this);
		}

		public ImageView getImageView() {
			return imageView;
		}

		@Override
		public void onClick(View v) {
			this.listener.onClick(v, getAdapterPosition());
		}

		public View getViewHolderContainer() {
			return this.container;
		}
	}
}
