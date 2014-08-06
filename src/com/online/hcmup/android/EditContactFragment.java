package com.online.hcmup.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import model.hcmup.DbHandler;
import model.hcmup.StudentContact;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.Errors;
import utils.ICallback;
import utils.Key;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EditContactFragment extends BaseFragment {
	Session session;
	Activity context;
	DbHandler db;
	View view;
	LinearLayout[] contents;
	Button btnOK, btnCancel;
	String[][] keys = new String[][] { Key.KEY_STUDENT_LOAD_EDITCONTACT_1_VI,
			Key.KEY_STUDENT_LOAD_EDITCONTACT_2_VI,
			Key.KEY_STUDENT_LOAD_EDITCONTACT_3_VI }, values;
	String studentID;
	StudentContact contact;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		session = Session.getInstance(context);
		session.checkLogin();
		setOnFragment(R.string.edit_contact_title);

		view = inflater.inflate(R.layout.fragment_edit_contact, container,
				false);

		contents = new LinearLayout[] {
				(LinearLayout) view.findViewById(R.id.edit_contact_1),
				(LinearLayout) view.findViewById(R.id.edit_contact_2),
				(LinearLayout) view.findViewById(R.id.edit_contact_3) };

		values = new String[keys.length][];

		studentID = session.getStudentID();
		btnOK = (Button) view.findViewById(R.id.btn_OK);
		btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendContact();
			}
		});
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		// Because don't have load contact function, it's just for current
		contact = DbHandler.getInstance(context).getStudentContact();
		String jsonString = contact.toString();
		try {
			JSONObject js = new JSONObject(jsonString);
			for (int j = 0; j < keys.length; j++) {
				String[] value = Utils.getValues(js, keys[j]).values;
				if (value != null)
					values[j] = value;
			}
			loadContact();
		} catch (JSONException e) {
			Utils.showError(context, Errors.DATA_ERROR);
			e.printStackTrace();
		}		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu.hasVisibleItems())
			menu.clear();
	}

	private String[] getValues(LinearLayout list) {
		int num = list.getChildCount();
		String[] values = new String[num];
		for (int i = 0; i < num; i++) {
			View view = list.getChildAt(i);
			EditText value = (EditText) view.findViewById(R.id.value);
			try {
				values[i] = URLEncoder.encode(value.getText().toString(),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				values[i] = "";
			}
		}
		return values;
	}

	private void loadContact() {
		for (int i = 0; i < contents.length; i++) {
			List<KeyValuePair> listAdapter = new ArrayList<KeyValuePair>();
			for (int j = 0; j < keys[i].length; j++) {
				listAdapter.add(new KeyValuePair(keys[i][j], values[i][j]));
			}

			KeyValueAdapter adapter = new KeyValueAdapter(context, listAdapter,
					R.layout.row_edit_contact, 0, true);

			contents[i].removeAllViews();
			for (int j = 0; j < adapter.getCount(); j++) {
				View item = adapter.getView(j, null, null);
				contents[i].addView(item);
			}
		}
	}

	private void sendContact() {
		for (int i = 0; i < contents.length; i++) {
			for (int j = 0; j < contents[i].getChildCount(); j++) {
				View v = contents[i].getChildAt(j);
				EditText value = (EditText) v.findViewById(R.id.value);
				Log.d("Value", value.getText() + "kk");
			}
		}
		ArrayList<String> values = new ArrayList<String>();
		values.add(studentID);
		for (int i = 0; i < keys.length; i++) {
			String[] value = getValues(contents[i]);
			for (int j = 0; j < value.length; j++) {
				values.add(value[j]);
			}
		}
		if (values.size() > 1) {
			ApiConnect.callUrls(context,
					new ICallback() {

						@Override
						public void onSuccess(Object json, boolean isArray) {
							// TODO save contact to db
							Utils.showToast(context,
									R.string.edit_contact_success);
							onBackPressed();
						}

						@Override
						public void onFailure(int statusCode, String jsonString) {
							Utils.showError(context, statusCode);
							onBackPressed();
						}
					}, String.format(Link.STUDENT_EDIT_INFO, (Object[]) values
							.toArray(new String[values.size()])));
		} else {
			Utils.showError(context, Errors.INPUT_ERROR);
			onBackPressed();
		}
	}

	private void onBackPressed() {
		Utils.hideKeyboard(context);
		context.onBackPressed();
	}
}
