package edu.njnu.dailyline.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class WeatherParser {

	public static List<String> getWeather(InputStream is) throws Exception{
		
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");
		
		int type  = parser.getEventType();
		List<String> lists = new ArrayList<String>();
		
		while (type!=XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if("string".equals(parser.getName())){
					lists.add(parser.nextText());
				}
				
				break;
			}
			
			type = parser.next();
		}
		
		return lists;
	}
}
