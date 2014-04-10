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
YUI.add("scrollview-list",function(a){var b=a.ClassNameManager.getClassName,d="scrollview",c=b(d,"list"),g=b(d,"item"),h="contentBox",e="rendered",i="renderUI",j="host";function f(){f.superclass.constructor.apply(this,arguments);}f.NAME="pluginList";f.NS="list";f.ATTRS={isAttached:{value:false,validator:a.Lang.isBoolean}};a.namespace("Plugin").ScrollViewList=a.extend(f,a.Plugin.Base,{initializer:function(){this._host=this.get(j);this.afterHostEvent("render",this._addClassesToList);},_addClassesToList:function(){if(!this.get("isAttached")){var l=this._host.get(h),m,k;if(l.hasChildNodes()){m=l.all("> ul");k=l.all("> ul > li");m.each(function(n){n.addClass(c);});k.each(function(n){n.addClass(g);});this.set("isAttached",true);this._host.syncUI();}}}});},"3.4.1",{requires:["plugin","classnamemanager"],skinnable:true});