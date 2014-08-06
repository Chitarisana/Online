package utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ImageCall extends AsyncTask<URL, Void, Bitmap> {
	private ICallback callback;
	private Context context;
	long timestamp;

	public ImageCall(Context context, ICallback callback) {
		this.context = context;
		this.callback = callback;
	}

	protected void onPreExecute() {
		// Cancel request if there is no network connection while loading
		if (!ApiConnect.isConnectingToInternet(context)) {
			cancel(true);
			callback.onFailure(ICallback.CONNECTION_ERROR, null);
			return;
		}
		timestamp = System.currentTimeMillis();
	}

	@Override
	protected Bitmap doInBackground(URL... urls) {
		Bitmap mIcon = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) urls[0]
					.openConnection();
			connection.connect();
			InputStream input = connection.getInputStream();
			mIcon = BitmapFactory.decodeStream(input);
		} catch (MalformedURLException e1) {
			Log.d("error URL", e1.getMessage());
			callback.onFailure(ICallback.DATA_ERROR, e1.getMessage());
		} catch (IOException e1) {
			Log.d("error IO", e1.getMessage());
			callback.onFailure(ICallback.DATA_ERROR, e1.getMessage());
		}
		return mIcon;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// Show the time had been used to connected to server.
		long time = System.currentTimeMillis() - timestamp;
		Toast.makeText(context,
				"Total time in process: " + time + " miliseconds.",
				Toast.LENGTH_SHORT).show();

		if (result != null) {
			Utils.saveToInternalSorage(context, result);
			callback.onSuccess(null, false);
		} else {
			callback.onFailure(ICallback.DATA_ERROR, null);
		}
	}

}
