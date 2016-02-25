package com.student_eg.student_egcom.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.student_eg.student_egcom.R;
import com.student_eg.student_egcom.data.TableItem;

import java.util.List;

public class TableListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<TableItem> feedItems;

	public TableListAdapter(Activity activity, List<TableItem> feedItems) {
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
			convertView = inflater.inflate(R.layout.table_item, null);



		TextView name = (TextView) convertView.findViewById(R.id.course_name);
		TextView timestamp = (TextView) convertView
				.findViewById(R.id.instructor);
		TextView statusMsg = (TextView) convertView
				.findViewById(R.id.day);


		TableItem item = feedItems.get(position);

		name.setText(item.getCourseName());

		timestamp.setText(item.getInstructor());


		statusMsg.setText(item.getDay_no() + "  " + item.getHall_or_place() +"  "+ item.getDuration());







		return convertView;
	}

}
