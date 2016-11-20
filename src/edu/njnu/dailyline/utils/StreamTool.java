package edu.njnu.dailyline.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamTool {

		public static byte[] getBytes(InputStream is) throws IOException{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();//ÄÚ´æÊä³öÁ÷
				byte[] buffer = new byte[1024];
				
				int len =  0;
				while((len = is.read(buffer))!=-1){
					baos.write(buffer, 0, len);
				}
			is.close();
			baos.flush();
			return baos.toByteArray();
		}
	
}
