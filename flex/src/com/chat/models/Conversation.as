package com.chat.models
{
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.utils.ObjectProxy;
	import mx.utils.StringUtil;
	
	[Bindable]
	public class Conversation extends ObjectProxy
	{
		private static var LOG:ILogger = Log.getLogger('com.chat.models.Conversation');
		
		public var id:String;
		
		public var partnerUser:ChatUser;
		
		public var playVideoPermission:Boolean;
		
		public var playAudioPermission:Boolean;
		
		public function Conversation(id:String)
		{
			this.id = id;
		}
		
		public function updatePartnerUser(result:Object):void {
			this.partnerUser.id = result.id;
			this.partnerUser.age = result.age;
			this.partnerUser.sex = result.sex;
			this.partnerUser.loginName = result.loginName;
			this.partnerUser.nickName = result.nickName;
			this.partnerUser.place = result.place;
			this.partnerUser.audio = result.audio;
			this.partnerUser.video = result.video;
			this.partnerUser.registeredUser = result.registeredUser;
			this.partnerUser.allowScan = result.allowScan; 
			LOG.debug("The partner user is : "+partnerUser.toString());
		}		
		
		public function toString():String {
			return StringUtil.substitute("Conversation user {0} partner info [{1}] play video permission {2} play audio permission {3}",id, partnerUser.toString(), playVideoPermission, playAudioPermission);
		}

	}
}