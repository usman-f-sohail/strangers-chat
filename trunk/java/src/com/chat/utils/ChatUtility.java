/**
 * File		: ChatUtility.java
 * Date		: 13-Jan-2011 
 * Owner	: arul
 * Project	: MemberChat
 * Contact	: http://www.arulraj.net
 * Description : 
 * History	:
 */
package com.chat.utils;

import static java.lang.Math.round; 
import static java.lang.Math.random; 
import static java.lang.Math.pow; 
import static java.lang.Math.abs; 
import static java.lang.Math.min; 
import static org.apache.commons.lang.StringUtils.leftPad;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chat.model.ChatUser;

/**
 * @author arul
 *
 */
public final class ChatUtility {
	
	private static final Logger LOG = LoggerFactory.getLogger(ChatUtility.class);
	
	/**
	 * default constructor
	 */
	private ChatUtility() {
		
	}
	
	/**
	 * Generate a string with the particular length
	 * @param length
	 * @return
	 */
	public static String generateString(int length) { 
		StringBuffer sb = new StringBuffer(); 
		for (int i = length; i > 0; i -= 12) { 
			int n = min(12, abs(i)); 
			sb.append(leftPad(Long.toString(round(random() * pow(36, n)), 36), n, '0')); 
		}
		return sb.toString().toUpperCase(); 
	}	
	
	/**
	 * Get the chat user from flex object map
	 * @param flexChatUser
	 * @param chatUser
	 */
	public static ChatUser getChatUser(HashMap<String, Object> flexChatUser) {
		ChatUser user = new ChatUser((String) flexChatUser.get("id"));
		user.setAge((Integer)flexChatUser.get("age"));
		user.setPlace((String) flexChatUser.get("place"));
		user.setSex((String) flexChatUser.get("sex"));
		user.setNickName((String)flexChatUser.get("nickName"));
		user.setLoginName((String)flexChatUser.get("loginName"));
		user.setVideo((Boolean)flexChatUser.get("video"));
		user.setAudio((Boolean) flexChatUser.get("audio"));
		user.setAllowScan((Boolean) flexChatUser.get("allowScan"));
		user.setRegisteredUser((Boolean)flexChatUser.get("registeredUser"));
		return user;
	}
	
}
