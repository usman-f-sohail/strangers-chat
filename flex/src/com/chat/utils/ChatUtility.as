package com.chat.utils
{
	import com.chat.models.ChatUser;
	
	import flash.events.EventDispatcher;
	
	import mx.formatters.DateFormatter;
	
	public final class ChatUtility
	{
		public function ChatUtility()
		{
		}
		
		public static function getCurrentTime():String {
			var currentDate:Date = new Date();
			var dateFormat:DateFormatter = new DateFormatter();
			dateFormat.formatString = "HH:NN:SS> ";
			return dateFormat.format(currentDate);			
		}	
		
		public static function dispatchEvent(event:ChatEvent):void {
			var dispatcher:EventDispatcher  = new EventDispatcher();
			dispatcher.dispatchEvent(event);
		}
		
		public static function getChatUser(obj:Object):ChatUser {
			var chatUser:ChatUser = new ChatUser(obj.id);
			chatUser.age = obj.age;
			chatUser.sex = obj.sex;
			chatUser.loginName = obj.loginName;
			chatUser.nickName = obj.nickName;
			chatUser.place = obj.place;
			chatUser.audio = obj.audio;
			chatUser.video = obj.video;
			chatUser.registeredUser = obj.registeredUser;
			chatUser.allowScan = obj.allowScan; 			
			return chatUser;
		}	
		
		/**
		 * remove the html tags in chat
		 */
		public static function stripHtmlTags(html:String, tags:String = ""):String
		{
		    var tagsToBeKept:Array = new Array();
		    if (tags.length > 0)
			tagsToBeKept = tags.split(new RegExp("\\s*,\\s*"));
		 
		    var tagsToKeep:Array = new Array();
		    for (var i:int = 0; i < tagsToBeKept.length; i++)
		    {
			if (tagsToBeKept[i] != null && tagsToBeKept[i] != "")
			    tagsToKeep.push(tagsToBeKept[i]);
		    }
		 
		    var toBeRemoved:Array = new Array();
		    var tagRegExp:RegExp = new RegExp("<([^>\\s]+)(\\s[^>]+)*>", "g");
		 
		    var foundedStrings:Array = html.match(tagRegExp);
		    for (i = 0; i < foundedStrings.length; i++) 
		    {
			var tagFlag:Boolean = false;
			if (tagsToKeep != null) 
			{
			    for (var j:int = 0; j < tagsToKeep.length; j++)
			    {
			        var tmpRegExp:RegExp = new RegExp("<\/?" + tagsToKeep[j] + "( [^<>]*)*>", "i");
			        var tmpStr:String = foundedStrings[i] as String;
			        if (tmpStr.search(tmpRegExp) != -1) 
			            tagFlag = true;
			    }
			}
			if (!tagFlag)
			    toBeRemoved.push(foundedStrings[i]);
		    }
		    for (i = 0; i < toBeRemoved.length; i++) 
		    {
			var tmpRE:RegExp = new RegExp("([\+\*\$\/])","g");
			var tmpRemRE:RegExp = new RegExp((toBeRemoved[i] as String).replace(tmpRE, "\\$1"),"g");
			html = html.replace(tmpRemRE, "");
		    } 
		    return html;
		}
	}
}
