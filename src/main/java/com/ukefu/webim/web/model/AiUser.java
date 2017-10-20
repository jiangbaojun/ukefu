package com.ukefu.webim.web.model;

import com.ukefu.util.IP;

public class AiUser implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id ;
	private String userid ;
	private long time ;
	private IP ipdata ;
	
	public AiUser(String id , String userid, long time , IP ipdata){
		this.id = id ;
		this.userid = userid ;
		this.time = time ;
		this.ipdata = ipdata ;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public IP getIpdata() {
		return ipdata;
	}

	public void setIpdata(IP ipdata) {
		this.ipdata = ipdata;
	}
}
