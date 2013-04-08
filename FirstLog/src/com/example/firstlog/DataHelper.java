package com.example.firstlog;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataHelper {

    //数据库名称

    private static String DB_NAME = "firstlog.db";

    //数据库版本

    private static int DB_VERSION = 2;

    private SQLiteDatabase dbUserInfo;
    private SQLiteDatabase dbUserData;

    private UserInfoHelper dbUserInfoHelper;
    private UserDataHelper dbUserDataHelper;

    public DataHelper(Context context){
    	Log.e("DataHelper", "call DataHelper()");
    	dbUserInfoHelper=new UserInfoHelper(context, DB_NAME, null, DB_VERSION);
        dbUserInfo= dbUserInfoHelper.getWritableDatabase();
        Log.e("DataHelper", "finish call DataHelper()");
    }

    public void Close()
    {
        dbUserInfo.close();
        dbUserInfoHelper.close();
        dbUserData.close();
        dbUserDataHelper.close();
    }

    //获取users表中的email、password的记录
    public List<UserInfo> GetUserList(Boolean isSimple)
    {
        List<UserInfo> userList = new ArrayList<UserInfo>();
        Cursor cursor=dbUserInfo.query(UserInfoHelper.TABLE_NAME, null, null, null, null, null, UserInfo.ID+" DESC");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()&& (cursor.getString(1)!=null)){
            UserInfo user=new UserInfo();
            user.setId(cursor.getString(0));
            user.setEmail(cursor.getString(1));
            user.setPasswd(cursor.getString(2));
            userList.add(user);
            cursor.moveToNext();
        }
        
        cursor.close();
        
        return userList;
    }
    
    //判断users表中用户名和密码是否正确
    public boolean IsEmailAndPasswdOk(String email, String passwd) {
		Cursor cursor=dbUserInfo.query(
        		UserInfoHelper.TABLE_NAME, 
        		new String[]{"passwd"},
        		UserInfo.EMAIL+"=\""+email+"\"",
        		null,
        		null, null,null);
        if(cursor.moveToFirst() && cursor.getString(0).equals(passwd)) {
        	cursor.close();
        	return true;
        }
        cursor.close();
		return false;
	}

    //判断users表中的是否包含某个email的记录
    public Boolean HaveUserInfo(String email)
    {
    	Log.e("HaveUserInfo","in haveuserinfo()");
        Boolean b=false;
        Cursor cursor=dbUserInfo.query(
        		UserInfoHelper.TABLE_NAME, 
        		null, 
        		UserInfo.EMAIL+"=\""+email+"\"",
        		null,
        		null, null,null);
        b=cursor.moveToFirst();
        Log.e("HaveUserInfo","have record of <"+UserInfo.EMAIL+"="+email+">? "+b.toString());
        cursor.close();
        return b;
    }

    //更新users表的记录
    public int UpdateUserInfo(UserInfo user)
    {
        ContentValues values = new ContentValues();
        values.put(UserInfo.EMAIL, user.getEmail());
        values.put(UserInfo.PASSWD, user.getPasswd());
        int id = dbUserInfo.update(UserInfoHelper.TABLE_NAME, null, UserInfo.PASSWD + "=" + user.getPasswd(), null);
        Log.e("UpdateUserInfo",id+"");
        return id;
    }

    //添加users表的记录
    public Long SaveUserInfo(UserInfo user)
    {
        ContentValues values = new ContentValues();
        values.put(UserInfo.ID, user.getId());
        values.put(UserInfo.EMAIL, user.getEmail());
        values.put(UserInfo.PASSWD, user.getPasswd());
        Long uid = dbUserInfo.insert(UserInfoHelper.TABLE_NAME, UserInfo.ID, values);
        Log.e("SaveUserInfo",uid+"");
        return uid;
    }

    //删除users表的记录
    public int DelUserInfo(String email)
    {
        int id = dbUserInfo.delete(UserInfoHelper.TABLE_NAME, UserInfo.EMAIL +"="+email, null);
        Log.e("DelUserInfo",id+"");
        return id;
    }
    
    //save user's firstlog data
    public Long SaveUserData(UserData data) {
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
    	Long uid = dbUserData.insert(UserDataHelper.TABLE_NAME, UserData.ID, values);
    	Log.e("SaveUserData", uid+"");
    	return uid;
	}
    
    
}
























