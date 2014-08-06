package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import model.utils.KeyValuePair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;

public class PrivateNews {
	private String[] strs;
	public String MessageSubject;
	public String CreationDate;
	public String SenderName;
	public String IsRead;
	public String MessageBody;
	public String FileAttach;

	public PrivateNews() {
	}

	public PrivateNews(String _MessageSubject, String _CreationDate,
			String _SenderName, String _IsRead, String _MessageBody,
			String _FileAttach) {
		MessageSubject = _MessageSubject;
		CreationDate = _CreationDate;
		SenderName = _SenderName;
		IsRead = _IsRead;
		MessageBody = _MessageBody;
		FileAttach = _FileAttach;
		strs = new String[] { MessageSubject, CreationDate, SenderName, IsRead,
				MessageBody, FileAttach };
	}

	public PrivateNews(String... values) {
		strs = values;
		MessageSubject = values[0];
		CreationDate = values[1];
		SenderName = values[2];
		IsRead = values[3];
		MessageBody = values[4];
		FileAttach = values[5];
	}

	public PrivateNews(String json) throws JSONException {
		this(Utils.getValues(new JSONObject(json), Key.KEY_PRIVATE_NEWS).values);
	}

	public static ArrayList<PrivateNews> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		ArrayList<PrivateNews> datas = new ArrayList<PrivateNews>();
		for (int i = 0; i < array.length(); i++) {
			PrivateNews data = new PrivateNews(array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		String result;
		try {
			String[] key = Key.KEY_PRIVATE_NEWS;
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