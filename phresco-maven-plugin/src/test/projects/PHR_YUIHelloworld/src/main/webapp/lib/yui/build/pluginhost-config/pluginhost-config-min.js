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
YUI.add("pluginhost-config",function(c){var b=c.Plugin.Host,a=c.Lang;b.prototype._initConfigPlugins=function(e){var g=(this._getClasses)?this._getClasses():[this.constructor],d=[],h={},f,j,l,m,k;for(j=g.length-1;j>=0;j--){f=g[j];m=f._UNPLUG;if(m){c.mix(h,m,true);}l=f._PLUG;if(l){c.mix(d,l,true);}}for(k in d){if(d.hasOwnProperty(k)){if(!h[k]){this.plug(d[k]);}}}if(e&&e.plugins){this.plug(e.plugins);}};b.plug=function(e,j,g){var k,h,d,f;if(e!==c.Base){e._PLUG=e._PLUG||{};if(!a.isArray(j)){if(g){j={fn:j,cfg:g};}j=[j];}for(h=0,d=j.length;h<d;h++){k=j[h];f=k.NAME||k.fn.NAME;e._PLUG[f]=k;}}};b.unplug=function(e,h){var j,g,d,f;if(e!==c.Base){e._UNPLUG=e._UNPLUG||{};if(!a.isArray(h)){h=[h];}for(g=0,d=h.length;g<d;g++){j=h[g];f=j.NAME;if(!e._PLUG[f]){e._UNPLUG[f]=j;}else{delete e._PLUG[f];}}}};},"3.4.1",{requires:["pluginhost-base"]});