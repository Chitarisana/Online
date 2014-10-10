package utils;

import android.util.SparseArray;

public class Errors {
	public static int JSON_ERROR = 0;
	public static int CONNECTION_ERROR = 1;
	public static int DATA_ERROR = 2;
	public static int DISABLE_RELOAD = 3;
	public static int INPUT_ERROR = 4;
	private static final SparseArray<String> ERROR;
	static {
		ERROR = new SparseArray<String>();
		ERROR.append(JSON_ERROR, "Lỗi parse JSON");
		ERROR.append(CONNECTION_ERROR, "Kết nối thất bại");
		ERROR.append(DATA_ERROR, "Dữ liệu sai");
		ERROR.append(DISABLE_RELOAD,""/* "Không cần tải lại"*/);
		ERROR.append(INPUT_ERROR, "Dữ liệu đầu vào không đúng");
		// ERROR.lock();

	}

	public static String getError(int error) {
		return ERROR.get(error);
	}
}
