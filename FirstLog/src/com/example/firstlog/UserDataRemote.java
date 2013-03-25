package com.example.firstlog;

public class UserDataRemote {
	public static final String TIMESEC		= "timesec";
	public static final String LONGITUDE	= "longitude";
	public static final String LATITUDE		= "latitude";
	public static final String MARK			= "mark";
	public static final String SORT			= "sort";
	public static final String CONTENT		= "content";
	public static final String DELETE		= "delete";
	
	public static final String TIMESEC_INDEX = "timesec_index";
	
	//sort
	public static final String TEXT		= "text";
	public static final String PHOTO	= "photo";
	public static final String AUDIO	= "audio";
	public static final String VIDEO	= "video";
	
	public static final int ENUM_TEXT	= 1;
	public static final int ENUM_PHOTO	= 2;
	public static final int ENUM_AUDIO	= 3;
	public static final int ENUM_VIDEO	= 4;
	
	private String timesec;
	private String longitude;
	private String latitude;
	private String mark;
	private String sort;
	private String content;
		
	public UserDataRemote(String timesec, String longitude, String latitude,
			String mark, String sort, String content) {
		super();
		this.timesec = timesec;
		this.longitude = longitude;
		this.latitude = latitude;
		this.mark = mark;
		this.sort = sort;
		this.content = content;
	}

	@Override
	public String toString() {
		return "timesec : " + timesec + ", longitude : " + longitude
				+ ", latitude : " + latitude + ", mark : " + mark + ", sort : "
				+ sort + ", content : " + content;
	}

	public String getTimesec() {
		return timesec;
	}

	public void setTimesec(String timesec) {
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
	
	
}
