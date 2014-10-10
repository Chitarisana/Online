package utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.TextView;

import com.online.hcmup.android.R;

public class DialogManager {
	/**
	 * <h2><i><b>public static ProgressDialog showDialog(Context context, String
	 * message) </b></i></h2>
	 * <p>
	 * Show progress dialog with <b>message</b>, using ProgressDialogCustom
	 * style.
	 * </p>
	 */
	public static ProgressDialog showDialog(Context context, String message) {
		ProgressDialog dialog = new ProgressDialog(context,
				R.style.ProgressDialogCustom);
		dialog.setMessage(message);
		dialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(false);
		dialog.setMax(100);
		dialog.show();
		return dialog;
	}

	/**
	 * <h2><i><b>public static ProgressDialog showDialog(Context context, int
	 * id) </b></i></h2>
	 * <p>
	 * Show progress dialog with message get from resource with <b>id</b>, using
	 * ProgressDialogCustom style.
	 * </p>
	 */
	public static ProgressDialog showDialog(Context context, int id) {
		return showDialog(context, context.getString(id));
	}

	/**
	 * <h2><i><b>public static ProgressDialog showLoadingDialog(Context context)
	 * </b></i></h2>
	 * <p>
	 * Show progress dialog with message "Loading" using ProgressDialogCustom
	 * style.
	 * </p>
	 */
	public static ProgressDialog showLoadingDialog(Context context) {
		return showDialog(context, R.string.noti_loading);
	}

	/**
	 * <h2><i><b>public static AlertDialog ShowAlertDialog(final Context
	 * context, String message, String posButton, String negButton, final
	 * IDialogCallback callback) </b></i></h2>
	 * <p>
	 * Show alert dialog with <b>message</b>, positiveButton, negativeButton
	 * using AlertDialogCustom style.
	 * </p>
	 * <p>
	 * Set <b>negButton = null</b> if don't want to show it.
	 * </p>
	 */
	public static AlertDialog showAlertDialog(final Context context,
			String message, String posButton, String negButton,
			final DialogListener callback) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context, R.style.AlertDialogCustom));
		builder.setMessage("");
		builder.setPositiveButton(posButton,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						callback.setOnPositiveButtonClicked();
					}
				});
		if (negButton != null)
			builder.setNegativeButton(negButton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		AlertDialog dialog = builder.show();
		TextView messageText = (TextView) dialog
				.findViewById(android.R.id.message);
		messageText.setGravity(Gravity.CENTER);
		int padding = context.getResources().getDimensionPixelSize(
				R.dimen.alert_dialog_padding);
		messageText.setPadding(padding, padding, padding, padding);
		messageText.setText(Utils.htmlParse(message));
		dialog.show();
		return dialog;
	}

	/**
	 * <h2><i><b>public static AlertDialog showAlertDialog(final Context
	 * context, int message, int posButton, int negButton, final IDialogCallback
	 * callback) </b></i></h2>
	 * <p>
	 * Show alert dialog with <b>message</b>, positiveButton, negativeButton
	 * using AlertDialogCustom style.
	 * </p>
	 * <p>
	 * Set <b>negButton = -1</b> if don't want to show it.
	 * </p>
	 */
	public static AlertDialog showAlertDialog(final Context context,
			int message, int posButton, int negButton,
			final DialogListener callback) {
		return showAlertDialog(context, context.getString(message),
				context.getString(posButton),
				negButton != -1 ? context.getString(negButton) : null, callback);
	}
}
