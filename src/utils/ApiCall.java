package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ApiCall extends AsyncTask<URL, Integer, Long> {
	private ICallback callback;
	private Context context;
	private List<String> results;
	public static ProgressDialog dialog;
	long timestamp;

	public ApiCall(Context context, ICallback callback) {
		this.context = context;
		this.callback = callback;
	}

	protected void onPreExecute() {
		// Cancel request if there is no network connection while loading
		if (!ApiConnect.isConnectingToInternet(context)) {
			cancel(true);
			callback.onFailure(Errors.CONNECTION_ERROR, null);
			return;
		}
		timestamp = System.currentTimeMillis();
		dialog = DialogManager.showLoadingDialog(context);
	}

	@Override
	protected Long doInBackground(URL... urls) {
		int count = urls.length;
		long totalSize = 0;
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			publishProgress((int) ((i / (float) count) * 100));
			StringBuilder resultBuilder = new StringBuilder();
			try {
				// Connect to server
				URL url = urls[i];
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setReadTimeout(Constant.READ_TIMEOUT);
				conn.setConnectTimeout(Constant.CONNECT_TIMEOUT);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				conn.connect();
				// Read all the text returned by the server
				// InputStreamReader reader = new
				// InputStreamReader(url.openStream());
				InputStreamReader reader = new InputStreamReader(
						conn.getInputStream());
				BufferedReader in = new BufferedReader(reader);
				String resultPiece;
				while ((resultPiece = in.readLine()) != null) {
					resultBuilder.append(resultPiece);
				}
				in.close();
				totalSize++;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// Utils.showConnectionError(context);
				e.printStackTrace();
			}
			// If cancel() is called, leave the loop early
			if (isCancelled()) {
				break;
			}
			resultList.add(resultBuilder.toString());
		}
		// Save the result
		this.results = resultList;
		return totalSize;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (dialog != null)
			dialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(Long result) {

		// Show the time had been used to connected to server.
		long time = System.currentTimeMillis() - timestamp;
		Toast.makeText(context,
				"Total time in process: " + time + " miliseconds.",
				Toast.LENGTH_SHORT).show();

		// Dismiss the dialog
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if (result <= 0) {
			Utils.showConnectionError(context);
			return;
		}
		// Handler results
		for (int i = 0; i < results.size(); i++) {
			try {
				String jsonString = results.get(i);
				JSONObject obj = new JSONObject(jsonString);
				int stt = obj.getInt(Key.KEY_STATUS);
				if (stt != 1) {
					callback.onFailure(Errors.DATA_ERROR,
							obj.getString(Key.KEY_ERRORS));
					continue;
				}
				if (obj.get(Key.KEY_DATA) == null) {
					Log.d("Check if array null", obj.toString());
					callback.onSuccess(null, false);
					continue;
				}
				try {
					JSONArray datas = obj.getJSONArray(Key.KEY_DATA);
					callback.onSuccess(datas, true);
				} catch (JSONException e) {
					e.printStackTrace();
					try {
						JSONObject data = obj.getJSONObject(Key.KEY_DATA);
						callback.onSuccess(data, false);
					} catch (JSONException e1) {
						e1.printStackTrace();
						callback.onFailure(Errors.JSON_ERROR,
								obj.getString(Key.KEY_DATA));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				callback.onFailure(Errors.JSON_ERROR, results.toString());
			}
		}
	}
}
