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
YUI.add("node-deprecated",function(b){var a=b.Node;a.ATTRS.data={getter:function(){return this._dataVal;},setter:function(c){this._dataVal=c;return c;},value:null};b.get=a.get=function(){return a.one.apply(a,arguments);};b.mix(a.prototype,{query:function(c){return this.one(c);},queryAll:function(c){return this.all(c);},each:function(d,c){c=c||this;return d.call(c,this);},item:function(c){return this;},size:function(){return this._node?1:0;}});},"3.4.1",{requires:["node-base"]});