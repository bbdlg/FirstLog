package com.example.firstlog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class GuideActivity extends Activity {
	
	private String saveDir = FirstLogHelper.localRootPath;
	public static boolean startRecordAudio = false;
	
	AlertDialog menuDialog;// menu菜单Dialog
    GridView menuGrid;
    View menuView;
    /** 菜单图片 **/
    int[] menu_image_array = {
    		R.drawable.menu_upload, R.drawable.menu_download, R.drawable.menu_change_user,
    		R.drawable.menu_good, R.drawable.menu_share, R.drawable.menu_info
    };
    /** 菜单文字 **/
    String[] menu_name_array = { 
    		"上传数据", "下载数据", "切换账号",
    		"评价", "分享", "关于"
    };
    /** 宏定义 **/
    private final static int MENU_UPLOAD = 0;
    private final static int MENU_DOWNLOAD = 1;
    private final static int MENU_CHANGEUSER = 2;
    private final static int MENU_GOOD = 3;
    private final static int MENU_SHARE = 4;
    private final static int MENU_INFO = 5;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.guide);
        
        new Handler();
        
        //check main directory
        File dirFirstFile=new File(saveDir);  
        if(!dirFirstFile.exists())	{  
             dirFirstFile.mkdir(); 
        }
        
        //start text
        Button textButton = (Button)findViewById(R.id.button_startlog);
        textButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, LogTextActivity.class);
				startActivity(intent);
			}
		});      
        
        //show log
        Button showlogButton = (Button)findViewById(R.id.button_showlog);
        showlogButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, ShowLogActivity.class);
				startActivity(intent);
			}
		});
        
        menuView = View.inflate(this, R.layout.gridview_menu, null);
        // 创建AlertDialog
        menuDialog = new AlertDialog.Builder(this).create();
        menuDialog.setView(menuView);
        menuDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
                    dialog.dismiss();
                return false;
            }
        });

        menuGrid = (GridView) menuView.findViewById(R.id.gridview);
        menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
        /** 监听menu选项 **/
        menuGrid.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                switch (arg2) {
                case MENU_CHANGEUSER:
                	Editor status = getSharedPreferences("firstlog", 0).edit();
                	status.putString("haveLogined", "no");
                	status.commit();
                	
                	SharedPreferences sharedPreferences = getSharedPreferences("firstlog", 0);
                	String email = sharedPreferences.getString("username", "noSuchEmailUser");
                	
                    Toast.makeText(getApplicationContext(), "当前账号<"+email+">已注销", Toast.LENGTH_LONG).show();
                    
                    Intent intent = new Intent();
                	intent.setClass(GuideActivity.this, LoginActivity.class);
                	Bundle bundle = new Bundle();
                	bundle.putString("email", email);
                	intent.putExtras(bundle);
                	startActivity(intent);
                	GuideActivity.this.finish();
                	
                	break;
                	
                case MENU_UPLOAD:

                	MessageBox msgBox =  new MessageBox(GuideActivity.this);
        	        int ret = msgBox.showDialog("上传数据可能产生较大流量，建议在wifi网络下进行。", "温馨提示");
        	    	if(0 == ret) {
        	    		break;
        	    	}
        	    	
                    SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
                    String curuser = statusPreferences.getString("username", "noSuchEmailUser");
                    String token = statusPreferences.getString("token_of_"+curuser, "no_token");
        			if(token.equals("no_token")) {
        				Log.e("sync", "token has not found");
        				Toast.makeText(getApplicationContext(), "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
        			}
        			else {				
        				FirstLogHelper.token = token;
        				
        				Log.w("sync", "start sync ...");
        				Toast.makeText(getApplicationContext(), "开始上传本地文件到云端，并同步logs...", Toast.LENGTH_LONG).show();
        				
        				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
        				fileSyncHelper.syncFiles("upload");
        				
        				TableSyncHelper tableSyncHelper = new TableSyncHelper(GuideActivity.this, curuser);
        				tableSyncHelper.syncTables();
        				
        				//Log.w("sync", "finish sync!");
        				//Toast.makeText(this, "恭喜恭喜！本地文件上传完成，logs同步完成~", Toast.LENGTH_LONG).show();
        			}
                      	
                	break;
                	
                case MENU_DOWNLOAD:
                	//((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "本操作将下载云端所有的照片、视频、语音等，可能占用大量空间，请慎重:)");
        	        MessageBox msgBox1 =  new MessageBox(GuideActivity.this);
        	        int ret1 = msgBox1.showDialog("下载数据可能产生较大流量，建议在wifi网络下进行。同时如果您使用了较长时间的firstlog，本操作还可能会占用您本地SD卡的较大空间，请慎重", "温馨提示");
                	if(0 == ret1) {
                		break;
                	}
        	        
                    SharedPreferences statusPreferences1 = getSharedPreferences("firstlog", 0);
                    String curuser1 = statusPreferences1.getString("username", "noSuchEmailUser");
                    String token1 = statusPreferences1.getString("token_of_"+curuser1, "no_token");
        			if(token1.equals("no_token")) {
        				Log.e("sync", "token has not found");
        				Toast.makeText(getApplicationContext(), "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
        			}
        			else {				
        				FirstLogHelper.token = token1;
        				
        				Log.w("sync", "start sync ...");
        				Toast.makeText(getApplicationContext(), "开始从云端下载文件...", Toast.LENGTH_LONG).show();
        				
        				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
        				fileSyncHelper.syncFiles("download");
        				
        				//Log.w("sync", "finish sync!");
        				//Toast.makeText(this, "恭喜恭喜！云端的所有文件均已下载到本地~", Toast.LENGTH_LONG).show();
        			}
        			
                	break;

                case MENU_INFO:
                	Intent aboutIntent = new Intent();
                	aboutIntent.setClass(GuideActivity.this, AboutActivity.class);
                	startActivity(aboutIntent);
                
                	break;
                	
            	case MENU_SHARE:
					Intent intentShare = new Intent(Intent.ACTION_SEND);

					intentShare.setType("text/plain");
					intentShare.putExtra(Intent.EXTRA_SUBJECT, "分享");
					intentShare.putExtra(Intent.EXTRA_TEXT,
							"嗨，有一款软件推荐给你：FirstLog，可以记录生活中的各种第一次。它有很强的生命力，正在不断的吸收用户反馈，积极完善自己。快点试用一下吧。");
					startActivity(Intent.createChooser(intentShare, getTitle()));                	
            
					break;
					
            	case MENU_GOOD:
            		Intent intentGood = new Intent(Intent.ACTION_VIEW);
    				intentGood.setData(Uri.parse("market://details?id=" + getPackageName()));
//    				intent.setData(Uri.parse("market://search?q=RT"));
    				startActivity(intentGood);
    				
    				break;
					
                default:
                	Log.d("debug", "arg2 = "+arg2);
                break;
                }
                
                menuDialog.hide();
            }
        });
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			//((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "您一定要抛弃我么:(");
			MessageBox msgBox =  new MessageBox(this);
	        int ret = msgBox.showDialog("确定要退出么？如果正在上传或下载数据，建议您稍候再退出 :)", "注意");
	    	if(1 == ret) {
	    		android.os.Process.killProcess(android.os.Process.myPid());
	    	}
			return true;
		}
		return false;
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("menu");// 必须创建一项
        return super.onCreateOptionsMenu(menu);
    }
    
    private SimpleAdapter getMenuAdapter(String[] menuNameArray,
            int[] imageResourceArray) {
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuNameArray.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", imageResourceArray[i]);
            map.put("itemText", menuNameArray[i]);
            data.add(map);
        }
        SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
                R.layout.item_menu, new String[] { "itemImage", "itemText" },
                new int[] { R.id.item_image, R.id.item_text });
        return simperAdapter;
    }
    
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menuDialog == null) {
            menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
        } else {
            menuDialog.show();
        }
        return false;// 返回为true 则显示系统menu
    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case Menu.FIRST + 1:	//change user id
//            break;
            
//        case Menu.FIRST + 2:	//upload files and sync data
//        	MessageBox msgBox =  new MessageBox(this);
//	        int ret = msgBox.showDialog("上传数据可能产生较大流量，建议在wifi网络下进行。", "温馨提示");
//	    	if(0 == ret) {
//	    		break;
//	    	}
//	    	
//            SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
//            String curuser = statusPreferences.getString("username", "noSuchEmailUser");
//            String token = statusPreferences.getString("token_of_"+curuser, "no_token");
//			if(token.equals("no_token")) {
//				Log.e("sync", "token has not found");
//				Toast.makeText(this, "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
//			}
//			else {				
//				FirstLogHelper.token = token;
//				
//				Log.w("sync", "start sync ...");
//				Toast.makeText(this, "开始上传本地文件到云端，并同步logs...", Toast.LENGTH_LONG).show();
//				
//				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
//				fileSyncHelper.syncFiles("upload");
//				
//				TableSyncHelper tableSyncHelper = new TableSyncHelper(GuideActivity.this, curuser);
//				tableSyncHelper.syncTables();
//				
//				//Log.w("sync", "finish sync!");
//				//Toast.makeText(this, "恭喜恭喜！本地文件上传完成，logs同步完成~", Toast.LENGTH_LONG).show();
//			}
//
//            break;
//            
//        case Menu.FIRST + 3:	//download files
//        	//((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "本操作将下载云端所有的照片、视频、语音等，可能占用大量空间，请慎重:)");
//	        MessageBox msgBox1 =  new MessageBox(this);
//	        int ret1 = msgBox1.showDialog("下载数据可能产生较大流量，建议在wifi网络下进行。同时如果您使用了较长时间的firstlog，本操作还可能会占用您本地SD卡的较大空间，请慎重", "温馨提示");
//        	if(0 == ret1) {
//        		break;
//        	}
//	        
//            SharedPreferences statusPreferences1 = getSharedPreferences("firstlog", 0);
//            String curuser1 = statusPreferences1.getString("username", "noSuchEmailUser");
//            String token1 = statusPreferences1.getString("token_of_"+curuser1, "no_token");
//			if(token1.equals("no_token")) {
//				Log.e("sync", "token has not found");
//				Toast.makeText(this, "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
//			}
//			else {				
//				FirstLogHelper.token = token1;
//				
//				Log.w("sync", "start sync ...");
//				Toast.makeText(this, "开始从云端下载文件...", Toast.LENGTH_LONG).show();
//				
//				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
//				fileSyncHelper.syncFiles("download");
//				
//				//Log.w("sync", "finish sync!");
//				//Toast.makeText(this, "恭喜恭喜！云端的所有文件均已下载到本地~", Toast.LENGTH_LONG).show();
//			}
//			
//        	break;
//        	
//        case Menu.FIRST + 4:	//about
//        	Intent aboutIntent = new Intent();
//        	aboutIntent.setClass(GuideActivity.this, AboutActivity.class);
//        	startActivity(aboutIntent);
//        
//        	break;
//        }
//        
//        return false;
//    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
	
	
}
