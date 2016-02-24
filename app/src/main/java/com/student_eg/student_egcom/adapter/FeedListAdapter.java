package com.student_eg.student_egcom.adapter;


import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.student_eg.student_egcom.FeedImageView;
import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.app.AppController;
import com.student_eg.student_egcom.data.FeedItem;


import java.util.List;

public class FeedListAdapter extends BaseAdapter {	
	private Activity activity;
	private LayoutInflater inflater;
	private List<FeedItem> feedItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

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

		if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.timestamp);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.txtStatusMsg);

		NetworkImageView profilePic = (NetworkImageView) convertView
				.findViewById(R.id.profilePic);
		FeedImageView feedImageView = (FeedImageView) convertView
				.findViewById(R.id.feedImage1);

		FeedItem item = feedItems.get(position);

		name.setText(item.getName());

		timestamp.setText(item.getTimeStamp());

		// Chcek for empty status message
		if (!TextUtils.isEmpty(item.getStatus())) {
			statusMsg.setText(item.getStatus());
			statusMsg.setVisibility(View.VISIBLE);
		} else {
			// status is empty, remove from view
			statusMsg.setVisibility(View.GONE);
		}



		// user profile pic
		profilePic.setImageUrl(item.getProfilePic(), imageLoader);

		// Feed image
		if (item.getImge() != null) {
			feedImageView.setImageUrl(item.getImge(), imageLoader);
			//feedImageView.setVisibility(View.VISIBLE);
			feedImageView
					.setResponseObserver(new FeedImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});
		} else {
			//feedImageView.setVisibility(View.GONE);
		}

		return convertView;
	}

}
