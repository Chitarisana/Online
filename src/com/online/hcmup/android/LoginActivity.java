package com.online.hcmup.android;

import java.util.Locale;

import model.hcmup.DbHandler;
import utils.ApiConnect;
import utils.ICallback;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class LoginActivity extends BaseActivity {
	EditText txtUsername, txtPassword;
	Context context;
	DbHandler db;
	Button btnLogin;
	TextView forgot;
	LinearLayout mainLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = this;
		db = DbHandler.getInstance(context);
		mainLayout = (LinearLayout) findViewById(R.id.main_layout);
		Utils.setMaxWidth(mainLayout);

		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		txtPassword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					login(txtUsername, txtPassword);
					return true;
				}
				return false;
			}
		});
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				login(txtUsername, txtPassword);
			}
		});
		forgot = (TextView) findViewById(R.id.forgot_password);
		forgot.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog = showAlertDialog(TYPE_INFO);
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		Utils.hideKeyboard(this);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/*
	 * Check null, trim space then send request to login
	 */
	private void login(EditText id, EditText pass) {
		final String studentID = id.getText().toString().trim()
				.toUpperCase(Locale.US);
		final String password = pass.getText().toString().trim();
		if (studentID.length() > 0 && password.length() > 0) {
			ApiConnect.callUrls(this, new ICallback() {

				@Override
				public void onSuccess(Object json, boolean isArray) {
					Utils.showToast(context, R.string.login_success);
					Session session = Session.getInstance(context);
					session.createLoginSession(studentID, password);

					// Initialize database
					initDb();

					// Load PrivateActivity
					Intent i = new Intent(context, PrivateActivity.class);
					startActivity(i);
					finish();
					MainActivity.context.finish();
				}

				@Override
				public void onFailure(int statusCode, String jsonString) {
					Utils.showToast(context, jsonString);
					txtUsername.requestFocus();
				}
			}, String.format(Link.LOGIN, studentID, password));
		} else {
			Utils.showToast(context, R.string.login_fail_noti_detail2);
			txtUsername.requestFocus();
		}
		txtUsername.setText("");
		txtPassword.setText("");
		Utils.hideKeyboard(this);
	}

	public void initDb() {
		// TODO
		// load all links
	}
}
