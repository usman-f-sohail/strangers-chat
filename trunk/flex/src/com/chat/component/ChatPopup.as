package com.chat.component
{
	import flash.filters.DropShadowFilter;
	
	import mx.core.FlexGlobals;
	import mx.managers.PopUpManager;
	
	import spark.components.Application;
	import spark.components.TitleWindow;

	public class ChatPopup extends TitleWindow
	{
		public function ChatPopup()
		{
			this.width = 300;
			this.height = 100;
			this.setStyle("color", "#000000");
			this.setStyle("backgroundColor", "#F5F5F5");
			this.setStyle("borderAlpha", 1.0);
			this.setStyle("borderStyle", "solid");
			this.setStyle("borderColor", "#b4b4b4");
			this.setStyle("borderThickness", 1);
			
			this.setStyle("fontFamily", "Verdana");
			this.setStyle("fontWeight", "normal");
			this.setStyle("textRollOverColor", "#ffffff");
			this.setStyle("textSelectedColor", "#0b333c");
			
			this.setStyle("cornerRadius", 7);
			this.setStyle("roundedBottomCorners", true);
			this.setStyle("dropShadowVisible", false);
			this.setStyle("dropShadowEnabled", true);
			this.setStyle("shadowDistance", 5);
			this.setStyle("shadowDirection", "right");
			this.setStyle("horizontalAlign", "center");
			this.setStyle("verticalAlign", "center");
			
			this.setStyle("fillAlphas", [0.50, 0.60, 0.50, 0.60]);
			this.setStyle("fillColors", [0x000000, 0x999999, 0x000000, 0x666666]);
			this.filters = [new DropShadowFilter(4, 80, 0x000000, 0.8, 8, 8, 0.65, 1, false, false)];
			
			this.isPopUp = true;
			super();
		}
		
	}
}