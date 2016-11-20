package edu.njnu.dailyline.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.njnu.dailyline.utils.StreamTool;
import edu.njnu.dailyline.utils.WeatherParser;

public class QueryService {

	public static List<String> getWeather(String city) throws Exception {

		InputStream is = QueryService.class.getClassLoader()
				.getResourceAsStream("weather_post.xml");
		byte[] data = StreamTool.getBytes(is);
		String xml = new String(data);
		String postXml = xml.replace("$city", city);
		//System.out.println(postXml);

		List<String> result = sendPostMessage(postXml);

		return result;

	}

	private static List<String> sendPostMessage(String postXml) throws Exception {
		boolean result = false;

		String path = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx";
//		String path = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/soap+xml; charset=utf-8");
		String content = postXml;
		byte[] data = content.getBytes();

		conn.setRequestProperty("Content-Length", data.length + "");
		conn.setDoOutput(true);// 允许http协议对外输出

		conn.getOutputStream().write(data);

		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			//byte[] resultdata = StreamTool.getBytes(is);
			//System.out.println("返回数据："+new String(resultdata));
			return WeatherParser.getWeather(is);
		}

		return null;
	}
}
