package utils;

public interface ICallback {
	public static int JSON_ERROR = 0;
	public static int CONNECTION_ERROR = 1;
	public static int DATA_ERROR = 2;
	public static int DISABLE_RELOAD = 4;

	public void onSuccess(Object json, boolean isArray);

	public void onFailure(int statusCode, String jsonString);
}
