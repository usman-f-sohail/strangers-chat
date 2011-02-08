/**
 * File		: ChatUser.java
 * Date		: 13-Jan-2011 
 * Owner	: arul
 * Project	: MemberChat
 * Contact	: http://www.arulraj.net
 * Description : 
 * History	:
 */
package com.chat.model;

import org.red5.io.utils.ObjectMap;

/**
 * @author arul
 *
 */
public class ChatUser {
	
	private String id;
	
	private String nickName;
	
	private String loginName;
	
	private int age;
	
	private String sex;
	
	private String place;
	
	private Boolean video;
	
	private Boolean audio;
	
	private Boolean registeredUser;
	
	private Boolean allowScan;
	
	/**
	 * 
	 */
	public ChatUser(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	/**
	 * @return the place
	 */
	public String getPlace() {
		return place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(String place) {
		this.place = place;
	}

	/**
	 * @return the video
	 */
	public Boolean isVideo() {
		return video;
	}

	/**
	 * @param video the video to set
	 */
	public void setVideo(Boolean video) {
		this.video = video;
	}

	/**
	 * @return the audio
	 */
	public Boolean isAudio() {
		return audio;
	}

	/**
	 * @param audio the audio to set
	 */
	public void setAudio(Boolean audio) {
		this.audio = audio;
	}
	
	/**
	 * @return the registeredUser
	 */
	public Boolean isRegisteredUser() {
		return registeredUser;
	}

	/**
	 * @param registeredUser the registeredUser to set
	 */
	public void setRegisteredUser(Boolean registeredUser) {
		this.registeredUser = registeredUser;
	}

	/**
	 * @return the allowScan
	 */
	public Boolean isAllowScan() {
		return allowScan;
	}

	/**
	 * @param allowScan the allowScan to set
	 */
	public void setAllowScan(Boolean allowScan) {
		this.allowScan = allowScan;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("ChatUser id %s Nickname %s LoginName %s age %d sex %s video %s audio %s registered %s allowscan %s",
				id, nickName, loginName, age, sex, video, audio, registeredUser, allowScan);
	}
	
	/**
	 * It returns object to easily bind with flex chatuser object
	 * @return
	 */
	public ObjectMap<String, Object> genFlexChatUser() {
		ObjectMap<String, Object> objectMap = new ObjectMap<String, Object>();
		objectMap.put("id", id);
		objectMap.put("age", age);
		objectMap.put("sex", sex);
		objectMap.put("isRegisteredUser", registeredUser);
		objectMap.put("place", place);
		objectMap.put("loginName", loginName);
		objectMap.put("nickName", nickName);
		objectMap.put("video", video);
		objectMap.put("audio", audio);
		objectMap.put("allowScan",allowScan);
		return objectMap;
	}
	
	/**
	 * This will update ChatUser object from flex 
	 * @param flexChatUser
	 * @param chatUser
	 */
	public void updateChatUser(ObjectMap<String, Object> flexChatUser) {
		this.registeredUser = (Boolean)flexChatUser.get("registeredUser");
		
		if(!registeredUser) {
			if(this.id == null) {
				this.id = (String) flexChatUser.get("id");
			}
			this.age = (Integer)flexChatUser.get("age");
			this.sex = (String) flexChatUser.get("sex");
			this.place = (String) flexChatUser.get("place");
			this.loginName = (String)flexChatUser.get("loginName");
			this.nickName = (String)flexChatUser.get("nickName");
			this.video = (Boolean) flexChatUser.get("video");
			this.audio = (Boolean) flexChatUser.get("audio");
			this.allowScan = (Boolean) flexChatUser.get("allowScan");
			
			System.out.println("this.tostring() : "+this.toString());
		} else {
			/*To do - Update user info from DB*/
		}
	}	
	
	
}
