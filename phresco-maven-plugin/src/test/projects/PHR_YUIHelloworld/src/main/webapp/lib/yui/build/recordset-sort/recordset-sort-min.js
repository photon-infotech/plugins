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
YUI.add("recordset-sort",function(d){var a=d.ArraySort.compare,c=d.Lang.isValue;function b(e,f,g){b.superclass.constructor.apply(this,arguments);}d.mix(b,{NS:"sort",NAME:"recordsetSort",ATTRS:{lastSortProperties:{value:{field:undefined,desc:true,sorter:undefined},validator:function(e){return(c(e.field)&&c(e.desc)&&c(e.sorter));}},defaultSorter:{value:function(g,e,h,i){var f=a(g.getValue(h),e.getValue(h),i);if(f===0){return a(g.get("id"),e.get("id"),i);}else{return f;}}},isSorted:{value:false}}});d.extend(b,d.Plugin.Base,{initializer:function(f){var e=this,g=this.get("host");this.publish("sort",{defaultFn:d.bind("_defSortFn",this)});this.on("sort",function(){e.set("isSorted",true);});this.onHostEvent("add",function(){e.set("isSorted",false);},g);this.onHostEvent("update",function(){e.set("isSorted",false);},g);},destructor:function(e){},_defSortFn:function(f){this.get("host")._items.sort(function(g,e){return(f.sorter)(g,e,f.field,f.desc);});this.set("lastSortProperties",f);},sort:function(e,f,g){this.fire("sort",{field:e,desc:f,sorter:g||this.get("defaultSorter")});},resort:function(){var e=this.get("lastSortProperties");this.fire("sort",{field:e.field,desc:e.desc,sorter:e.sorter||this.get("defaultSorter")});},reverse:function(){this.get("host")._items.reverse();},flip:function(){var e=this.get("lastSortProperties");if(c(e.field)){this.fire("sort",{field:e.field,desc:!e.desc,sorter:e.sorter||this.get("defaultSorter")});}else{}}});d.namespace("Plugin").RecordsetSort=b;},"3.4.1",{requires:["arraysort","recordset-base","plugin"]});