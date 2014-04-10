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
YUI.add("dd-gestures",function(a){a.DD.Drag.START_EVENT="gesturemovestart";a.DD.Drag.prototype._prep=function(){this._dragThreshMet=false;var c=this.get("node"),b=a.DD.DDM;c.addClass(b.CSS_PREFIX+"-draggable");c.on(a.DD.Drag.START_EVENT,a.bind(this._handleMouseDownEvent,this),{minDistance:0,minTime:0});c.on("gesturemoveend",a.bind(this._handleMouseUp,this),{standAlone:true});c.on("dragstart",a.bind(this._fixDragStart,this));};a.DD.DDM._setupListeners=function(){var b=a.DD.DDM;this._createPG();this._active=true;a.one(a.config.doc).on("gesturemove",a.throttle(a.bind(b._move,b),b.get("throttleTime")),{standAlone:true});};},"3.4.1",{skinnable:false,requires:["dd-drag","event-synthetic","event-gestures"]});