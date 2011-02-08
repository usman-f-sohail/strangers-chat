package com.chat.utils
{
	public final class ChatConstants
	{
		public function ChatConstants()
		{
		}
		/*rtmp constants*/
		public static const MAIN_APP:String = "MemberChat";
		public static const RTMP_PORT:Number = 1935; 
		
		/*search constants*/
		public static const START_AGE:Number = 18;
		public static const END_AGE:Number = 35;
		
		/*video constants*/
		public static const VIDEO_WIDTH:Number = 320;
		public static const VIDEO_HEIGHT:Number = 240;
		public static const CAMERA_FPS:int = 15; //15
		public static const CAMERA_BANDWIDTH:int = 0; // 1 to ...
		public static const CAMERA_QUALITY:int = 75;//1 to 100		
		public static const AUDIO_QUALITY:int = 7; // 0 to 10
		public static const AUDIO_RATE:int = 16;
		public static const AUDIO_PACKET:int = 2;
		public static const AUDIO_GAIN:int = 50;
		public static const AUDIO_SILENCE_LEVEL:int = 5000;
		
		/*Non-member constants*/
		public static const GUEST_ID:String = "Guest";
		public static const ALL:String = "All";
		
		/*Member constants*/

	}
}