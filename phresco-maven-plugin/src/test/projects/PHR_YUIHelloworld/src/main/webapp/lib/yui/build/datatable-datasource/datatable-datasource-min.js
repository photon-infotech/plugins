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
YUI.add("datatable-datasource",function(b){function a(){a.superclass.constructor.apply(this,arguments);}b.mix(a,{NS:"datasource",NAME:"dataTableDataSource",ATTRS:{datasource:{setter:"_setDataSource"},initialRequest:{setter:"_setInitialRequest"}}});b.extend(a,b.Plugin.Base,{_setDataSource:function(c){return c||new b.DataSource.Local(c);},_setInitialRequest:function(c){},initializer:function(c){if(!b.Lang.isUndefined(c.initialRequest)){this.load({request:c.initialRequest});}},load:function(c){c=c||{};c.request=c.request||this.get("initialRequest");c.callback=c.callback||{success:b.bind(this.onDataReturnInitializeTable,this),failure:b.bind(this.onDataReturnInitializeTable,this),argument:this.get("host").get("state")};var d=(c.datasource||this.get("datasource"));if(d){d.sendRequest(c);}},onDataReturnInitializeTable:function(d){var c=(d.response&&d.response.results)||[];this.get("host").get("recordset").set("records",c);}});b.namespace("Plugin").DataTableDataSource=a;},"3.4.1",{requires:["datatable-base","plugin","datasource-local"]});