package com.example.firstlog;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.baidu.solution.client.service.ServiceException;
import com.baidu.solution.pcs.sd.PcsSd;
import com.baidu.solution.pcs.sd.impl.ErrorInfo;
import com.baidu.solution.pcs.sd.impl.tables.CreateTable;
import com.baidu.solution.pcs.sd.model.ColumnType;
import com.baidu.solution.pcs.sd.model.Order;
import com.baidu.solution.pcs.sd.model.Record;
import com.baidu.solution.pcs.sd.model.RecordSet;
import com.baidu.solution.pcs.sd.model.Table;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class TableSyncHelper {
	
	private final static String FISRTLOG_TABLE = "firstlog_remote1";
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

	public TableSyncHelper(final Context context, final String email) {
		// TODO Auto-generated constructor stub
		TableSyncHelper.context = context;
		//TableSyncHelper.email = new FirstLogHelper().getCurUsername();
		TableSyncHelper.email = email;
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
		create.addColumn(UserDataRemote.TEXT, "text", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.AUDIO, "audio", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.VIDEO, "video", ColumnType.STRING, true);
		create.addColumn(UserDataRemote.PHOTO, "photo", ColumnType.STRING, true);
		
		//add index
		create.addIndex(UserDataRemote.TIMESEC_INDEX, UserDataRemote.TIMESEC, Order.DESC);
		
		//set the index to be unique.
		create.setIndexUnique(UserDataRemote.TIMESEC_INDEX);
		
		return create.execute();
	}
	
	/**
	 * Get the lastest record.
	 * @return
	 * @throws IOException
	 *
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
					userDatas.get(i).getTimesec(), 
					userDatas.get(i).getLongitude(), 
					userDatas.get(i).getLatitude(), 
					userDatas.get(i).getMark(), 
					userDatas.get(i).getText(), 
					userDatas.get(i).getAudio(),
					userDatas.get(i).getVideo(),
					userDatas.get(i).getPhoto()));
			Log.i("sync tables", "remote save at time:"+userDataRemotes.get(i).getTimesec());
		}
		RecordSet aa = service.records().insert(FISRTLOG_TABLE, userDataRemotes).execute();
		return aa;
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
		
		for(int i=0; i<recordSet.getCount(); i++) {
			Record record = recordSet.getRecords().get(0);
			userDataRemote = record.toType(UserDataRemote.class);
			userData.setEmail(email);
			userData.setTimesec(userDataRemote.getTimesec());
			userData.setLongitude(userDataRemote.getLongitude());
			userData.setLatitude(userDataRemote.getLatitude());
			userData.setMark(userDataRemote.getMark());
			userData.setText(userDataRemote.getText());
			userData.setAudio(userDataRemote.getAudio());
			userData.setVideo(userDataRemote.getVideo());
			userData.setPhoto(userDataRemote.getPhoto());
			userData.setIsdeleted("");
			userDataHelper.saveUserData(userData);
			
			Log.i("sync tables", "download log time:"+userData.getTimesec());
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
					Log.i("sync tables", "start sync tables ...");
					try {
						//createTable
						try {
							createTable();
						} catch (ServiceException e) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
							ErrorInfo info = e.toErrorInformation(ErrorInfo.class);
							long code = info.getErrorCode();
							if (code == 31476 || code == 31472) {
								Log.w("sync tables", "Table has exist!");
							}
							else {
								Log.e("sync tables", "Step 1: Create " + TableSyncHelper.FISRTLOG_TABLE
										+ " table failed:" + e.getMessage());
								throw e;
							}
						}
						
						//delete local database where deleted flag is setted ...
						//delete same record in remote database ...
						
						//insert all local record into remote database			
						insertRecords();
						Log.i("sync tables", "have pushed local records to cloud pan");
						
						//drop local database
						new UserDataHelper(context).delUserData(email);
						
						//download remote database
						downloadRecords();
						Log.i("sync tables", "have download records on cloud pan to local database");
						
					}catch (Exception e_io) {
						// TODO: handle exception
						e_io.printStackTrace();
					}
					finally {
						//finish upload list
						isSyncing = false;
						Log.i("sync tables", "finish sync tables");
						
					}
				}
			});
			 
    		workThread.start();
    		isSyncing = true;
    	}
    }
	
}

