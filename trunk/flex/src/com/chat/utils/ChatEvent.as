package com.chat.utils
{
	import flash.events.Event;

	public class ChatEvent extends Event
	{
		public static const PUBLIC_CONN_SUCCESS:String = "connection.public.success";
		public static const VIDEO_CONN_SUCCESS:String = "connection.video.success";
		public static const AUDIO_CONN_SUCCESS:String = "connection.audio.success";
		
		public static const VIDEO_PUBLISHED:String = "netstream.video.published";
		public static const AUDIO_PUBLISHED:String = "netstream.audio.published";
		public static const VIDEO_AUDIO_PUBLISHED:String = "video.audio.published";
		
		public function ChatEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
	}
}