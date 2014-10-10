package com.online.hcmup.android;

import model.utils.DialogContent;
import utils.ApiCall;
import utils.DialogManager;
import utils.DialogListener;
import utils.Session;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Khikon
 * @description BaseActivity class to define some general functions will be used
 *              for overall of this application, include handler life cycle,
 *              dialog management... so on.
 */
public class BaseActivity extends Activity {

	protected final int TYPE_EXIT = 0;
	protected final int TYPE_INFO = 1;
	protected final int TYPE_ABOUT = 2;
	protected final int TYPE_LOGOUT = 3;
	public AlertDialog dialog;
	protected DialogContent[] dialogs;
	protected String TAG_LOADING = "Loading";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("oncreate of BaseActivity", "it means parent always run first!!!");
		dialogs = new DialogContent[] {
				new DialogContent("Exit", getString(R.string.exit_noti_detail),
						R.string.exit_noti_positive,
						R.string.exit_noti_negative, TYPE_EXIT),
				new DialogContent("Contact", getString(R.string.contact),
						R.string.ok, -1, TYPE_INFO),
				new DialogContent("About",
						getString(R.string.about_us_details), R.string.ok, -1,
						TYPE_INFO), };
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (int i = 0; i < dialogs.length; i++) {
			if (dialogs[i].isShowing()) {
				dismissDialog(outState, dialog, dialogs[i].getTag());
				dialogs[i].setShowing(false);
			}
		}
		dismissDialog(outState, ApiCall.dialog, TAG_LOADING);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		for (int i = 0; i < dialogs.length; i++) {
			if (savedInstanceState.getBoolean(dialogs[i].getTag())) {
				dialog = showAlertDialog(i);
			}
		}

		if (savedInstanceState.getBoolean(TAG_LOADING)) {
			ApiCall.dialog = DialogManager.showLoadingDialog(this);
		}
	}

	protected void dismissDialog(Bundle outState, Dialog dialog, String key) {
		if (dialog != null)
			if (dialog.isShowing()) {
				dialog.dismiss();
				outState.putBoolean(key, true);
			}
	}

	protected AlertDialog showAlertDialog(int position) {
		if (position < 0)
			return null;
		final DialogContent d = dialogs[position];
		if (d.getType() != position)
			return null;
		dialog = DialogManager.showAlertDialog(this, d.getMessage(),
				getString(d.getPosButton()),
				d.getNegButton() != -1 ? getString(d.getNegButton()) : null,
				new DialogListener() {

					@Override
					public void setOnPositiveButtonClicked() {
						dialog.dismiss();
						if (d.getType() == TYPE_LOGOUT) {
							Session session = Session
									.getInstance(getApplicationContext());
							session.logoutUser();
							finish();
						} else if (d.getType() == TYPE_EXIT)
							finish();
						else {
						}
					}
				});
		for (int j = 0; j < dialogs.length; j++) {
			dialogs[j].setShowing(false);
		}
		dialogs[position].setShowing(true);
		return dialog;
	}
}
