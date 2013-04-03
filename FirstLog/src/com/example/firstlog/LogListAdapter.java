package com.example.firstlog;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
        final UserData info = list.get(position);  
        String sort = info.getSort();
        TextView  dateAndTime 	= (TextView) itemView.findViewById(R.id.list_item_date_and_time);  
        TextView  text 	= (TextView) itemView.findViewById(R.id.list_item_text);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.list_item_photo); 
        
        dateAndTime.setText(FirstLogHelper.getDateTimeByMillisecond(info.getTimesec()));
        text.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        
        if(sort.equals(UserData.TEXT)) {
        	text.setVisibility(View.VISIBLE);
        	text.setText(info.getContent()); 
        }
        else if(sort.equals(UserData.PHOTO)) {
        	imageView.setVisibility(View.VISIBLE);
        	//Bitmap bmpTmp = FirstLogHelper.getImageThumbnail((FirstLogHelper.localRootPath+"/"+info.getContent()), 200, 200); 
        	//Log.i("LogListAdapter", "set bmp path:<"+FirstLogHelper.localRootPath+"/"+info.getContent()+">");
        	//imageView.setImageBitmap(bmpTmp);
        	imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
    				bundle.putString("imagePath", FirstLogHelper.localRootPath+"/"+info.getContent());
    				intent.putExtras(bundle);
					intent.setClass(context, PreviewPhotoActivity.class);
					context.startActivity(intent);
				}
			});
        }
          
        return itemView;  
	}

}
