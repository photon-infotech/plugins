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
YUI.add("clickable-rail",function(b){function a(){this._initClickableRail();}b.ClickableRail=b.mix(a,{prototype:{_initClickableRail:function(){this._evtGuid=this._evtGuid||(b.guid()+"|");this.publish("railMouseDown",{defaultFn:this._defRailMouseDownFn});this.after("render",this._bindClickableRail);this.on("destroy",this._unbindClickableRail);},_bindClickableRail:function(){this._dd.addHandle(this.rail);this.rail.on(this._evtGuid+b.DD.Drag.START_EVENT,b.bind(this._onRailMouseDown,this));},_unbindClickableRail:function(){if(this.get("rendered")){var c=this.get("contentBox"),d=c.one("."+this.getClassName("rail"));d.detach(this.evtGuid+"*");}},_onRailMouseDown:function(c){if(this.get("clickableRail")&&!this.get("disabled")){this.fire("railMouseDown",{ev:c});}},_defRailMouseDownFn:function(k){k=k.ev;var c=this._resolveThumb(k),g=this._key.xyIndex,h=parseFloat(this.get("length"),10),f,d,j;if(c){f=c.get("dragNode");d=parseFloat(f.getStyle(this._key.dim),10);j=this._getThumbDestination(k,f);j=j[g]-this.rail.getXY()[g];j=Math.min(Math.max(j,0),(h-d));this._uiMoveThumb(j);k.target=this.thumb.one("img")||this.thumb;c._handleMouseDownEvent(k);}},_resolveThumb:function(c){return this._dd;},_getThumbDestination:function(g,f){var d=f.get("offsetWidth"),c=f.get("offsetHeight");return[(g.pageX-Math.round((d/2))),(g.pageY-Math.round((c/2)))];}},ATTRS:{clickableRail:{value:true,validator:b.Lang.isBoolean}}},true);},"3.4.1",{requires:["slider-base"]});