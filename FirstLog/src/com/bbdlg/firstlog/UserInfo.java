package com.bbdlg.firstlog;

public class UserInfo {
	public static final String ID="id";
	public static final String EMAIL="email";
	public static final String PASSWD="passwd";

	private String id;
	private String email;
	private String passwd;

	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
}
