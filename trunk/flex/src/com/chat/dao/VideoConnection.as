package com.chat.dao
{
	import com.chat.models.ChatUser;
	import com.chat.models.Conversation;
	import com.chat.utils.ChatConstants;
	import com.chat.utils.ChatEvent;
	import com.chat.views.ErrorPopup;
	import com.chat.views.ScanCriteriaBox;
	import com.chat.views.VideoBox;
	
	import flash.events.ActivityEvent;
	import flash.events.AsyncErrorEvent;
	import flash.events.Event;
	import flash.events.NetStatusEvent;
	import flash.media.Camera;
	import flash.media.Video;
	import flash.net.NetConnection;
	import flash.net.NetStream;
	import flash.net.ObjectEncoding;
	
	import mx.collections.ArrayCollection;
	import mx.core.FlexGlobals;
	import mx.core.IFlexDisplayObject;
	import mx.events.FlexEvent;
	import mx.events.PropertyChangeEvent;
	import mx.events.PropertyChangeEventKind;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.managers.PopUpManager;
	
	import spark.components.Application;
	import spark.components.ComboBox;
	import spark.components.HGroup;
	import spark.components.VideoDisplay;
	
	[Bindable]
	public class VideoConnection extends NetConnection
	{
		private static var LOG:ILogger = Log.getLogger('com.chat.dao.VideoConnection');
		private var mainApp:HGroup;
		private var user:ChatUser;
		private var hostname:String;
		private var port:Number;
		private var isHttps:Boolean;
		private var conversation:Conversation;
		
		private var yourVideoStream:NetStream = null;
		private var yourVideo:Video = null;		
		
		private var partnerVideoStream:NetStream;
		private var partnerVideo:Video = null;
		
		private var camera:Camera = null;
		
		private var videoBox:VideoBox;
		private var scanBox:ScanCriteriaBox;
		private var errorPopup:IFlexDisplayObject;
		
		public function VideoConnection()
		{
			this.mainApp = FlexGlobals.topLevelApplication.mainGroup;
			this.hostname = FlexGlobals.topLevelApplication.hostname;
			this.port = FlexGlobals.topLevelApplication.rtmpPort;
			this.isHttps = FlexGlobals.topLevelApplication.issecure;
			this.user = FlexGlobals.topLevelApplication.chatUser;
			this.conversation = FlexGlobals.topLevelApplication.conversation;
			
			this.videoBox = FlexGlobals.topLevelApplication.videoBox;
			this.scanBox =  FlexGlobals.topLevelApplication.scanBox;
			
			this.conversation.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, onConversationChange);
			LOG.debug("hostname : "+hostname+" port : "+port+" isHttps : "+isHttps+" chatUser : "+ this.user.toString());
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
				this.connect("rtmps://" + this.hostname + ":"+this.port+"/"+ChatConstants.MAIN_APP, "videoConn",user);
			} else {
				this.connect("rtmp://" + this.hostname + ":"+this.port+"/"+ChatConstants.MAIN_APP, "videoConn",user);
			}
		}
		
		public function publishYourVideo(event:FlexEvent=null):void {
			if(!user.video){
				var vidCombo:ComboBox = videoBox.vidCombo;
				var cameraName:String = vidCombo.selectedItem;
				var cameraArray:ArrayCollection = videoBox.cameraArray;
				var yourVideoDisplay:VideoDisplay = videoBox.yourVideoDisplay;
				LOG.debug("vidCombo.selectedItem : "+vidCombo.selectedItem+" cameraName "+cameraName);
				
				var camIndex:int = -1;

				if(cameraName != null) {
					camIndex = cameraArray.getItemAt(cameraArray.getItemIndex(cameraName)) as int;
				}
				
				if(camIndex != -1) {
					camera = Camera.getCamera(camIndex as String);
				}
					
				if(camera != null) {
					/*Initialize the camera properties*/
					camera.setMode(ChatConstants.VIDEO_WIDTH, ChatConstants.VIDEO_HEIGHT, ChatConstants.CAMERA_FPS, true);
					camera.setQuality(ChatConstants.CAMERA_BANDWIDTH, ChatConstants.CAMERA_QUALITY);
					camera.addEventListener(ActivityEvent.ACTIVITY, onVideoActivity);
					
					/*Initialize the Netstream for your video */
					yourVideoStream = new NetStream(this);
					yourVideoStream.addEventListener(NetStatusEvent.NET_STATUS, netConnectionStatus);
					yourVideoStream.addEventListener("status", streamStatus);
					yourVideoStream.addEventListener("error", streamError);
					yourVideoStream.attachCamera(camera);					
					yourVideo = new Video(ChatConstants.VIDEO_WIDTH, ChatConstants.VIDEO_HEIGHT);
					yourVideo.attachCamera(camera);
					yourVideo.x = 0;
					yourVideo.y = 0;
					yourVideoDisplay.addChild(yourVideo);
					yourVideoStream.publish(user.id,"live");
				} else {
					/* Add a black window? */
					LOG.debug("camera not published");
				}
			}	
		}
		
		/**
		 * 
		 */
		private function playPartnerVideo():void {
			stopPartnerVideo();
			var partnerID:String = conversation.partnerUser.id;
			var partnerVideoDisplay:VideoDisplay = videoBox.partnerVideoDisplay;
			
			partnerVideoStream = new NetStream(this);
			partnerVideoStream.addEventListener(NetStatusEvent.NET_STATUS, netConnectionStatus);
			partnerVideoStream.addEventListener("status", streamStatus);
			partnerVideoStream.addEventListener("error", streamError);
			partnerVideoStream.play(partnerID, -1, -1, true);
			partnerVideo = new Video(ChatConstants.VIDEO_WIDTH, ChatConstants.VIDEO_HEIGHT);
			partnerVideo.attachNetStream(partnerVideoStream);
			partnerVideo.x = 0;
			partnerVideo.y = 0;
			partnerVideoDisplay.addChild(partnerVideo);	
			
			LOG.debug("partner video stream "+partnerVideoStream);				
		}
		
		private function stopPartnerVideo():void {
			if(partnerVideoStream != null) {
				partnerVideoStream.close();
				partnerVideoStream = null;
				if(partnerVideo != null) {
					partnerVideo.attachNetStream(null);
					partnerVideo = null;
				}
			}			
		}		
		
		private function netConnectionStatus(event:NetStatusEvent):void {
			LOG.debug("Status : "+event.info.code);
			if (event.info.code == "NetConnection.Connect.Success") {
				this.dispatchEvent(new ChatEvent(ChatEvent.VIDEO_CONN_SUCCESS));
				/*setting connection object for other class*/
			} else if (event.info.code == "NetConnection.Connect.Closed") {

			} else if (event.info.code == "NetConnection.Connect.Failed") {
				errorPopup = PopUpManager.createPopUp(FlexGlobals.topLevelApplication as Application, ErrorPopup, true);
				PopUpManager.centerPopUp(errorPopup);
			} else if (event.info.code == "NetStream.Play.UnpublishNotify") {

			} else if(event.info.code == "NetStream.Play.Start") {
				
			} else if(event.info.code == "NetStream.Publish.Start") {
				user.video = true;
				this.dispatchEvent(new ChatEvent(ChatEvent.VIDEO_PUBLISHED));
				LOG.debug("event "+ChatEvent.VIDEO_PUBLISHED+" dispatched");
				if(user.audio) {
					this.dispatchEvent(new ChatEvent(ChatEvent.VIDEO_AUDIO_PUBLISHED));
					LOG.debug("event "+ChatEvent.VIDEO_AUDIO_PUBLISHED+" dispatched");
				}
			} else if(event.info.code == "NetStream.Unpublish.Success") {
				user.video = false;
			} else if(event.info.code == "NetStream.Publish.BadName") {
				user.video = false;
				errorPopup = PopUpManager.createPopUp(FlexGlobals.topLevelApplication as Application, ErrorPopup, true);
				PopUpManager.centerPopUp(errorPopup);				
			} else if(event.info.code == "NetStream.Play.UnpublishNotify") {
				
			} else if(event.info.code == "NetStream.Record.Stop") {
				
			}
		} 
		
		private function netConnectionClose(event:NetStatusEvent):void {
			LOG.debug("net Connection close "+event.info);
		}
		
		/**
		 * this is invoked when the conversation object is changed
		 * If the partner user is updated then play corresponding partner video
		 * If the partner user is null then stop the play
		 */
		private function onConversationChange(event:PropertyChangeEvent):void {
			LOG.debug("on chage conversaton "+event.toString());
			if(event.property == "partnerUser") {
				LOG.debug("kind "+event.kind);
				LOG.debug("new value "+event.newValue);
				LOG.debug("old value "+event.oldValue);
				LOG.debug("property "+event.property);
				LOG.debug("source "+event.source);
			}
			
			if(event.kind == PropertyChangeEventKind.UPDATE) {
				if(event.property == "partnerUser" && event.oldValue != event.newValue) {
					var partnerUser:ChatUser = event.newValue as ChatUser;
					if(partnerUser.id == ChatConstants.GUEST_ID) {
						stopPartnerVideo();
					} else {
						LOG.debug("play partner video");
						playPartnerVideo();
					}					
				}
			}
		}
		
		private function streamStatus(evtObject:Object):void {

		}

		private function streamError(evtObject:Object):void {

		}
		
		private function onVideoActivity(event:ActivityEvent):void {

		}
		
		private function asyncErrorHandler(event:AsyncErrorEvent):void {
			LOG.debug(event.text)
		}	
	}
}