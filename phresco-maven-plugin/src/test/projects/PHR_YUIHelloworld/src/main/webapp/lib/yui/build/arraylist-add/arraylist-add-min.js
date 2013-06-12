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
YUI.add("arraylist-add",function(a){a.mix(a.ArrayList.prototype,{add:function(d,c){var b=this._items;if(a.Lang.isNumber(c)){b.splice(c,0,d);}else{b.push(d);}return this;},remove:function(e,d,b){b=b||this.itemsAreEqual;for(var c=this._items.length-1;c>=0;--c){if(b.call(this,e,this.item(c))){this._items.splice(c,1);if(!d){break;}}}return this;},itemsAreEqual:function(d,c){return d===c;}});},"3.4.1",{requires:["arraylist"]});