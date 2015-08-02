package com.aerifiu.popularmovies.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.aerifiu.popularmovies.R;
import com.aerifiu.popularmovies.model.Result;
import com.aerifiu.popularmovies.util.AppConstants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MoviesAdapter extends BaseAdapter {

	private ArrayList<Result> dataSet = new ArrayList<>(30);
	private final int imageHeight;
	private Context context;

	public MoviesAdapter(Context context, int imageHeight) {
		this.context = context;
		this.imageHeight = imageHeight;
	}

	@Override
	public int getCount() {
		return dataSet.size();
	}

	@Override
	public Object getItem(int position) {
		if (dataSet != null && position < dataSet.size()) {
			return dataSet.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeight));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		} else {
			imageView = (ImageView) convertView;
		}

		final String posterPath = dataSet.get(position).getPosterPath();
		Glide.with(context).load(AppConstants.POSTER_URI + posterPath).placeholder(R.drawable.ic_shape_rect).centerCrop().into(imageView);

		return imageView;
	}

	public void swapDataSet(ArrayList<Result> entries) {
		dataSet.clear();
		dataSet.addAll(entries);
		notifyDataSetChanged();
	}

	private void printMovieList(ArrayList<Result> movies) {
		for (Result r : movies) {
			Log.d("ALER", r.getTitle() + " " + r.getPopularity() + " " + r.getVoteAverage());
		}
	}
}
