package model.utils;

public class JSONValue {
	public String[] values;
	public boolean isItemNull;

	public JSONValue(String[] values, boolean isNull) {
		this.values = values;
		this.isItemNull = isNull;
	}
}