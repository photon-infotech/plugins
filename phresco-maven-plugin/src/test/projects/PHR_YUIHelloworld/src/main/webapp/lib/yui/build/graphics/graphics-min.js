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
YUI.add("graphics",function(c){var g="setter",h=c.Plugin.Host,j="value",b="valueFn",k="readOnly",d=c.Lang,f="string",i="writeOnce",l,e,a;a=function(m){this.init(m);};a.prototype={_rounder:100000,multiply:function(u,s,r,q,y,x){var t=this,p=t.a*u+t.c*s,o=t.b*u+t.d*s,n=t.a*r+t.c*q,m=t.b*r+t.d*q,w=t.a*y+t.c*x+t.dx,v=t.b*y+t.d*x+t.dy;t.a=p;t.b=o;t.c=n;t.d=m;t.dx=w;t.dy=v;return this;},applyCSSText:function(q){var p=/\s*([a-z]*)\(([\w,\s]*)\)/gi,o,n;while((n=p.exec(q))){if(typeof this[n[1]]==="function"){o=n[2].split(",");console.log(o);this[n[1]].apply(this,o);}}},getTransformArray:function(r){var q=/\s*([a-z]*)\(([\w,\s]*)\)/gi,p=[],o,n;while((n=q.exec(r))){if(typeof this[n[1]]==="function"){o=n[2].split(",");o.unshift(n[1]);p.push(o);}}return p;},_defaults:{a:1,b:0,c:0,d:1,dx:0,dy:0},_round:function(m){m=Math.round(m*this._rounder)/this._rounder;return m;},init:function(m){var n=this._defaults,o;m=m||{};for(o in n){if(n.hasOwnProperty(o)){this[o]=(o in m)?m[o]:n[o];}}this._config=m;},scale:function(m,n){this.multiply(m,0,0,n,0,0);return this;},skew:function(m,n){m=m||0;n=n||0;if(m!==undefined){m=this._round(Math.tan(this.angle2rad(m)));}if(n!==undefined){n=this._round(Math.tan(this.angle2rad(n)));}this.multiply(1,n,m,1,0,0);return this;},skewX:function(m){this.skew(m);return this;},skewY:function(m){this.skew(null,m);return this;},toCSSText:function(){var o=this,n=o.dx,m=o.dy,p="matrix(";if(c.UA.gecko){if(!isNaN(n)){n+="px";}if(!isNaN(m)){m+="px";}}p+=o.a+","+o.b+","+o.c+","+o.d+","+n+","+m;p+=")";return p;},toFilterText:function(){var m=this,n="progid:DXImageTransform.Microsoft.Matrix(";n+="M11="+m.a+","+"M21="+m.b+","+"M12="+m.c+","+"M22="+m.d+","+'sizingMethod="auto expand")';n+="";return n;},rad2deg:function(m){var n=m*(180/Math.PI);return n;},deg2rad:function(n){var m=n*(Math.PI/180);return m;},angle2rad:function(m){if(typeof m==="string"&&m.indexOf("rad")>-1){m=parseFloat(m);}else{m=this.deg2rad(parseFloat(m));}return m;},rotate:function(q,n,s){var p=[],m=this.angle2rad(q),o=this._round(Math.sin(m)),r=this._round(Math.cos(m));this.multiply(r,o,0-o,r,0,0);return this;},translate:function(m,n){this.multiply(1,0,0,1,parseFloat(m),parseFloat(n));return this;}};c.Matrix=a;e=function(){var m=this;m._ATTR_E_FACADE={};c.EventTarget.call(this,{emitFacade:true});m._state={};m.prototype=c.mix(e.prototype,m.prototype);};e.prototype={addAttrs:function(n){var r=this,p=this.constructor.ATTRS,m,o,q,s=r._state;for(o in p){if(p.hasOwnProperty(o)){m=p[o];if(m.hasOwnProperty(j)){s[o]=m.value;}else{if(m.hasOwnProperty(b)){q=m.valueFn;if(d.isString(q)){s[o]=r[q].apply(r);}else{s[o]=q.apply(r);}}}}}r._state=s;for(o in p){if(p.hasOwnProperty(o)){m=p[o];if(m.hasOwnProperty(k)&&m.readOnly){continue;}if(m.hasOwnProperty(i)&&m.writeOnce){m.readOnly=true;}if(n&&n.hasOwnProperty(o)){if(m.hasOwnProperty(g)){r._state[o]=m.setter.apply(r,[n[o]]);}else{r._state[o]=n[o];}}}}},get:function(n){var p=this,m,o=p.constructor.ATTRS;if(o&&o[n]){m=o[n].getter;if(m){if(typeof m==f){return p[m].apply(p);}return o[n].getter.apply(p);}return p._state[n];}return null;},set:function(m,o){var n;if(d.isObject(m)){for(n in m){if(m.hasOwnProperty(n)){this._set(n,m[n]);}}}else{this._set.apply(this,arguments);}},_set:function(m,q){var p=this,r,n,o=p.constructor.ATTRS;if(o&&o.hasOwnProperty(m)){r=o[m].setter;if(r){n=[q];if(typeof r==f){q=p[r].apply(p,n);}else{q=o[m].setter.apply(p,n);}}p._state[m]=q;}}};c.mix(e,c.EventTarget,false,null,1);c.AttributeLite=e;l=function(m){var o=this,n=c.Plugin&&c.Plugin.Host;if(o._initPlugins&&n){n.call(o);}o.name=o.constructor.NAME;o._eventPrefix=o.constructor.EVENT_PREFIX||o.constructor.NAME;e.call(o);o.addAttrs(m);o.init.apply(this,arguments);if(o._initPlugins){o._initPlugins(m);}o.initialized=true;};l.NAME="baseGraphic";l.prototype={init:function(){this.publish("init",{fireOnce:true});this.initializer.apply(this,arguments);this.fire("init",{cfg:arguments[0]});}};c.mix(l,c.AttributeLite,false,null,1);c.mix(l,h,false,null,1);l.prototype.constructor=l;l.plug=h.plug;l.unplug=h.unplug;c.BaseGraphic=l;},"3.4.1",{requires:["event-custom","node","pluginhost"]});