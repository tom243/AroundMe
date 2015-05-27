package com.aroundme.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.aroundme.R;
import com.aroundme.R.drawable;
import com.aroundme.R.id;
import com.aroundme.R.layout;
import com.aroundme.common.ConversationItem;
import com.aroundme.controller.AppController;
import com.aroundme.controller.Controller;
import com.google.api.client.util.DateTime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomConversationsAdapter extends BaseAdapter{

	private Controller controller;
    private Context context;
    private List<ConversationItem> conversations;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

   public CustomConversationsAdapter(Context context, List<ConversationItem> conversations) {
    	this.controller = Controller.getInstance();
        this.context = context;
        this.conversations = conversations;
    }

    @Override
    public int getCount() {

        return conversations.size();
    }

    @Override
    public Object getItem(int position) {

        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {

        return conversations.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.conversations_list_item, null);
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.icon2);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.friendName);
        TextView txtDate = (TextView) convertView.findViewById(R.id.date);
        ConversationItem row_pos = conversations.get(position);
        String friendName = controller.getUserNameByMail(row_pos.getFriendMail());
        if (friendName != null){
        	txtTitle.setText(controller.getUserNameByMail(row_pos.getFriendMail()));
        }
        
        if(row_pos.getImageUrl() != null)
        	thumbNail.setImageUrl(row_pos.getImageUrl(), imageLoader);
        else
        	thumbNail.setDefaultImageResId(R.drawable.user_default);
        
         txtDate.setText(getDate(row_pos.getTimeStamp(),"dd/MM hh:mm"));
        	
        return convertView;

    }
    
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format 
     * @return String representing date in specified format
     */
    public String getDate(Long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date. 
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(milliSeconds);
         return formatter.format(calendar.getTime());
    }
    

}
