package edu.njnu.dailyline;

import edu.njnu.dailyline.activity.MainTabActivity;
import edu.njnu.dailyline.activity.SystemSet;
import edu.njnu.dailyline.activity.UserInfoActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentPage5 extends Fragment{

	SharedPreferences wea_info;
	private LinearLayout mAbout;
	private LinearLayout mMessage;
	private LinearLayout mSystem;
	private LinearLayout userLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		
		View view = inflater.inflate(R.layout.fragment_5, null);
		
		userLayout = (LinearLayout) view.findViewById(R.id.userLayout);
		
		userLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),UserInfoActivity.class);
				startActivity(intent);
			}
		});
		
		return view;
		
	}	
	
	public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);

        wea_info = getActivity().getSharedPreferences("wea_info", 0);	//开启sp
		TextView headText = (TextView) getActivity().findViewById(R.id.TextView1);
        //默认阴天界面
		int weaCode = wea_info.getInt("wea_code", 0);
        if(weaCode == 1){	//晴天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_orange));
        }
        if(weaCode == 3){	//雨天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_gray));
        }
        if(weaCode == 4){	//雪天
        	headText.setBackground(getResources().getDrawable(R.drawable.skinpic_blue));
        }
        
        View view1 = (View)getActivity().findViewById(R.id.info_set);
		
		mSystem = (LinearLayout) view1.findViewById(R.id.system);
		mSystem.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent(getActivity(), SystemSet.class);
				getActivity().startActivity(intent);
			}
		});
		
		mMessage = (LinearLayout) view1.findViewById(R.id.message);
		mMessage.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				new AlertDialog.Builder(getActivity())
				.setTitle("联系我们")
				.setMessage("联系邮箱：\nka_tou@outlook.com\n976162878@qq.com")
				.setPositiveButton("确定", null)
				.show();
			}
		});
		
		mAbout = (LinearLayout) view1.findViewById(R.id.about);
		mAbout.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				new AlertDialog.Builder(getActivity())
				.setTitle("关于")
				.setMessage("Copyright He Tao & Wang Jin All right reserved.")
				.setPositiveButton("确定", null)
				.show();
			}
		});
	}
}