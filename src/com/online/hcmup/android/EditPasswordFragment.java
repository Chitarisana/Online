package com.online.hcmup.android;

import utils.ApiConnect;
import utils.ICallback;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class EditPasswordFragment extends BaseFragment {

	Activity context;
	Session session;
	EditText oldPass, newPass, reNewPass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		session = Session.getInstance(context);
		session.checkLogin();
		setOnFragment(R.string.change_password_title);

		View view = inflater.inflate(R.layout.fragment_change_password,
				container, false);

		oldPass = (EditText) view.findViewById(R.id.txt_old);
		newPass = (EditText) view.findViewById(R.id.txt_new);
		reNewPass = (EditText) view.findViewById(R.id.txt_renew);

		reNewPass.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					actionEdit();
					return true;
				}
				return false;
			}
		});

		Button btnOK = (Button) view.findViewById(R.id.btn_OK);
		btnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				actionEdit();
			}
		});

		Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				context.onBackPressed();
			}
		});
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_null, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	private void actionEdit() {
		if (isEmpty(oldPass) || isEmpty(newPass) || isEmpty(reNewPass)) {
			Utils.showToast(context, R.string.edit_password_noti_null);
			return;
		}
		if (!getText(newPass).matches(getText(reNewPass))) {
			Utils.showToast(context, R.string.edit_password_noti_dismatch);
			return;
		}
		ApiConnect.callUrls(context,
				new ICallback() {

					@Override
					public void onSuccess(Object json, boolean isArray) {
						Utils.showToast(context,
								R.string.edit_password_noti_success);
						// edit in session or not???
						// need edit to make token ==> if needed
						context.onBackPressed();
					}

					@Override
					public void onFailure(int statusCode, String jsonString) {
						Utils.showToast(context, jsonString);
					}
				}, String.format(Link.STUDENT_CHANGE_PASSWORD,
						session.getStudentID(), getText(oldPass),
						getText(newPass)));
	}

	private boolean isEmpty(EditText editText) {
		return getText(editText).length() == 0;
	}

	private String getText(EditText editText) {
		return editText.getText().toString().trim();
	}
}
