package com.aroundme;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.aroundme.controller.AppController;
import com.aroundme.controller.Controller;

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

    CustomConversationsAdapter(Context context, List<ConversationItem> conversations) {
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
        ConversationItem row_pos = conversations.get(position);
        String friendName = controller.getUserNameByMail(row_pos.getFriendMail());
        if (friendName != null){
        	txtTitle.setText(controller.getUserNameByMail(row_pos.getFriendMail()));
        }
        if(row_pos.getImageUrl() != null)
        	thumbNail.setImageUrl(row_pos.getImageUrl(), imageLoader);
        else
        	thumbNail.setDefaultImageResId(R.drawable.user_default);

        return convertView;

    }

}
