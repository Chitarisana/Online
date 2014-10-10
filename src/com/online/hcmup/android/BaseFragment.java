package com.online.hcmup.android;

import utils.Utils;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

public class BaseFragment extends Fragment {
	static Activity context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		context = getActivity();
	}

	public BaseFragment() {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case android.R.id.home:
			Utils.hideKeyboard(context);
			context.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setOnFragment(String title) {
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setTitle(title);

		PrivateActivity.menuToggle.setDrawerIndicatorEnabled(false);
		PrivateActivity.mainLayout
				.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		setHasOptionsMenu(true);
	}

	public void setOnFragment(int title) {
		setOnFragment(getString(title));
	}

	public void setTitle(int id) {
		setTitle(getActivity().getString(id));
	}

	public void setTitle(CharSequence title) {
		getActivity().getActionBar().setTitle(title);
	}

	public static int getColor(int id) {
		return context.getResources().getColor(id);
	}
}