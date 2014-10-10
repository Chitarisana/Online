package utils;

public interface ApiListener {

	public void onSuccess(Object json, boolean isArray);

	public void onFailure(int statusCode, String jsonString);
}
