package com.pigcms.library.view.searchview.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**最开始的思路 觉得是需要把能请求到的所有结果都保存到本地，下次再搜索 都会从本地走一遍，有至少一个结果 直接展示，没有的话再去请求
 * 但是 很遗憾，这些会员或者信息是可变的
 * Created by Won on 2017/1/13.
 */

public class SearchHistoryDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "SearchHistory_db";

    public static final String TABLE_NAME = "SearchHistory";

    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + "history text)";

    public SearchHistoryDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);//创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * 查询全部搜索记录
     */
    public ArrayList<String> queryAllHistory() {
        ArrayList<String> historys = new ArrayList<>();
        //获取数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //查询表中的数据
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "id desc");
        //获取name列的索引
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            String history = cursor.getString(1);
            historys.add(history);
        }
        cursor.close();//关闭结果集
        db.close();//关闭数据库对象
        return historys;
    }

    /**
     * 插入数据到数据库
     */
    public void insertHistory(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        //生成ContentValues对象
        ContentValues cv = new ContentValues();
        //往ContentValues对象存放数据，键-值对模式
        cv.put("history", keyword);
        //调用insert方法，将数据插入数据库
        db.insert(TABLE_NAME, null, cv);
        //关闭数据库
        db.close();
    }

    /**
     * 删除某条数据
     */
    public void deleteHistory(String keyword) {
        SQLiteDatabase db = getWritableDatabase();
        //生成ContentValues对象
        db.delete(TABLE_NAME, "history=?", new String[]{keyword});
        //关闭数据库
        db.close();
    }

    /**
     * 删除全部数据
     */
    public void deleteAllHistory() {
        SQLiteDatabase db = getWritableDatabase();
        //删除全部数据
        db.execSQL("delete from " + TABLE_NAME);
        //关闭数据库
        db.close();
    }

}
