package com.bbdlg.firstlog;

import com.baidu.mobstat.StatService;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	Button btShare;
	Button btGood;
	TextView tvUrl;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.about);
		
		btShare = (Button)findViewById(R.id.bt_share);
		btGood = (Button)findViewById(R.id.bt_good);
		tvUrl = (TextView)findViewById(R.id.url);
		
		btShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent intent=new Intent(Intent.ACTION_SEND);
			      
			      intent.setType("text/plain");
			      intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			      intent.putExtra(Intent.EXTRA_TEXT, "嗨，有一款软件推荐给你：FirstLog，可以记录生活中的各种第一次。它有很强的生命力，正在不断的吸收用户反馈，积极完善自己。快点试用一下吧，在应用商店搜索FirstLog即可。");
			      startActivity(Intent.createChooser(intent, getTitle()));
			
			      StatService.onEvent(AboutActivity.this, "share", "start");
			}
		});
		
		btGood.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + getPackageName()));
//				intent.setData(Uri.parse("market://search?q=RT"));
				startActivity(intent);
				
				StatService.onEvent(AboutActivity.this, "assess", "start");
			}
		});
		
		tvUrl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse(getResources().getString(R.string.about_url));  
				Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				startActivity(it);
				
				StatService.onEvent(AboutActivity.this, "click_url", "start");
			}
		});
		
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(AboutActivity.this);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(AboutActivity.this);
	}

}