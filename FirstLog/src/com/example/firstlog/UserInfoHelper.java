package com.example.firstlog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserInfoHelper extends SQLiteOpenHelper {
    //用来保存email、password的表名
    public static final String TABLE_NAME="userinfo";
    
    public UserInfoHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    
    //创建表
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("UserInfoHelper","call UserInfoHelper onCreate");
		// TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS "+
                TABLE_NAME+"("+
                UserInfo.ID+" integer primary key,"+
                UserInfo.EMAIL+" varchar,"+
                UserInfo.PASSWD+" varchar"+
                ")"
                );
        
        UserDataHelper.createTable(db);
	}

	//更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.e("Database","onUpgrade");
	}

    //更新列
    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try{
            db.execSQL("ALTER TABLE " +
                    TABLE_NAME + " CHANGE " +
                    oldColumn + " "+ newColumn +
                    " " + typeColumn
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
