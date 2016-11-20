package edu.njnu.dailyline.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.njnu.dailyline.utils.StreamTool;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class GetImgService {

	public static void getImg(String path) throws Exception{
		
		int start = path.lastIndexOf("/");
		String iconname = path.substring(start+1,path.length());
		File file = new File(Environment.getExternalStorageDirectory(),iconname);
		FileOutputStream fos = new FileOutputStream(file);
		
		
		URL url = new URL(path);
		HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		
		int code = conn.getResponseCode();
		if(code==200){
			InputStream is = conn.getInputStream();
			byte[]bytes = StreamTool.getBytes(is);
			
			//°ÑÍ¼Æ¬±£´æµ½sdk¿¨
			fos.write(bytes);
			fos.flush();
			fos.close();
			
//			Bitmap bitmap = BitmapFactory.decodeStream(is);
//			return bitmap;
		}
//		return null;
	}
}
