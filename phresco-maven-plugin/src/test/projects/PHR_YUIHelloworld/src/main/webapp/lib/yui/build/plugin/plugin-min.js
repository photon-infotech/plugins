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
YUI.add("plugin",function(b){function a(c){if(!(this.hasImpl&&this.hasImpl(b.Plugin.Base))){a.superclass.constructor.apply(this,arguments);}else{a.prototype.initializer.apply(this,arguments);}}a.ATTRS={host:{writeOnce:true}};a.NAME="plugin";a.NS="plugin";b.extend(a,b.Base,{_handles:null,initializer:function(c){this._handles=[];},destructor:function(){if(this._handles){for(var d=0,c=this._handles.length;d<c;d++){this._handles[d].detach();}}},doBefore:function(g,d,c){var e=this.get("host"),f;if(g in e){f=this.beforeHostMethod(g,d,c);}else{if(e.on){f=this.onHostEvent(g,d,c);}}return f;},doAfter:function(g,d,c){var e=this.get("host"),f;if(g in e){f=this.afterHostMethod(g,d,c);}else{if(e.after){f=this.afterHostEvent(g,d,c);}}return f;},onHostEvent:function(e,d,c){var f=this.get("host").on(e,d,c||this);this._handles.push(f);return f;},afterHostEvent:function(e,d,c){var f=this.get("host").after(e,d,c||this);this._handles.push(f);return f;},beforeHostMethod:function(f,d,c){var e=b.Do.before(d,this.get("host"),f,c||this);this._handles.push(e);return e;},afterHostMethod:function(f,d,c){var e=b.Do.after(d,this.get("host"),f,c||this);this._handles.push(e);return e;},toString:function(){return this.constructor.NAME+"["+this.constructor.NS+"]";}});b.namespace("Plugin").Base=a;},"3.4.1",{requires:["base-base"]});