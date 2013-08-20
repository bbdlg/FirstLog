package com.bbdlg.firstlog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Context;
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
	
	//搜索相关
    BMapManager mBMapManager = null;
    public static final String strKey = "BFbb356bfd081e156a52067ce5e504e6";
	public static MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	
	private long exitTime = 0;
	private String saveDir = FirstLogHelper.localRootPath;
	public static boolean startRecordAudio = false;
	
	AlertDialog menuDialog;// menu菜单Dialog
    GridView menuGrid;
    View menuView;
    /** 菜单图片 **/
    int[] menu_image_array = {
    		R.drawable.menu_upload, R.drawable.menu_download, R.drawable.menu_change_user,R.drawable.menu_info
    };
    /** 菜单文字 **/
    String[] menu_name_array = { 
    		"备份数据", "恢复数据", "切换账号","关于"
    };
    /** 宏定义 **/
    private final static int MENU_UPLOAD = 0;
    private final static int MENU_DOWNLOAD = 1;
    private final static int MENU_CHANGEUSER = 2;
    private final static int MENU_INFO = 3;
	
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

                SharedPreferences statusPreferences = getSharedPreferences("firstlog", 0);
                String curuser = statusPreferences.getString("username", "noSuchEmailUser");
                String token = statusPreferences.getString("token_of_"+curuser, "no_token");
                
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
                	
                	if(FileSyncHelper.isSyncing == true || TableSyncHelper.isSyncing == true) {
                		Toast.makeText(GuideActivity.this, "正在备份数据，稍安勿躁~", Toast.LENGTH_LONG).show();
                		break;
                	}
                	
                	MessageBox msgBox =  new MessageBox(GuideActivity.this);
        	        int ret = msgBox.showDialog("备份数据可能产生较大流量，建议在wifi网络下进行。", "温馨提示");
        	    	if(0 == ret) {
        	    		break;
        	    	}
        	    	
        			if(token.equals("no_token")) {
        				Log.e("sync", "token has not found");
        				Toast.makeText(getApplicationContext(), "当前账户未授权，不妨换个账号登录试试~", Toast.LENGTH_LONG).show();
        			}
        			else {				
        				FirstLogHelper.token = token;
        				
        				Log.w("sync", "start sync ...");
        				Toast.makeText(getApplicationContext(), "开始备份本地数据到云端...", Toast.LENGTH_LONG).show();
        				
        				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
        				fileSyncHelper.syncFiles("upload");
        				
        				TableSyncHelper tableSyncHelper = new TableSyncHelper(GuideActivity.this, curuser);
        				tableSyncHelper.syncTables("upload");
        				
        				//Log.w("sync", "finish sync!");
        				//Toast.makeText(this, "恭喜恭喜！本地文件备份完成，logs同步完成~", Toast.LENGTH_LONG).show();
        			}
                      	
                	break;
                	
                case MENU_DOWNLOAD:
                	
                	if(FileSyncHelper.isSyncing == true || TableSyncHelper.isSyncing == true) {
                		Toast.makeText(GuideActivity.this, "正在恢复数据，稍安勿躁~", Toast.LENGTH_LONG).show();
                		break;
                	}
                	
                	//((FirstLogHelper)getApplication()).dialogConfirm(GuideActivity.this, "本操作将下载云端所有的照片、视频、语音等，可能占用大量空间，请慎重:)");
        	        MessageBox msgBox1 =  new MessageBox(GuideActivity.this);
        	        int ret1 = msgBox1.showDialog("恢复数据可能产生较大流量，建议在wifi网络下进行。同时如果您使用了较长时间的firstlog，本操作还可能会占用您本地SD卡的较大空间，请慎重", "温馨提示");
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
        				Toast.makeText(getApplicationContext(), "开始从云端恢复数据...", Toast.LENGTH_LONG).show();
        				
        				FileSyncHelper fileSyncHelper = new FileSyncHelper(GuideActivity.this);
        				fileSyncHelper.syncFiles("download");

        				TableSyncHelper tableSyncHelper = new TableSyncHelper(GuideActivity.this, curuser);
        				tableSyncHelper.syncTables("download");
        				
        				//Log.w("sync", "finish sync!");
        				//Toast.makeText(this, "恭喜恭喜！云端的所有文件均已恢复到本地~", Toast.LENGTH_LONG).show();
        			}
        			
                	break;

                case MENU_INFO:
                	Intent aboutIntent = new Intent();
                	aboutIntent.setClass(GuideActivity.this, AboutActivity.class);
                	startActivity(aboutIntent);
//                	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                
                	break;
					
                default:
                	Log.d("debug", "arg2 = "+arg2);
                break;
                }
                
                menuDialog.hide();
            }
        });
        
		// 初始化搜索模块，注册事件监听
		initEngineManager(this);
        mSearch = new MKSearch();
        mSearch.init(mBMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetAddrResult(MKAddrInfo res, int error) {
				//wait to debug
//				Toast.makeText(getApplicationContext(), "onGetAddrResult return: " + res.strAddr, Toast.LENGTH_LONG).show();
				UserDataHelper userDataHelper = new UserDataHelper(GuideActivity.this);
//				userDataHelper.updateAddrByPoi(res.strAddr, ""+(float)(res.geoPt.getLatitudeE6())/1e6, ""+(float)(res.geoPt.getLongitudeE6())/1e6);
				userDataHelper.updateAddrByPoi(res.strAddr, StatusUpdateAddr.getLatitude(), StatusUpdateAddr.getLongitude());
				StatusUpdateAddr.setStatusUpdate(true);
			}
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				//wait to debug
				Log.d("debug", "onGetPoiResult return: " + res);
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub
				
			}

        });
        
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(GuideActivity.this, 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Log.d("debug", "您的网络出错啦！");
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Log.d("debug", "输入正确的检索条件！");
            }
        }
        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Log.d("debug", "请在 DemoApplication.java文件输入正确的授权Key！");
//                DemoApplication.getInstance().m_bKeyRight = false;
            }
        }
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
    
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序~", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
//	            finish();
	            android.os.Process.killProcess(android.os.Process.myPid());
	            //System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
