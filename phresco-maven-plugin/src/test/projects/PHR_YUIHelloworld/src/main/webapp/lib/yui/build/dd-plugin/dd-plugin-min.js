/*
 * Phresco Maven Plugin
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
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
/*
YUI 3.4.1 (build 4118)
Copyright 2011 Yahoo! Inc. All rights reserved.
Licensed under the BSD License.
http://yuilibrary.com/license/
*/
YUI.add("dd-plugin",function(c){var a=function(e){if(c.Widget&&e.host instanceof c.Widget){e.node=e.host.get("boundingBox");e.widget=e.host;}else{e.node=e.host;e.widget=false;}a.superclass.constructor.call(this,e);},b="drag:drag",d="drag:end";a.NAME="dd-plugin";a.NS="dd";c.extend(a,c.DD.Drag,{_widget:undefined,_stoppedPosition:undefined,_usesWidgetPosition:function(f){var e=false;if(f){e=(f.hasImpl&&f.hasImpl(c.WidgetPosition))?true:false;}return e;},initializer:function(e){this._widget=e.widget;if(this._usesWidgetPosition(this._widget)){this.on(b,this._setWidgetCoords);this.on(d,this._updateStopPosition);}},_setWidgetCoords:function(i){var h=this._stoppedPosition||i.target.nodeXY,f=i.target.realXY,g=[f[0]-h[0],f[1]-h[0]];if(g[0]!==0&&g[1]!==0){this._widget.set("xy",f);}else{if(g[0]===0){this._widget.set("y",f[1]);}else{if(g[1]===0){this._widget.set("x",f[0]);}}}},updateStopPosition:function(f){this._stoppedPosition=f.target.realXY;}});c.namespace("Plugin");c.Plugin.Drag=a;},"3.4.1",{optional:["dd-constrain","dd-proxy"],requires:["dd-drag"],skinnable:false});