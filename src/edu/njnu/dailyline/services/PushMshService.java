package edu.njnu.dailyline.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import edu.njnu.dailyline.R;
import edu.njnu.dailyline.domain.PushMsg;
import edu.njnu.dailyline.utils.StreamTool;

public class PushMshService {
	private Context context;

	public PushMshService(Context context) {
		this.context = context;
	}

	public List<PushMsg>  getPushMsg() throws Exception {
		String path = context.getResources().getString(R.string.pushmsgurl);

		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");

		int code = conn.getResponseCode();
		if (code == 200) {
			InputStream is = conn.getInputStream();
			byte[] bytes = StreamTool.getBytes(is);
			String json = new String(bytes, "utf-8");

			Gson gson = new Gson();

			TypeToken<List<PushMsg>> token = new com.google.gson.reflect.TypeToken<List<PushMsg>>() {
			};
			
			return  gson.fromJson(json, token.getType());
			
		}
		return null;

	}
}
