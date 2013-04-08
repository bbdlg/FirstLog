package com.example.firstlog;

public class UserData {
	public static final String ID			= "id";
	public static final String USERNAME		= "username";
	public static final String TIMESEC		= "timesec";
	public static final String LONGITUDE	= "longitude";
	public static final String LATITUDE		= "latitude";
	public static final String MARK			= "mark";
	public static final String TEXT			= "text";
	public static final String AUDIO 		= "audio";
	public static final String VIDEO		= "video";
	public static final String PHOTO		= "photo";
	public static final String ISDELETE		= "isdelete";
	
	private String email;
	private String timesec;
	private String longitude;
	private String latitude;
	private String mark;
	private String text;
	private String audio;
	private String video;
	private String photo;
	private String isdeleted;
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the timesec
	 */
	public String getTimesec() {
		return timesec;
	}
	/**
	 * @param timesec the timesec to set
	 */
	public void setTimesec(String timesec) {
		this.timesec = timesec;
	}
	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public String getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}
	/**
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the audio
	 */
	public String getAudio() {
		return audio;
	}
	/**
	 * @param audio the audio to set
	 */
	public void setAudio(String audio) {
		this.audio = audio;
	}
	/**
	 * @return the video
	 */
	public String getVideo() {
		return video;
	}
	/**
	 * @param video the video to set
	 */
	public void setVideo(String video) {
		this.video = video;
	}
	/**
	 * @return the photo
	 */
	public String getPhoto() {
		return photo;
	}
	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	/**
	 * @return the deleted
	 */
	public String getIsdeleted() {
		return isdeleted;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setIsdeleted(String isdeleted) {
		this.isdeleted = isdeleted;
	}

	
}
