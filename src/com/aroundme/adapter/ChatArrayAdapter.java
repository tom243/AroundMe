package com.aroundme.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aroundme.R;
import com.aroundme.common.AroundMeApp;
import com.aroundme.common.ChatMessage;
import com.aroundme.controller.Controller;
import com.google.api.client.util.DateTime;

public class ChatArrayAdapter extends ArrayAdapter {

    private final List<ChatMessage> chatMessages;
    private Controller controller;
    private Context context;

    public ChatArrayAdapter(Context context, int textViewResourceId) {
    	super(context, textViewResourceId);
    	this.controller = Controller.getInstance();
        this.context = AroundMeApp.getContext();
        this.chatMessages = new ArrayList<ChatMessage>();
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public ChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = vi.inflate(R.layout.conversation_singlemessage, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setAlignment(holder, !chatMessage.isLeft() , chatMessage.isLocationBased());
        holder.txtMessage.setText(chatMessage.getMessage());
        setDateTitle(holder, chatMessage.getDateTime().getValue());
        
        return convertView;
    }

    public void add(ChatMessage message) {
        chatMessages.add(message);
        super.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe, boolean isGeo) {
        if (isMe) {
            holder.txtMessage.setBackgroundResource(R.drawable.bubble_b);

            LinearLayout.LayoutParams layoutParams = 
            	(LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = 
            	(RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
          
            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
            
        	holder.geoIcon_L.setVisibility(View.GONE);
            if (isGeo) 
            	holder.geoIcon_R.setVisibility(View.VISIBLE);
            else 
            	holder.geoIcon_R.setVisibility(View.GONE);
            
        } else {
            holder.txtMessage.setBackgroundResource(R.drawable.bubble_a);

            LinearLayout.LayoutParams layoutParams = 
            	(LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = 
            	(RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
            
            holder.geoIcon_R.setVisibility(View.GONE);
            if (isGeo) 
            	holder.geoIcon_L.setVisibility(View.VISIBLE);
            else 
            	holder.geoIcon_L.setVisibility(View.GONE);
        }
    }
   
	private Date resetTime(long dateTime) {
		Date date = new Date(dateTime);
		date.setTime(0);
		return date;
	}
    
    private void setDateTitle(ViewHolder holder, long msgDateTime) {
    	long currDateInMillis = new DateTime(new Date()).getValue();
    	Date currDate = resetTime(currDateInMillis);
    	Date msgDate = resetTime(msgDateTime);
    	
    	if (currDate.equals(msgDate)) 
    		holder.txtInfo.setText(controller.dateToTimeString(msgDateTime));
    	else 
    		holder.txtInfo.setText(controller.dateToDateString(msgDateTime)+" , "+ controller.dateToTimeString(msgDateTime));
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (RelativeLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.geoIcon_L = (ImageView) v.findViewById(R.id.geoIcon_L);
        holder.geoIcon_R = (ImageView) v.findViewById(R.id.geoIcon_R);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public RelativeLayout contentWithBG;
        public ImageView geoIcon_L;
        public ImageView geoIcon_R;
    }
    
}