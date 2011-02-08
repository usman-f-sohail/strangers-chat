/**
 * File		: SearchCriteria.java
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
public class SearchCriteria {
	
	private String sex;
	
	private int startAge;
	
	private int endAge;
	
	private String place;
	
	
	/**
	 * @param sex the sex to set
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return the sex
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @return the age
	 */
	public int getStartAge() {
		return startAge;
	}

	/**
	 * @param age the age to set
	 */
	public void setStartAge(int startAge) {
		this.startAge = startAge;
	}

	/**
	 * @return the endAge
	 */
	public int getEndAge() {
		return endAge;
	}

	/**
	 * @param endAge the endAge to set
	 */
	public void setEndAge(int endAge) {
		this.endAge = endAge;
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
	 * 
	 * @param objectMap
	 */
	public void updateCriteria(ObjectMap<String, Object> objectMap) {
		this.sex = (String) objectMap.get("sex");
		this.startAge = (Integer) objectMap.get("startAge");
		this.endAge = (Integer) objectMap.get("endAge");
		this.place = (String) objectMap.get("place");
	}
	
	@Override
	public String toString() {
		return String.format("Criteria sex %s place %s start age %d end age %d", sex, place, startAge, endAge);
	}

}
