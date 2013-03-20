package com.example.firstlog;

public class UserData {
	public static final String ID="id";
	public static final String EMAIL="email";
	public static final String TIMESEC="timesec";
	public static final String LONGITUDE="longitude";
	public static final String LATITUDE="latitude";
	public static final String MARK="mark";
	public static final String SORT="sort";
	public static final String CONTENT="content";
	public static final String COMMENT="comment";
	
	//sort
	public static final String TEXT="text";
	public static final String PHOTO="photo";
	public static final String AUDIO="audio";
	public static final String VIDEO="video";
	
	private String email;
	private String timesec;
	private String longitude;
	private String latitude;
	private String mark;
	private String sort;
	private String content;
	private String comment;

	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getTimesec(){
		return timesec;
	}
	
	public void setTimesec(String timesec){
		this.timesec = timesec;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getMark() {
		return mark;
	}
	
	public void setMark(String mark) {
		this.mark = mark;
	}
	
	public String getSort() {
		return sort;
	}
	
	public void setSort(String sort) {
		this.sort = sort;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
