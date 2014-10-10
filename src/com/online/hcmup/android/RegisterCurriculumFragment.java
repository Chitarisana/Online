package com.online.hcmup.android;

import java.util.ArrayList;

import model.hcmup.Curriculum;
import model.hcmup.TermStudy;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;
import utils.Constant;
import utils.Session;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
				.replace(R.id.content, frag, tag).addToBackStack(null).commit();
	}

	public static class ResultCurrentFragment extends BaseFragment {
		public static TermStudy current;
		static ListView list, details;

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
						.replace(R.id.content, new AllResultFragment(),
								Constant.TAG_REGISTER_CURRICULUM_RESULT_ALL)
						.addToBackStack(null).commit();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setOnFragment(R.string.register_curriculum_result_current);

			View row = inflater.inflate(R.layout.fragment_registered_current,
					container, false);

			TextView stdName = (TextView) row.findViewById(R.id.studentName);
			stdName.setText(session.getStudentName());
			TextView stdID = (TextView) row.findViewById(R.id.studentID);
			stdID.setText(session.getStudentID());

			details = (ListView) row.findViewById(R.id.reg_details);
			list = (ListView) row.findViewById(R.id.reg_content);

			/*
			 * DisplayMetrics displaymetrics = new DisplayMetrics();
			 * context.getWindowManager().getDefaultDisplay()
			 * .getMetrics(displaymetrics); LinearLayout.LayoutParams params =
			 * new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT, (int)
			 * (displaymetrics.heightPixels / 5));
			 * 
			 * list.setLayoutParams(params);
			 */
			ArrayAdapter<String> adt = new ArrayAdapter<String>(context,
					R.layout.row_key_value, R.id.key, new String[] { "test 1",
							"test 2", "test 3", "test 2", "test 3", "test 2",
							"test 3" });
			if (adt.getCount() > 5) {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, 160);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				details.setLayoutParams(params);
			}
			details.setAdapter(adt);

			// call api
			return row;
		}

		/*public static void LoadFragment() {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					R.layout.row_registered_current, R.id.curriName,
					getCurrisName(current));
			list.setAdapter(adapter);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterView, View v,
						int position, long arg3) {
					v.setSelected(true);
					// get Details of that curriculum
					ArrayList<KeyValuePair> data = new ArrayList<KeyValuePair>();
					data.add(new KeyValuePair("Tên học phần", current.StudyUnit
							.get(position).CurriculumName));
					data.add(new KeyValuePair("Số tín chỉ", current.StudyUnit
							.get(position).Credits + ""));
					data.add(new KeyValuePair("Ngày học", current.StudyUnit
							.get(position).Informations));
					data.add(new KeyValuePair("Giảng viên", current.StudyUnit
							.get(position).ProfessorName));
					KeyValueAdapter adapter = new KeyValueAdapter(context,
							data, R.layout.row_study_program_term_details, null);
					details.setAdapter(adapter);
				}
			});
		}

		protected static ArrayList<String> getCurrisName(TermStudy target) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < target.StudyUnit.size(); i++)
				list.add(target.StudyUnit.get(i).CurriculumName);
			return list;
		}*/

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