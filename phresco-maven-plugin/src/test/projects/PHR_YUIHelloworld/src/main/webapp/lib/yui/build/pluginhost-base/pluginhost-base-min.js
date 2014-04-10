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
YUI.add("pluginhost-base",function(c){var a=c.Lang;function b(){this._plugins={};}b.prototype={plug:function(g,d){var e,h,f;if(a.isArray(g)){for(e=0,h=g.length;e<h;e++){this.plug(g[e]);}}else{if(g&&!a.isFunction(g)){d=g.cfg;g=g.fn;}if(g&&g.NS){f=g.NS;d=d||{};d.host=this;if(this.hasPlugin(f)){this[f].setAttrs(d);}else{this[f]=new g(d);this._plugins[f]=g;}}}return this;},unplug:function(f){var e=f,d=this._plugins;if(f){if(a.isFunction(f)){e=f.NS;if(e&&(!d[e]||d[e]!==f)){e=null;}}if(e){if(this[e]){this[e].destroy();delete this[e];}if(d[e]){delete d[e];}}}else{for(e in this._plugins){if(this._plugins.hasOwnProperty(e)){this.unplug(e);}}}return this;},hasPlugin:function(d){return(this._plugins[d]&&this[d]);},_initPlugins:function(d){this._plugins=this._plugins||{};if(this._initConfigPlugins){this._initConfigPlugins(d);}},_destroyPlugins:function(){this.unplug();}};c.namespace("Plugin").Host=b;},"3.4.1",{requires:["yui-base"]});