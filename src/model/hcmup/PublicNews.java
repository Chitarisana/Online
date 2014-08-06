package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import model.utils.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;

public class PublicNews {
	private String[] strs;
	public String MessageSubject;
	public String CreationDate;
	public String SenderName;
	public String MessageNote;
	public String FileAttach;

	public PublicNews() {
	}

	public PublicNews(String _MessageSubject, String _CreationDate,
			String _SenderName, String _MessageNote, String _FileAttach) {
		MessageSubject = _MessageSubject;
		CreationDate = _CreationDate;
		SenderName = _SenderName;
		MessageNote = _MessageNote;
		FileAttach = _FileAttach;
		strs = new String[] { MessageSubject, CreationDate, SenderName,
				MessageNote, FileAttach };
	}

	public PublicNews(String... strings) {
		strs = strings;
		MessageSubject = strings[0];
		CreationDate = strings[1];
		SenderName = strings[2];
		MessageNote = strings[3];
		FileAttach = strings[4];
	}

	public PublicNews(String json) throws JSONException {
		this(Utils.getValues(new JSONObject(json), Key.KEY_PUBLIC_NEWS).values);
	}

	public static ArrayList<PublicNews> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		ArrayList<PublicNews> datas = new ArrayList<PublicNews>();
		for (int i = 0; i < array.length(); i++) {
			PublicNews data = new PublicNews(array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		String result;
		try {
			String[] key = Key.KEY_PUBLIC_NEWS;
			if (key.length != strs.length)
				return null;
			List<KeyValuePair> map = new ArrayList<KeyValuePair>();
			for (int i = 0; i < strs.length; i++) {
				map.add(new KeyValuePair(key[i], strs[i]));
			}
			result = Utils.toJSONObjectString(map);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
}
