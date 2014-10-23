package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;

public class StudyProgram {
	private String[] strs;
	public String CurriculumID;
	public String CurriculumName;
	public String CurriculumTypeName;
	public String StudyProgramID;
	public String StudyProgramName;
	public Double Credits;
	public String SemesterID;
	public String SemesterName;

	public StudyProgram(String... values) {
		if (values == null) {			
			return;
		}
		strs = values;
		CurriculumID = values[0];
		CurriculumName = values[1];
		CurriculumTypeName = values[2];
		SemesterID = values[3];
		SemesterName = values[4];
		Credits = Double.parseDouble(values[5]);
		StudyProgramID = values[6];
		StudyProgramName = values[7];
	}

	public StudyProgram(String json) throws JSONException {
		this(Utils.getValues(new JSONObject(json), Key.KEY_STUDY_PROGRAMS_INFO,
				true));		
	}

	public static List<StudyProgram> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		List<StudyProgram> datas = new ArrayList<StudyProgram>();
		for (int i = 0; i < array.length(); i++) {
			StudyProgram data = new StudyProgram(array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		try {
			return Utils.toJSONObjectString(Key.KEY_STUDY_PROGRAMS_INFO, strs);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] toArray() {
		return strs;
	}
}
