package com.online.hcmup.android;

import utils.Constant;
import utils.Session;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity {
	Session session;
	public static Activity context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		marqueeTitle(R.string.app_title);

		// Load Private Activity if logged
		session = Session.getInstance(this);
		if (session.isLoggedIn()) {
			Intent i = new Intent(this, PrivateActivity.class);
			startActivity(i);
			finish();
			return;
		}

		if (savedInstanceState == null) {
			replaceFragment();
			/*
			 * ApiConnect.callUrls(this, new ICallback() {
			 * 
			 * @Override public void onSuccess(Object json, boolean isArray) {
			 * // public news accept array, not object if (isArray) { JSONArray
			 * datas = (JSONArray) json; replaceFragment(datas.toString()); } }
			 * 
			 * @Override public void onFailure(int statusCode, String
			 * jsonString) { if (statusCode == ICallback.CONNECTION_ERROR) {
			 * Utils.showConnectionError(context); } else {
			 * Utils.showToast(context, jsonString); } } },
			 * String.format(Link.PUBLIC_NEWS, 1, 0));
			 */
		}
	}

	protected void marqueeTitle(int id) {
		// Set marquee title <-- dang mau`
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);
		LayoutInflater inflator = LayoutInflater.from(this);
		View v = inflator.inflate(R.layout.title_view, null);
		((TextView) v.findViewById(R.id.title)).setText(id);
		v.setSelected(true);
		v.requestFocus();
		this.getActionBar().setCustomView(v);
	}

	private void replaceFragment() {
		NewsListFragment fragment = new NewsListFragment();
		Bundle args = new Bundle();
		args.putInt(NewsListFragment.KEY_TYPE, NewsListFragment.TYPE_PUBLIC);
		// args.putString(NewsListFragment.KEY_JSON, json);
		fragment.setArguments(args);

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.listview, fragment, Constant.TAG_PUBLIC_NEWS_LIST)
				.disallowAddToBackStack().commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_login:
			// start Activity Login
			Intent i = new Intent(this, LoginActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		boolean nav = navDetailsToList();
		if (nav) {
			getFragmentManager().popBackStack();
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setHomeButtonEnabled(false);
			getActionBar().setTitle(R.string.app_name);
		} else
			showAlertDialog(TYPE_EXIT);
	}

	public boolean navDetailsToList() {
		NewsDetailsFragment frag = (NewsDetailsFragment) getFragmentManager()
				.findFragmentByTag(Constant.TAG_PUBLIC_NEWS_DETAILS);
		return frag != null && frag.isVisible();
	}
}
