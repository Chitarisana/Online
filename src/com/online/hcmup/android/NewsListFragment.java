package com.online.hcmup.android;

import java.util.ArrayList;

import model.hcmup.DbHandler;
import model.hcmup.PrivateNews;
import model.hcmup.PublicNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.ApiConnect;
import utils.Constant;
import utils.ICallback;
import utils.Link;
import utils.Session;
import utils.Utils;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NewsListFragment extends BaseFragment {
	public static String KEY_TYPE = "TYPE";
	public static int TYPE_PUBLIC = 0;
	public static int TYPE_PRIVATE = 1;
	public static String KEY_TITLE = "TITLE";
	public static String KEY_CONTENT = "CONTENT";
	static int TYPE = TYPE_PUBLIC;

	DbHandler db;
	ListView listView;

	public NewsListFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("News List", "on create view");
		View v = inflater.inflate(R.layout.frame_news_list, container, false);
		listView = (ListView) v.findViewById(R.id.listview);
		db = DbHandler.getInstance(getActivity());
		Bundle extras = getArguments();
		TYPE = extras.getInt(KEY_TYPE);
		// JSON = extras.getString(KEY_JSON);

		if (TYPE == TYPE_PUBLIC)
			ApiConnect.callUrls(getActivity(), new ICallback() {

				@Override
				public void onSuccess(Object json, boolean isArray) {
					// public news accept array, not object
					if (isArray) {
						JSONArray datas = (JSONArray) json;
						try {
							listView.setAdapter(new PublicNewsAdapter(
									getActivity(), PublicNews
											.jsonArrayToList(datas.toString())));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onFailure(int statusCode, String jsonString) {
					onFailureTask(statusCode, jsonString);
				}
			}, String.format(Link.PUBLIC_NEWS, 1, 0));
		else
			ApiConnect.callUrls(getActivity(), new ICallback() {

				@Override
				public void onSuccess(Object json, boolean isArray) {
					// private news accept array, not object
					if (isArray) {
						JSONArray datas = (JSONArray) json;
						// save to database
						for (int i = 0; i < datas.length(); i++) {
							try {
								JSONObject obj = datas.getJSONObject(i);
								PrivateNews news = new PrivateNews(obj
										.toString());
								db.addPrivateNews(news);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					setPrivateNewsAdapter();
				}

				@Override
				public void onFailure(int statusCode, String jsonString) {
					onFailureTask(statusCode, jsonString);
					setPrivateNewsAdapter();
				}
			}, String.format(Link.PRIVATE_NEWS,
					Session.getInstance(getActivity()).getStudentID()));

		return v;
	}

	private void setPrivateNewsAdapter() {
		listView.setAdapter(new PrivateNewsAdapter(getActivity(), db
				.getAllPrivateNews()));
	}

	private void onFailureTask(int statusCode, String jsonString) {
		if (statusCode == ICallback.CONNECTION_ERROR) {
			Utils.showConnectionError(getActivity());
		} else {
			Utils.showToast(getActivity(), jsonString);
		}
	}

	public View loadView(final String titleString, String dateString,
			final String shortDetailString, final String tag) {
		View row = (getActivity().getLayoutInflater()).inflate(
				R.layout.row_news, null);
		TextView title = (TextView) row.findViewById(R.id.title);
		TextView date = (TextView) row.findViewById(R.id.date);
		TextView shortDetail = (TextView) row.findViewById(R.id.shortdetails);
		title.setText(titleString);
		date.setText(dateString);
		shortDetail.setText(Utils.htmlParse(shortDetailString));

		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new NewsDetailsFragment();
				Bundle args = new Bundle();
				args.putInt(KEY_TYPE, TYPE);
				args.putString(KEY_TITLE, titleString);
				args.putString(KEY_CONTENT, shortDetailString);
				fragment.setArguments(args);

				FragmentManager fragmentManager = getActivity()
						.getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.sub_content, fragment, tag)
						.addToBackStack(null).commit();
			}
		});
		return row;
	}

	public class PublicNewsAdapter extends BaseAdapter {
		public Activity activity;
		public ArrayList<PublicNews> data;

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public PublicNewsAdapter(Activity a, ArrayList<PublicNews> d) {
			activity = a;
			data = d;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			PublicNews news = new PublicNews();
			news = data.get(position);
			return loadView(news.MessageSubject, news.CreationDate,
					news.MessageNote, Constant.TAG_PUBLIC_NEWS_DETAILS);
		}
	}

	public class PrivateNewsAdapter extends BaseAdapter {
		public ArrayList<PrivateNews> data;
		public Activity activity;

		public int getCount() {
			return data.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public PrivateNewsAdapter(Activity a, ArrayList<PrivateNews> d) {
			activity = a;
			data = d;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			PrivateNews news = new PrivateNews();
			news = data.get(position);
			return loadView(news.MessageSubject, news.CreationDate,
					news.MessageBody, Constant.TAG_PRIVATE_NEWS_DETAILS);
		}
	}
}