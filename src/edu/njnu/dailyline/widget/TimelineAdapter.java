package edu.njnu.dailyline.widget;

import java.util.List;
import java.util.Map;

import edu.njnu.dailyline.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimelineAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;
	private LayoutInflater inflater;

	public TimelineAdapter(Context context, List<Map<String, Object>> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {

		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.listview_item, null);
			viewHolder = new ViewHolder();

			viewHolder.firstIcon = (ImageView) convertView.findViewById(R.id.firstIcon);
			viewHolder.secondIcon = (ImageView) convertView.findViewById(R.id.secondIcon);
			viewHolder.thirdIcon = (ImageView) convertView.findViewById(R.id.thirdIcon);
			viewHolder.fourthIcon = (ImageView) convertView.findViewById(R.id.fourthIcon);
			viewHolder.fifthIcon = (ImageView) convertView.findViewById(R.id.fifthIcon);
			viewHolder.time = (TextView) convertView.findViewById(R.id.timeView);
			viewHolder.eventLayout = (RelativeLayout) convertView.findViewById(R.id.relative2);
			viewHolder.eventName = (TextView) convertView.findViewById(R.id.title2);
			viewHolder.noAppInfo = (TextView) convertView.findViewById(R.id.locText);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Drawable firstIconDraw = (Drawable) list.get(position).get("firstIcon");
		viewHolder.firstIcon.setImageDrawable(firstIconDraw);

		Drawable secondIconDraw = (Drawable) list.get(position).get("secondIcon");
		viewHolder.secondIcon.setImageDrawable(secondIconDraw);
		
		Drawable thirdIconDraw = (Drawable) list.get(position).get("thirdIcon");
		viewHolder.thirdIcon.setImageDrawable(thirdIconDraw);
		
		Drawable fourthIconDraw = (Drawable) list.get(position).get("fourthIcon");
		viewHolder.fourthIcon.setImageDrawable(fourthIconDraw);
		
		Drawable fifthIconDraw = (Drawable) list.get(position).get("fifthIcon");
		viewHolder.fifthIcon.setImageDrawable(fifthIconDraw);
		
		String timeText = (String) list.get(position).get("time");
		viewHolder.time.setText(timeText);
		
		String noApp = (String) list.get(position).get("noApp");
		viewHolder.noAppInfo.setText(noApp);
		
		String isShowEvent = (String) list.get(position).get("isShowEvent");
		if(isShowEvent == "1"){
			viewHolder.eventLayout.setVisibility(View.VISIBLE);
		}
		else{
			viewHolder.eventLayout.setVisibility(View.INVISIBLE);
		}
		
		String eventName = (String) list.get(position).get("eventName");
		viewHolder.eventName.setText(eventName);
		
		return convertView;
	}

	static class ViewHolder {
		public TextView year;
		public TextView month;
		public ImageView firstIcon;
		public ImageView secondIcon;
		public ImageView thirdIcon;
		public ImageView fourthIcon;
		public ImageView fifthIcon;
		public TextView time;
		public RelativeLayout eventLayout;
		public TextView noAppInfo;
		public TextView eventName;
	}
}
