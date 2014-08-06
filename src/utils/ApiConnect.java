package utils;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ApiConnect {

	private static boolean isReloadable(Context context, ICallback callback) {
		Session session = Session.getInstance(context);
		int oldOrient = session.getOrientation();
		int currentOrient = context.getResources().getConfiguration().orientation;
		session.setOrientation(currentOrient);
		Log.d("Orientation",oldOrient+"==>"+currentOrient);
		
		if (currentOrient != oldOrient
				&& oldOrient != Configuration.ORIENTATION_UNDEFINED) {
			callback.onFailure(Errors.DISABLE_RELOAD, null);
			return false;
		} else if (!isConnectingToInternet(context)) {
			callback.onFailure(Errors.CONNECTION_ERROR, null);
			return false;
		}
		return true;
	}

	public static void callUrls(Context context, ICallback callback,
			String... strs) {
		if (isReloadable(context, callback)) {
			URL[] urls = new URL[strs.length];
			for (int i = 0; i < strs.length; i++) {
				try {
					urls[i] = new URL(strs[i]);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			new ApiCall(context, callback).execute(urls);
		}
	}

	public static void callImageUrl(Context context, ICallback callback,
			String str) {
		if (isReloadable(context, callback)) {
			try {
				URL url = new URL(str);
				new ImageCall(context, callback).execute(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Check available connection.
	 * 
	 * @param context
	 * @return <li>Return true if have at least 1 available connection.</li> <li>
	 *         Return false if there is no available network connection.</li>
	 */
	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null)
				return true;
		}
		return false;
	}
}