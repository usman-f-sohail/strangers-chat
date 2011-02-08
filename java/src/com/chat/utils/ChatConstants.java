/**
 * File		: ChatConstants.java
 * Date		: 13-Jan-2011 
 * Owner	: arul
 * Project	: MemberChat
 * Contact	: http://www.arulraj.net
 * Description : 
 * History	:
 */
package com.chat.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author arul
 *
 */
public final class ChatConstants {
	
	private ChatConstants() {
		
	}
	
	public final static List<String> CONNECTIONS = Arrays.asList("publicConn", "videoConn", "audioConn", "playerConn");
	public final static List<String> ROOMS = Arrays.asList("MemberChat","public","private");	
	
	public final static String GUEST = "Guest";
	public final static String ALL = "All";
	public final static int USER_ID_LENGTH = 8;
}
