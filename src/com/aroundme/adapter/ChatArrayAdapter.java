package com.aroundme.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.transition.Visibility;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.aroundme.R;
import com.aroundme.common.ChatMessage;
import com.aroundme.controller.ImagesController;
import com.google.api.client.repackaged.com.google.common.annotations.VisibleForTesting;

public class ChatArrayAdapter extends ArrayAdapter {

	private TextView chatText;
	private ImageView geoMsgIcon;
	private List chatMessageList = new ArrayList();
	private RelativeLayout singleMessageContainer;
	
	public void add(ChatMessage object) {
		chatMessageList.add(object);
		super.add(object);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return (ChatMessage) this.chatMessageList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.conversation_singlemessage, parent, false);
		}
		singleMessageContainer = (RelativeLayout) row.findViewById(R.id.singleMessageContainer);
		ChatMessage chatMessageObj = getItem(position);
		singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
		chatText = (TextView) row.findViewById(R.id.singleMessage);
		String str = chatMessageObj.message;
		chatText.setText(chatMessageObj.message);
		chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_a : R.drawable.bubble_b);
		
		geoMsgIcon = (ImageView) row.findViewById(R.id.singleMessage_icon);
		if (chatMessageObj.left) {	// move the icon to left
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)chatText.getLayoutParams(); 
			params.addRule(RelativeLayout.RIGHT_OF, geoMsgIcon.getId()); 
			chatText.setLayoutParams(params);
		}
		else {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)geoMsgIcon.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			geoMsgIcon.setLayoutParams(params); //causes layout update
			params = (RelativeLayout.LayoutParams)chatText.getLayoutParams(); 
			params.addRule(RelativeLayout.LEFT_OF, geoMsgIcon.getId()); 
			chatText.setLayoutParams(params);
		}
		//
		if (chatMessageObj.locationBased) 
			geoMsgIcon.setVisibility(View.VISIBLE);
		else // hide the icon because it is not geo message
			geoMsgIcon.setVisibility(View.GONE);
		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}