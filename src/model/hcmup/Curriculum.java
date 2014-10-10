package model.hcmup;

public class Curriculum {
	public String CurriculumID;
	public String CurriculumName;
	public String CurriculumTypeName;
	public double Credits;

	public Curriculum(String... values) {
		CurriculumID = values[0];
		CurriculumName = values[1];
		CurriculumTypeName = values[2];
		Credits = Double.parseDouble(values[3]);
	}
}
