package edu.njnu.dailyline.domain;

public class PushMsg {

	private int Id;
	private String PicPath;
	private String Text;
	private String Title;
	private String URL;
	private String Datetime;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getPicPath() {
		return PicPath;
	}
	public void setPicPath(String picPath) {
		PicPath = picPath;
	}
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public String getDateTime() {
		return Datetime;
	}
	public void setDateTime(String dateTime) {
		Datetime = dateTime;
	}
	@Override
	public String toString() {
		return "PushMsg [Id=" + Id + ", PicPath=" + PicPath + ", Text=" + Text
				+ ", Title=" + Title + ", URL=" + URL + ", DateTime="
				+ Datetime + "]";
	}
	
	
}
