package com.aerifiu.popularmovies.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aerifiu.popularmovies.R;
import com.aerifiu.popularmovies.model.Result;
import com.aerifiu.popularmovies.service.FetchMoviesIntentService;
import com.aerifiu.popularmovies.util.AppConstants;
import com.aerifiu.popularmovies.util.MovieDataHelper;

public class OverviewFragment extends Fragment implements AdapterView.OnItemClickListener {

	public static final String TAG = OverviewFragment.class.getSimpleName();
	private static final IntentFilter FETCH_MOVIES_BROADCAST = new IntentFilter(FetchMoviesIntentService.DATA_LOADED);

	private FetchMoviesIntentService.SortOrder sortOrder;
	private MoviesAdapter adapter;
	private MenuItem menuItemMostPop;
	private MenuItem menuItemRating;

	public OverviewFragment() {
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!getActivity().isFinishing() && adapter != null) {

				final String jsonResponse = intent.getStringExtra(FetchMoviesIntentService.DATA_LOADED_EXTRA);
				if (TextUtils.isEmpty(jsonResponse)) {
					adapter.swapDataSet(MovieDataHelper.getInstance().getDataSetFromFile(context));
				} else {
					adapter.swapDataSet(MovieDataHelper.getInstance().getDataSetFromJsonString(jsonResponse));
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		final String sortOrderStr = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(AppConstants
						.SHARED_PREF_SORT_ORDER,
				FetchMoviesIntentService.SortOrder.POPULARITY.toString());
		sortOrder = FetchMoviesIntentService.SortOrder.fromString(sortOrderStr);

		if (savedInstanceState == null) {
			getActivity().startService(FetchMoviesIntentService.getIntent(getActivity(), sortOrder));
		}

	}

	@Override
	public void onStart() {
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, FETCH_MOVIES_BROADCAST);
		super.onStart();
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View root = inflater.inflate(R.layout.fragment_movies_overview, container, false);

		final GridView gridview = (GridView) root.findViewById(R.id.fragment_movies_gridview);
		gridview.setOnItemClickListener(this);

		// okay, this is a bit hacky but I don't know another way of fitting them exactly to the screen
		root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				// we need to wait until the view has been inflated to get sizes
				final int width = (int) (Math.ceil(root.getWidth() * 0.5f));
				final int height = (int) (Math.ceil(root.getHeight() * 0.5f));
				adapter = new MoviesAdapter(getActivity(), height > width ? height : width);
				gridview.setAdapter(adapter);
				// load the cached data for better ux
				adapter.swapDataSet(MovieDataHelper.getInstance().getDataSetFromFile(getActivity()));
			}
		});

		return root;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Result result = (Result) adapter.getItem((int) id);

		Intent i = new Intent(getActivity(), DetailActivity.class);
		i.putExtra(DetailActivity.EXTRA_RESULT, result);
		getActivity().startActivity(i);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
		menuItemMostPop = menu.findItem(R.id.action_most_popular);
		menuItemRating = menu.findItem(R.id.action_highest_rated);
		updateMenuActionVisibility(sortOrder);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();

		switch (id) {
		case R.id.action_most_popular:
			handleAction(FetchMoviesIntentService.SortOrder.POPULARITY);
			return true;
		case R.id.action_highest_rated:
			handleAction(FetchMoviesIntentService.SortOrder.RATING);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void handleAction(final FetchMoviesIntentService.SortOrder sortOrder) {
		getActivity().startService(FetchMoviesIntentService.getIntent(getActivity(), sortOrder));
		updateMenuActionVisibility(sortOrder);
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString(AppConstants.SHARED_PREF_SORT_ORDER, sortOrder
				.toString()).commit();
	}

	private void updateMenuActionVisibility(FetchMoviesIntentService.SortOrder sortOrder) {
		switch (sortOrder) {
		case POPULARITY:
			menuItemMostPop.setVisible(false);
			menuItemRating.setVisible(true);
			break;
		case RATING:
			menuItemMostPop.setVisible(true);
			menuItemRating.setVisible(false);
			break;
		}
	}
}
