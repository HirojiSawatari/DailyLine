package edu.njnu.dailyline.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

import edu.njnu.dailyline.R;
import edu.njnu.dailyline.domain.HTTPResult;
import edu.njnu.dailyline.domain.LoginResult;
import edu.njnu.dailyline.utils.StreamTool;
import android.content.Context;

public class LoginService {
	private Context context;
	public LoginService(Context context){
		this.context = context;
	}

	public LoginResult login(String name,String pwd) throws Exception{
		String path = this.context.getResources().getString(R.string.loginurl);
		
		URL url = new URL(path+"?account="+name+"&password="+pwd);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		
		int code = conn.getResponseCode();
		if(code==200){
			
			
			InputStream is = conn.getInputStream();
			byte[] bytes = StreamTool.getBytes(is);
//			System.out.println();
			String res = new String(bytes,"utf-8");
			if(res.contains("data")){
				Gson gson = new Gson();
				LoginResult r = gson.fromJson(res, LoginResult.class);
				
				return r;
			}
		}
		return null;
	}
}
