package edu.njnu.dailyline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {		// 监听开机广播
			Intent intent2 = new Intent(context, StartService.class);
			intent2.setClass(context, StartService.class);
			context.startService(intent2);
		}
		if(intent.getAction().equals("edu.njnu.dailyline.destroy")){	//监听服务Kill广播
			//重新启动后台服务
			Intent intent2 = new Intent(context, StartService.class);
			intent2.setClass(context, StartService.class);
			context.startService(intent2);
		}
	}
}