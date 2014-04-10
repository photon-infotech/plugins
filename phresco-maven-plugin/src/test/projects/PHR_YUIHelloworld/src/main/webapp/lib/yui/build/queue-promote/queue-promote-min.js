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
YUI.add("queue-promote",function(a){a.mix(a.Queue.prototype,{indexOf:function(b){return a.Array.indexOf(this._q,b);},promote:function(c){var b=this.indexOf(c);if(b>-1){this._q.unshift(this._q.splice(b,1)[0]);}},remove:function(c){var b=this.indexOf(c);if(b>-1){this._q.splice(b,1);}}});},"3.4.1",{requires:["yui-base"]});