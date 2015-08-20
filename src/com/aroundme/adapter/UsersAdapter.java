package com.aroundme.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.User;
import com.appspot.enhanced_cable_88320.aroundmeapi.model.UserAroundMe;
import com.aroundme.R;
import com.aroundme.common.ChatMessage;
import com.aroundme.common.CustomNetworkImageView;
import com.aroundme.controller.ImagesController;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UsersAdapter extends BaseAdapter {

    private Context context;
    private List<UserAroundMe> users;
    private ImageLoader imageLoader;

   /**
 * @param context context that received
 * @param users list of users
 */
public UsersAdapter(Context context, List<UserAroundMe> users) {
        this.context = context;
        this.users = users;
        this.imageLoader = ImagesController.getInstance().getImageLoader();
    }

    @Override
    public int getCount() {
        if (users != null) 
            return users.size();
         else 
            return 0;
    }

    @Override
    public UserAroundMe getItem(int position) {
        if (users != null)
        	return users.get(position);
        else
        	return null;
    }

    @Override
    public long getItemId(int position) {
        return users.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.users_list_item, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }
    	else
    		holder = (ViewHolder) convertView.getTag();
    	
        if (imageLoader == null)
            imageLoader = ImagesController.getInstance().getImageLoader();
        
        UserAroundMe row_pos = users.get(position);
        holder.txtTitle.setText(row_pos.getDisplayName());
        holder.thumbNail.setDefaultImageResId(R.drawable.user_default);
        holder.thumbNail.setImageUrl(row_pos.getImageUrl(), imageLoader);
        return convertView;
    }
    
    /**
     * @param v gets a view
     * @return the holder of the view
     * 
     * create a view holder 
     */
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.thumbNail = (CustomNetworkImageView) v.findViewById(R.id.users_item_icon);
        holder.txtTitle = (TextView) v.findViewById(R.id.userName);
        return holder;
    }

    /**
     *  instance of holder
     */
    private static class ViewHolder {
        public CustomNetworkImageView thumbNail;
        public TextView txtTitle;
    }
    
}