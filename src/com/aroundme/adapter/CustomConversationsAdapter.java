package com.aroundme.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.aroundme.R;
import com.aroundme.R.drawable;
import com.aroundme.R.id;
import com.aroundme.R.layout;
import com.aroundme.common.ConversationItem;
import com.aroundme.controller.ImagesController;
import com.aroundme.controller.Controller;
import com.google.api.client.util.DateTime;

import android.app.Activity;
import android.content.Context;
import android.opengl.Visibility;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomConversationsAdapter extends BaseAdapter{

	private Controller controller;
    private Context context;
    private List<ConversationItem> conversations;
    ImageLoader imageLoader = ImagesController.getInstance().getImageLoader();

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
            imageLoader = ImagesController.getInstance().getImageLoader();

        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.icon2);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.friendName);
        TextView txtDate = (TextView) convertView.findViewById(R.id.date);
        TextView txtMessage = (TextView) convertView.findViewById(R.id.message);
        TextView txtUnreadMessages = (TextView) convertView.findViewById(R.id.unread_messages);
        
        ConversationItem row_pos = conversations.get(position);
        String friendName = controller.getUserNameByMail(row_pos.getFriendMail());
        if (friendName != null){
        	txtTitle.setText(controller.getUserNameByMail(row_pos.getFriendMail()));
        }
        
        thumbNail.setDefaultImageResId(R.drawable.user_default);
        thumbNail.setImageUrl(row_pos.getImageUrl(), imageLoader);
        
          if (DateUtils.isToday(row_pos.getTimeStamp()))
        	 txtDate.setText(getDate(row_pos.getTimeStamp(),"HH:mm"));
          else
        	 txtDate.setText(getDate(row_pos.getTimeStamp(),"dd/MM"));
         
          if (row_pos.getUnreadMess() > 0) {
        	  txtUnreadMessages.setVisibility(View.VISIBLE);
        	  txtUnreadMessages.setText(String.valueOf(row_pos.getUnreadMess()));
       		}
         	else 
         		txtUnreadMessages.setVisibility(View.INVISIBLE);
        	 
          String dotMessage;
          if (row_pos.getContentMess().length() >= 18) {
        	  dotMessage = row_pos.getContentMess().substring(0, 18)+ "...";
          } else {
        	  dotMessage = row_pos.getContentMess();
          	}
          txtMessage.setText(dotMessage); 
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
    
/*    private boolean checkIfMoreThen24Hours(Long date){
    	Long currentDate = new DateTime(new Date()).getValue();
        long diff = currentDate - date;
        int diffInDays = (int) (diff / (1000 * 60 * 60 * 24));
        if (diffInDays >= 1) 
    	if (DateUtils.isToday(date))
        	return true;
        else
        	return false;
    }*/

}
