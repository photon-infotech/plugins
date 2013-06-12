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
YUI.add("datasource-arrayschema",function(b){var a=function(){a.superclass.constructor.apply(this,arguments);};b.mix(a,{NS:"schema",NAME:"dataSourceArraySchema",ATTRS:{schema:{}}});b.extend(a,b.Plugin.Base,{initializer:function(c){this.doBefore("_defDataFn",this._beforeDefDataFn);},_beforeDefDataFn:function(g){var d=(b.DataSource.IO&&(this.get("host") instanceof b.DataSource.IO)&&b.Lang.isString(g.data.responseText))?g.data.responseText:g.data,c=b.DataSchema.Array.apply.call(this,this.get("schema"),d),f=g.details[0];if(!c){c={meta:{},results:d};}f.response=c;this.get("host").fire("response",f);return new b.Do.Halt("DataSourceArraySchema plugin halted _defDataFn");}});b.namespace("Plugin").DataSourceArraySchema=a;},"3.4.1",{requires:["datasource-local","plugin","dataschema-array"]});