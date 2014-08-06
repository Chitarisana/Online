package model.hcmup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import utils.Key;
import utils.Utils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static DbHandler instance;

	private static final String DATABASE_NAME = "DB_Online.db";
	private static final String TABLE_STUDENT_INFO = "StudentInfo";
	private static final String TABLE_STUDENT_COURSE = "StudentCourse";
	private static final String TABLE_STUDENT_CONTACT = "StudentContact";
	private static final String TABLE_PRIVATE_NEWS = "PrivateNews";
	private static final String TABLE_STUDY_PROGRAM = "StudyProgram";
	private static final String TABLE_REGISTERED = "Registered";
	private static final String TABLE_DISACCUMULATE = "Disaccumulate";
	private static final String TABLE_SCHEDULE_CALENDAR = "Calendar";
	private static final String TABLE_SCORE = "Score";
	private static final String TABLE_TERM_SCORE = "TermScore";
	private static final String TABLE_SCORE_DETAILS = "ScoreDetails";
	public static String[] KEY_PRIVATE_NEWS, KEY_STUDENT_INFO,
			KEY_STUDENT_COURSE, KEY_STUDENT_CONTACT, KEY_STUDY_PROGRAM,
			KEY_REGISTERED, KEY_DISACCUMULATE, KEY_SCHEDULE_CALENDAR,
			KEY_SCORE, KEY_TERM_SCORE, KEY_SCORE_DETAILS;

	private static String[] tableList = new String[] { TABLE_PRIVATE_NEWS,
			TABLE_STUDENT_INFO, TABLE_STUDENT_COURSE, TABLE_STUDENT_CONTACT,
			TABLE_STUDY_PROGRAM, TABLE_REGISTERED, TABLE_DISACCUMULATE,
			TABLE_SCHEDULE_CALENDAR, TABLE_SCORE, TABLE_TERM_SCORE,
			TABLE_SCORE_DETAILS };
	private String[][] keyList;

	/**
	 * General SQL
	 */
	private String createSQL(String table, String[] keys) {
		String str = "CREATE TABLE " + table + "(";
		str += keys[0] + " TEXT PRIMARY KEY ON CONFLICT REPLACE, ";
		for (int i = 1; i < keys.length; i++) {
			str += keys[i] + " TEXT, ";
		}
		str = str.substring(0, str.length() - 2);
		str += ")";
		return str;
	}

	private String[] addToList(String id, String[] list) {
		String[] result = new String[list.length + 1];
		result[0] = id;
		for (int i = 1; i < result.length; i++) {
			result[i] = list[i - 1];
		}
		return result;
	}

	public static DbHandler getInstance(Context context) {
		if (instance == null) {
			instance = new DbHandler(context);
		}
		return instance;
	}

	public DbHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		KEY_PRIVATE_NEWS = Key.KEY_PRIVATE_NEWS;
		KEY_STUDENT_INFO = Key.KEY_STUDENT_INFO;
		KEY_STUDENT_COURSE = Key.KEY_STUDENT_COURSE;
		KEY_STUDENT_CONTACT = Utils.merge2Array(Key.KEY_STUDENT_CONTACT_1,
				Key.KEY_STUDENT_CONTACT_2);
		KEY_STUDY_PROGRAM = Key.KEY_STUDY_PROGRAMS_INFO;
		KEY_REGISTERED = addToList("TermScheduleID", Key.KEY_REGISTER_SCHEDULE);
		KEY_DISACCUMULATE = Key.KEY_NOT_ACCUMULATE_CURRICULUM;
		KEY_SCHEDULE_CALENDAR = addToList("CalendarStudyUnitID",
				Key.KEY_SCHEDULE_CALENDAR);
		KEY_SCORE = Key.KEY_SCORE;
		KEY_TERM_SCORE = Key.KEY_TERM_SCORE;
		KEY_SCORE_DETAILS = addToList("ScoreDetailID", Key.KEY_SCORE_DETAILS);
		keyList = new String[][] { KEY_PRIVATE_NEWS, KEY_STUDENT_INFO,
				KEY_STUDENT_COURSE, KEY_STUDENT_CONTACT, KEY_STUDY_PROGRAM,
				KEY_REGISTERED, KEY_DISACCUMULATE, KEY_SCHEDULE_CALENDAR,
				KEY_SCORE, KEY_TERM_SCORE, KEY_SCORE_DETAILS };
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create tables
		for (int i = 0; i < tableList.length; i++)
			db.execSQL(createSQL(tableList[i], keyList[i]));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int i = 0; i < tableList.length; i++)
			db.execSQL("DROP TABLE IF EXISTS " + tableList[i]);
		onCreate(db);
	}

	/**
	 * Delete all data in local DB. Use it when Logout.
	 */
	public void deleteAll() {
		SQLiteDatabase db = getWritableDatabase();
		for (int i = 0; i < tableList.length; i++)
			db.delete(tableList[i], null, null);
	}

	/**
	 * Add Private News to database
	 * 
	 * @param news
	 * @return
	 */
	public Boolean addPrivateNews(PrivateNews news) {
		return insert(news, PrivateNews.class, KEY_PRIVATE_NEWS,
				TABLE_PRIVATE_NEWS);
	}

	public Boolean addPrivateNews(List<PrivateNews> newsList) {
		for (PrivateNews news : newsList) {
			if (!addPrivateNews(news))
				return false;
		}
		return true;
	}

	public ArrayList<PrivateNews> getAllPrivateNews() {
		ArrayList<PrivateNews> list = new ArrayList<PrivateNews>();
		SQLiteDatabase db = getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_PRIVATE_NEWS;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				list.add(new PrivateNews(cursor2Array(cursor)));
			} while (cursor.moveToNext());
		}
		return list;
	}

	public Boolean addStudentInfo(StudentInfo student) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_STUDENT_INFO, null, null);
		return insert(student, StudentInfo.class, KEY_STUDENT_INFO,
				TABLE_STUDENT_INFO);
	}

	public StudentInfo getStudentInfo(String studentID) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_STUDENT_INFO, KEY_STUDENT_INFO,
				KEY_STUDENT_INFO[0] + "=?",
				new String[] { String.valueOf(studentID) }, null, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return new StudentInfo(cursor2Array(cursor));
	}

	public Boolean addStudentCourse(StudentCourse student) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_STUDENT_COURSE, null, null);
		return insert(student, StudentCourse.class, KEY_STUDENT_COURSE,
				TABLE_STUDENT_COURSE);
	}

	public StudentCourse getStudentCourse() {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_STUDENT_COURSE;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return new StudentCourse(cursor2Array(cursor));
	}

	public Boolean addStudentContact(StudentContact student) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_STUDENT_CONTACT, null, null);
		return insert(student, StudentContact.class, KEY_STUDENT_CONTACT,
				TABLE_STUDENT_CONTACT);
	}

	public StudentContact getStudentContact() {
		SQLiteDatabase db = getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_STUDENT_CONTACT;
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return new StudentContact(cursor2Array(cursor));
	}

	/**
	 * Insert an object to database using reflect method.
	 * 
	 * @param obj
	 *            object that had data to save
	 * @param cls
	 *            class name, usually use <b>Example.class</b> with Example is a
	 *            class
	 * @param key
	 *            list of keys (also mean column name)
	 * @param table
	 *            target table to save to
	 * @return
	 */
	private Boolean insert(Object obj, Class<?> cls, String[] key, String table) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		for (int i = 0; i < key.length; i++) {
			try {
				Field f = cls.getField(key[i]);
				values.put(key[i], (String) (f.get(obj) + ""));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				continue;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				continue;
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				continue;
			}
		}
		db.beginTransaction();
		try {
			long result = db.insert(table, null, values);
			db.setTransactionSuccessful();
			if (result == -1)
				return false;
		} finally {
			db.endTransaction();
		}
		return true;
	}

	/**
	 * Convert cursor to array of String.
	 */
	private static String[] cursor2Array(Cursor cursor) {
		int count = cursor.getColumnCount();
		String[] str = new String[count];
		for (int i = 0; i < count; i++) {
			str[i] = cursor.getString(i);
		}
		return str;
	}
}
