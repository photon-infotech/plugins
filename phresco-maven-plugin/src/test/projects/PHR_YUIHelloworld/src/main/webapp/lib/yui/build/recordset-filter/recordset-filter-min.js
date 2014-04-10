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
YUI.add("recordset-filter",function(d){var c=d.Array,b=d.Lang;function a(e){a.superclass.constructor.apply(this,arguments);}d.mix(a,{NS:"filter",NAME:"recordsetFilter",ATTRS:{}});d.extend(a,d.Plugin.Base,{filter:function(f,h){var g=this.get("host").get("records"),e;if(h&&b.isString(f)){e=f;f=function(i){return(i.getValue(e)===h);};}return new d.Recordset({records:c.filter(g,f)});},reject:function(e){return new d.Recordset({records:c.reject(this.get("host").get("records"),e)});},grep:function(e){return new d.Recordset({records:c.grep(this.get("host").get("records"),e)});}});d.namespace("Plugin").RecordsetFilter=a;},"3.4.1",{requires:["recordset-base","array-extras","plugin"]});