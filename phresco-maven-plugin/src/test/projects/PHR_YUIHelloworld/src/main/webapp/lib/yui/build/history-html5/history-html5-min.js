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
YUI.add("history-html5",function(g){var b=g.HistoryBase,c=g.Lang,f=g.config.win,d=g.config.useHistoryHTML5,h="popstate",e=b.SRC_REPLACE;function a(){a.superclass.constructor.apply(this,arguments);}g.extend(a,b,{_init:function(i){var j=f.history.state;i||(i={});if(i.initialState&&c.type(i.initialState)==="object"&&c.type(j)==="object"){this._initialState=g.merge(i.initialState,j);}else{this._initialState=j;}g.on("popstate",this._onPopState,f,this);a.superclass._init.apply(this,arguments);},_storeState:function(k,j,i){if(k!==h){f.history[k===e?"replaceState":"pushState"](j,i.title||g.config.doc.title||"",i.url||null);}a.superclass._storeState.apply(this,arguments);},_onPopState:function(i){this._resolveChanges(h,i._event.state||null);}},{NAME:"historyhtml5",SRC_POPSTATE:h});if(!g.Node.DOM_EVENTS.popstate){g.Node.DOM_EVENTS.popstate=1;}g.HistoryHTML5=a;if(d===true||(d!==false&&b.html5)){g.History=a;}},"3.4.1",{requires:["event-base","history-base","node-base"],optional:["json"]});