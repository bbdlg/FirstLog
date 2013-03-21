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

	public FileSyncHelper() {
		// TODO Auto-generated constructor stub
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
	
	public int mkdir(final Context context, String remotePath) {
		if(null != FirstLogHelper.token) {
			BaiduPCSClient api_mkdir = new BaiduPCSClient();
			api_mkdir.setAccessToken(FirstLogHelper.token);
			final BaiduPCSActionInfo.PCSFileInfoResponse ret = api_mkdir.makeDir(FirstLogHelper.remoteRootPath+"/"+remotePath);
			return ret.status.errorCode;
		}
		return -1;
	}
	
	public boolean upload(final Context context) {
		
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
}
