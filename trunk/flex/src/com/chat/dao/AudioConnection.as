package com.chat.dao
{
	import com.chat.models.ChatUser;
	import com.chat.models.Conversation;
	import com.chat.utils.ChatConstants;
	import com.chat.utils.ChatEvent;
	import com.chat.views.ErrorPopup;
	import com.chat.views.VideoBox;
	
	import flash.events.ActivityEvent;
	import flash.events.AsyncErrorEvent;
	import flash.events.Event;
	import flash.events.NetStatusEvent;
	import flash.events.StatusEvent;
	import flash.media.Microphone;
	import flash.media.SoundTransform;
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
	
	[Bindable]
	public class AudioConnection extends NetConnection
	{
		private static var LOG:ILogger = Log.getLogger('com.chat.dao.AudioConnection');
		private var mainApp:HGroup;
		private var user:ChatUser;
		private var hostname:String;
		private var port:Number;
		private var isHttps:Boolean;
		private var conversation:Conversation;
		
		private var videoBox:VideoBox;
		public var microphone:Microphone = null;
		private var errorPopup:IFlexDisplayObject;		
		
		private var yourAudioStream:NetStream;
		public var partnerAudioStream:NetStream;
		public var partnerTransform:SoundTransform;
		
		public function AudioConnection()
		{
			this.mainApp = FlexGlobals.topLevelApplication.mainGroup;
			this.hostname = FlexGlobals.topLevelApplication.hostname;
			this.port = FlexGlobals.topLevelApplication.rtmpPort;
			this.isHttps = FlexGlobals.topLevelApplication.issecure;
			this.user = FlexGlobals.topLevelApplication.chatUser;
			this.conversation = FlexGlobals.topLevelApplication.conversation;
			
			this.conversation.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, onConversationChange);
			
			this.videoBox = FlexGlobals.topLevelApplication.videoBox;
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
				this.connect("rtmps://" + this.hostname + ":"+this.port+"/"+ChatConstants.MAIN_APP, "audioConn",user);
			} else {
				this.connect("rtmp://" + this.hostname + ":"+this.port+"/"+ChatConstants.MAIN_APP, "audioConn",user);
			}
		}
		
		public function publishYourAudio(event:FlexEvent=null):void {
			if(!user.audio){
				var audioCombo:ComboBox = videoBox.micCombo;
				var microphoneName:String = audioCombo.selectedItem as String;
				var micArray:ArrayCollection = videoBox.micArray;
				var micIndex:int = -1;
				
				if(microphoneName != null) {
					micIndex = micArray.getItemAt(micArray.getItemIndex(microphoneName)) as int;
				}
				
				if(micIndex != -1) {
					microphone = Microphone.getMicrophone(micIndex);
				}
				LOG.info("Publishing microphone "+microphoneName+ "  "+microphone);	
				
				/*Initialize the microphone properties*/
				if(microphone != null) {		
					/*Initialize the Netstream for your audio */
					yourAudioStream = new NetStream(this);
					yourAudioStream.addEventListener(NetStatusEvent.NET_STATUS, netConnectionStatus);
					yourAudioStream.addEventListener("status", streamStatus);
					yourAudioStream.addEventListener("error", streamError);						
								
					//microphone.codec = SoundCodec.SPEEX;
					microphone.encodeQuality = ChatConstants.AUDIO_QUALITY;
					microphone.rate = ChatConstants.AUDIO_RATE;
					microphone.framesPerPacket = ChatConstants.AUDIO_PACKET;
					microphone.gain = ChatConstants.AUDIO_GAIN;
					microphone.setUseEchoSuppression(true); 
					microphone.setLoopBack(false); 
					microphone.setSilenceLevel(2, ChatConstants.AUDIO_SILENCE_LEVEL); 

					microphone.addEventListener(ActivityEvent.ACTIVITY, this.onMicActivity); 
					microphone.addEventListener(StatusEvent.STATUS, this.onMicStatus); 

					var micDetails:String = '\n'+"Sound input device name: " + microphone.name + '\n'; 
					micDetails += "Gain: " + microphone.gain + '\n'; 
					micDetails += "Rate: " + microphone.rate + " kHz" + '\n'; 
					micDetails += "Muted: " + microphone.muted + '\n';
					micDetails += "Quality: " + microphone.encodeQuality + '\n';
					micDetails += "Frames / Packet: " + microphone.framesPerPacket + '\n';
					micDetails += "Silence level: " + microphone.silenceLevel + '\n'; 
					micDetails += "Silence timeout: " + microphone.silenceTimeout + '\n'; 
					micDetails += "Echo suppression: " + microphone.useEchoSuppression + '\n';
					LOG.info(micDetails);
					yourAudioStream.attachAudio(microphone);
					yourAudioStream.publish("MIC"+user.id,"live");
				} else {
					/* Add a black window? */
					LOG.debug("microphone not published");					
				}					
			}
		}
		
		private function playPartnerAudio():void {
			stopPartnerAudio();
			var partnerID:String = conversation.partnerUser.id;
			partnerTransform = new SoundTransform();
			partnerTransform.volume = 1;
			
			partnerAudioStream = new NetStream(this);
			partnerAudioStream.addEventListener(NetStatusEvent.NET_STATUS, netConnectionStatus);
			partnerAudioStream.addEventListener("status", streamStatus);
			partnerAudioStream.addEventListener("error", streamError);
			partnerAudioStream.play("MIC"+partnerID, -1, -1, true);			
			partnerAudioStream.soundTransform = partnerTransform;
			
			LOG.debug("partner audio stream : "+partnerAudioStream.toString());			
		}
		
		private function stopPartnerAudio():void {
			if(partnerAudioStream != null) {
				partnerAudioStream.attachAudio(null);
				partnerAudioStream.close();
				partnerAudioStream = null;
			}			
		}

		private function netConnectionStatus(event:NetStatusEvent):void {
			LOG.debug("Status : "+event.info.code);
			if (event.info.code == "NetConnection.Connect.Success") {
				this.dispatchEvent(new ChatEvent(ChatEvent.AUDIO_CONN_SUCCESS));
				/*setting connection object for other class*/
			} else if (event.info.code == "NetConnection.Connect.Closed") {

			} else if (event.info.code == "NetConnection.Connect.Failed") {
				errorPopup = PopUpManager.createPopUp(FlexGlobals.topLevelApplication as Application, ErrorPopup, true);
				PopUpManager.centerPopUp(errorPopup);
			} else if (event.info.code == "NetStream.Play.UnpublishNotify") {

			} else if(event.info.code == "NetStream.Play.Start") {
				
			} else if(event.info.code == "NetStream.Publish.Start") {
				user.audio = true;
				this.dispatchEvent(new ChatEvent(ChatEvent.AUDIO_PUBLISHED));
				LOG.debug("event "+ChatEvent.AUDIO_PUBLISHED+" dispatched");
				if(user.video) {
					this.dispatchEvent(new ChatEvent(ChatEvent.VIDEO_AUDIO_PUBLISHED));
					LOG.debug("event "+ChatEvent.VIDEO_AUDIO_PUBLISHED+" dispatched");
				}				
			} else if(event.info.code == "NetStream.Unpublish.Success") {
				user.audio = false;
			} else if(event.info.code == "NetStream.Publish.BadName") {
				user.audio = false;
				errorPopup = PopUpManager.createPopUp(FlexGlobals.topLevelApplication as Application, ErrorPopup, true);
				PopUpManager.centerPopUp(errorPopup);				
			} if(event.info.code == "NetStream.Play.UnpublishNotify") {
				
			}else if(event.info.code == "NetStream.Record.Stop") {
				
			}	
		} 
		
		private function netConnectionClose(event:NetStatusEvent):void {
			LOG.debug("net Connection close "+event.info);
		}
		
		private function onConversationChange(event:PropertyChangeEvent):void {
			LOG.debug("on conversation change "+event.type);
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
						LOG.debug(" stop partner audio");
						stopPartnerAudio();
					} else {
						LOG.debug("play partner audio");
						playPartnerAudio();
					}
				}
			}			
		}
		
		private function streamStatus(evtObject:Object):void {
			
		}

		private function streamError(evtObject:Object):void {
			
		}		
							
		public function onMicActivity(event:ActivityEvent):void { 

		} 

		public function onMicStatus(event:StatusEvent):void { 

		}
		
		private function asyncErrorHandler(event:AsyncErrorEvent):void {
			LOG.debug(event.text)
		}		

	}
}