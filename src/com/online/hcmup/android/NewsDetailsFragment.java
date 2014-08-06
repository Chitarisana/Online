package com.online.hcmup.android;

import utils.Utils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NewsDetailsFragment extends BaseFragment {

	TextView title, content;

	public NewsDetailsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.details_news, container,
				false);
		title = (TextView) rootView.findViewById(R.id.title);
		content = (TextView) rootView.findViewById(R.id.content);
		Bundle extras = getArguments();
		int TYPE;
		if (extras != null) {
			TYPE = extras.getInt(NewsListFragment.KEY_TYPE);
			title.setText(extras.getString(NewsListFragment.KEY_TITLE));
			String text = extras.getString(NewsListFragment.KEY_CONTENT);
			content = Utils.htmlParse(content, text);

			if (TYPE == NewsListFragment.TYPE_PUBLIC) {
				setTitle(R.string.public_news_details);
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
				setHasOptionsMenu(true);
			} else {
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
				setOnFragment(R.string.private_news_details);
				PrivateActivity.itemTitle = getActivity().getTitle();
			}
		}
		return rootView;
	}
}