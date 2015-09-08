package com.tc.aroundme.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.tc.aroundme.R;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.ConversationItem;
import com.tc.aroundme.common.CustomNetworkImageView;
import com.tc.aroundme.controller.Controller;
import com.tc.aroundme.controller.ImagesController;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationsAdapter extends BaseAdapter{

	private Controller controller;
    private Context context;
    private List<ConversationItem> conversations;
    private ImageLoader imageLoader;

   /**
 * @param context context that received
 * @param conversations list of conversations
 */
public ConversationsAdapter(Context context, List<ConversationItem> conversations) {
    	this.controller = Controller.getInstance();
        this.context = context;
        this.conversations = conversations;
        this.imageLoader = ImagesController.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
    	if (conversations != null)
    		return conversations.size();
    	else
    		return 0;
    }

    @Override
    public ConversationItem getItem(int position) {
    	if (conversations != null)
    		return conversations.get(position);
    	else 
    		return null;
    }

    @Override
    public long getItemId(int position) {
        return conversations.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.conversations_list_item,null);
			holder = createViewHolder(convertView);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if (imageLoader == null)
			imageLoader = ImagesController.getInstance().getImageLoader();

		ConversationItem row_pos = conversations.get(position);
		String friendName = controller.getUserNameByMail(row_pos.getFriendMail());
		if (friendName != null) 
			holder.friendName.setText(controller.getUserNameByMail(row_pos.getFriendMail()));
		holder.thumbNail.setDefaultImageResId(R.drawable.user_default);
		holder.thumbNail.setImageUrl(row_pos.getImageUrl(), imageLoader);

		if (DateUtils.isToday(row_pos.getTimeStamp()))
			holder.date.setText(getDate(row_pos.getTimeStamp(), "HH:mm"));
		else
			holder.date.setText(getDate(row_pos.getTimeStamp(), "dd/MM"));

		if (row_pos.getUnreadMess() > 0) {
			holder.unreadMessages.setVisibility(View.VISIBLE);
			holder.unreadMessages.setText(String.valueOf(row_pos
					.getUnreadMess()));
		} else
			holder.unreadMessages.setVisibility(View.INVISIBLE);

		String dotMessage;
		if (row_pos.getContentMess().length() >= 18) {
			dotMessage = row_pos.getContentMess().substring(0, 18) + "...";
		} else 
			dotMessage = row_pos.getContentMess();
		holder.message.setText(dotMessage);

		if (row_pos.getMsgType().equals(AppConsts.TYPE_GEO_MSG)) {
			holder.geoIcon.setVisibility(View.VISIBLE);
			holder.geoIcon.setImageResource(R.drawable.geo_msg);
		}
		else if (row_pos.getMsgType().equals(AppConsts.TYPE_PIN_MSG)) {
			holder.geoIcon.setVisibility(View.VISIBLE);
			holder.geoIcon.setImageResource(R.drawable.pin_icon);
		}
		else 
			holder.geoIcon.setVisibility(View.GONE);
		
		return convertView;

    }
    
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format 
     * @return String representing date in specified format
     * 
     * get the current date
     */
    private String getDate(Long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date. 
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(milliSeconds);
         return formatter.format(calendar.getTime());
    }
    
    /**
     * @param v gets a view
     * @return the holder of the view
     * 
     * create a view holder 
     */
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.thumbNail = (CustomNetworkImageView) v.findViewById(R.id.conversations_item_icon);
        holder.friendName = (TextView) v.findViewById(R.id.friendName);
        holder.date = (TextView) v.findViewById(R.id.date);
        holder.message = (TextView) v.findViewById(R.id.message);
        holder.unreadMessages = (TextView) v.findViewById(R.id.unread_messages);
        holder.geoIcon = (ImageView) v.findViewById(R.id.geoIcon);
        return holder;
    }

    /**
     *  instance of holder
     */
    private static class ViewHolder {
        public CustomNetworkImageView thumbNail;
        public TextView friendName;
        public TextView date;
        public TextView message;
        public TextView unreadMessages;
        public ImageView geoIcon;
    }
}
