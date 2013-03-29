package com.example.firstlog;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

public class ShowLogActivity extends Activity implements IXListViewListener {
	private XListView mListView;
	private LogListAdapter mAdapter;
	private ArrayList<UserData> items = new ArrayList<UserData>();
	private Handler mHandler;
	private int searchLogOffset = 0;
	private static int searchLogStep = 5;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showlog);
		geneItems();
		mListView = (XListView) findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		mAdapter = new LogListAdapter(ShowLogActivity.this, items);
		mListView.setAdapter(mAdapter);
//		mListView.setPullLoadEnable(false);		//down
		mListView.setPullRefreshEnable(false);	//up
		mListView.setXListViewListener(this);
		mHandler = new Handler();
	}

	private void geneItems() {
    	UserDataHelper userDataHelper = new UserDataHelper(ShowLogActivity.this);
    	List<UserData> data = userDataHelper.getUserData(searchLogOffset, searchLogStep);
    	items.addAll(data);
    	setSearchLogOffset(searchLogOffset+searchLogStep);
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}
	
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				items.clear();
				geneItems();
				mAdapter.notifyDataSetChanged();
				mAdapter = new LogListAdapter(ShowLogActivity.this, items);
				mListView.setAdapter(mAdapter);
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneItems();
				mAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 1000);
	}

    public int getSearchLogOffset() {
		return searchLogOffset;
	}

	public void setSearchLogOffset(int searchLogOffset) {
		this.searchLogOffset = searchLogOffset;
	}
	
}
