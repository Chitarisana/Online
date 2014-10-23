package com.online.hcmup.android;

import java.util.ArrayList;
import java.util.List;

import model.hcmup.DbHandler;
import model.hcmup.Semester;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;
import utils.Session;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class StudentFeeFragment extends BaseFragment {
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
		context.getActionBar().setTitle(R.string.menu_7);
		db = DbHandler.getInstance(context);
		session = Session.getInstance(context);
		session.checkLogin();
		View view = inflater.inflate(R.layout.fragment_fee, container, false);
		TextView studentID = (TextView) view.findViewById(R.id.studentID);
		// TextView studentName = (TextView)
		// view.findViewById(R.id.studentName);
		studentID.setText(session.getStudentID());
		// studentName.setText(session.getStudentName());
		// isLand = context.getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE;

		// if (isLand) {
		// listLayout = (ListView) view.findViewById(R.id.list_layout);
		// detailsLayout = (FrameLayout) view
		// .findViewById(R.id.details_layout);
		// } else
		// portLayout = (LinearLayout) view.findViewById(R.id.port_layout);
		listLayout = (ListView) view.findViewById(R.id.fee_info);
		String[] list = new String[] { "Toán rời rạc", "Nấu ăn",
				"Tiếng Nhật trong giao tiếp" };

		listLayout.setAdapter(new ArrayAdapter<String>(context,
				R.layout.row_key_value, R.id.key, list));

		List<KeyValuePair> fee = new ArrayList<KeyValuePair>();
		fee.add(new KeyValuePair("Tên phí", "Toán rời rạc"));
		fee.add(new KeyValuePair("Số tiền nợ", "0"));
		fee.add(new KeyValuePair("Ngày ghi nợ", ""));
		fee.add(new KeyValuePair("Số tiền trả", ""));
		fee.add(new KeyValuePair("Ngày trả", ""));
		ListView details = (ListView) view.findViewById(R.id.port_layout);
		details.setAdapter(new KeyValueAdapter(context, fee, 0, 0, true));

		return view;
	}
}
