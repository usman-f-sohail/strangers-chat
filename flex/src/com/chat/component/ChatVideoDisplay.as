package com.chat.component{

import mx.logging.Log;
import mx.logging.ILogger;

import spark.components.VideoDisplay;

	public class ChatVideoDisplay extends VideoDisplay {

		private static var LOG:ILogger = Log.getLogger('com.chat.component.VideoDisplay');
		private var displayWidth:int = 320;
		private var displayHeight:int = 240;

		/*Button Images*/
		[Embed(source = "../../../../assets/videocamera.png")] [Bindable] private var backgroundImage:Class;	

		public function ChatVideoDisplay() {

			this.width = displayWidth;
			this.height = displayHeight;
			//this.autoPlay = true;
			//this.live = true;
			//this.maintainAspectRatio = true;
			//this.volume = 1;
			
			this.setStyle("backgroundAlpha","1.0");
			this.setStyle("backgroundColor","white");
			this.setStyle("backgroundImage",backgroundImage);
			this.setStyle("borderColor","black");
			this.setStyle("borderStyle","solid");
			this.setStyle("cornerRadius","5");
			//this.setStyle("","");
			//this.setStyle("","");
			super();
		}
	}
}
