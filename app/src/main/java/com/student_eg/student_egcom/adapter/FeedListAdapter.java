package com.student_eg.student_egcom.adapter;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.data.FeedItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FeedListAdapter extends BaseAdapter {	
	private Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;

	public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
		this.activity = activity;
		this.feedItems = feedItems;
	}

	@Override
	public int getCount() {
		return feedItems.size();
	}

	@Override
	public Object getItem(int location) {
		return feedItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override


	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.feed_item, null);


		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg);

		ImageView profilePic = (ImageView) convertView
				.findViewById(R.id.profilePic);
		ImageView feedImageView = (ImageView) convertView
				.findViewById(R.id.feedImage1);

		FeedItem item = feedItems.get(position);

		name.setText(item.getName());

		timestamp.setText(manipulateDateFormat(item.getTimeStamp()));

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getStatus())) {
			statusMsg.setText(item.getStatus());
			statusMsg.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			statusMsg.setVisibility(View.GONE);
		}



		// user profile pic
		Glide.with(activity)
				.load(item.getProfilePic())
				.thumbnail(0.5f)
				.crossFade()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(profilePic);

		// feed image view
		if (item.getImge() != null)
			feedImageView.setVisibility(View.VISIBLE);
		Glide.with(activity)
				.load(item.getImge())
				.thumbnail(0.5f)
				.crossFade()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(feedImageView);

		return convertView;
	}


	public static String manipulateDateFormat(String post_date){

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = (Date)formatter.parse(post_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (date != null) {
			// Converting timestamp into x ago format
			CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
					Long.parseLong(String.valueOf(date.getTime())),
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
			return timeAgo + "";
		}else {
			return post_date;
		}
	}

}
