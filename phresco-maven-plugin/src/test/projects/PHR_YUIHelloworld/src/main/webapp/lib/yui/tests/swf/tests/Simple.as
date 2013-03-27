/**
 * Phresco Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package {
	import flash.display.Sprite;
	import flash.display.Stage;
	import flash.text.TextField;
	import com.yahoo.util.YUIBridge;
	
	public class Simple extends Sprite {
		
		public var txt:TextField = new TextField();
		public var yuiBridge:YUIBridge;
		function Simple () {
			yuiBridge = new YUIBridge(this.stage);
			this.graphics.beginFill(0xFFCC00);
			this.graphics.drawCircle(200,200,50);
			this.graphics.endFill();
			
			txt.width = 400;
			txt.height = 200;
			this.addChild(txt);
			this.addText("Initializing...");
			yuiBridge.addCallbacks ({addText:addText});
		}
		
		public function addText(someText:String) : void {
			txt.appendText(someText);
			yuiBridge.sendEvent({type:"textAdded", text:someText});
		}
		
		
	}
	
	
}