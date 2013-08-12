package com.example.firstlog;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        UserData info = list.get(position);
        TextView  tvYearAndMonth 	= (TextView) itemView.findViewById(R.id.year_month);
        TextView  tvDay 	= (TextView) itemView.findViewById(R.id.day);
        TextView  tvTime 	= (TextView) itemView.findViewById(R.id.time);
        TextView  tvText 	= (TextView) itemView.findViewById(R.id.list_item_text);
        ImageView ivLbs		= (ImageView)itemView.findViewById(R.id.lbs); 
        TextView  tvLbsText = (TextView)itemView.findViewById(R.id.lbs_text);
        ImageView ivPhoto	= (ImageView)itemView.findViewById(R.id.photo);
        ImageView ivVideo	= (ImageView)itemView.findViewById(R.id.video); 
        ImageView ivMark	= (ImageView)itemView.findViewById(R.id.mark); 
                
        tvYearAndMonth.setText(FirstLogHelper.getFormatValueByMillisecond(info.getTimesec(), "yyyy年MM月"));
        tvDay.setText(FirstLogHelper.getFormatValueByMillisecond(info.getTimesec(), "dd"));
        tvTime.setText(FirstLogHelper.getFormatValueByMillisecond(info.getTimesec(), "hh:mm:ss"));
       
        String text = info.getText();
        if(null != text) {
        	tvText.setText(text);
        }
        
        String address = info.getAddress();
        if(null != address) {
        	tvLbsText.setText(address);
        } else {
        	tvLbsText.setVisibility(View.GONE);
        }
        
        String photo = info.getPhoto();
        if(null == photo || photo.length() == 0) {
        	ivPhoto.setVisibility(View.GONE);
        }
        
        String video = info.getVideo();
        if(null == video || video.length() == 0) {
        	ivVideo.setVisibility(View.GONE);
        }
        
        String mark = info.getMark();
        if(null == mark || mark.length() == 0) {
        	ivMark.setVisibility(View.GONE);
        }
      
        return itemView;  
	}

}
