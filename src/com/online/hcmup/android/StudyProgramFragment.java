package com.online.hcmup.android;

import java.util.ArrayList;
import java.util.List;

import model.hcmup.DbHandler;
import model.hcmup.Semester;
import model.hcmup.StudyProgram;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.ApiListener;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StudyProgramFragment extends BaseFragment {
	Session session;
	DbHandler db;
	static boolean isLand;
	LinearLayout portLayout;
	ListView listLayout;
	FrameLayout detailsLayout;
	ArrayList<Semester> semesters;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		context.getActionBar().setDisplayHomeAsUpEnabled(true);
		context.getActionBar().setTitle(R.string.menu_2);
		db = DbHandler.getInstance(context);
		session = Session.getInstance(context);
		session.checkLogin();
		View view = inflater.inflate(R.layout.fragment_study_program,
				container, false);
		TextView studentID = (TextView) view.findViewById(R.id.studentID);
		TextView studentName = (TextView) view.findViewById(R.id.studentName);
		studentID.setText(session.getStudentID());
		studentName.setText(session.getStudentName());
		isLand = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

		if (isLand) {
			listLayout = (ListView) view.findViewById(R.id.list_layout);
			detailsLayout = (FrameLayout) view
					.findViewById(R.id.details_layout);
		} else
			portLayout = (LinearLayout) view.findViewById(R.id.port_layout);

		// Call api
		ApiConnect.callUrls(context, new ApiListener() {

			@Override
			public void onSuccess(Object json, boolean isArray) {

				if (isArray) {
					JSONArray datas = (JSONArray) json;
					try {
						for (int i = 0; i < datas.length(); i++) {
							JSONObject js = datas.getJSONObject(i);
							StudyProgram std = new StudyProgram(js.toString());
							db.addStudyProgram(std);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				loadContent();
			}

			@Override
			public void onFailure(int statusCode, String jsonString) {
				Utils.showError(context, statusCode);
				loadContent();
			}
		}, String.format(Link.STUDY_PROGRAM, session.getStudentID()));
		return view;
	}

	private void loadContent() {
		semesters = db.getAllSemester();
		final TermAdapter adapter = new TermAdapter(semesters);
		if (!isLand) {
			int adapterCount = adapter.getCount();
			portLayout.removeAllViews();
			for (int i = 0; i < adapterCount; i++) {
				View item = adapter.getView(i, null, null);
				portLayout.addView(item);
			}
		} else {
			ArrayAdapter<String> adt = new ArrayAdapter<String>(context,
					R.layout.row_left_list, R.id.txt_left,
					semesterListToStringList(semesters));
			listLayout.setAdapter(adt);
			listLayout
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long index) {
							setAdapterView(adapter, position);
						}
					});
			// Set default choice is the first item.
			listLayout.setItemChecked(0, true);
			setAdapterView(adapter, 0);
		}
	}

	private List<String> semesterListToStringList(List<Semester> sems) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < sems.size(); i++) {
			list.add(sems.get(i).getHeader());
		}
		return list;
	}

	private void setAdapterView(TermAdapter adapter, int position) {
		View item = adapter.getView(position, null, null);
		detailsLayout.removeAllViews();
		detailsLayout.addView(item);
	}

	public static class TermAdapter extends BaseAdapter {
		ArrayList<Semester> semesters;

		public TermAdapter(ArrayList<Semester> data) {
			semesters = data;
		}

		@Override
		public int getCount() {
			return semesters.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = context.getLayoutInflater().inflate(
					R.layout.fragment_study_program_term, null);

			TextView title = (TextView) row.findViewById(R.id.title);
			title.setText(semesters.get(position).getHeader());
			LinearLayout list = (LinearLayout) row
					.findViewById(R.id.stdprg_content);

			ArrayList<StudyProgram> studyPrograms = semesters.get(position).StudyProgram;
			ArrayList<KeyValuePair> data = new ArrayList<KeyValuePair>();
			for (int i = 0; i < studyPrograms.size(); i++) {
				data.add(new KeyValuePair(studyPrograms.get(i).CurriculumName,
						studyPrograms.get(i).Credits + ""));
			}

			KeyValueAdapter ad = new KeyValueAdapter(context, data,
					R.layout.row_key_value_divider, 0,
					R.color.key_study_program, R.color.value_study_program,
					R.color.divider_study_program, false);

			int adapterCount = ad.getCount();
			list.removeAllViews();
			for (int i = 0; i < adapterCount; i++) {
				View item = ad.getView(i, null, null);
				list.addView(item);
			}
			return row;
		}
	}
}
