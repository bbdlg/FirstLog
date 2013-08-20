package com.bbdlg.firstlog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSStatusListener;

public class FileSyncHelper {

	private List<String> 		localFileList 			= new ArrayList<String>();
	private List<String> 		remoteFilelist 			= new ArrayList<String>();
	public static List<String> 	uploadFileList 			= new ArrayList<String>();
	public static boolean 		isSyncing 				= false;
	public static List<String> 	dirListNeedRemoteMake 	= new ArrayList<String>();
	public static List<String> 	downloadFileList 		= new ArrayList<String>();
	
	private final Context context;
	public FileSyncHelper(final Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public boolean addUploadFileList(String file) {
		
		uploadFileList.add(file);
		
		return true;
	}
	
	public boolean addDirListNeedRemoteMake(String file) {
		
		dirListNeedRemoteMake.add(file);
		Log.e("haha", "add into dir list need remote make is "+file);
		
		return true;
	}
	
	public String getRelativePath(String path) {
		int index = path.indexOf(FirstLogHelper.localRootPath);
		if(0 == index) {
			Log.i("sync files", "file is local:"+path);
			return path.substring(FirstLogHelper.localRootPath.length()+1);
		}
		index = path.indexOf(FirstLogHelper.remoteRootPath);
		if(0 == index) {
			Log.i("sync files", "file is remote:"+path);
			return path.substring(FirstLogHelper.remoteRootPath.length()+1);
		}
		Log.i("sync files", "file is error:"+path);
		return path;
	}
	
	public List<String> showAllFiles(File dir) throws Exception{
		List<String> ret = new ArrayList<String>();
		File[] fs = dir.listFiles();
		for(int i=0; i<fs.length; i++){
			if(fs[i].isFile()) {
				Log.i("get file path", getRelativePath(fs[i].getAbsolutePath()));
				ret.add(getRelativePath(fs[i].getAbsolutePath()));
			}
			if(fs[i].isDirectory()){
				try{
					ret.addAll(showAllFiles(fs[i]));
				}catch(Exception e){}
			}
		}
		return ret;
	}
	
	
	public String getFileNameOfPath(String path) {
		String temp[] = path.split("/");   
		Log.i("getFileNameOfPath", "temp.length = "+temp.length);
		return temp[temp.length - 1];   
	}
	
	public String getLastDirOfPath(String path) {
		String temp[] = path.split("/");
		Log.i("getLastDirOfPath", "path is "+path+", temp.length = "+temp.length);
		if(temp.length > 1) {
			String ret = temp[0];
			for(int i=1; i<temp.length-1; i++) {
				ret = ret+"/"+temp[i];
			}
			return ret;
		}
		return "";
	}
	
	public int mkdir(String remotePath) {
		if(null != FirstLogHelper.token) {
			BaiduPCSClient api_mkdir = new BaiduPCSClient();
			api_mkdir.setAccessToken(FirstLogHelper.token);
			final BaiduPCSActionInfo.PCSFileInfoResponse ret = api_mkdir.makeDir(FirstLogHelper.remoteRootPath+"/"+remotePath);
			return ret.status.errorCode;
		}
		return -1;
	}
	
	public boolean download(List<String> downloadFileList) {

		while(downloadFileList.size() > 0) {
			if(null != FirstLogHelper.token) {

				Log.e("lock", "size = "+downloadFileList.size());
								
				BaiduPCSClient api = new BaiduPCSClient();
	    		
	    		//Set access_token for pcs api
	    		api.setAccessToken(FirstLogHelper.token);

				Log.e("upload", "from:"+FirstLogHelper.remoteRootPath+"/"+downloadFileList.get(downloadFileList.size()-1)
				+" to"+FirstLogHelper.localRootPath+"/"+downloadFileList.get(downloadFileList.size()-1));
				
				//if local dir not exist, create it.
				File tmpFile = new File(FirstLogHelper.localRootPath+"/"+getLastDirOfPath(downloadFileList.get(downloadFileList.size()-1)));
				if(!tmpFile.exists()) {
					tmpFile.mkdir();
					Log.w("download", "create dir:"+tmpFile.getAbsolutePath()+", also "+FirstLogHelper.localRootPath+"/"+getLastDirOfPath(downloadFileList.get(downloadFileList.size()-1)));
				}
	    	    //Use pcs uploadFile API to uplaod files
				final BaiduPCSActionInfo.PCSSimplefiedResponse downloadResponse = api.downloadFileFromStream(
						FirstLogHelper.remoteRootPath+"/"+downloadFileList.get(downloadFileList.size()-1),
						FirstLogHelper.localRootPath+"/"+downloadFileList.get(downloadFileList.size()-1), 
						new BaiduPCSStatusListener(){

					@Override
					public void onProgress(long bytes, long total) {
						// TODO Auto-generated method stub		
						Log.w("sync files", "processed "+bytes*100/total+"%");
					}
	    		});
				
				if(downloadResponse.errorCode == 0){
					
					Log.e("download", "下载成功");
					
				}else{
					
					Log.e("download","下载失败，错误代码"+downloadResponse.errorCode); 
				}
					    		
				//The interface of the thread UI
				FirstLogHelper.uiThreadHandler.post(new Runnable(){
					
	    			public void run(){
	    				if(downloadResponse.errorCode == 0){
//	    					Toast.makeText(context,"下载成功", Toast.LENGTH_SHORT).show();
	    				}else{
	    					Toast.makeText(context,"下载失败，错误代码："+downloadResponse.errorCode+"，错误信息："+downloadResponse.message, Toast.LENGTH_SHORT).show(); 
	    				}
	    			}
	    		});	
			}
	    		
			downloadFileList.remove(downloadFileList.size()-1);				
		}
		
		FirstLogHelper.uiThreadHandler.post(new Runnable(){
			
			public void run(){
				Toast.makeText(context,"成功恢复云端文件至本地", Toast.LENGTH_SHORT).show();
			}
		});
		
		return false;
	
	}
	
	public boolean upload(List<String> uploadFileList) {
		
		while(uploadFileList.size() > 0) {
			if(null != FirstLogHelper.token) {

				Log.e("lock", "size = "+uploadFileList.size());
								
				BaiduPCSClient api = new BaiduPCSClient();
	    		
	    		//Set access_token for pcs api
	    		api.setAccessToken(FirstLogHelper.token);

				Log.e("upload", "from:"+FirstLogHelper.localRootPath+"/"+uploadFileList.get(uploadFileList.size()-1)
				+" to"+FirstLogHelper.remoteRootPath+"/"+uploadFileList.get(uploadFileList.size()-1));

	    	    //Use pcs uploadFile API to upload files
				final BaiduPCSActionInfo.PCSFileInfoResponse uploadResponse = api.uploadFile(
						FirstLogHelper.localRootPath+"/"+uploadFileList.get(uploadFileList.size()-1), 
						FirstLogHelper.remoteRootPath+"/"+uploadFileList.get(uploadFileList.size()-1),
						new BaiduPCSStatusListener(){

					@Override
					public void onProgress(long bytes, long total) {
						// TODO Auto-generated method stub		
						Log.w("sync files", "processed "+bytes*100/total+"%");
					}
	    		});
				
				if(uploadResponse.status.errorCode == 0){
					Log.e("upload", "上传成功");
				}else{
					Log.e("upload","上传失败，错误代码："+uploadResponse.status.errorCode+"，错误信息："+uploadResponse.status.message); 
				}
				Log.e("upload", "upload success:"+uploadFileList.get(uploadFileList.size()-1));
					    		
				//The interface of the thread UI
				FirstLogHelper.uiThreadHandler.post(new Runnable(){
					
	    			public void run(){
	  
	    				if(uploadResponse.status.errorCode == 0){
//	    					Toast.makeText(context,"上传成功", Toast.LENGTH_SHORT).show();
	    				}else{
	    					Toast.makeText(context,"上传失败，错误代码："+uploadResponse.status.errorCode+"，错误信息："+uploadResponse.status.message, Toast.LENGTH_SHORT).show(); 
	    				}
	    			}
	    		});	
			}
	    		
			uploadFileList.remove(uploadFileList.size()-1);				
		}
		
		FirstLogHelper.uiThreadHandler.post(new Runnable(){
			
			public void run(){
				Toast.makeText(context,"成功备份本地文件至云端", Toast.LENGTH_SHORT).show();
			}
		});
		
		return false;
	}
	
    //Back to the content activity
    public void back(Context context, Class<?> cls){    	  		
    	Intent content = new Intent();
  	    content.setClass(context, cls);	
  	    context.startActivity(content);   		  	
    }
    
    public void syncFiles(final String flag) {
    	if(null != FirstLogHelper.token){
    		if(true == isSyncing) {
    			Toast.makeText(context, "正在同步文件ing，稍安勿躁~", Toast.LENGTH_LONG).show();
    			return;
    		}

    		Thread workThread = new Thread(new Runnable(){
				public void run() {
					Log.i("sync files", "start sync files ...");					

					//get remote file list
		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(FirstLogHelper.token);
		    		final BaiduPCSActionInfo.PCSListInfoResponse remoteRootFileList = api.list(FirstLogHelper.remoteRootPath, "name", "asc");
		    		for(int i=0; (null != remoteRootFileList.list)&&(i<remoteRootFileList.list.size()); i++) {
		    			if(remoteRootFileList.list.get(i).isDir) {
		    				final BaiduPCSActionInfo.PCSListInfoResponse remoteSecondFileList = api.list(remoteRootFileList.list.get(i).path, "name", "asc");
		    				for(int j=0; (null != remoteSecondFileList.list)&&(j<remoteSecondFileList.list.size()); j++) {
		    					if(remoteSecondFileList.list.get(j).isDir) {
		    						Log.w("sync files", "there's lower dir in second dir:"+remoteSecondFileList.list.get(j).path);
		    					}
		    					else {
		    						remoteFilelist.add(remoteSecondFileList.list.get(j).path.substring(FirstLogHelper.remoteRootPath.length()+1));
		    					}
		    				}
		    			}
		    			else {
		    				remoteFilelist.add(remoteRootFileList.list.get(i).path.substring(FirstLogHelper.remoteRootPath.length()+1));
		    			}
		    		}
		    		Log.i("sync files", "got remote file list");
		    		
		    		//get local file list
		    		File localDir = new File(FirstLogHelper.localRootPath);	
					try {
						localFileList = showAllFiles(localDir);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.i("sync files", "got local file list");
										
					/*/debug
					str = "localfilelist size = "+localFileList.size()+"\n";
					for(int i=0; i<localFileList.size(); i++) {
						str += (localFileList.get(i)+"\n");
					}
					str += ("remotefilelist size = "+remoteFilelist.size()+"\n");
					for(int i=0; i<remoteFilelist.size(); i++) {
						str += (remoteFilelist.get(i)+"\n");
					}
					Log.i("sync files", str);
					*/
					
					//compare remoteFileList and localFileList
					for(int i=localFileList.size()-1; i>=0; i--) {
						String tmpLocalFile = localFileList.get(i);
						boolean isExist = false;
						for(int j=remoteFilelist.size()-1; j>=0; j--) {
							if(0 <= remoteFilelist.get(j).indexOf(tmpLocalFile)) {
								remoteFilelist.remove(j);
								localFileList.remove(i);
								isExist = true;
								break;
							}
						}
						if(false == isExist) {
							uploadFileList.add(tmpLocalFile);
							localFileList.remove(i);
						}
					}
					downloadFileList.addAll(remoteFilelist);
					Log.i("sync files", "analyzed download and upload file lists");
					
					/*/debug
					str = "uploadFileList size = "+uploadFileList.size()+"\n";
					for(int i=0; i<uploadFileList.size(); i++) {
						str += (uploadFileList.get(i)+"\n");
					}
					str += ("downloadFileList size = "+downloadFileList.size()+"\n");
					for(int i=0; i<downloadFileList.size(); i++) {
						str += (downloadFileList.get(i)+"\n");
					}
					Log.i("sync files", str);
					*/
		    		
		    		//start upload list
					upload(uploadFileList);
					Log.i("sync files", "have pushed local files to cloud pan");
					
					if(flag.equals("download")) {
						download(downloadFileList);
						Log.i("sync files", "have download fields on cloud pan to local database");
					} else if(flag.equals("upload")) {
						//do nothing
						
					} else {
						Log.i("sync tables", "unknown flag: "+flag);
					}
					
					//finish upload list
					isSyncing = false;
					Log.i("sync files", "finish sync files");
				}
			});
			 
    		workThread.start();
    		isSyncing = true;
    	}
    }
    
}
