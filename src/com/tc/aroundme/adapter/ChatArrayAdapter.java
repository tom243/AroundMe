package com.tc.aroundme.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.tc.aroundme.R;
import com.google.api.client.util.DateTime;
import com.tc.aroundme.common.AppConsts;
import com.tc.aroundme.common.AroundMeApp;
import com.tc.aroundme.common.ChatMessage;
import com.tc.aroundme.controller.Controller;

public class ChatArrayAdapter extends ArrayAdapter {

    private final List<ChatMessage> chatMessages;
    private Controller controller;
    private Context context;

    /**
     * @param context context that received
     * @param textViewResourceId   text view resource id
     * 
     * constructor of ChatArrayAdapter class
     */
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
        return chatMessages.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ChatMessage chatMessage = getItem(position);

        if (convertView == null) {
        	LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.conversation_singlemessage, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else 
            holder = (ViewHolder) convertView.getTag();

        setAlignment(holder, !chatMessage.isLeft() , chatMessage.getMsgType());
        holder.txtMessage.setText(chatMessage.getMessage());
        setDateTitle(holder, chatMessage.getDateTime().getValue());
        
        return convertView;
    }

    /**
     * @param message message that will be added to chat
     * 
     * add message to the chat
     */
    public void add(ChatMessage message) {
        chatMessages.add(message);
        super.add(message);
    }

    /**
     * @param messages list of chat messages
     * 
     * adds list of messsages to the chat
     */
    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    /**
     * @param holder holds a variables of the view
     * @param isMe 	 determines if this is the current user message	
     * @param isGeo  determines if its a GEO message
     * 
     * sets the current alignment
     */
    private void setAlignment(ViewHolder holder, boolean isMe, String msgType) {
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
            if (msgType.equals(AppConsts.TYPE_GEO_MSG)) {
    			holder.geoIcon_R.setVisibility(View.VISIBLE);
    			holder.geoIcon_R.setImageResource(R.drawable.geo_msg);
    		}
    		else if (msgType.equals(AppConsts.TYPE_PIN_MSG)) {
    			holder.geoIcon_R.setVisibility(View.VISIBLE);
    			holder.geoIcon_R.setImageResource(R.drawable.pin_icon);
    		}
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
            if (msgType.equals(AppConsts.TYPE_GEO_MSG)) {
    			holder.geoIcon_L.setVisibility(View.VISIBLE);
    			holder.geoIcon_L.setImageResource(R.drawable.geo_msg);
    		}
    		else if (msgType.equals(AppConsts.TYPE_PIN_MSG)) {
    			holder.geoIcon_L.setVisibility(View.VISIBLE);
    			holder.geoIcon_L.setImageResource(R.drawable.pin_icon);
    		}
    		else 
    			holder.geoIcon_L.setVisibility(View.GONE);
        }
    }
   
	/**
	 * @param dateTime contains date
	 * @return current time
	 * 
	 * reset the date
	 */
	private Date resetTime(long dateTime) {
		Date date = new Date(dateTime);
		date.setTime(0);
		return date;
	}
    
    /**
     * @param holder holds a variables of the view
     * @param msgDateTime date of the message
     * 
     *  set the date title
     */
    private void setDateTitle(ViewHolder holder, long msgDateTime) {
    	long currDateInMillis = new DateTime(new Date()).getValue();
    	Date currDate = resetTime(currDateInMillis);
    	Date msgDate = resetTime(msgDateTime);
    	
    	if (currDate.equals(msgDate)) 
    		holder.txtInfo.setText(controller.dateToTimeString(msgDateTime));
    	else 
    		holder.txtInfo.setText(controller.dateToDateString(msgDateTime)+" , "+ controller.dateToTimeString(msgDateTime));
    }

    /**
     * @param v gets a view
     * @return  the holder of the view
     * 
     *  create a view holder 
     */
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

    /**
     *  instance of holder
     */
    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public RelativeLayout contentWithBG;
        public ImageView geoIcon_L;
        public ImageView geoIcon_R;
    }
    
}