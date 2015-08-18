package com.aroundme.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.android.volley.toolbox.ImageLoader;
import com.aroundme.R;
import com.aroundme.common.ConversationItem;
import com.aroundme.common.CustomNetworkImageView;
import com.aroundme.controller.ImagesController;
import com.aroundme.controller.Controller;
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
		holder.friendName.setText(dotMessage);

		if (row_pos.isLastMsgIsGeo()) {
			holder.geoIcon.setVisibility(View.VISIBLE);
			holder.friendName.setPadding(0, 10, 0, 0);
		} else {
			holder.geoIcon.setVisibility(View.GONE);
			holder.friendName.setPadding(0, 55, 0, 0);
		}
		return convertView;

    }
    
    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format 
     * @return String representing date in specified format
     */
    private String getDate(Long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date. 
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(milliSeconds);
         return formatter.format(calendar.getTime());
    }
    
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

    private static class ViewHolder {
        public CustomNetworkImageView thumbNail;
        public TextView friendName;
        public TextView date;
        public TextView message;
        public TextView unreadMessages;
        public ImageView geoIcon;
    }
}
