package com.example.firstlog;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDataHelper extends SQLiteOpenHelper {
    //数据库名称
    private static String DB_NAME = "firstlog.db";
    
    //数据库版本
    private static int DB_VERSION = 2;

    private SQLiteDatabase dbUserData;
    
    //用来保存email、password的表名
    public static final String TABLE_NAME="userdata";
    
    public UserDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        dbUserData= this.getWritableDatabase();
        createTable(dbUserData);
    }
    
	//save user's firstlog data
    public Long saveUserData(UserData data) {
		ContentValues values = new ContentValues();
		values.put(UserData.USERNAME, data.getEmail());
		values.put(UserData.TIMESEC, data.getTimesec());
		values.put(UserData.LONGITUDE, data.getLongitude());
		values.put(UserData.LATITUDE, data.getLatitude());
		values.put(UserData.MARK, data.getMark());
		values.put(UserData.TEXT, data.getText());
    	values.put(UserData.AUDIO, data.getAudio());
    	values.put(UserData.VIDEO, data.getVideo());
    	values.put(UserData.PHOTO, data.getPhoto());
    	values.put(UserData.ISDELETE, data.getIsdeleted());
    	values.put(UserData.ADDRESS, data.getAddress());
    	Long uid = dbUserData.insert(UserDataHelper.TABLE_NAME, UserData.ID, values);
    	Log.e("SaveUserData", uid+"");
    	return uid;
	}
    
    //创建表
	public static void createTable(SQLiteDatabase db) {
		Log.e("UserDataHelper","call UserDataHelper onCreate");
		// TODO Auto-generated method stub
        db.execSQL("CREATE TABLE IF NOT EXISTS "+
                TABLE_NAME+"("+
                UserData.ID+" integer primary key,"+
                UserData.USERNAME+" varchar,"+
                UserData.TIMESEC+" varchar,"+
                UserData.LONGITUDE+" varchar,"+
                UserData.LATITUDE+" varchar,"+
                UserData.MARK+" varchar,"+
                UserData.TEXT+" varchar,"+
                UserData.AUDIO+" varchar,"+
                UserData.VIDEO+" varchar,"+
                UserData.PHOTO+" varchar,"+
                UserData.ISDELETE+" varchar,"+
                UserData.ADDRESS+" varchar" +
                ")"
                );
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.e("Database","onUpgrade");
	}

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
    
    public List<UserData> getUserData(int start, int end)
    {
        List<UserData> dataList = new ArrayList<UserData>();
        Cursor cursor = dbUserData.query(UserDataHelper.TABLE_NAME, null, null, null, null, null, UserData.TIMESEC+" DESC", ""+start+","+end);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()&& (cursor.getString(1)!=null)){
            UserData data = new UserData();
            data.setEmail(cursor.getString(1));
            data.setTimesec(cursor.getString(2));
            data.setLongitude(cursor.getString(3));
            data.setLatitude(cursor.getString(4));
            data.setMark(cursor.getString(5));
            data.setText(cursor.getString(6));
            data.setAudio(cursor.getString(7));
            data.setVideo(cursor.getString(8));
            data.setPhoto(cursor.getString(9));
            data.setIsdeleted(cursor.getString(10));
            data.setAddress(cursor.getString(11));
            dataList.add(data);
            cursor.moveToNext();
        }
        
        cursor.close();
        
        return dataList;
    }
    
    //删除UserData表的记录
    public int delUserData(String username)
    {
        int id = dbUserData.delete(UserDataHelper.TABLE_NAME, UserData.USERNAME +"=\""+username+"\"", null);
        Log.e("DelUserInfo","delete username<"+username+"> databases, ret = "+id+"");
        return id;
    }

}
