package edu.njnu.dailyline.activity;

import edu.njnu.dailyline.FragmentPage1;
import edu.njnu.dailyline.FragmentPage2;
import edu.njnu.dailyline.FragmentPage3;
import edu.njnu.dailyline.FragmentPage4;
import edu.njnu.dailyline.FragmentPage5;
import edu.njnu.dailyline.R;
import edu.njnu.dailyline.StartService;
import edu.njnu.dailyline.utils.StepDetector;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class MainTabActivity extends FragmentActivity{		
	private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
	private long exitTime = 0;
	private static final int ITEM_1=Menu.FIRST;
	//����FragmentTabHost����
	public static FragmentTabHost mTabHost;
	//����һ������
	private LayoutInflater layoutInflater;
	//�������������Fragment����
	private Class fragmentArray[] = {FragmentPage1.class,FragmentPage2.class,FragmentPage4.class,FragmentPage5.class};	
	//������������Ű�ťͼƬ
	private int mImageViewArray[] = {R.drawable.tab_line_btn,R.drawable.tab_map_btn,
									 R.drawable.tab_friend_btn,R.drawable.tab_more_btn};
	//Tabѡ�������
	private String mTextviewArray[] = {"ʱ����", "��ͼ", "�Ƽ�", "����"};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //���û�δ����Ȩ�ޣ��������û�������Apps with usage access��Ȩ��
            	Toast.makeText(getApplicationContext(), "����ǰ̨Ӧ�ü�¼��Ҫ����������", Toast.LENGTH_SHORT).show();      
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //����Ƿ��л�ȡ��ǰ����APPȨ��
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
            	Toast.makeText(getApplicationContext(), "����ǰ̨Ӧ�ü�¼��Ҫ����������", Toast.LENGTH_SHORT).show(); 
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
        
        //������̨��λ����
		Intent startIntent = new Intent(MainTabActivity.this, StartService.class);  
        startService(startIntent); 
        setContentView(R.layout.main_tab_layout); 
        initView();
    }
	 
	/**
	 * ��ʼ�����
	 */
	private void initView(){
		//ʵ�������ֶ���
		layoutInflater = LayoutInflater.from(this);
		//ʵ����TabHost���󣬵õ�TabHost
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		//�õ�fragment�ĸ���
		int count = fragmentArray.length;	
				
		for(int i = 0; i < count; i++){	
			//Ϊÿһ��Tab��ť����ͼ�ꡢ���ֺ�����
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//��Tab��ť��ӽ�Tabѡ���
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			//����Tab��ť�ı���
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}
	}
				
	/**
	 * ��Tab��ť����ͼ�������
	 */
	private View getTabItemView(int index){
		View view = layoutInflater.inflate(R.layout.tab_item_view, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTextviewArray[index]);
		textView.setTextColor(Color.rgb(112, 112, 112));
		return view;
	}
	
	//�˵�����ʽ
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    menu.add(0,ITEM_1,0,"����").setIcon(android.R.drawable.ic_menu_info_details);
	    return super.onCreateOptionsMenu(menu);
	}
	//�˵�������
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case ITEM_1:
			new AlertDialog.Builder(MainTabActivity.this)
			.setTitle("����")
			.setMessage("Copyright Nanjing Normal University All right reserved.")
			.setPositiveButton("ȷ��", null)
			.show();
			break;
		}	
		return true;
	}
	
	
	//˫�����ؼ��˳�
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
			if((System.currentTimeMillis()-exitTime) > 2000){	            	
	            Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();      
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	//����û��Ƿ�Ա�app�����ˡ�Apps with usage access��Ȩ��
	@SuppressLint("NewApi")
	private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
