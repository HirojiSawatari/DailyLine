package edu.njnu.dailyline;

import edu.njnu.dailyline.widget.RefreshableView;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class FragmentPage3 extends Fragment implements RefreshableView.RefreshListener {

	private RefreshableView mRefreshableView;
	LinearLayout linearLayout1;
	SharedPreferences wea_info;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		View view = inflater.inflate(R.layout.fragment_3,container, false);
		//��ʼ����λ�����Ͽ���������
		linearLayout1 = (LinearLayout)view.findViewById(R.id.point_collec);
		//��ʼ����
		getTable();
		return view;
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);
		//��ʼ��ˢ�¿ؼ�
		mRefreshableView = (RefreshableView)getActivity().findViewById(R.id.refresh_root);
		mRefreshableView.setRefreshListener(this);

        wea_info = getActivity().getSharedPreferences("wea_info", 0);	//����sp
		TextView headText = (TextView) getActivity().findViewById(R.id.TextView1);
        //Ĭ���������
		int weaCode = wea_info.getInt("wea_code", 0);
        if(weaCode == 1){	//����
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_orange));
        }
        if(weaCode == 3){	//����
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_gray));
        }
        if(weaCode == 4){	//ѩ��
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_blue));
        }
	}
	
	
	//��ȡ�㳡��
	public void getTable() {
		//TODO:����ʮ���㳡��Ϣ�����Ϊ����List��ʽ
		/*
		//��¼���ݿ��¼֮������Ϣ
		int nextDay = 0;
		String nextPoi = null;
		//��¼���ɿ�ͼʱ��ʱ����Ϣ�����ʱ���
		int endHour = 25;
		int endMinute = 0;
		int endSecond = 0;
		//����SQLite������е�
		db = getActivity().openOrCreateDatabase("Track_info.db", Context.MODE_PRIVATE, null);  //��SQLite���ݿ�
		Cursor c = db.query("track", null, null, null, null, null, null);  //����α�
		if(c.moveToLast()){//�ж��α��Ƿ�Ϊ��
			while(!c.isBeforeFirst()){	//�ж�ָ������Ƿ���Ԫ��
				int reid = c.getInt(c.getColumnIndex("_id"));
				int p_year = c.getInt(c.getColumnIndex("year"));
				int p_month = c.getInt(c.getColumnIndex("month"));
				int p_day = c.getInt(c.getColumnIndex("day"));
				int p_hour = c.getInt(c.getColumnIndex("hour"));
				int p_minute = c.getInt(c.getColumnIndex("minute"));
				int p_second = c.getInt(c.getColumnIndex("second"));
				String p_wea = c.getString(c.getColumnIndex("weather"));
				String p_poi = c.getString(c.getColumnIndex("address"));
				if(p_wea == null){
					p_wea = "ȱ��";
				}
				c.moveToPrevious();
				
				if(endHour == 25){	//��ʱ�������ǵ�һ����
					endHour = p_hour;
					endMinute = p_minute;
					endSecond = p_second;
				}
				
				if(reid > 1){
					nextDay = c.getInt(c.getColumnIndex("day"));
					nextPoi = c.getString(c.getColumnIndex("address"));
				}
				
				if(!nextPoi.equals(p_poi) || nextDay != p_day || reid == 1){	//���л�poi��򾭹�һ���Ϊ���һ���㼴��ӡ�ý��
					//��������Լ�ͼ��������
					final LinearLayout pwTable = new LinearLayout(getActivity());
					pwTable.setOrientation(LinearLayout.HORIZONTAL);
					pwTable.setBackgroundColor(Color.WHITE);
					pwTable.setPadding(30, 30, 30, 30);
					LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					margin.setMargins(40, 35, 40, 0);	//���ò��־����������Ҿ���
				
				
					//���˵������������
					final LinearLayout pointTable = new LinearLayout(getActivity());
					pointTable.setOrientation(LinearLayout.VERTICAL);
					pointTable.setBackgroundColor(Color.WHITE);	
				
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
					params1.weight = 3.0f;	//ռ�ķ�֮������

					TextView p_id = new TextView(getActivity());
					TextView p_time = new TextView(getActivity());
					TextView p_loc = new TextView(getActivity());
					p_id.setTextColor(Color.BLACK);
					p_time.setTextColor(Color.BLACK);
					p_loc.setTextColor(Color.BLACK);
					p_id.setText("���ڣ�" + p_year + "��" + p_month + "��" + p_day + "��");
					p_time.setText("ʱ��Σ�" + p_hour + "ʱ" + p_minute + "�� �� " + endHour + "ʱ" + endMinute + "��");
					p_loc.setText("�ص㣺" + p_poi);
					pointTable.addView(p_id);
					pointTable.addView(p_time);
					pointTable.addView(p_loc);
					pwTable.addView(pointTable, params1);
				
					//�������ͼ��������
					final ImageView weaView = new ImageView(getActivity());
					weaView.setBackgroundColor(Color.WHITE);	
				
					LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
					params2.weight = 1.0f;	//ռ�ķ�֮һ����
					weaView.setLayoutParams(params2);
				
					if(p_wea.indexOf("��") != -1){
						weaView.setImageResource(R.drawable.wea_hare);
					}else if(p_wea.indexOf("����") != -1){
						weaView.setImageResource(R.drawable.wea_kumori1);
					}else if(p_wea.indexOf("��") != -1){
						weaView.setImageResource(R.drawable.wea_kumori2);
					}else if(p_wea.indexOf("��") != -1){
						weaView.setImageResource(R.drawable.wea_kaminari);
					}else if(p_wea.indexOf("���ѩ") != -1){
						weaView.setImageResource(R.drawable.wea_mizore);
					}else if(p_wea.indexOf("����") != -1){
						weaView.setImageResource(R.drawable.wea_niwakaame);
					}else if(p_wea.indexOf("��") != -1){
						weaView.setImageResource(R.drawable.wea_ame);
					}else if(p_wea.indexOf("ѩ") != -1){
						weaView.setImageResource(R.drawable.wea_yuki);
					}else if(p_wea.indexOf("��") != -1 || p_wea.indexOf("��") != -1){
						weaView.setImageResource(R.drawable.wea_kiri);
					}else{
						weaView.setImageResource(R.drawable.wea_nashi);
					}
				
					pwTable.addView(weaView);
					//�����ܲ���
					linearLayout1.addView(pwTable, margin);
					//������¼��ʱ�Ľ�����
					if(reid > 1){
						endHour = c.getInt(c.getColumnIndex("hour"));
						endMinute = c.getInt(c.getColumnIndex("minute"));
						endSecond = c.getInt(c.getColumnIndex("second"));
					}
				}
			}
		}
		c.close();
		db.close();
		*/
	}

	public void removeTable() {
		//�����������ʾ�����е���Ϣ
		int count = linearLayout1.getChildCount();
		for(int i = count - 1; i > -1; i --){
			linearLayout1.removeViewAt(i);
		}
	}
	
	public void onRefresh(RefreshableView view) {
		// TODO �Զ����ɵķ������
		removeTable();
		getTable();
		mRefreshableView.finishRefresh();
		Toast.makeText(getActivity().getApplicationContext(), R.string.toast_text, Toast.LENGTH_SHORT).show();
	}
}