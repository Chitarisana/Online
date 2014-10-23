package model.hcmup;

import java.util.ArrayList;

public class TermStudy {
	public String YearStudy;
	public String TermID;
	public String TermName;
	public Double CreditNum;
	public ArrayList<RegisteredStudyUnit> StudyUnit;

	public TermStudy() {
	}

	public TermStudy(String y, String t, ArrayList<RegisteredStudyUnit> std) {
		YearStudy = y;
		TermID = t;
		TermName = getName(y, t);
		StudyUnit = std;
		CreditNum = getCreditNum();
	}

	private String getName(String y, String t) {
		return t + ", " + y;
	}

	public TermStudy(String y, String t) {
		this(y, t, new ArrayList<RegisteredStudyUnit>());
	}

	public double getCreditNum() {
		double num = 0;
		for (int i = 0; i < StudyUnit.size(); i++)
			num += StudyUnit.get(i).Credits;
		return num;
	}

	public String getHeader() {
		return TermName + " (" + (int) getCreditNum() + " tc)";
	}

	public ArrayList<String> getCurrisName() {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < StudyUnit.size(); i++)
			list.add(StudyUnit.get(i).CurriculumName);
		return list;
	}
}
