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
YUI.add("dd-scroll",function(b){var h=function(){h.superclass.constructor.apply(this,arguments);},c,d,l="host",a="buffer",j="parentScroll",g="windowScroll",i="scrollTop",f="scrollLeft",e="offsetWidth",k="offsetHeight";h.ATTRS={parentScroll:{value:false,setter:function(m){if(m){return m;}return false;}},buffer:{value:30,validator:b.Lang.isNumber},scrollDelay:{value:235,validator:b.Lang.isNumber},host:{value:null},windowScroll:{value:false,validator:b.Lang.isBoolean},vertical:{value:true,validator:b.Lang.isBoolean},horizontal:{value:true,validator:b.Lang.isBoolean}};b.extend(h,b.Base,{_scrolling:null,_vpRegionCache:null,_dimCache:null,_scrollTimer:null,_getVPRegion:function(){var m={},o=this.get(j),u=this.get(a),s=this.get(g),y=((s)?[]:o.getXY()),v=((s)?"winWidth":e),q=((s)?"winHeight":k),x=((s)?o.get(i):y[1]),p=((s)?o.get(f):y[0]);m={top:x+u,right:(o.get(v)+p)-u,bottom:(o.get(q)+x)-u,left:p+u};this._vpRegionCache=m;return m;},initializer:function(){var m=this.get(l);m.after("drag:start",b.bind(this.start,this));m.after("drag:end",b.bind(this.end,this));m.on("drag:align",b.bind(this.align,this));b.one("win").on("scroll",b.bind(function(){this._vpRegionCache=null;},this));},_checkWinScroll:function(A){var z=this._getVPRegion(),m=this.get(l),o=this.get(g),t=m.lastXY,n=false,F=this.get(a),s=this.get(j),H=s.get(i),v=s.get(f),x=this._dimCache.w,C=this._dimCache.h,u=t[1]+C,y=t[1],E=t[0]+x,q=t[0],G=y,p=q,B=H,D=v;if(this.get("horizontal")){if(q<=z.left){n=true;p=t[0]-((o)?F:0);D=v-F;}if(E>=z.right){n=true;p=t[0]+((o)?F:0);D=v+F;}}if(this.get("vertical")){if(u>=z.bottom){n=true;G=t[1]+((o)?F:0);B=H+F;}if(y<=z.top){n=true;G=t[1]-((o)?F:0);B=H-F;}}if(B<0){B=0;G=t[1];}if(D<0){D=0;p=t[0];}if(G<0){G=t[1];}if(p<0){p=t[0];}if(A){m.actXY=[p,G];m._moveNode({node:s,top:B,left:D});if(!B&&!D){this._cancelScroll();}}else{if(n){this._initScroll();}else{this._cancelScroll();}}},_initScroll:function(){this._cancelScroll();this._scrollTimer=b.Lang.later(this.get("scrollDelay"),this,this._checkWinScroll,[true],true);},_cancelScroll:function(){this._scrolling=false;if(this._scrollTimer){this._scrollTimer.cancel();delete this._scrollTimer;}},align:function(m){if(this._scrolling){this._cancelScroll();m.preventDefault();}if(!this._scrolling){this._checkWinScroll();}},_setDimCache:function(){var m=this.get(l).get("dragNode");this._dimCache={h:m.get(k),w:m.get(e)};},start:function(){this._setDimCache();},end:function(m){this._dimCache=null;this._cancelScroll();},toString:function(){return h.NAME+" #"+this.get("node").get("id");}});b.namespace("Plugin");c=function(){c.superclass.constructor.apply(this,arguments);};c.ATTRS=b.merge(h.ATTRS,{windowScroll:{value:true,setter:function(m){if(m){this.set(j,b.one("win"));}return m;}}});b.extend(c,h,{initializer:function(){this.set("windowScroll",this.get("windowScroll"));}});c.NAME=c.NS="winscroll";b.Plugin.DDWinScroll=c;d=function(){d.superclass.constructor.apply(this,arguments);};d.ATTRS=b.merge(h.ATTRS,{node:{value:false,setter:function(m){var o=b.one(m);if(!o){if(m!==false){b.error("DDNodeScroll: Invalid Node Given: "+m);}}else{this.set(j,o);}return o;}}});b.extend(d,h,{initializer:function(){this.set("node",this.get("node"));}});d.NAME=d.NS="nodescroll";b.Plugin.DDNodeScroll=d;b.DD.Scroll=h;},"3.4.1",{optional:["dd-proxy"],requires:["dd-drag"],skinnable:false});