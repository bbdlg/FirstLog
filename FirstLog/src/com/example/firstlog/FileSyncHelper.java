package com.example.firstlog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSStatusListener;

public class FileSyncHelper {

	private final Context context;
	public FileSyncHelper(final Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	public boolean addUploadFileList(String file) {
		
		FirstLogHelper.uploadFileList.add(file);
		
		return true;
	}
	
	public boolean addDirListNeedRemoteMake(String file) {
		
		FirstLogHelper.dirListNeedRemoteMake.add(file);
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
	
	public boolean upload() {
		
		while(FirstLogHelper.uploadFileList.size() > 0) {
			if(null != FirstLogHelper.token) {

				Log.e("lock", "size = "+FirstLogHelper.uploadFileList.size());
								
				BaiduPCSClient api = new BaiduPCSClient();
	    		
	    		//Set access_token for pcs api
	    		api.setAccessToken(FirstLogHelper.token);

				Log.e("upload", "from:"+FirstLogHelper.localRootPath+"/"+FirstLogHelper.uploadFileList.get(FirstLogHelper.uploadFileList.size()-1)
				+" to"+FirstLogHelper.remoteRootPath+"/"+FirstLogHelper.uploadFileList.get(FirstLogHelper.uploadFileList.size()-1));

	    	    //Use pcs uploadFile API to uplaod files
				final BaiduPCSActionInfo.PCSFileInfoResponse uploadResponse = api.uploadFile(
						FirstLogHelper.localRootPath+"/"+FirstLogHelper.uploadFileList.get(FirstLogHelper.uploadFileList.size()-1), 
						FirstLogHelper.remoteRootPath+"/"+FirstLogHelper.uploadFileList.get(FirstLogHelper.uploadFileList.size()-1),
						new BaiduPCSStatusListener(){

					@Override
					public void onProgress(long bytes, long total) {
						// TODO Auto-generated method stub		
					}
	    		});
				
				Log.e("upload", "upload success:"+FirstLogHelper.uploadFileList.get(FirstLogHelper.uploadFileList.size()-1));
					    		
				//The interface of the thread UI
				FirstLogHelper.uiThreadHandler.post(new Runnable(){
					
	    			public void run(){
	  
	    				if(uploadResponse.status.errorCode == 0){
	    					
	    					Toast.makeText(context,"上传成功", Toast.LENGTH_SHORT).show();
	    					
	    				}else{
	    					
	    					Toast.makeText(context,"错误代码"+uploadResponse.status.errorCode, Toast.LENGTH_SHORT).show(); 
	    				}
	    				
	    			}
	    		});	
			}
	    		
			FirstLogHelper.uploadFileList.remove(FirstLogHelper.uploadFileList.size()-1);				
		}

		
		return false;
	}
	
    //Back to the content activity
    public void back(Context context, Class<?> cls){    	  		
    	Intent content = new Intent();
  	    content.setClass(context, cls);	
  	    context.startActivity(content);   		  	
    }
    
    public void syncFiles() {
    	if(null != FirstLogHelper.token){
    		if(true == FirstLogHelper.isSyncing) {
    			Toast.makeText(context, "正在同步文件中，稍安勿躁~", Toast.LENGTH_LONG).show();
    			return;
    		}

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(FirstLogHelper.token);

		    		final BaiduPCSActionInfo.PCSListInfoResponse remoteRootFileList = api.list(FirstLogHelper.remoteRootPath, "name", "asc");
		    		
		    		//upload
		    		File localDir = new File(FirstLogHelper.localRootPath);		
		    		List<String> localFileList = null;
					try {
						localFileList = showAllFiles(localDir);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					
					Log.i("sync files", "localfilelist size = "+localFileList.size());
					
					//get dirListNeedRemoteMake
		    		for(int i=0; i<localFileList.size(); i++) {
		    			boolean noDirRemote = true;
		    			Log.i("sync files", "now process "+localFileList.get(i));
		    			String dirName = getLastDirOfPath(localFileList.get(i));
		    			Log.i("sync files", "isDir = <"+dirName+">");
		    			if(0 == dirName.length()) {
		    				String fileNameLocal = getFileNameOfPath(localFileList.get(i));
		    				Log.i("sync files", "get local file "+fileNameLocal);
		    				addUploadFileList(localFileList.get(i));
		    			}
		    			else {
		    				Log.i("sync files", "get local dir "+dirName);
		    				int j=0;
		    				for(j=0; j<FirstLogHelper.dirListNeedRemoteMake.size(); j++) {
		    					if(FirstLogHelper.dirListNeedRemoteMake.get(j).equals(dirName)) {
		    						noDirRemote = false;
		    						Log.w("sync files", "find <"+dirName+"> in FirstLogHelper.dirListNeedRemoteMake.get("+j+")");
		    						break;
		    					}
		    				}
		    				for(j=0; noDirRemote && (null != remoteRootFileList.list) && j<remoteRootFileList.list.size() && remoteRootFileList.list.get(j).isDir; j++) {
		    					String dirNameRemote = getFileNameOfPath(remoteRootFileList.list.get(j).path);
		    					Log.w("sync files", "dirNameRemote:"+dirNameRemote+", dirNameLocal:"+dirName);
		    					if(remoteRootFileList.list.get(j).isDir && dirNameRemote.equals(dirName)) {
			    					noDirRemote = false;
			    					Log.w("sync files", "find <"+dirName+"> in ret.list.get("+j+")");
			    					break;
		    					}
		    				}
		    				
		    				if(noDirRemote) {
		    					//add to UploadFileList
		    					addDirListNeedRemoteMake(dirName);
		    					/*
		    					File dir = new File(localRootPath+"/"+dirName);
		    					String subFiles[] = dir.list();
		    					for(int m=0; m<subFiles.length; m++) {
		    						fileSyncHelper.addUploadFileList(dirName+"/"+subFiles[m]);
		    						Log.i("sync files", "add <"+dirName+"/"+subFiles[m]+"> to FirstLogHelper.uploadFileList");
		    					}
		    					*/
		    				}
		    				else {
		    					Log.e("sync files", "remote has this dir<"+dirName+">");
		    				}
		    				
		    			}
		    		}
		    		

					//mkdir remote
		    		for(int i=0; i<FirstLogHelper.dirListNeedRemoteMake.size(); i++) {
		    			mkdir(FirstLogHelper.dirListNeedRemoteMake.get(i));
						Log.i("sync files", "mkdir in remote <"+FirstLogHelper.remoteRootPath+"/"+FirstLogHelper.dirListNeedRemoteMake.get(i)+">");	
		    		}
		    		
		    		//get uploadFileList
		    		for (int i=0; i<localFileList.size(); i++) {
		    			for(int j=0; (null != remoteRootFileList.list) && j<remoteRootFileList.list.size() && remoteRootFileList.list.get(j).isDir; j++) {
		    				if(0 <= remoteRootFileList.list.get(j).path.indexOf(localFileList.get(i))) {
		    					addUploadFileList(localFileList.get(i));
		    				}
		    			}
		    		}
		    		
		    		//print uploadFileList
		    		for(int i=0; i<FirstLogHelper.uploadFileList.size(); i++) {
		    			Log.i("sync files", "uploadFileList<"+i+"> = "+FirstLogHelper.uploadFileList.get(i));
		    		}
		    		
		    		//start upload list
					upload();
					
					//finish upload list
					FirstLogHelper.isSyncing = false;
				}
			});
			 
    		workThread.start();
    		FirstLogHelper.isSyncing = true;
    	}
    }
    
}
