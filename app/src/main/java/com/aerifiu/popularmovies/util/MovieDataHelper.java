package com.aerifiu.popularmovies.util;

import android.content.Context;

import com.aerifiu.popularmovies.model.MovieDbResponse;
import com.aerifiu.popularmovies.model.Result;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MovieDataHelper {

	private final Object mutex = new Object();
	private static final String FILE_NAME = "movie_data.json";
	private static MovieDataHelper instance;
	private Gson gson;

	private MovieDataHelper() {
		gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
	}

	public static MovieDataHelper getInstance() {
		if (instance == null) {
			synchronized (MovieDataHelper.class) {
				if (instance == null) {
					instance = new MovieDataHelper();
				}
			}
		}
		return instance;
	}

	public ArrayList<Result> getDataSetFromFile(Context context) {
		final String json = readFromFile(context);
		return getDataSetFromJsonString(json);
	}

	public ArrayList<Result> getDataSetFromJsonString(final String json) {
		try {
			final MovieDbResponse response = gson.fromJson(json, MovieDbResponse.class);
			return new ArrayList<>(response.getResults());
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public String readFromFile(Context context) {
		synchronized (mutex) {
			final File file = new File(context.getFilesDir(), FILE_NAME);
			StringBuilder stringBuilder = new StringBuilder();

			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String line;

				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				bufferedReader.close();
			} catch (IOException ignore) {
			}

			return stringBuilder.toString();
		}
	}

	public void writeToFile(String json, Context context) {
		synchronized (mutex) {
			BufferedWriter bufferedWriter = null;
			try {
				final File file = new File(context.getFilesDir(), FILE_NAME);
				bufferedWriter = new BufferedWriter(new FileWriter(file));
				bufferedWriter.write(json);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bufferedWriter != null) {
						bufferedWriter.close();
					}
				} catch (Exception ignore) {
				}
			}
		}
	}
}
