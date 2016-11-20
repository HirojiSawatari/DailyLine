package edu.njnu.dailyline.domain;

import android.net.Uri;

public class UserEvent {
	
	//����
	private String content;
	//ʱ��
	private int year;
	private int month;
	private int day;
	private int hour;
	private int min;
	//λ��
	private double lat;
	private double lng;
	//ͼƬ����
	private String uri;
	
	public void setContent(String content){
		this.content = content;
	}
	public String getContent(){
		return content;
	}
	
	public void setYear(int year){
		this.year = year;
	}
	public int getYear(){
		return year;
	}
	
	public void setMonth(int month){
		this.month = month;
	}
	public int getMonth(){
		return month;
	}
	
	public void setDay(int day){
		this.day = day;
	}
	public int getDay(){
		return day;
	}
	
	public void setHour(int hour){
		this.hour = hour;
	}
	public int getHour(){
		return hour;
	}
	
	public void setMin(int min){
		this.min = min;
	}
	public int getMin(){
		return min;
	}
	
	public void setLat(double lat){
		this.lat = lat;
	}
	public double getLat(){
		return lat;
	}
	
	public void setLng(double lng){
		this.lng = lng;
	}
	public double getLng(){
		return lng;
	}
	
	public void setUri(String uri){
		this.uri = uri;
	}
	public String getUri(){
		return uri;
	}
}