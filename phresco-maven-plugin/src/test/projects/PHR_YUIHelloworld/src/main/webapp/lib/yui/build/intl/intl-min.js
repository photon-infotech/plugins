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
YUI.add("intl",function(d){var b={},a="yuiRootLang",e="yuiActiveLang",c=[];d.mix(d.namespace("Intl"),{_mod:function(f){if(!b[f]){b[f]={};}return b[f];},setLang:function(g,j){var i=this._mod(g),f=i[e],h=!!i[j];if(h&&j!==f){i[e]=j;this.fire("intl:langChange",{module:g,prevVal:f,newVal:(j===a)?"":j});}return h;},getLang:function(f){var g=this._mod(f)[e];return(g===a)?"":g;},add:function(g,h,f){h=h||a;this._mod(g)[h]=f;this.setLang(g,h);},get:function(h,g,j){var f=this._mod(h),i;j=j||f[e];i=f[j]||{};return(g)?i[g]:d.merge(i);},getAvailableLangs:function(h){var f=d.Env._loader,g=f&&f.moduleInfo[h],i=g&&g.lang;return(i)?i.concat():c;}});d.augment(d.Intl,d.EventTarget);d.Intl.publish("intl:langChange",{emitFacade:true});},"3.4.1",{requires:["event-custom","intl-base"]});