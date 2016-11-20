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
	//定义FragmentTabHost对象
	public static FragmentTabHost mTabHost;
	//定义一个布局
	private LayoutInflater layoutInflater;
	//定义数组来存放Fragment界面
	private Class fragmentArray[] = {FragmentPage1.class,FragmentPage2.class,FragmentPage4.class,FragmentPage5.class};	
	//定义数组来存放按钮图片
	private int mImageViewArray[] = {R.drawable.tab_line_btn,R.drawable.tab_map_btn,
									 R.drawable.tab_friend_btn,R.drawable.tab_more_btn};
	//Tab选项卡的文字
	private String mTextviewArray[] = {"时间轴", "地图", "推荐", "更多"};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS) {
            if (!hasPermission()) {
                //若用户未开启权限，则引导用户开启“Apps with usage access”权限
            	Toast.makeText(getApplicationContext(), "进行前台应用记录需要设置相关许可", Toast.LENGTH_SHORT).show();      
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //检查是否有获取当前运行APP权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPermission()) {
            	Toast.makeText(getApplicationContext(), "进行前台应用记录需要设置相关许可", Toast.LENGTH_SHORT).show(); 
                startActivityForResult(
                        new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }
        
        //启动后台定位服务
		Intent startIntent = new Intent(MainTabActivity.this, StartService.class);  
        startService(startIntent); 
        setContentView(R.layout.main_tab_layout); 
        initView();
    }
	 
	/**
	 * 初始化组件
	 */
	private void initView(){
		//实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		//实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		//得到fragment的个数
		int count = fragmentArray.length;	
				
		for(int i = 0; i < count; i++){	
			//为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			//设置Tab按钮的背景
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}
	}
				
	/**
	 * 给Tab按钮设置图标和文字
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
	
	//菜单键样式
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    menu.add(0,ITEM_1,0,"关于").setIcon(android.R.drawable.ic_menu_info_details);
	    return super.onCreateOptionsMenu(menu);
	}
	//菜单键功能
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case ITEM_1:
			new AlertDialog.Builder(MainTabActivity.this)
			.setTitle("关于")
			.setMessage("Copyright Nanjing Normal University All right reserved.")
			.setPositiveButton("确定", null)
			.show();
			break;
		}	
		return true;
	}
	
	
	//双击返回键退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
			if((System.currentTimeMillis()-exitTime) > 2000){	            	
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();      
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	//检测用户是否对本app开启了“Apps with usage access”权限
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
