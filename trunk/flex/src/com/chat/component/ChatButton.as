package com.chat.component {

	import mx.logging.Log;
	import mx.logging.ILogger;

	import spark.components.Button;
	
	import mx.effects.Glow;
	import mx.effects.Parallel;
	
	import flash.filters.DropShadowFilter;


	public class ChatButton extends Button {
	
		private static var LOG:ILogger = Log.getLogger('com.chat.component.Button');
		
		public function ChatButton() {
			this.setStyle("color", "#800000");
			this.setStyle("themeColor", "#000000");
			this.setStyle("borderColor", "#000033");
			
			this.setStyle("fontFamily", "Verdana");
			this.setStyle("fontWeight", "normal");
			this.setStyle("fontStyle", "normal");
			
			this.setStyle("textRollOverColor", "#ffffff");
			this.setStyle("textSelectedColor", "black");
			
			this.setStyle("highlightAlphas", [0, 0.30]);
			this.setStyle("fillAlphas", [1, 0.80, 1, 0.60]);
			this.setStyle("fillColors", [0x000000, 0x222233, 0x003333, 0x0000ff]);
			this.filters = [new DropShadowFilter(4, 40, 0xcccccc, 0.8, 8, 8, 0.65, 1, false, false)];
			//fadeEffect();
			super();
		}

		public function set():void {
			
			this.setStyle("fillColors", [0x003333, 0x0000ff, 0x003333, 0x0000ff]);
			this.filters = [new DropShadowFilter(0, 10, 0xcccccc, 0.8, 8, 8, 0.65, 1, false, false)];
			//fadeEffect();
		}

		public function reset():void {
			
			this.setStyle("fillColors", [0x000000, 0x222233, 0x003333, 0x0000ff]);
			this.filters = [new DropShadowFilter(4, 40, 0xcccccc, 0.8, 8, 8, 0.65, 1, false, false)];
			//fadeEffect();
		}

		/* Apply Fade effect for components*/
		private function fadeEffect():void {
			
			var myParallel:Parallel = null;
			myParallel = new Parallel(this);
			
			var myGlow:Glow = new Glow();
			myGlow.alphaFrom = 1;
			myGlow.alphaTo = 0;
			myGlow.blurXFrom = 0;
			myGlow.blurXTo = this.width;
			myGlow.blurYFrom = 0;
			myGlow.blurYTo = this.height;
			myGlow.color = 0x000000;
			myGlow.duration = 500;
			
			myParallel.addChild(myGlow);
			myParallel.play(null, true);
		}
	
	}
}
