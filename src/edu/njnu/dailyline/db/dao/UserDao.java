package edu.njnu.dailyline.db.dao;

import java.util.ArrayList;
import java.util.List;

import edu.njnu.dailyline.db.DLDBOpenHelper;
import edu.njnu.dailyline.domain.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {

	private DLDBOpenHelper helper;

	public UserDao(Context context) {
		helper = DLDBOpenHelper.getInstance(context);
	}

	/**
	 * 得到所有的用户
	 * @return 所有用户的集合
	 */
	public List<User> getAllUser() {
	//		SQLiteDatabase db = helper.getReadableDatabase();
	//		String sql = "select * from " + DLDB.User.TABLE_NAME;
	//		Cursor cursor = db.rawQuery(sql, null);
	//
	//		List<User> list = null;
	//		if (cursor != null) {
	//			while (cursor.moveToNext()) {
	//				if (list == null) {
	//					list = new ArrayList<User>();
	//				}
	//				User User = new User();
	//
	//				User.setCurrent(cursor.getInt(cursor
	//						.getColumnIndex(DLDB.User.COLUMN_CURRENT)) == 1);
	//				User.setIcon(cursor.getString(cursor
	//						.getColumnIndex(DLDB.User.COLUMN_ICON)));
	//				User.setName(cursor.getString(cursor
	//						.getColumnIndex(DLDB.User.COLUMN_NAME)));
	//				User.setSex(cursor.getInt(cursor
	//						.getColumnIndex(DLDB.User.COLUMN_SEX)));
	//				list.add(User);
	//			}
	//		}
	//		return list;
		
		return null;
	}

	public User getCurrentUser() {
//		SQLiteDatabase db = helper.getReadableDatabase();
//		String sql = "select * from " + DLDB.User.TABLE_NAME + " where "
//				+ DLDB.User.COLUMN_CURRENT + "=1";
//		Cursor cursor = db.rawQuery(sql, null);
//
//		if (cursor != null) {
//			while (cursor.moveToNext()) {
//				User User = new User();
//
//				User.setCurrent(cursor.getInt(cursor
//						.getColumnIndex(DLDB.User.COLUMN_CURRENT)) == 1);
//				User.setIcon(cursor.getString(cursor
//						.getColumnIndex(DLDB.User.COLUMN_ICON)));
//				User.setName(cursor.getString(cursor
//						.getColumnIndex(DLDB.User.COLUMN_NAME)));
//				User.setSex(cursor.getInt(cursor
//						.getColumnIndex(DLDB.User.COLUMN_SEX)));
//				return User;
//			}
//		}
		return null;
	}

	public User getByUser(String User) {
//		SQLiteDatabase db = helper.getReadableDatabase();
//		String sql = "select * from " + DLDB.User.TABLE_NAME + " where "
//				+ DLDB.User.COLUMN_User + "=?";
//		Cursor cursor = db.rawQuery(sql, new String[] { User });
//
//		if (cursor != null) {
//			while (cursor.moveToNext()) {
//				User a = new User();
//
//				a.setUser(cursor.getString(cursor
//						.getColumnIndex(DLDB.User.COLUMN_User)));
//				a.setArea(cursor.getString(cursor
//						.getColumnIndex(DLDB.User.COLUMN_AREA)));
//				a.setCurrent(cursor.getInt(cursor
//						.getColumnIndex(DLDB.User.COLUMN_CURRENT)) == 1);
//				a.setIcon(cursor.getString(cursor
//						.getColumnIndex(DLDB.User.COLUMN_ICON)));
//				a.setName(cursor.getString(cursor
//						.getColumnIndex(DLDB.User.COLUMN_NAME)));
//				a.setSex(cursor.getInt(cursor
//						.getColumnIndex(DLDB.User.COLUMN_SEX)));
//				return a;
//			}
//		}
//		return null;
		return null;
	}

	public void addUser(User User) {
//		SQLiteDatabase db = helper.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(DLDB.User.COLUMN_ICON, User.getIcon());
//		values.put(DLDB.User.COLUMN_NAME, User.getName());
//		values.put(DLDB.User.COLUMN_SEX, User.getSex());
//		values.put(DLDB.User.COLUMN_CURRENT, User.isCurrent() ? 1 : 0);
//
//		db.insert(DLDB.User.TABLE_NAME, null, values);
	}

	public void updateUser(User User) {
//		SQLiteDatabase db = helper.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(DLDB.User.COLUMN_ICON, User.getIcon());
//		values.put(DLDB.User.COLUMN_NAME, User.getName());
//		values.put(DLDB.User.COLUMN_SEX, User.getSex());
//		values.put(DLDB.User.COLUMN_CURRENT, User.isCurrent() ? 1 : 0);
//
//		String whereClause = DLDB.User.COLUMN_User + "=?";
//		String[] whereArgs = new String[] { User.getUser() };
//		db.update(DLDB.User.TABLE_NAME, values, whereClause, whereArgs);
	}

	public void deleteUser(User User) {
//		SQLiteDatabase db = helper.getWritableDatabase();
//		String whereClause = DLDB.User.COLUMN_User + "=?";
//		String[] whereArgs = new String[] { User.getUser() };
//		db.delete(DLDB.User.TABLE_NAME, whereClause, whereArgs);
	}
}
