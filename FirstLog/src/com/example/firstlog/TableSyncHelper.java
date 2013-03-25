package com.example.firstlog;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.baidu.solution.client.service.ServiceException;
import com.baidu.solution.pcs.sd.PcsSd;
import com.baidu.solution.pcs.sd.impl.tables.CreateTable;
import com.baidu.solution.pcs.sd.model.ColumnType;
import com.baidu.solution.pcs.sd.model.Order;
import com.baidu.solution.pcs.sd.model.RecordSet;
import com.baidu.solution.pcs.sd.model.Table;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class TableSyncHelper {
	
	private final static String FISRTLOG_TABLE = "firstlog_userdata";
	private static Context context;
	private static String token;
	private static String email;
	public static boolean isSyncing = false;
	
	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		TableSyncHelper.token = token;
	}

	public static String getEmail() {
		return email;
	}

	public static void setEmail(String email) {
		TableSyncHelper.email = email;
	}

	public TableSyncHelper(final Context context) {
		// TODO Auto-generated constructor stub
		TableSyncHelper.context = context;
		TableSyncHelper.email = new FirstLogHelper().getCurUsername();
		setToken(FirstLogHelper.token);
	}

	/**
	 * Create favorite song table with the access token of developer(You).
	 * 
	 * @throws IOException
	 *             If error occurs while executing the request
	 * @throws ServiceException
	 *             (extends IOException) If the server of service returns error
	 *             code
	 */
	private static Table createTable()
			throws IOException {
		// MUST use the access token of developer. The users' access token has
		// no permission to do the request about table, only the records.
		PcsSd service = new PcsSd(getToken());
		
		// Add columns.
		CreateTable create = service.tables().create(FISRTLOG_TABLE);
		create.addColumn(UserDataRemote.TIMESEC, "firstlog's occur time", ColumnType.INT, true);
		create.addColumn(UserDataRemote.LONGITUDE, "location, longitude", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.LATITUDE, "location, latitude", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.MARK, "marks", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.SORT, "sort of content", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.CONTENT, "text content or path of audio, video and photo.", ColumnType.STRING, true);
		
		//add index
		create.addIndex(UserDataRemote.TIMESEC_INDEX, UserDataRemote.TIMESEC, Order.ASC);
		
		return create.execute();
	}
	
	/**
	 * Get the lastest record.
	 * @return
	 * @throws IOException
	 *
	private static int getLastRecordTime()
			throws IOException {		
		RecordSet recordSet = new PcsSd(getToken())
		.records()
		.select(FISRTLOG_TABLE)
		.addOrderBy(UserData.TIMESEC, Order.DESC)
		.setScope(0, 1).execute();
		
		UserDataRemote ret = recordSet.getRecords().get(0).toType(UserDataRemote.class);
		
		Log.i("sync table", "get lastest record time is "+Integer.parseInt(ret.getTimesec()));
		return  Integer.parseInt(ret.getTimesec());
	}
	*/
	
	private static RecordSet insertRecords()
			throws IOException {
		
		// MUST use access token of user whose information will be inserted to
		// the table with the record.
		PcsSd service = new PcsSd(getToken());

		// Create some favorite songs and inserts them to favorite table.
		List<UserDataRemote> userDataRemotes = new LinkedList<UserDataRemote>();
		UserDataHelper userDataHelper = new UserDataHelper(context);
		List<UserData> userDatas = userDataHelper.getUserData(0, 10000);
		for(int i=0; i<userDatas.size(); i++) {
			userDataRemotes.add(new UserDataRemote(
					userDataRemotes.get(i).getTimesec(), 
					userDataRemotes.get(i).getLongitude(), 
					userDataRemotes.get(i).getLatitude(), 
					userDataRemotes.get(i).getMark(), 
					userDataRemotes.get(i).getSort(), 
					userDataRemotes.get(i).getContent()));
			Log.i("sync tables", "remote save content:"+userDataRemotes.get(i).getContent());
		}
		
		return service.records().insert(FISRTLOG_TABLE, userDataRemotes).execute();
	}
	
	private static void downloadRecords() 
			throws IOException {
		
		RecordSet recordSet = new PcsSd(getToken())
		.records()
		.select(FISRTLOG_TABLE)
		.addOrderBy(UserData.TIMESEC, Order.DESC)
		.setScope(0, 1).execute();
		
		UserDataRemote userDataRemote;
		UserData userData = new UserData();
		UserDataHelper userDataHelper = new UserDataHelper(context);
		
		for(int i=0; i<recordSet.getRecords().size(); i++) {
			userDataRemote = recordSet.getRecords().get(i).toType(UserDataRemote.class);
			userData.setEmail(email);
			userData.setTimesec(userDataRemote.getTimesec());
			userData.setLongitude(userDataRemote.getLongitude());
			userData.setLatitude(userDataRemote.getLatitude());
			userData.setMark(userDataRemote.getMark());
			userData.setSort(userDataRemote.getSort());
			userData.setContent(userDataRemote.getContent());
			userData.setDeleted("");
			userDataHelper.saveUserData(userData);
			
			Log.i("sync tables", "download content:"+userData.getContent());
		}
	}
	
	public void syncTables() {
    	if(null != FirstLogHelper.token){
    		if(true == isSyncing) {
    			Toast.makeText(context, "正在同步数据库ing，稍安勿躁~", Toast.LENGTH_LONG).show();
    			return;
    		}

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
					
					//createTable
					try {
						createTable();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					//delete local database where deleted flag is setted ...
					//delete same record in remote database ...
					
					//insert all local record into remote database					
					try {
						insertRecords();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//drop local database
					new UserDataHelper(context).delUserData(email);
					
					//download remote database
					try {
						downloadRecords();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//finish upload list
					isSyncing = false;
				}
			});
			 
    		workThread.start();
    		isSyncing = true;
    	}
    }
	
}

