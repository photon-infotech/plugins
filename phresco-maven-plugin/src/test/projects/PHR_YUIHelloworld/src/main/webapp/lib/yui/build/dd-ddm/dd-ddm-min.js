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
YUI.add("dd-ddm",function(a){a.mix(a.DD.DDM,{_pg:null,_debugShim:false,_activateTargets:function(){},_deactivateTargets:function(){},_startDrag:function(){if(this.activeDrag&&this.activeDrag.get("useShim")){this._pg_activate();this._activateTargets();}},_endDrag:function(){this._pg_deactivate();this._deactivateTargets();},_pg_deactivate:function(){this._pg.setStyle("display","none");},_pg_activate:function(){var b=this.activeDrag.get("activeHandle"),c="auto";if(b){c=b.getStyle("cursor");}if(c=="auto"){c=this.get("dragCursor");}this._pg_size();this._pg.setStyles({top:0,left:0,display:"block",opacity:((this._debugShim)?".5":"0"),cursor:c});},_pg_size:function(){if(this.activeDrag){var c=a.one("body"),e=c.get("docHeight"),d=c.get("docWidth");this._pg.setStyles({height:e+"px",width:d+"px"});}},_createPG:function(){var d=a.Node.create("<div></div>"),b=a.one("body"),c;d.setStyles({top:"0",left:"0",position:"absolute",zIndex:"9999",overflow:"hidden",backgroundColor:"red",display:"none",height:"5px",width:"5px"});d.set("id",a.stamp(d));d.addClass(a.DD.DDM.CSS_PREFIX+"-shim");b.prepend(d);this._pg=d;this._pg.on("mousemove",a.throttle(a.bind(this._move,this),this.get("throttleTime")));this._pg.on("mouseup",a.bind(this._end,this));c=a.one("win");a.on("window:resize",a.bind(this._pg_size,this));c.on("scroll",a.bind(this._pg_size,this));}},true);},"3.4.1",{skinnable:false,requires:["dd-ddm-base","event-resize"]});