package model.utils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {
	int color;
	int textID;

	public CustomArrayAdapter(Context context, int resource, int text,
			List<T> object, int textColor) {
		super(context, resource, text, object);
		textID = text;
		color = context.getResources().getColor(textColor);
	}

	public CustomArrayAdapter(Context context, int resource, int text,
			T[] object, int textColor) {
		super(context, resource, text, object);
		textID = text;
		color = context.getResources().getColor(textColor);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView text = (TextView) view.findViewById(textID);
		text.setTextColor(color);
		return view;
	}
}
