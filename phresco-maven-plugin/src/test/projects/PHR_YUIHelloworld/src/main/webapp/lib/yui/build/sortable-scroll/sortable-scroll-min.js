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
YUI.add("sortable-scroll",function(b){var a=function(){a.superclass.constructor.apply(this,arguments);};b.extend(a,b.Base,{initializer:function(){var c=this.get("host");c.plug(b.Plugin.DDNodeScroll,{node:c.get("container")});c.delegate.on("drop:over",function(d){if(this.dd.nodescroll&&d.drag.nodescroll){d.drag.nodescroll.set("parentScroll",b.one(this.get("container")));}});}},{ATTRS:{host:{value:""}},NAME:"SortScroll",NS:"scroll"});b.namespace("Y.Plugin");b.Plugin.SortableScroll=a;},"3.4.1",{requires:["sortable","dd-scroll"]});