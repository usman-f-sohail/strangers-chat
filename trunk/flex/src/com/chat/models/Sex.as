package com.chat.models
{
	public final class Sex
	{
		public static const MALE:Sex = new Sex("Male");
		
		public static const FEMALE:Sex = new Sex("Female");
		
		public static const ALL:Sex = new Sex("Both");
		
		public var value:String;
		
		public function Sex(value:String)
		{
			this.value = value;
		}
		
		/*public function get value():String {
			return this.value;
		}
		
		public function set value(value:String):void {
			this.value = value;
		}*/

	}
}