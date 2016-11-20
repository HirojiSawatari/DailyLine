package edu.njnu.dailyline.db;

/**
 * 
 * @author 王进
 * 
 * 2016年7月24日14:45:20
 * 
 * 所有创建table的操作
 *
 */
public interface DLDB {

	String NAME = "dailyline.db";
	int VERSION = 1;

	public interface User {
		String TABLE_NAME = "user";

		String COLUMN_ID = "_id";
		String COLUMN_NAME = "name";
		String COLUMN_SEX = "sex";
		String COLUMN_ICON = "icon";
		String COLUMN_CURRENT = "current";

		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + " ("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+  COLUMN_NAME + " text," + COLUMN_SEX + " integer," 
				+ COLUMN_ICON + " text," + COLUMN_CURRENT + " integer" + ")";
	}

	
	//后台任务
	public interface BackTask {
		String TABLE_NAME = "back_task";

		String COLUMN_ID = "_id";
		String COLUMN_OWNER = "owner";
		String COLUMN_PATH = "path";
		String COLUMN_STATE = "state";// 0:未执行 1:正在执行 2:执行完成 

		String SQL_CREATE_TABLE = "create table " + TABLE_NAME + " ("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_OWNER + " text," + COLUMN_PATH + " text,"
				+ COLUMN_STATE + " integer" + ")";
	}
}
