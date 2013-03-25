package com.example.firstlog;

import java.io.IOException;

import com.baidu.solution.client.service.ServiceException;
import com.baidu.solution.pcs.sd.PcsSd;
import com.baidu.solution.pcs.sd.impl.tables.CreateTable;
import com.baidu.solution.pcs.sd.model.ColumnType;
import com.baidu.solution.pcs.sd.model.Order;
import com.baidu.solution.pcs.sd.model.Table;

import android.content.Context;

public class TableSyncHelper {
	
	private final static String FISRTLOG_TABLE = "firstlog_userdata";
	private final Context context;
	private static String token;
	public TableSyncHelper(final Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		token = FirstLogHelper.token;
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
		PcsSd service = new PcsSd(token);
		/*
        UserData.ID+" integer primary key,"+
        UserData.EMAIL+" varchar,"+
        UserData.TIMESEC+" varchar,"+
        UserData.LONGITUDE+" varchar,"+
        UserData.LATITUDE+" varchar,"+
        UserData.MARK+" varchar,"+
        UserData.SORT+" varchar,"+
        UserData.CONTENT+" varchar,"+
        UserData.DELETE+" varchar"+
        */
		// Add columns.
		CreateTable create = service.tables().create(FISRTLOG_TABLE);
		create.addColumn(UserData.TIMESEC, "firstlog's occur time", ColumnType.STRING, true);
		create.addColumn(UserData.LONGITUDE, "location, longitude", ColumnType.STRING, true);
		create.addColumn(UserData.LATITUDE, "location, latitude", ColumnType.STRING, true);
		create.addColumn(UserData.MARK, "marks", ColumnType.STRING, true);
		create.addColumn(UserData.SORT, "sort of content", ColumnType.STRING, true);
		create.addColumn(UserData.CONTENT, "text content or path of audio, video and photo.", ColumnType.STRING, true);
		
		return create.execute();
	}
}

