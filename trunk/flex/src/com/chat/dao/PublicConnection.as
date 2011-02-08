package com.chat.dao
{
	import com.chat.models.ChatUser;
	import com.chat.models.Conversation;
	import com.chat.utils.ChatConstants;
	import com.chat.utils.ChatEvent;
	import com.chat.utils.ChatUtility;
	import com.chat.views.ConnectingPopup;
	
	import flash.events.AsyncErrorEvent;
	import flash.events.Event;
	import flash.events.NetStatusEvent;
	import flash.net.NetConnection;
	import flash.net.ObjectEncoding;
	
	import mx.core.FlexGlobals;
	import mx.core.IFlexDisplayObject;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.managers.PopUpManager;
	
	import spark.components.Application;
	import spark.components.HGroup;	
	
	[Bindable]
	public class PublicConnection extends NetConnection
	{
		private static var LOG:ILogger = Log.getLogger('com.chat.dao.PublicConnection');
		private var mainApp:HGroup;
		private var user:ChatUser;
		private var conversation:Conversation;
		private var hostname:String;
		private var port:Number;
		private var isHttps:Boolean;
		
		private var connectingPopup:IFlexDisplayObject;
		
		public function PublicConnection()
		{
			this.mainApp = FlexGlobals.topLevelApplication.mainGroup;
			this.hostname = FlexGlobals.topLevelApplication.hostname;
			this.port = FlexGlobals.topLevelApplication.rtmpPort;
			this.isHttps = FlexGlobals.topLevelApplication.issecure;
			this.user = FlexGlobals.topLevelApplication.chatUser;
			this.conversation = FlexGlobals.topLevelApplication.conversation;

			connectingPopup = PopUpManager.createPopUp(FlexGlobals.topLevelApplication as Application, ConnectingPopup, true);
			PopUpManager.centerPopUp(connectingPopup);
			super();
		}
		
		public function createConnection():void 
		{	
			LOG.info("you are connecting to "+this.hostname+"...");
			this.objectEncoding = ObjectEncoding.AMF0;
			this.addEventListener(NetStatusEvent.NET_STATUS, netConnectionStatus);
			this.addEventListener(Event.CLOSE, netConnectionClose);
			this.addEventListener(AsyncErrorEvent.ASYNC_ERROR, asyncErrorHandler);
			this.proxyType = "best";
			this.client = this;

			if (this.isHttps) {
				this.connect("rtmps://" + this.hostname + ":"+this.port+"/"+ChatConstants.MAIN_APP, "publicConn",user);
			} else {
				this.connect("rtmp://" + this.hostname + ":"+this.port+"/"+ChatConstants.MAIN_APP, "publicConn",user);
			}
		}

		private function netConnectionStatus(event:NetStatusEvent):void {
			LOG.debug("Status : "+event.info.code);
			if (event.info.code == "NetConnection.Connect.Success") {
				PopUpManager.removePopUp(connectingPopup);
				this.dispatchEvent(new ChatEvent(ChatEvent.PUBLIC_CONN_SUCCESS));
				/*setting connection object for other class*/
			} else if (event.info.code == "NetConnection.Connect.Closed") {

			} else if (event.info.code == "NetConnection.Connect.Failed") {

			} else if (event.info.code == "NetStream.Play.UnpublishNotify") {

			}			
		} 
		
		private function netConnectionClose(event:NetStatusEvent):void {
			LOG.debug("net Connection close "+event.info);
		}
		
		private function asyncErrorHandler(event:AsyncErrorEvent):void {
			LOG.debug(event.text)
		}
		
		/**
		 * Remote call ;
		 */
		public function updateChatUser(result:Object):void {
			user.id = result.id;
			user.age = result.age;
			user.sex = result.sex;
			user.loginName = result.loginName;
			user.nickName = result.nickName;
			user.place = result.place;
			user.audio = result.audio;
			user.video = result.video;
			user.registeredUser = result.registeredUser;
			
			LOG.debug(user.toString());
		}
		
		public function updateConversation(result:Object):void {
			conversation.id = result.id;
			conversation.playVideoPermission = result.playVideoPermission;
			conversation.playAudioPermission = result.playAudioPermission;
			if(result.partnerUser != null) {
				conversation.partnerUser = ChatUtility.getChatUser(result.partnerUser);				
			} else {
				if(conversation.partnerUser.id != ChatConstants.GUEST_ID){
					conversation.partnerUser = new ChatUser(ChatConstants.GUEST_ID);
				}
			}
			LOG.debug("update Conversation "+conversation.toString());
		}
		
		public function receiveMessage(msg:String):void {
			FlexGlobals.topLevelApplication.chatBox.receiveMessage(msg);
		}
	}
}
