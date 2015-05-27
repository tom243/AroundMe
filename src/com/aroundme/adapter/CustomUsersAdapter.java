package com.aroundme.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.R;
import com.aroundme.R.drawable;
import com.aroundme.R.id;
import com.aroundme.R.layout;
import com.aroundme.controller.AppController;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomUsersAdapter extends BaseAdapter {

    private Context context;
    private List<UserAroundMe> users;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

   public CustomUsersAdapter(Context context, List<UserAroundMe> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {

        return users.size();
    }

    @Override
    public Object getItem(int position) {

        return users.get(position);
    }

    @Override
    public long getItemId(int position) {

        return users.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.users_list_item, null);
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.userName);
        UserAroundMe row_pos = users.get(position);
        txtTitle.setText(row_pos.getDisplayName());
        if(row_pos.getImageUrl() != null)
        	thumbNail.setImageUrl(row_pos.getImageUrl(), imageLoader);
        else
        	thumbNail.setDefaultImageResId(R.drawable.user_default);
        return convertView;

    }

}