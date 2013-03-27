/*
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
/*
YUI 3.4.1 (build 4118)
Copyright 2011 Yahoo! Inc. All rights reserved.
Licensed under the BSD License.
http://yuilibrary.com/license/
*/
YUI.add("dd-proxy",function(h){var f=h.DD.DDM,b="node",c="dragNode",a="host",d=true,e,g=function(i){g.superclass.constructor.apply(this,arguments);};g.NAME="DDProxy";g.NS="proxy";g.ATTRS={host:{},moveOnEnd:{value:d},hideOnEnd:{value:d},resizeFrame:{value:d},positionProxy:{value:d},borderStyle:{value:"1px solid #808080"},cloneNode:{value:false}};e={_hands:null,_init:function(){if(!f._proxy){f._createFrame();h.on("domready",h.bind(this._init,this));return;}if(!this._hands){this._hands=[];}var k,j,l=this.get(a),i=l.get(c);if(i.compareTo(l.get(b))){if(f._proxy){l.set(c,f._proxy);}}h.each(this._hands,function(m){m.detach();});k=f.on("ddm:start",h.bind(function(){if(f.activeDrag===l){f._setFrame(l);}},this));j=f.on("ddm:end",h.bind(function(){if(l.get("dragging")){if(this.get("moveOnEnd")){l.get(b).setXY(l.lastXY);}if(this.get("hideOnEnd")){l.get(c).setStyle("display","none");}if(this.get("cloneNode")){l.get(c).remove();l.set(c,f._proxy);}}},this));this._hands=[k,j];},initializer:function(){this._init();},destructor:function(){var i=this.get(a);h.each(this._hands,function(j){j.detach();});i.set(c,i.get(b));},clone:function(){var i=this.get(a),k=i.get(b),j=k.cloneNode(true);delete j._yuid;j.setAttribute("id",h.guid());j.setStyle("position","absolute");k.get("parentNode").appendChild(j);i.set(c,j);return j;}};h.namespace("Plugin");h.extend(g,h.Base,e);h.Plugin.DDProxy=g;h.mix(f,{_createFrame:function(){if(!f._proxy){f._proxy=d;var j=h.Node.create("<div></div>"),i=h.one("body");j.setStyles({position:"absolute",display:"none",zIndex:"999",top:"-999px",left:"-999px"});i.prepend(j);j.set("id",h.guid());j.addClass(f.CSS_PREFIX+"-proxy");f._proxy=j;}},_setFrame:function(j){var m=j.get(b),l=j.get(c),i,k="auto";i=f.activeDrag.get("activeHandle");if(i){k=i.getStyle("cursor");}if(k=="auto"){k=f.get("dragCursor");}l.setStyles({visibility:"hidden",display:"block",cursor:k,border:j.proxy.get("borderStyle")});if(j.proxy.get("cloneNode")){l=j.proxy.clone();}if(j.proxy.get("resizeFrame")){l.setStyles({height:m.get("offsetHeight")+"px",width:m.get("offsetWidth")+"px"});}if(j.proxy.get("positionProxy")){l.setXY(j.nodeXY);}l.setStyle("visibility","visible");}});},"3.4.1",{skinnable:false,requires:["dd-drag"]});