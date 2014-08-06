package utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;

import com.online.hcmup.android.MainActivity;
import com.online.hcmup.android.PrivateActivity;

public class Session {

	private SharedPreferences pref;
	private Editor editor;
	private Context context;
	private static Session instance;
	private int PRIVATE_MODE = 0;
	private static final String PREF_NAME = "OnlineMobile";
	private static final String KEY_IS_LOGIN = "IsLoggedIn";
	private static final String KEY_ORIENTATION = "Orientation";
	private static final String KEY_FILE_IMAGE = "FileImage";
	private static final String KEY_IMAGE_DIRECTORY = "Directory";

	public static final String KEY_STUDENT_ID = "StudentID";
	public static final String KEY_PASSWORD = "Password";
	public static final String KEY_STUDENT_NAME = "StudentName";

	public static Session getInstance(Context context) {
		if (instance == null)
			return new Session(context);
		return instance;
	}

	public Session(Context context) {
		this.context = context;
		pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
		editor.commit();
	}

	/**
	 * Set current orientation
	 */
	public void setOrientation(int config) {
		editor.putInt(KEY_ORIENTATION, config);
		editor.commit();
	}

	/**
	 * Get saved orientation
	 */
	public int getOrientation() {
		return pref
				.getInt(KEY_ORIENTATION, Configuration.ORIENTATION_UNDEFINED);
	}

	/**
	 * Set File Directory Path
	 */
	public void setDirectoryPath(String path) {
		editor.putString(KEY_IMAGE_DIRECTORY, path);
		editor.commit();
	}

	/**
	 * Get File Directory Path
	 */
	public String getDirectoryPath() {
		return pref.getString(KEY_IMAGE_DIRECTORY, null);
	}

	/**
	 * Set File Image Name
	 */
	public void setImageName(String file) {
		editor.putString(KEY_FILE_IMAGE, file);
		editor.commit();
	}

	/**
	 * Get File Image Name
	 */
	public String getImageName() {
		return pref.getString(KEY_FILE_IMAGE, null);
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String studentID, String password) {
		// Storing login value as TRUE
		editor.putBoolean(KEY_IS_LOGIN, true);
		editor.putString(KEY_STUDENT_ID, studentID);
		editor.putString(KEY_PASSWORD, password);
		editor.commit();
	}

	/**
	 * Quick check for login
	 */
	public boolean isLoggedIn() {
		return pref.getBoolean(KEY_IS_LOGIN, false);
	}

	/**
	 * Check login user
	 */
	public void checkLogin() {
		if (!isLoggedIn()) {
			logoutUser();
		}
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(Class<?> cls) {
		// remember to clear all data and reset default values
		// TODO

		editor.clear();
		editor.commit();

		Intent i = new Intent(context, cls);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		// close PrivateActivity
		PrivateActivity.context.finish();
	}

	/**
	 * Clear session details to default Activity
	 * */
	public void logoutUser() {
		logoutUser(MainActivity.class);
	}

	/**
	 * Get Student ID
	 */
	public String getStudentID() {
		return pref.getString(KEY_STUDENT_ID, null);
	}

	/**
	 * Get Student Name
	 */
	public String getStudentName() {
		return pref.getString(KEY_STUDENT_NAME, null);
	}

}
