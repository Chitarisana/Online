package model.hcmup;

import java.util.ArrayList;

public class Semester {
	public String SemesterID;
	public String SemesterName;
	public Double CreditNum;
	public ArrayList<StudyProgram> StudyProgram;

	public Semester(String id, String name, ArrayList<StudyProgram> std) {
		SemesterID = id;
		SemesterName = name;
		StudyProgram = std;
		CreditNum = getCreditNum();
	}

	public Semester() {
	}

	public double getCreditNum() {
		double num = 0;
		for (int i = 0; i < StudyProgram.size(); i++)
			num += StudyProgram.get(i).Credits;
		return num;
	}

	public String getHeader() {
		String str = SemesterName;
		str += " (" + CreditNum + " tc)";
		return str;
	}
}
