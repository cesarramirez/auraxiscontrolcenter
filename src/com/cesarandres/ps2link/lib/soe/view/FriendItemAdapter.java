package com.cesarandres.ps2link.lib.soe.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cesarandres.ps2link.lib.R;
import com.cesarandres.ps2link.lib.soe.content.CharacterFriend;

public class FriendItemAdapter extends BaseAdapter {

	private ArrayList<CharacterFriend> friends;
	protected LayoutInflater mInflater;

	public FriendItemAdapter(Context context, ArrayList<CharacterFriend> friends) {
		this.mInflater = LayoutInflater.from(context);
		this.friends = friends;
	}

	@Override
	public int getCount() {
		return this.friends.size();
	}

	@Override
	public CharacterFriend getItem(int position) {
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.layout_friend_item, null);

			holder = new ViewHolder();
			holder.friendName = (TextView) convertView.findViewById(R.id.textViewFriendName);
			holder.friendStatus = (TextView) convertView.findViewById(R.id.textViewFriendtatus);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.friendName.setText(getItem(position).getName().getFirst());

		if (getItem(position).getOnline() == 0) {
			holder.friendStatus.setText("Offline");
			holder.friendStatus.setTextColor(Color.RED);
		} else {
			holder.friendStatus.setText("Online");
			holder.friendStatus.setTextColor(Color.GREEN);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView friendName;
		TextView friendStatus;
	}
}