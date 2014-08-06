package model.utils;

import java.util.List;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.online.hcmup.android.R;

public class KeyValueAdapter extends BaseAdapter {
	List<KeyValuePair> data;
	Activity context;
	Boolean isKey;
	int layout;
	int maxWidth;
	double percent;

	/*
	 * isKeyDepend = true: Key's size = static, Value's size = dynamic
	 * isKeyDepend = false: Key's size = dynamic, Value's size = static
	 * isKeyDepend = null: Key's size = dynamic, Value's size = dynamic
	 */
	public KeyValueAdapter(Activity context, List<KeyValuePair> data,
			int layoutID, int maxWidth, Boolean isKeyDepend) {
		this.data = data;
		this.context = context;
		this.isKey = isKeyDepend;
		this.layout = layoutID;
		this.maxWidth = maxWidth;
	}

	public KeyValueAdapter(Activity context, List<KeyValuePair> data,
			int layoutID, double percent, Boolean isKeyDepend) {
		this.data = data;
		this.context = context;
		this.isKey = isKeyDepend;
		this.layout = layoutID;
		this.maxWidth = 0;
		this.percent = percent;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = (context.getLayoutInflater()).inflate(layout != 0 ? layout
				: R.layout.row_key_value, null);
		TextView key = (TextView) row.findViewById(R.id.key);
		TextView value = (TextView) row.findViewById(R.id.value);
		RelativeLayout rowLayout = (RelativeLayout) row
				.findViewById(R.id.row_layout);

		key.setText(data.get(position).Key);
		value.setText(data.get(position).Value);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		key.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		value.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

		if (maxWidth == 0) {
			maxWidth = displaymetrics.widthPixels
					- 2
					* context.getResources().getDimensionPixelSize(
							R.dimen.bounder_padding);
			if (percent != 0)
				maxWidth = (int) (maxWidth * percent);
		}

		int padding = 0;
		if (rowLayout != null) {
			padding = rowLayout.getPaddingLeft() + rowLayout.getPaddingRight();
		}
		padding += key.getPaddingLeft() + key.getPaddingRight()
				+ value.getPaddingLeft() + value.getPaddingRight();
		int keyWidth = key.getMeasuredWidth();
		int valueWidth = value.getMeasuredWidth();
		int total = maxWidth - padding;

		if (isKey == null) {
			double percent = 1.0 * keyWidth / (keyWidth + valueWidth);
			percent = Math.max(percent, 0.2);
			key.setMaxWidth((int) (total * percent));
			value.setMaxWidth((int) (total * (1 - percent))); // chua on lam...
		} else if (isKey) {
			// value.setMaxWidth(total - key.getMeasuredWidth());
			value.setPadding(key.getMeasuredWidth() + padding,
					value.getPaddingTop(), value.getPaddingRight(),
					value.getPaddingBottom());
		} else {
			// key.setMaxWidth(total - value.getMeasuredWidth());
			key.setPadding(key.getPaddingLeft(), key.getPaddingTop(),
					value.getMeasuredWidth() + padding, key.getPaddingBottom());
		}
		return row;
	}
}