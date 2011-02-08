/**
 * File		: Conversation.java
 * Date		: 05-Feb-2011 
 * Owner	: arul
 * Project	: MemberChat
 * Contact	: http://www.arulraj.net
 * History	:
 */
package com.chat.model;

import org.red5.io.utils.ObjectMap;

/**
 * @author arul
 *
 */
public class Conversation {
	
	private String id;
	
	private ChatUser partnerUser;
	
	private Boolean playVideoPermission;
	
	private Boolean playAudioPermission;
	
	/**
	 * Default constructor
	 * @param id
	 */
	public Conversation(String id) {
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
	 * @return the partnerUser
	 */
	public ChatUser getPartnerUser() {
		return partnerUser;
	}

	/**
	 * @param partnerUser the partnerUser to set
	 */
	public void setPartnerUser(ChatUser partnerUser) {
		this.partnerUser = partnerUser;
	}

	/**
	 * @return the playVideoPermission
	 */
	public Boolean getPlayVideoPermission() {
		return playVideoPermission;
	}

	/**
	 * @param playVideoPermission the playVideoPermission to set
	 */
	public void setPlayVideoPermission(Boolean playVideoPermission) {
		this.playVideoPermission = playVideoPermission;
	}

	/**
	 * @return the playAudioPermission
	 */
	public Boolean getPlayAudioPermission() {
		return playAudioPermission;
	}

	/**
	 * @param playAudioPermission the playAudioPermission to set
	 */
	public void setPlayAudioPermission(Boolean playAudioPermission) {
		this.playAudioPermission = playAudioPermission;
	}
	
	/**
	 * Get the AMF compatible object for transfer
	 * @return
	 */
	public ObjectMap<String, Object> getFlexConversation() {
		ObjectMap<String, Object> objectMap = new ObjectMap<String, Object>();
		objectMap.put("id", id);
		if(partnerUser != null) {
			objectMap.put("partnerUser", partnerUser.genFlexChatUser());
		} else {
			objectMap.put("partnerUser", partnerUser);
		}
		objectMap.put("playVideoPermission", playVideoPermission);
		objectMap.put("playAudioPermission", playAudioPermission);
		return objectMap;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		return String.format("Converstation id %s partner user [%s] video permission %s audio permission %s",
							id, partnerUser, playAudioPermission, playVideoPermission);
	}
}
