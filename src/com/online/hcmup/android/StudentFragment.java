package com.online.hcmup.android;

import java.util.ArrayList;
import java.util.List;

import model.hcmup.DbHandler;
import model.hcmup.StudentContact;
import model.hcmup.StudentCourse;
import model.hcmup.StudentInfo;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.Constant;
import utils.ICallback;
import utils.Key;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StudentFragment extends BaseFragment {
	public static int TYPE;
	Session session;
	Activity context;
	DbHandler db;
	ListView list;
	LinearLayout list1, list2;

	Button btnLeft, btnRight;
	View rootView;
	ImageView avatar;

	final int[] COLORS = new int[] { R.color.info, R.color.course,
			R.color.contact };
	final String[] LINKS = new String[] { Link.STUDENT_INFO,
			Link.STUDENT_COURSE, Link.STUDENT_CONTACT };
	final int[] BUTTON_NAMES = new int[] { R.string.key_info_title,
			R.string.key_course_title, R.string.key_contact_title };
	final int[] TITLES = new int[] { R.string.menu_1, R.string.menu_1,
			R.string.key_contact_title };
	final int[] LAYOUTS = new int[] { R.layout.frame_student_info,
			R.layout.frame_student_info, R.layout.frame_student_contact };

	public final String[] TAGS = new String[] { Constant.TAG_STUDENT_INFO,
			Constant.TAG_STUDENT_COURSE, Constant.TAG_STUDENT_CONTACT };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		context.getActionBar().setDisplayHomeAsUpEnabled(true);
		db = DbHandler.getInstance(context);
		session = Session.getInstance(context);
		session.checkLogin();

		setHasOptionsMenu(true);

		rootView = inflater
				.inflate(R.layout.fragment_student, container, false);
		avatar = (ImageView) rootView.findViewById(R.id.profile);
		btnLeft = (Button) rootView.findViewById(R.id.btn_left);
		btnRight = (Button) rootView.findViewById(R.id.btn_right);

		inflateChild(TYPE);

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_student, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentTransaction ft = context.getFragmentManager()
				.beginTransaction();
		switch (item.getItemId()) {
		case R.id.edit_pass:
			if (ApiConnect.isConnectingToInternet(context)) {
				ft.replace(R.id.sub_content, new EditPasswordFragment(),
						Constant.TAG_STUDENT_CHANGE_PASSWORD)
						.addToBackStack(null).commit();
			} else
				Utils.showConnectionError(context);
			return true;
		case R.id.edit_contact:
			if (ApiConnect.isConnectingToInternet(context)) {
				ft.replace(R.id.sub_content, new EditContactFragment(),
						Constant.TAG_STUDENT_EDIT_INFO).addToBackStack(null)
						.commit();
			} else
				Utils.showConnectionError(context);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Inflate new frame
	 */
	private void inflateChild(int type) {
		TYPE = type;

		ViewGroup sub = (ViewGroup) rootView.findViewById(R.id.student_sub);
		if (sub != null) {
			ViewGroup parent = (ViewGroup) sub.getParent();
			parent.removeView(sub);
		}

		FrameLayout frame = (FrameLayout) rootView
				.findViewById(R.id.student_content);
		LayoutInflater inflater = LayoutInflater.from(context);
		frame.addView(inflater.inflate(LAYOUTS[TYPE], frame, false));
		setControl();

		ApiConnect.callUrls(context, new ICallback() {

			@Override
			public void onSuccess(Object json, boolean isArray) {

				if (isArray) {
					JSONArray datas = (JSONArray) json;
					try {
						JSONObject obj = datas.getJSONObject(0);
						switch (TYPE) {
						case 0:
							final StudentInfo info = new StudentInfo(obj
									.toString());
							db.addStudentInfo(info);
							// Load Image
							ApiConnect.callImageUrl(context, new ICallback() {

								@Override
								public void onSuccess(Object json,
										boolean isArray) {
									loadImage();
								}

								@Override
								public void onFailure(int statusCode,
										String jsonString) {
									Utils.showToast(context, jsonString);
								}
							}, info.FileImage);
							break;
						case 1:
							StudentCourse course = new StudentCourse(obj
									.toString());
							db.addStudentCourse(course);
							break;
						case 2:
							StudentContact student = new StudentContact(obj
									.toString());
							db.addStudentContact(student);
							break;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				loadDb();
			}

			@Override
			public void onFailure(int statusCode, String jsonString) {
				loadDb();
			}
		}, String.format(LINKS[TYPE], session.getStudentID()));
	}

	private KeyValueAdapter dataToAdapter(String[] keys, String[] keys_vi,
			Object data, int layoutId) {
		String[] values = Utils.getValues(data, data.getClass(), keys);
		List<KeyValuePair> listAdapter = new ArrayList<KeyValuePair>();
		for (int i = 0; i < keys_vi.length; i++) {
			listAdapter.add(new KeyValuePair(keys_vi[i], values[i]));
		}
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return new KeyValueAdapter(context, listAdapter, layoutId, 0.75,
					null);
		} else
			return new KeyValueAdapter(context, listAdapter, layoutId, 0, null);
	}

	private void setAdapter(String[] keys, String[] keys_vi, Object data,
			ListView list) {
		list.setAdapter(dataToAdapter(keys, keys_vi, data, 0));
	}

	private void setAdapter(String[] keys, String[] keys_vi, Object data,
			LinearLayout list) {
		KeyValueAdapter adapter = dataToAdapter(keys, keys_vi, data,
				R.layout.row_key_value_divider);
		if (list.getChildCount() > 0)
			list.removeAllViews();

		for (int i = 0; i < adapter.getCount(); i++) {
			View item = adapter.getView(i, null, null);
			list.addView(item);
		}
	}

	/**
	 * Load local database to show in list view/linear layout
	 */
	private void loadDb() {
		switch (TYPE) {
		case 0:
			StudentInfo info = db.getStudentInfo(session.getStudentID());
			if (info != null) {
				setAdapter(Key.KEY_STUDENT_INFO, Key.KEY_STUDENT_INFO_VI, info,
						list);
			}
			break;
		case 1:
			StudentCourse course = db.getStudentCourse();
			if (course != null) {
				setAdapter(Key.KEY_STUDENT_COURSE, Key.KEY_STUDENT_COURSE_VI,
						course, list);
			}
			break;
		case 2:
			StudentContact contact = db.getStudentContact();
			if (contact != null) {
				setAdapter(Key.KEY_STUDENT_CONTACT_1,
						Key.KEY_STUDENT_CONTACT_1_VI, contact, list1);
				setAdapter(Key.KEY_STUDENT_CONTACT_2,
						Key.KEY_STUDENT_CONTACT_2_VI, contact, list2);
			}
			break;
		}
	}

	private void setTable(View rootView, int tvResId, int messId) {
		// Set Title of ActionBar
		context.getActionBar().setTitle(TITLES[TYPE]);

		// Set Title of ListView/LinearLayout
		TextView title = (TextView) rootView.findViewById(tvResId);
		title.setText(messId);
		title.setBackgroundColor(getColor(COLORS[TYPE]));

		// Set Button
		final int left = (TYPE + 1) % 3;
		final int right = (TYPE + 2) % 3;
		btnLeft.setBackgroundColor(getColor(COLORS[left]));
		btnRight.setBackgroundColor(getColor(COLORS[right]));
		btnLeft.setText(BUTTON_NAMES[left]);
		btnRight.setText(BUTTON_NAMES[right]);
		btnLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				inflateChild(left);
			}
		});
		btnRight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				inflateChild(right);
			}
		});
	}

	/**
	 * Setting controls for this fragment, include button, title, list view,
	 * linear layout...
	 */
	private void setControl() {
		loadImage();
		switch (TYPE) {
		case 0:
			list = (ListView) rootView.findViewById(R.id.content);
			setTable(rootView, R.id.title, R.string.key_info_title);
			break;
		case 1:
			list = (ListView) rootView.findViewById(R.id.content);
			setTable(rootView, R.id.title, R.string.key_course_title);
			break;
		case 2:
			list1 = (LinearLayout) rootView.findViewById(R.id.content1);
			list2 = (LinearLayout) rootView.findViewById(R.id.content2);
			setTable(rootView, R.id.title, R.string.key_contact_1_title);
			setTable(rootView, R.id.title2, R.string.key_contact_2_title);
			break;
		}
	}

	private void loadImage() {
		Bitmap bitmap = Utils.getBitmapImage(context);
		if (bitmap != null)
			avatar.setImageBitmap(bitmap);
	}
}