package com.chat.models
{
	import com.chat.utils.ChatConstants;
	
	[Bindable]
	public class SearchCriteria
	{
		public var startAge:Number = ChatConstants.START_AGE;
		
		public var endAge:Number = ChatConstants.END_AGE;
		
		public var sex:String = Sex.ALL.value;
		
		public var place:String = ChatConstants.ALL;
		
		public function SearchCriteria()
		{
		}
		
		/*public function getStartAge():Number {
			return this.startAge;	
		}
		
		public function setStartAge(startAge:Number):void {
			this.startAge = startAge;
		}
		
		public function getEndAge():Number {
			return this.endAge;	
		}
		
		public function setEndAge(endAge:Number):void {
			this.endAge = endAge;
		}		
		
		public function getSex():Sex {
			return this.sex;
		}	
		
		public function setSex(sex:Sex):void {
			this.sex = sex;
		}
		
		public function getPlace():String {
			return this.place;
		}
		
		public function setPlace(place:String):void {
			this.place = place;
		}*/
	}
}