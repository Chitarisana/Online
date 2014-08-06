package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;

public class StudentContact {
	private String[] strs;
	public String HomePhone;
	public String MobilePhone;
	public String Email;
	public String ContactAddress;
	public String Note;
	public String ContactPersonName;
	public String ContactPersonAddress;
	public String ContactPersonPhone;

	public StudentContact(String... values) {
		strs = values;
		HomePhone = values[0];
		MobilePhone = values[1];
		Email = values[2];
		ContactAddress = values[3];
		Note = values[4];
		ContactPersonName = values[5];
		ContactPersonPhone = values[6];
		ContactPersonAddress = values[7];
	}

	public StudentContact(String json) throws JSONException {
		this(Utils.getValues(new JSONObject(json), Utils.merge2Array(
				Key.KEY_STUDENT_CONTACT_1, Key.KEY_STUDENT_CONTACT_2)).values);
	}

	public static List<StudentContact> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		List<StudentContact> datas = new ArrayList<StudentContact>();
		for (int i = 0; i < array.length(); i++) {
			StudentContact data = new StudentContact(array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		try {
			return Utils
					.toJSONObjectString(Utils.merge2Array(
							Key.KEY_STUDENT_CONTACT_1,
							Key.KEY_STUDENT_CONTACT_2), strs);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] toArray() {
		return strs;
	}
}
