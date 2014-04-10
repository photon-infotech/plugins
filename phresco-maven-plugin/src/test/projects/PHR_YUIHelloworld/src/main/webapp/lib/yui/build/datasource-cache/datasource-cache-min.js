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
YUI.add("datasource-cache",function(c){var b=function(){};c.mix(b,{NS:"cache",NAME:"dataSourceCacheExtension"});b.prototype={initializer:function(d){this.doBefore("_defRequestFn",this._beforeDefRequestFn);this.doBefore("_defResponseFn",this._beforeDefResponseFn);},_beforeDefRequestFn:function(g){var d=(this.retrieve(g.request))||null,f=g.details[0];if(d&&d.response){f.cached=d.cached;f.response=d.response;f.data=d.data;this.get("host").fire("response",f);return new c.Do.Halt("DataSourceCache extension halted _defRequestFn");}},_beforeDefResponseFn:function(d){if(d.response&&!d.cached){this.add(d.request,d.response);}}};c.namespace("Plugin").DataSourceCacheExtension=b;function a(f){var e=f&&f.cache?f.cache:c.Cache,g=c.Base.create("dataSourceCache",e,[c.Plugin.Base,c.Plugin.DataSourceCacheExtension]),d=new g(f);g.NS="tmpClass";return d;}c.mix(a,{NS:"cache",NAME:"dataSourceCache"});c.namespace("Plugin").DataSourceCache=a;},"3.4.1",{requires:["datasource-local","cache-base","plugin"]});