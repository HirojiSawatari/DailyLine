package edu.njnu.dailyline.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DLDBOpenHelper extends SQLiteOpenHelper {

	public DLDBOpenHelper(Context context) {
		super(context, DLDB.NAME, null, DLDB.VERSION);
	}

	private static DLDBOpenHelper instance;

	//单例模式
	public static DLDBOpenHelper getInstance(Context context) {
		if (instance == null) {
			synchronized (DLDBOpenHelper.class) {
				if (instance == null) {
					instance = new DLDBOpenHelper(context);
				}
			}
		}
		return instance;
	}
	
	//数据库首次创建时执行的方法，在此方法中完成初始数据表的创建
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DLDB.User.SQL_CREATE_TABLE);
		
		//TODO：其他数据表，自行在DLDB接口中添加创建语句，在此处调用。
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
