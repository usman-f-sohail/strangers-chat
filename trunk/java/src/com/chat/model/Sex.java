/**
 * File		: Sex.java
 * Date		: 13-Jan-2011 
 * Owner	: arul
 * Project	: MemberChat
 * Contact	: http://www.arulraj.net
 * Description : 
 * History	:
 */
package com.chat.model;

/**
 * @author arul
 *
 */
public enum Sex {
	MALE("Male"),FEMALE("Female"),ALL("Both");
	
	private String value = null;
	
	Sex(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
}
