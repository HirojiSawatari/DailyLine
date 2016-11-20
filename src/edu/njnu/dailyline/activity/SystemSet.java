package edu.njnu.dailyline.activity;

import edu.njnu.dailyline.R;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SystemSet extends PreferenceActivity {

	SharedPreferences set_info;
	
	//界面样式
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.system_set);
		//获取preferences
		set_info = PreferenceManager.getDefaultSharedPreferences(this);

		Preference noti = findPreference("notif");
		noti.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			public boolean onPreferenceClick(Preference preference) { 
		    	
		        return true;  
		    }  
		}); 
	}
}
