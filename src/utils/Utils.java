package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import model.utils.JSONValue;
import model.utils.KeyValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.online.hcmup.android.R;

public class Utils {
	public static Spannable htmlParse(String html) {
		Spanned s = Html.fromHtml(html);
		URLSpan[] currentSpans = s.getSpans(0, s.length(), URLSpan.class);

		SpannableString buffer = new SpannableString(s);
		Linkify.addLinks(buffer, Linkify.ALL);

		for (URLSpan span : currentSpans) {
			int end = s.getSpanEnd(span);
			int start = s.getSpanStart(span);
			buffer.setSpan(span, start, end, 0);
		}
		return buffer;
	}

	public static TextView htmlParse(TextView txt, String html) {
		txt.setText(htmlParse(html));
		txt.setMovementMethod(LinkMovementMethod.getInstance());
		return txt;
	}

	public static String[] merge2Array(String[] a, String[] b) {
		String[] result = new String[a.length + b.length];
		int i = 0;
		for (; i < a.length; i++)
			result[i] = a[i];
		for (; i < result.length; i++)
			result[i] = b[i - a.length];
		return result;
	}

	public static JSONValue getValues(JSONObject js, String[] keys) {
		String[] values = new String[keys.length];
		boolean isNull = false;
		try {
			for (int i = 0; i < keys.length; i++) {
				if (js.has(keys[i])) {
					values[i] = trimSpace(js.getString(keys[i]));
					if (values[i].isEmpty() || "".equals(values[i])) {
						isNull = true;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			isNull = true;
		}
		return new JSONValue(values, isNull);
	}

	public static String[] getValues(JSONObject js, String[] keys,
			boolean notNull) {
		JSONValue jsv = getValues(js, keys);
		if (jsv.isItemNull && notNull) {
			return null;
		}
		return jsv.values;
	}

	public static String[] getValues(Object obj, Class<?> cls, String[] key) {
		String[] values = new String[key.length];
		for (int i = 0; i < key.length; i++) {
			try {
				Field f = cls.getField(key[i]);
				values[i] = (String) (f.get(obj));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				continue;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				continue;
			}
		}
		return values;
	}

	public static String toJSONObjectString(List<KeyValuePair> map)
			throws JSONException {
		JSONObject obj = new JSONObject();
		for (int i = 0; i < map.size(); i++) {
			obj.put(map.get(i).Key, map.get(i).Value);
		}
		return obj.toString();
	}

	public static String toJSONObjectString(String[] keys, String[] values)
			throws JSONException {
		if (keys.length != values.length)
			return null;
		List<KeyValuePair> map = new ArrayList<KeyValuePair>();
		for (int i = 0; i < values.length; i++) {
			map.add(new KeyValuePair(keys[i], values[i]));
		}
		return Utils.toJSONObjectString(map);
	}

	public static String trimSpace(String str) {
		return str.replaceAll("  ", " ").trim();
	}

	public static void showConnectionError(Context context) {
		Toast.makeText(context, R.string.error_connect, Toast.LENGTH_SHORT)
				.show();
	}

	public static void showError(Context context, int errorID) {
		if (errorID != Errors.DISABLE_RELOAD)
			Toast.makeText(context, Errors.getError(errorID),
					Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int resId) {
		showToast(context, context.getString(resId));
	};

	public static void saveImageToIS(Context context, Bitmap bitmapImage) {
		Session session = Session.getInstance(context);
		String fileName = "profile.png";
		session.setImageName(fileName);
		ContextWrapper cw = new ContextWrapper(context);
		// path to /data/data/yourapp/app_data/imageDir
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		// Create imageDir
		File path = new File(directory, fileName);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			// Use the compress method on the BitMap object to write image to
			// the OutputStream
			bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		session.setDirectoryPath(directory.getAbsolutePath());
	}

	public static Bitmap getBitmapImage(Context context) {
		try {
			Session session = Session.getInstance(context);
			String directory = session.getDirectoryPath();
			String fileName = session.getImageName();
			if (directory != null && fileName != null) {
				File f = new File(session.getDirectoryPath(),
						session.getImageName());
				Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
				return b;
			}
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setMaxWidth(LinearLayout layout) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				Constant.MAX_WIDTH, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		layout.setLayoutParams(params);
	}

	public static void hideKeyboard(Activity context) {
		InputMethodManager img = ((InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE));
		if (context.getCurrentFocus() != null) {
			img.hideSoftInputFromWindow(context.getCurrentFocus()
					.getWindowToken(), 0);
			// img.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
		}
	}
}
