package utils;

public interface ApiListener {

	public void onSuccess(int position, Object json, boolean isArray);

	public void onFailure(int position, int statusCode, String jsonString);
}
