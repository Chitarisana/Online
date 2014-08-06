package com.online.hcmup.android;

import java.util.ArrayList;

import model.utils.MenuAdapter;
import utils.Constant;
import utils.Session;
import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class PrivateActivity extends BaseActivity {

	public static DrawerLayout mainLayout;
	public static ActionBarDrawerToggle menuToggle;
	private CharSequence mainTitle;
	public static CharSequence itemTitle, title;
	public static PrivateActivity context;
	// list of fragment TAG that will pop back when button back pressed.
	private final String[] fragTag = new String[] {
			Constant.TAG_PRIVATE_NEWS_DETAILS,
			Constant.TAG_STUDENT_CHANGE_PASSWORD,
			Constant.TAG_STUDENT_EDIT_INFO,
			Constant.TAG_REGISTER_CURRICULUM_RESULT,
			Constant.TAG_REGISTER_CURRICULUM_RESULT_ALL,
			Constant.TAG_REGISTER_CURRICULUM_DISACCUMULATED,
			Constant.TAG_REGISTER_CURRICULUM_REGISTER };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_private);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = this;

		// add slider bar
		mainTitle = getString(R.string.app_name);
		mainLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ListView menuLV = (ListView) findViewById(R.id.menu);

		String[] menuArray = getResources().getStringArray(R.array.menu_array);
		TypedArray iconArray = getResources().obtainTypedArray(
				R.array.menu_icon);
		ArrayList<SliderMenu> sliderMenu = new ArrayList<SliderMenu>();

		for (int i = 0; i < menuArray.length; i++) {
			sliderMenu.add(new SliderMenu(iconArray.getResourceId(i, -1),
					menuArray[i]));
		}
		iconArray.recycle();

		menuLV.setAdapter(new MenuAdapter(this, sliderMenu, mainLayout, menuLV));

		menuToggle = new ActionBarDrawerToggle(this, mainLayout,
				R.drawable.ic_menu, 0, 0) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(itemTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mainTitle);
				invalidateOptionsMenu();
			}
		};
		mainLayout.setDrawerListener(menuToggle);

		if (savedInstanceState == null) {
			loadMainFragment();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		title = getActionBar().getTitle();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (title != null)
			getActionBar().setTitle(title);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (menuToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		itemTitle = title;
		getActionBar().setTitle(itemTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		menuToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		menuToggle.onConfigurationChanged(newConfig);
		// setContentView(R.layout.activity_private);
		Log.d("Private Activity", "On Configuration Changed!!!");
		int currentOrient = context.getResources().getConfiguration().orientation;
		Session.getInstance(context).setOrientation(currentOrient);
	}

	@Override
	public void onBackPressed() {
		if (navBack()) {
			getFragmentManager().popBackStack();
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(itemTitle);
			menuToggle.setDrawerIndicatorEnabled(true);
			mainLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		} else
			showAlertDialog(TYPE_EXIT);
	}

	public void loadMainFragment() {
		itemTitle = title = getString(R.string.menu_0);
		setTitle(title);
		NewsListFragment fragment = new NewsListFragment();
		Bundle args = new Bundle();
		args.putInt(NewsListFragment.KEY_TYPE, NewsListFragment.TYPE_PRIVATE);
		// args.putString(NewsListFragment.KEY_JSON, json);
		fragment.setArguments(args);

		getFragmentManager()
				.beginTransaction()
				.replace(R.id.content, fragment, Constant.TAG_PRIVATE_NEWS_LIST)
				.disallowAddToBackStack().commit();
	}

	public boolean navBack() {
		for (int i = 0; i < fragTag.length; i++) {
			Fragment frag = getFragmentManager().findFragmentByTag(fragTag[i]);
			if (frag != null && frag.isVisible())
				return true;
		}
		return false;
	}

	public class SliderMenu {
		public int Icon;
		public String Text;

		public SliderMenu(int _icon, String _text) {
			Icon = _icon;
			Text = _text;
		}
	}
}
