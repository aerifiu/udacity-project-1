package com.aerifiu.popularmovies.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aerifiu.popularmovies.R;
import com.aerifiu.popularmovies.model.Result;
import com.aerifiu.popularmovies.util.AppConstants;
import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {

	public static final String TAG = DetailFragment.class.getSimpleName();
	private static final String BUNDLE_PARAM_RESULT = "fragmentBundleParamResult";
	private Result result;

	public DetailFragment() {
	}

	public static DetailFragment getInstance(Result result) {
		DetailFragment fragment = new DetailFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUNDLE_PARAM_RESULT, result);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		result = (Result) args.getSerializable(BUNDLE_PARAM_RESULT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View root = inflater.inflate(R.layout.fragment_detail, container, false);

		try {
			TextView textViewTitle = (TextView) root.findViewById(R.id.fragment_movies_detail_title);
			textViewTitle.setText(result.getTitle());

			TextView textViewDate = (TextView) root.findViewById(R.id.fragment_movies_detail_date);
			textViewDate.setText(result.getReleaseDate().substring(0, 4));

			TextView textViewVote = (TextView) root.findViewById(R.id.fragment_movies_detail_vote_avg);
			textViewVote.setText(String.valueOf(result.getVoteAverage()));

			TextView textViewOverview = (TextView) root.findViewById(R.id.fragment_movies_detail_overview);
			textViewOverview.setText(String.valueOf(result.getOverview()));

			ImageView imageViewPoster = (ImageView) root.findViewById(R.id.fragment_movies_detail_poster);
			Glide.with(getActivity()).load(AppConstants.POSTER_URI + result.getPosterPath()).placeholder(R.drawable.ic_shape_rect)
					.centerCrop().into(imageViewPoster);

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				getActivity().setTitle(getString(R.string.movie_details));

			} else {
				getActivity().setTitle(result.getTitle());
				root.findViewById(R.id.fragment_movies_detail_header).setVisibility(View.GONE);
			}

		} catch (Exception e) {
			// maybe we are dealing with malformed/missing json data on this movie
			// lets not show the user half empty screens and skip this movie
			Toast.makeText(getActivity(), getString(R.string.movie_details_unknown), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			getActivity().finish();
		}

		return root;
	}
}

