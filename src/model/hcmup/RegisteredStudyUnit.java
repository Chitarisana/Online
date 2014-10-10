package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;
import android.text.Html;

public class RegisteredStudyUnit {
	private String[] strs;
	public String State;
	public String IsAccepted;
	public Double Credits;
	public String Informations;
	public String ProfessorName;
	public String BeginDate;
	public String EndDate;
	public String CurriculumID;
	public String CurriculumName;
	public String TermID;
	public String YearStudy;
	public String TermScheduleID;

	public RegisteredStudyUnit() {
	}

	public RegisteredStudyUnit(String... values) {
		strs = values;
		CurriculumID = values[0];
		CurriculumName = values[1];
		TermID = values[2];
		YearStudy = values[3];
		State = values[4];
		IsAccepted = values[5];
		Credits = Double.parseDouble(values[6]);
		Informations = Html.fromHtml(values[7]).toString();
		ProfessorName = values[8];
		BeginDate = values[9];
		EndDate = values[10];
		TermScheduleID = YearStudy.substring(2, 4)
				+ TermID.substring(TermID.length() - 1) + "1" + CurriculumID;
	}

	public RegisteredStudyUnit(String json) throws JSONException {
		this(
				Utils.getValues(new JSONObject(json), Key.KEY_REGISTER_SCHEDULE).values);
	}

	public static List<RegisteredStudyUnit> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		List<RegisteredStudyUnit> datas = new ArrayList<RegisteredStudyUnit>();
		for (int i = 0; i < array.length(); i++) {
			RegisteredStudyUnit data = new RegisteredStudyUnit(
					array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		try {
			return Utils.toJSONObjectString(Key.KEY_REGISTER_SCHEDULE, strs);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] toArray() {
		return strs;
	}
}