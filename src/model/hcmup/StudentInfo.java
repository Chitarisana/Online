package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;

public class StudentInfo {
	private String[] strs;
	public String StudentID;
	public String FullName;
	public String BirthDay;
	public String BirthPlace;
	public String Gender;
	public String EthnicName;
	public String IDCard;
	public String ReligionName;
	public String SocialPersonName;
	public String AreaName;
	public String PriorityName;
	public String Party;
	public String PartyDate;
	public String StudentTypeName;
	public String StudyStatusName;
	public String ProvinceName;
	public String DistrictName;
	public String CountryName;
	public String PermanentResidence;
	public String FileImage;

	public StudentInfo(String... values) {
		strs = values;
		StudentID = values[0];
		FullName = values[1];
		BirthDay = values[2];
		BirthPlace = values[3];
		String gender = values[4];
		if (gender != null) {
			gender = (gender.matches("True") || gender.matches("Nam") ? "Nam"
					: "Nữ");
		}
		Gender = gender;
		EthnicName = values[5];
		ReligionName = values[6];
		IDCard = values[7];
		SocialPersonName = values[8];
		AreaName = values[9];
		PriorityName = values[10];
		Party = ((String) (values[11] != null ? values[11] : "")).matches("1") ? "Có tham gia"
				: "";
		PartyDate = values[12];
		StudentTypeName = values[13];
		StudyStatusName = values[14];
		ProvinceName = values[15];
		DistrictName = values[16];
		CountryName = values[17];
		PermanentResidence = values[18];
		FileImage = values[19];
	}

	public StudentInfo(String json) throws JSONException {
		this(Utils.getValues(new JSONObject(json), Key.KEY_STUDENT_INFO).values);
	}

	public static List<StudentInfo> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		List<StudentInfo> datas = new ArrayList<StudentInfo>();
		for (int i = 0; i < array.length(); i++) {
			StudentInfo data = new StudentInfo(array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		try {
			return Utils.toJSONObjectString(Key.KEY_STUDENT_INFO, strs);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}