package com.aroundme;

import java.util.List;

import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomUsersAdapter extends BaseAdapter {

    Context context;
    List<UserAroundMe> users;

    CustomUsersAdapter(Context context, List<UserAroundMe> users) {
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

        TextView txtTitle = (TextView) convertView.findViewById(R.id.userName);
        UserAroundMe row_pos = users.get(position);
        txtTitle.setText(row_pos.getDisplayName());

        return convertView;

    }

}