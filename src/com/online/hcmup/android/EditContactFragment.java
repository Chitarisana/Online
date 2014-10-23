package com.online.hcmup.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import model.hcmup.DbHandler;
import model.hcmup.StudentContact;
import model.utils.KeyValueAdapter;
import model.utils.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.Errors;
import utils.ApiListener;
import utils.Key;
import utils.Link;
import utils.Session;
import utils.Utils;
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
	LinearLayout[] contents;
	Button btnOK, btnCancel;
	String[][] keys = new String[][] { Key.KEY_STUDENT_LOAD_EDITCONTACT_1,
			Key.KEY_STUDENT_LOAD_EDITCONTACT_2,
			Key.KEY_STUDENT_LOAD_EDITCONTACT_3 };
	String[][] keys_vi = new String[][] {
			Key.KEY_STUDENT_LOAD_EDITCONTACT_1_VI,
			Key.KEY_STUDENT_LOAD_EDITCONTACT_2_VI,
			Key.KEY_STUDENT_LOAD_EDITCONTACT_3_VI };
	String[][] values;
	String studentID;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("Edit Contact", "on Create View");
		context = getActivity();
		session = Session.getInstance(context);
		session.checkLogin();
		setOnFragment(R.string.edit_contact_title);

		View view = inflater.inflate(R.layout.fragment_edit_contact, container,
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
				Utils.hideKeyboard(context);
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
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		// TODO: why call it at onCreateView is not work???????????
		// Because don't have load contact function, it's just for current
		StudentContact contact = DbHandler.getInstance(context)
				.getStudentContact();
		Log.d("Edit Contact", "load local contact");
		Log.d("Contact", contact.toString());

		try {
			JSONObject js = new JSONObject(contact.toString());
			for (int j = 0; j < keys.length; j++) {
				String[] value = Utils.getValues(js, keys[j]).values;
				if (value != null) {
					values[j] = value;
				}
			}
			loadContact();
		} catch (JSONException e) {
			Utils.showError(context, Errors.DATA_ERROR);
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (menu.hasVisibleItems())
			menu.clear();
	}

	private void loadContact() {
		Log.d("Edit Contact", "Load contact to view");
		for (int i = 0; i < contents.length; i++) {
			List<KeyValuePair> listAdapter = new ArrayList<KeyValuePair>();
			for (int j = 0; j < keys_vi[i].length; j++) {
				listAdapter.add(new KeyValuePair(keys_vi[i][j], values[i][j]));
			}

			KeyValueAdapter adapter = new KeyValueAdapter(context, listAdapter,
					R.layout.row_edit_contact, 0, true);

			if (contents[i].getChildCount() > 0)
				contents[i].removeAllViews();

			for (int j = 0; j < adapter.getCount(); j++) {
				View item = adapter.getView(j, null, null);
				contents[i].addView(item);
				Log.d("adapter", ((EditText) item.findViewById(R.id.value))
						.getText().toString());
			}
		}
	}

	private void sendContact() {
		ArrayList<String> values = new ArrayList<String>();
		values.add(studentID);
		for (int i = 0; i < contents.length; i++) {
			for (int j = 0; j < contents[i].getChildCount(); j++) {
				View view = contents[i].getChildAt(j);
				EditText value = (EditText) view.findViewById(R.id.value);
				try {
					values.add(URLEncoder.encode(value.getText().toString(),
							"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					values.add("");
				}
			}
		}
		Log.d("url",
				String.format(Link.STUDENT_EDIT_INFO,
						(Object[]) values.toArray(new String[values.size()])));
		if (values.size() > 1) {
			ApiConnect.callUrls(context, new ApiListener() {

				@Override
				public void onSuccess(int position, Object json, boolean isArray) {
					// Save contact to db
					// call new url because the result of past url is not same
					// the one in db
					ApiConnect.callUrls(context, new ApiListener() {

						@Override
						public void onSuccess(int position, Object json,
								boolean isArray) {
							if (isArray) {
								JSONArray datas = (JSONArray) json;
								try {
									JSONObject obj = datas.getJSONObject(0);
									StudentContact student = new StudentContact(
											obj.toString());
									DbHandler.getInstance(context)
											.addStudentContact(student);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							Utils.showToast(context,
									R.string.edit_contact_success);
							onBackPressed();
						}

						@Override
						public void onFailure(int position, int statusCode,
								String jsonString) {
							Utils.showError(context, statusCode);
							onBackPressed();
						}
					}, String.format(Link.STUDENT_CONTACT, studentID));
				}

				@Override
				public void onFailure(int position, int statusCode,
						String jsonString) {
					Utils.showError(context, statusCode);
					onBackPressed();
				}

			}, String.format(Link.STUDENT_EDIT_INFO,
					(Object[]) values.toArray(new String[values.size()])));
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
