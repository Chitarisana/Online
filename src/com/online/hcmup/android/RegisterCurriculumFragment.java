package com.online.hcmup.android;

import java.util.ArrayList;
import java.util.HashMap;

import model.hcmup.Curriculum;
import model.hcmup.DbHandler;
import model.hcmup.RegisteredStudyUnit;
import model.hcmup.TermStudy;
import model.utils.CustomArrayAdapter;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.ApiListener;
import utils.Constant;
import utils.Errors;
import utils.Key;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RegisterCurriculumFragment extends BaseFragment {

	static Session session;

	String[] TAG = new String[] { Constant.TAG_REGISTER_CURRICULUM_RESULT,
			Constant.TAG_REGISTER_CURRICULUM_DISACCUMULATED,
			Constant.TAG_REGISTER_CURRICULUM_REGISTER };
	Fragment[] FRAGS = new Fragment[] { new ResultCurrentFragment(),
			new NotAccumulatedFragment(), new RegisterActionFragment() };

	public RegisterCurriculumFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		session = Session.getInstance(context);
		session.checkLogin();
		context.getActionBar().setDisplayHomeAsUpEnabled(true);
		context.getActionBar().setTitle(R.string.menu_3);

		View row = inflater.inflate(R.layout.fragment_register_curriculum,
				container, false);

		Button[] command = new Button[] {
				(Button) row.findViewById(R.id.btnResult),
				(Button) row.findViewById(R.id.btnDisaccumulate),
				(Button) row.findViewById(R.id.btnRegister) };

		for (int i = 0; i < command.length; i++) {
			final int position = i;
			command[position].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setHandlerButton(FRAGS[position], TAG[position]);
				}
			});
		}
		return row;
	}

	private void setHandlerButton(Fragment frag, String tag) {
		context.getFragmentManager().beginTransaction()
				.replace(R.id.sub_content, frag, tag).addToBackStack(null)
				.commit();
	}

	public static class ResultCurrentFragment extends BaseFragment {
		TermStudy current;
		ListView list, detailsPort;
		FrameLayout detailsLand;
		static boolean isLand;
		static int selected = 0;
		DbHandler db;

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.menu_registered, menu);
			menu.removeItem(R.id.action_view_current);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_view_all:
				// navigate to fragment view all
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.sub_content, new AllResultFragment(),
								Constant.TAG_REGISTER_CURRICULUM_RESULT_ALL)
						.addToBackStack(null).commit();
				return true;
			case android.R.id.home:
				// reset static variable
				selected = 0;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setOnFragment(R.string.register_curriculum_result_current);

			db = DbHandler.getInstance(context);
			session = Session.getInstance(context);
			session.checkLogin();

			View view = inflater.inflate(R.layout.fragment_registered_current,
					container, false);
			TextView studentID = (TextView) view.findViewById(R.id.studentID);
			TextView studentName = (TextView) view
					.findViewById(R.id.studentName);
			studentID.setText(session.getStudentID());
			studentName.setText(session.getStudentName());

			list = (ListView) view.findViewById(R.id.list_layout);
			isLand = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

			if (isLand) {
				detailsLand = (FrameLayout) view
						.findViewById(R.id.details_layout);
			} else {
				detailsPort = (ListView) view.findViewById(R.id.reg_details);
			}

			// Call API
			ApiConnect.callUrls(
					context,
					new ApiListener() {

						@Override
						public void onSuccess(int position, Object json,
								boolean isArray) {

							if (isArray) {
								JSONArray datas = (JSONArray) json;
								try {
									if (position == 0) {
										for (int i = 0; i < datas.length(); i++) {
											JSONObject js = datas
													.getJSONObject(i);
											RegisteredStudyUnit reg = new RegisteredStudyUnit(
													js.toString());
											db.addRegisteredCurriculum(reg);
										}
									} else {
										JSONObject js = datas.getJSONObject(0);
										String year = js
												.getString(Key.KEY_TERM_YEAR[0]);
										String term = js
												.getString(Key.KEY_TERM_YEAR[1]);
										session.setCurrentTermYear(year, term);
										loadContent();
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onFailure(int position, int statusCode,
								String jsonString) {
							Utils.showError(context, statusCode);
							loadContent();
						}
					},
					new String[] {
							String.format(Link.REGISTER_SCHEDULE,
									session.getStudentID()), Link.CURRENT_TERM });

			return view;
		}

		private void loadContent() {
			HashMap<String, String> crrTermYear = session.getCurrentTermYear();
			String termID = crrTermYear.get(Session.KEY_TERM_ID);
			String year = crrTermYear.get(Session.KEY_YEAR_STUDY);
			if (termID == null || year == null) {
				Utils.showError(context, Errors.NULL_ERROR);
				return;
			}
			ArrayList<RegisteredStudyUnit> regs = db
					.getByTermYear(year, termID);
			if (regs.size() == 0) {
				Utils.showError(context, Errors.NULL_ERROR);
				return;
			}
			current = new TermStudy(year, termID, regs);

			CustomArrayAdapter<String> adapter = new CustomArrayAdapter<String>(
					context, isLand ? R.layout.row_left_list
							: R.layout.row_key_value, isLand ? R.id.tv_left
							: R.id.key, current.getCurrisName(), R.color.main);

			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View v,
						int position, long arg3) {
					selected = list.getCheckedItemPosition();
					setDetails(position);
				}
			});

			list.setItemChecked(selected, true);
			setDetails(selected);
		}

		private void setDetails(int position) {
			String[] keys = Key.KEY_REGISTER_VIEW;
			RegisteredStudyUnit std = current.StudyUnit.get(position);
			String[] values = new String[] { std.CurriculumName,
					std.Credits + "", std.Informations, std.ProfessorName };

			ArrayList<KeyValuePair> data = new ArrayList<KeyValuePair>();
			for (int i = 0; i < keys.length; i++) {
				data.add(new KeyValuePair(keys[i], values[i]));
			}
			KeyValueAdapter adapter = new KeyValueAdapter(context, data,
					isLand ? R.layout.row_key_value_divider : 0, 0,
					R.color.key_study_program, R.color.value_study_program,
					R.color.divider_study_program, true);
			if (isLand) {
				View view = context.getLayoutInflater().inflate(
						R.layout.fragment_study_program_term, null);

				RelativeLayout header = (RelativeLayout) view
						.findViewById(R.id.header);
				header.setBackgroundColor(context.getResources().getColor(
						R.color.registered));

				TextView title = (TextView) view.findViewById(R.id.title);
				title.setText(R.string.curriculum_id);
				TextView credit = (TextView) view.findViewById(R.id.credit);
				credit.setText(std.CurriculumID);

				LinearLayout list = (LinearLayout) view
						.findViewById(R.id.stdprg_content);

				int adapterCount = adapter.getCount();
				list.removeAllViews();
				for (int i = 0; i < adapterCount; i++) {
					View item = adapter.getView(i, null, null);
					list.addView(item);
				}
				detailsLand.removeAllViews();
				detailsLand.addView(view);
			} else {
				detailsPort.setAdapter(adapter);
				// Setting height
				int height = 0;
				for (int i = 0; i < adapter.getCount(); i++) {
					height += adapter.getMeasureHeight(i);
				}
				DisplayMetrics displaymetrics = new DisplayMetrics();
				context.getWindowManager().getDefaultDisplay()
						.getMetrics(displaymetrics);
				int maxHeight = displaymetrics.heightPixels / 5;

				if (height > maxHeight) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, maxHeight);
					params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					detailsPort.setLayoutParams(params);
				}
			}
		}
	}

	public static class AllResultFragment extends BaseFragment {
		public static LinearLayout termLayout;
		public static ArrayList<TermStudy> TermStudies;

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			inflater.inflate(R.menu.menu_registered, menu);
			menu.removeItem(R.id.action_view_all);
			// -> if in view current: remove view current
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_view_current:
				// back to view current
				context.onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setOnFragment(R.string.register_curriculum_result_registered);

			View row = inflater.inflate(R.layout.fragment_study_program,
					container, false);
			TextView stdID = (TextView) row.findViewById(R.id.studentID);
			TextView stdName = (TextView) row.findViewById(R.id.studentName);

			stdID.setText(session.getStudentID());
			stdName.setText(session.getStudentName());
			termLayout = (LinearLayout) row.findViewById(R.id.port_layout);
			// call api

			return row;
		}

		/*
		 * public static void LoadFragment() { ResultAdapter adapter = new
		 * ResultAdapter(); int adapterCount = adapter.getCount();
		 * termLayout.removeAllViews(); for (int i = 0; i < adapterCount; i++) {
		 * View item = adapter.getView(i, null, null); termLayout.addView(item);
		 * } }
		 */

		/*
		 * public static class ResultAdapter extends BaseAdapter {
		 * 
		 * public ResultAdapter() { }
		 * 
		 * @Override public int getCount() { return TermStudies.size(); }
		 * 
		 * @Override public Object getItem(int position) { return position; }
		 * 
		 * @Override public long getItemId(int position) { return position; }
		 * 
		 * @Override public View getView(final int position, View convertView,
		 * ViewGroup parent) { View row = context.getLayoutInflater().inflate(
		 * R.layout.fragment_study_program_term, null);
		 * 
		 * TextView title = (TextView) row.findViewById(R.id.title);
		 * title.setText(TermStudies.get(position).getHeader()); RelativeLayout
		 * header = (RelativeLayout) row .findViewById(R.id.header);
		 * header.setBackgroundColor(context.getResources().getColor(
		 * R.color.titleRegisterColor)); LinearLayout list = (LinearLayout) row
		 * .findViewById(R.id.content); ArrayList<JSONType> data = new
		 * ArrayList<JSONType>(); ArrayList<RegisteredStudyUnit> studyUnits =
		 * TermStudies .get(position).StudyUnit; for (int i = 0; i <
		 * studyUnits.size(); i++) { data.add(new
		 * JSONType(studyUnits.get(i).CurriculumName, studyUnits.get(i).Credits
		 * + "")); } KeyValueAdapter ad = new KeyValueAdapter(activity, data,
		 * R.layout.row_study_program_term_details, false);
		 * 
		 * final int adapterCount = ad.getCount(); list.removeAllViews(); for
		 * (int i = 0; i < adapterCount; i++) { View item = ad.getView(i, null,
		 * null); list.addView(item); } return row; } }
		 */
	}

	public static class NotAccumulatedFragment extends BaseFragment {
		public static ArrayList<Curriculum> curris;
		public static LinearLayout content, termLayout;
		static View subView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setOnFragment(R.string.register_curriculum_disaccumulate);

			View row = inflater.inflate(R.layout.fragment_study_program,
					container, false);
			TextView stdentID = (TextView) row.findViewById(R.id.studentID);
			TextView stdentName = (TextView) row.findViewById(R.id.studentName);

			stdentID.setText(session.getStudentID());
			stdentName.setText(session.getStudentName());

			termLayout = (LinearLayout) row.findViewById(R.id.port_layout);
			curris = new ArrayList<Curriculum>();

			subView = inflater.inflate(R.layout.fragment_study_program_term,
					container, false);
			TextView title = (TextView) subView.findViewById(R.id.title);
			title.setText(context.getResources().getString(
					R.string.register_curriculum_disaccumulate));
			RelativeLayout header = (RelativeLayout) subView
					.findViewById(R.id.header);
			// header.setBackgroundColor(context.getResources().getColor(
			// R.color.titleDisaccColor));
			content = (LinearLayout) subView.findViewById(R.id.stdprg_content);
			// call api
			return row;
		}
	}

	public static class RegisterActionFragment extends BaseFragment {

	}
}