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
YUI.add("recordset-indexer",function(b){function a(c){a.superclass.constructor.apply(this,arguments);}b.mix(a,{NS:"indexer",NAME:"recordsetIndexer",ATTRS:{hashTables:{value:{}},keys:{value:{}}}});b.extend(a,b.Plugin.Base,{initializer:function(c){var d=this.get("host");this.onHostEvent("add",b.bind("_defAddHash",this),d);this.onHostEvent("remove",b.bind("_defRemoveHash",this),d);this.onHostEvent("update",b.bind("_defUpdateHash",this),d);},destructor:function(c){},_setHashTable:function(e){var f=this.get("host"),g={},d=0,c=f.getLength();for(;d<c;d++){g[f._items[d].getValue(e)]=f._items[d];}return g;},_defAddHash:function(d){var c=this.get("hashTables");b.each(c,function(e,f){b.each(d.added||d.updated,function(g){if(g.getValue(f)){e[g.getValue(f)]=g;}});});},_defRemoveHash:function(f){var d=this.get("hashTables"),c;b.each(d,function(e,g){b.each(f.removed||f.overwritten,function(h){c=h.getValue(g);if(c&&e[c]===h){delete e[c];}});});},_defUpdateHash:function(c){c.added=c.updated;c.removed=c.overwritten;this._defAddHash(c);this._defRemoveHash(c);},createTable:function(c){var d=this.get("hashTables");d[c]=this._setHashTable(c);this.set("hashTables",d);return d[c];},getTable:function(c){return this.get("hashTables")[c];}});b.namespace("Plugin").RecordsetIndexer=a;},"3.4.1",{requires:["recordset-base","plugin"]});