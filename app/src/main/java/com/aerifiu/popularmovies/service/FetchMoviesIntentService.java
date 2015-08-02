package com.aerifiu.popularmovies.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.aerifiu.popularmovies.util.AppConstants;
import com.aerifiu.popularmovies.util.MovieDataHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Network code adapted from the udacity Sunshine app
 */
public class FetchMoviesIntentService extends IntentService {

	public enum SortOrder {
		// note: query with vote_average.asc does not return anything - so i used descending
		POPULARITY("popularity.desc"), RATING("vote_average.desc");

		protected final String query;

		SortOrder(String query) {
			this.query = query;
		}

		public static SortOrder fromString(final String sortOrder){
			return sortOrder.equals(POPULARITY.toString()) ? POPULARITY : RATING;
		}
	}

	public static final String TAG = FetchMoviesIntentService.class.getSimpleName();
	public static final String DATA_LOADED = "intentMovieDataLoaded";
	public static final String DATA_LOADED_EXTRA = "intentDataLoadedJsonResponse";
	private static final String EXTRA_SORT_ORDER = "intentExtraSortOrder";

	public FetchMoviesIntentService() {
		super(FetchMoviesIntentService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "starting movie sync");

		final SortOrder sortOrder = (SortOrder) intent.getSerializableExtra(EXTRA_SORT_ORDER);
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		String jsonResponse = null;

		try {

			final String SORT_BY = "sort_by";
			final String API_KEY = "api_key";

			if(TextUtils.isEmpty(AppConstants.MOVIE_DB_API_KEY)){
				throw new IllegalArgumentException("add your api key");
			}

			Uri builtUri = Uri.parse(AppConstants.MOVIES_BASE_URL).buildUpon()
					.appendQueryParameter(SORT_BY, sortOrder.query)
					.appendQueryParameter(API_KEY, AppConstants.MOVIE_DB_API_KEY)
					.build();

			URL url = new URL(builtUri.toString());

			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream();
			StringBuilder buffer = new StringBuilder();
			if (inputStream == null) {
				// Nothing to do.
				return;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			if (buffer.length() == 0) {
				return;
			}
			jsonResponse = buffer.toString();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (urlConnection != null) {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (final IOException ignore) {
				}
			}
		}

		if (TextUtils.isEmpty(jsonResponse)) {
			return;
		}

		MovieDataHelper.getInstance().writeToFile(jsonResponse, getApplicationContext());

		Intent i = new Intent(DATA_LOADED);
		try {
			if (jsonResponse.getBytes("ISO-8859-1").length > 100000) { //100kb
				i.putExtra(DATA_LOADED_EXTRA, jsonResponse); // it should stay roughly under the limit for an extra
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(DATA_LOADED));
		Log.v(TAG, "movie sync finished");
	}

	public static Intent getIntent(Context context, SortOrder sortOrder) {
		Intent i = new Intent(context, FetchMoviesIntentService.class);
		i.putExtra(EXTRA_SORT_ORDER, sortOrder);
		return i;
	}
}
