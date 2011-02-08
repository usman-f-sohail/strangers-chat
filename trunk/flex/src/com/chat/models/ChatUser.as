package com.chat.models
{
	import com.chat.utils.ChatConstants;
	
	import mx.utils.StringUtil;
	
	[Bindable]
	public class ChatUser
	{
		public var id:String;
		
		public var nickName:String;
		
		public var loginName:String;
		
		public var age:Number = ChatConstants.START_AGE;
		
		public var sex:String = Sex.FEMALE.value;
		
		public var place:String = ChatConstants.ALL;
		
		public var video:Boolean = false;
		
		public var audio:Boolean = false;
		
		public var registeredUser:Boolean = false;
		
		public var allowScan:Boolean = false;
		
		public function ChatUser(id:String)
		{
			this.id = id;
		}
		
		public function toString():String {
			return StringUtil.substitute("The chat user id {0} age {1} sex {2} loginname {3} " + 
					"nickname {4} audio {5} video {6} registereduser {7} allowscan {8}", id, age, sex, loginName, nickName, audio, video, registeredUser, allowScan);
		}
	}
}