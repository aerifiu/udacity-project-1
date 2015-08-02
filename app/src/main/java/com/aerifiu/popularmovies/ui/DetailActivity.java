package com.aerifiu.popularmovies.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.aerifiu.popularmovies.R;
import com.aerifiu.popularmovies.model.Result;

public class DetailActivity extends AppCompatActivity {

	public static final String EXTRA_RESULT = "intentExtraResult";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		if (savedInstanceState == null) {
			try {
				getSupportActionBar().setHomeButtonEnabled(true);
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			final Result result = (Result) getIntent().getSerializableExtra(EXTRA_RESULT);
			getSupportFragmentManager().beginTransaction().replace(R.id.activity_detail_container, DetailFragment.getInstance
					(result)).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
