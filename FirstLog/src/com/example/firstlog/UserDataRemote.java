package com.example.firstlog;

public class UserDataRemote {
	public static final String TIMESEC		= "timesec";
	public static final String LONGITUDE	= "longitude";
	public static final String LATITUDE		= "latitude";
	public static final String MARK			= "mark";
	public static final String TEXT			= "text";
	public static final String AUDIO 		= "audio";
	public static final String VIDEO		= "video";
	public static final String PHOTO		= "photo";
	
	public static final String TIMESEC_INDEX = "timesec_index";
	
	private String timesec;
	private String longitude;
	private String latitude;
	private String mark;
	private String text;
	private String audio;
	private String video;
	private String photo;
		
	public UserDataRemote(String timesec, String longitude, String latitude,
			String mark, String text, String audio, String video, String photo) {
		super();
		this.timesec = timesec;
		this.longitude = longitude;
		this.latitude = latitude;
		this.mark = mark;
		this.text = text;
		this.audio = audio;
		this.video = video;
		this.photo = photo;
	}

	@Override
	public String toString() {
		return "timesec : " + timesec + ", longitude : " + longitude
				+ ", latitude : " + latitude + ", mark : " + mark  + ", text : " + text + ", audio : "
				+ audio + ", video : " + video + ", photo : " + photo;
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	
}
