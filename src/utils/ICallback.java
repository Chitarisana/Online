package utils;

public interface ICallback {

	public void onSuccess(Object json, boolean isArray);

	public void onFailure(int statusCode, String jsonString);
}
