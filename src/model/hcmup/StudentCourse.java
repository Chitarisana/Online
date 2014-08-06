package model.hcmup;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.Key;
import utils.Utils;

public class StudentCourse {
	private String[] strs;
	public String CourseName;
	public String CourseTime;
	public String EnrollTestingYear;
	public String GraduateYear;
	public String StudyProgramID;
	public String ClassStudentID;
	public String ClassRoleName;
	public String ProfessorName;
	public String ProfessorPhone;
	public String ProfessorEmail;

	public StudentCourse(String... values) {
		strs = values;
		CourseName = values[0];
		CourseTime = values[1];
		EnrollTestingYear = values[2];
		GraduateYear = values[3];
		StudyProgramID = values[4];
		ClassStudentID = values[5];
		ClassRoleName = values[6];
		ProfessorName = values[7];
		ProfessorPhone = values[8];
		ProfessorEmail = values[9];
	}

	public StudentCourse(String json) throws JSONException {
		this(
				Utils.getValues(new JSONObject(json), Key.KEY_STUDENT_COURSE).values);
	}

	public static List<StudentCourse> jsonArrayToList(String json)
			throws JSONException {
		JSONArray array = new JSONArray(json);
		List<StudentCourse> datas = new ArrayList<StudentCourse>();
		for (int i = 0; i < array.length(); i++) {
			StudentCourse data = new StudentCourse(array.getString(i));
			datas.add(data);
		}
		return datas;
	}

	public String toString() {
		try {
			return Utils.toJSONObjectString(Key.KEY_STUDENT_COURSE, strs);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
