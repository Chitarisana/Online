package com.online.hcmup.android;

import java.util.Locale;

import model.hcmup.DbHandler;
import model.hcmup.RegisteredStudyUnit;
import model.hcmup.StudentContact;
import model.hcmup.StudentCourse;
import model.hcmup.StudentInfo;
import model.hcmup.StudyProgram;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.ApiListener;
import utils.Key;
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
			ApiConnect.callUrls(this, new ApiListener() {

				@Override
				public void onSuccess(int position, Object json, boolean isArray) {
					Utils.showToast(context, R.string.login_success);
					Session session = Session.getInstance(context);
					session.createLoginSession(studentID, password);

					// Initialize database
					initDb();
				}

				@Override
				public void onFailure(int position, int statusCode,
						String jsonString) {
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
		// Make sure that delete all old database before add new one
		// because of the errors can happen to last session if corrupted or
		// anything else.
		db.deleteAll();
		final Session session = Session.getInstance(context);
		String studentID = session.getStudentID();
		ApiConnect.callUrls(
				context,
				new ApiListener() {

					@Override
					public void onSuccess(int position, Object json,
							boolean isArray) {
						switch (position) {
						case 0:
							if (isArray) {
								try {
									JSONArray datas = (JSONArray) json;
									JSONObject obj = datas.getJSONObject(0);
									StudentInfo info = new StudentInfo(obj
											.toString());
									db.addStudentInfo(info);
									session.setStudentName(info.FullName);
									ApiConnect.callImageUrl(context,
											new ApiListener() {

												@Override
												public void onSuccess(
														int position,
														Object json,
														boolean isArray) {
												}

												@Override
												public void onFailure(
														int position,
														int statusCode,
														String jsonString) {
													Utils.showError(context,
															statusCode);
												}
											}, info.FileImage);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							break;
						case 1:
							if (isArray) {
								try {
									JSONArray datas = (JSONArray) json;
									JSONObject obj = datas.getJSONObject(0);
									StudentCourse course = new StudentCourse(
											obj.toString());
									db.addStudentCourse(course);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							break;
						case 2:
							if (isArray) {
								try {
									JSONArray datas = (JSONArray) json;
									JSONObject obj = datas.getJSONObject(0);
									StudentContact student = new StudentContact(
											obj.toString());
									db.addStudentContact(student);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							break;
						case 3:
							if (isArray) {
								JSONArray datas = (JSONArray) json;
								try {
									for (int i = 0; i < datas.length(); i++) {
										JSONObject js = datas.getJSONObject(i);
										StudyProgram std = new StudyProgram(js
												.toString());
										if (std.toArray() != null)
											db.addStudyProgram(std);
									}

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							break;
						case 4:
							break;
						case 5:
							if (isArray) {
								JSONArray datas = (JSONArray) json;
								try {
									for (int i = 0; i < datas.length(); i++) {
										JSONObject js = datas.getJSONObject(i);
										RegisteredStudyUnit reg = new RegisteredStudyUnit(
												js.toString());
										db.addRegisteredCurriculum(reg);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							break;
						case 6:
							if (isArray) {
								JSONArray datas = (JSONArray) json;
								try {
									JSONObject js = datas.getJSONObject(0);
									String year = js
											.getString(Key.KEY_TERM_YEAR[0]);
									String term = js
											.getString(Key.KEY_TERM_YEAR[1]);
									session.setCurrentTermYear(year, term);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							break;
						case 7:
							break;
						case 8:
							break;
						case 9:

							// Load PrivateActivity after init all
							Intent i = new Intent(context,
									PrivateActivity.class);
							startActivity(i);
							finish();
							MainActivity.context.finish();
							break;
						}
					}

					@Override
					public void onFailure(int position, int statusCode,
							String jsonString) {
						Utils.showError(context, statusCode);
					}
				},
				new String[] { String.format(Link.STUDENT_INFO, studentID),
						String.format(Link.STUDENT_COURSE, studentID),
						String.format(Link.STUDENT_CONTACT, studentID),
						String.format(Link.STUDY_PROGRAM, studentID),
						String.format(Link.REGISTERED_TERM_YEAR, studentID),
						String.format(Link.REGISTER_SCHEDULE, studentID),
						Link.CURRENT_TERM,
						String.format(Link.SCHEDULE_CALENDAR, studentID),
						String.format(Link.SCORE, studentID),
						String.format(Link.BEHAVIOR_SCORE, studentID),
						String.format(Link.SCORE_SUM, studentID) });
		// session student name
		// load all links
	}
}
