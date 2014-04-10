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
YUI.add("text-accentfold",function(e){var c=e.Array,b=e.Text,a=b.Data.AccentFold,d={canFold:function(f){var g;for(g in a){if(a.hasOwnProperty(g)&&f.search(a[g])!==-1){return true;}}return false;},compare:function(g,f,h){var i=d.fold(g),j=d.fold(f);return h?!!h(i,j):i===j;},filter:function(g,f){return c.filter(g,function(h){return f(d.fold(h));});},fold:function(f){if(e.Lang.isArray(f)){return c.map(f,d.fold);}f=f.toLowerCase();e.Object.each(a,function(h,g){f=f.replace(h,g);});return f;}};b.AccentFold=d;},"3.4.1",{requires:["array-extras","text-data-accentfold"]});