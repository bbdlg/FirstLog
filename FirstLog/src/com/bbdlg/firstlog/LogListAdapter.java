package com.bbdlg.firstlog;

import java.io.File;
import java.util.List;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LogListAdapter extends BaseAdapter {
    private Activity context;  
    private List<UserData> list;  
    
    public LogListAdapter(Activity context, List<UserData> list) {  
        this.context = context;  
        this.list = list;  
    }  

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
        LayoutInflater inflater = context.getLayoutInflater();  
        View itemView = inflater.inflate(R.layout.list_item, null);  
        final UserData info = list.get(position);
        TextView  tvYearAndMonth 	= (TextView) itemView.findViewById(R.id.year_month);
        TextView  tvDay 	= (TextView) itemView.findViewById(R.id.day);
        TextView  tvTime 	= (TextView) itemView.findViewById(R.id.time);
        TextView  tvText 	= (TextView) itemView.findViewById(R.id.list_item_text);
        TextView  tvLbsText = (TextView)itemView.findViewById(R.id.lbs_text);
        TextView  tvMarkText = (TextView)itemView.findViewById(R.id.mark_text);
        RelativeLayout rlText	= (RelativeLayout)itemView.findViewById(R.id.text_container); 
        RelativeLayout rlLbs	= (RelativeLayout)itemView.findViewById(R.id.lbs_container); 
        RelativeLayout rlPhoto	= (RelativeLayout)itemView.findViewById(R.id.photo_container);
        RelativeLayout rlVideo	= (RelativeLayout)itemView.findViewById(R.id.video_container); 
        RelativeLayout rlMark	= (RelativeLayout)itemView.findViewById(R.id.mark_container); 
        ImageView ivPhoto = (ImageView)itemView.findViewById(R.id.photo_thumb);
        ImageView ivVideo = (ImageView)itemView.findViewById(R.id.video_thumb);
        
        
        tvYearAndMonth.setText(FirstLogHelper.getFormatValueByMillisecond(info.getTimesec(), "yyyy年MM月"));
        tvDay.setText(FirstLogHelper.getFormatValueByMillisecond(info.getTimesec(), "dd"));
        tvTime.setText(FirstLogHelper.getFormatValueByMillisecond(info.getTimesec(), "hh:mm:ss"));
       
        String text = info.getText();
        if(null == text || text.length() == 0) {
        	rlText.setVisibility(View.GONE);
        } else {
        	tvText.setText(text);
        }
        

        OnClickListener clickToGetAddr = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		    	if(StatusUpdateAddr.isValidUpdate() == true) {
		        	//start search addr of latitude and longitude
		        	GeoPoint ptCenter = new GeoPoint((int)(Float.valueOf(info.getLatitude())*1e6), (int)(Float.valueOf(info.getLongitude())*1e6));
					//反Geo搜索
		        	GuideActivity.mSearch.reverseGeocode(ptCenter);
		        	
		        	StatusUpdateAddr.setPosition(info.getLatitude(), info.getLongitude());
		        	StatusUpdateAddr.setStatusUpdate(false);
		        	((TextView)v).setText("重进本页面即可刷新地址");
		    	}
			}
		};
        
        String address = info.getAddress();
        if(null == address || address.length() == 0) {
//        	rlLbs.setVisibility(View.GONE);
        	tvLbsText.setText("试着轻轻的点我一下");
        	tvLbsText.setOnClickListener(clickToGetAddr);
        	
        } else {
        	tvLbsText.setText(address);
        }
        
        final String photo = info.getPhoto();
        if(null == photo || photo.length() == 0) {
        	rlPhoto.setVisibility(View.GONE);
        } else {
        	ivPhoto.setImageBitmap(FirstLogHelper.getImageThumbnail(FirstLogHelper.localRootPath + photo, 200, 200));
        	ivPhoto.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = UntilOpenFile.openFile(FirstLogHelper.localRootPath + photo);
					context.startActivity(intent);
				}
			});
        }
        
        final String video = info.getVideo();
        if(null == video || video.length() == 0) {
        	rlVideo.setVisibility(View.GONE);
        } else {
        	ivVideo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = UntilOpenFile.openFile(FirstLogHelper.localRootPath + video);
					context.startActivity(intent);
				}
			});
        }
        
        String mark = info.getMark();
        if(null == mark || mark.length() == 0) {
        	rlMark.setVisibility(View.GONE);
        } else {
        	tvMarkText.setText(mark);
        }
      
        return itemView;  
	}

}
