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
YUI.add("autocomplete-filters-accentfold",function(d){var c=d.Text.AccentFold,a=d.Text.WordBreak,b=d.Array,e=d.Object;d.mix(d.namespace("AutoCompleteFilters"),{charMatchFold:function(h,g){if(!h){return g;}var f=b.unique(c.fold(h).split(""));return b.filter(g,function(i){var j=c.fold(i.text);return b.every(f,function(k){return j.indexOf(k)!==-1;});});},phraseMatchFold:function(g,f){if(!g){return f;}g=c.fold(g);return b.filter(f,function(h){return c.fold(h.text).indexOf(g)!==-1;});},startsWithFold:function(g,f){if(!g){return f;}g=c.fold(g);return b.filter(f,function(h){return c.fold(h.text).indexOf(g)===0;});},subWordMatchFold:function(h,f){if(!h){return f;}var g=a.getUniqueWords(c.fold(h));return b.filter(f,function(i){var j=c.fold(i.text);return b.every(g,function(k){return j.indexOf(k)!==-1;});});},wordMatchFold:function(h,f){if(!h){return f;}var g=a.getUniqueWords(c.fold(h));return b.filter(f,function(i){var j=b.hash(a.getUniqueWords(c.fold(i.text)));return b.every(g,function(k){return e.owns(j,k);});});}});},"3.4.1",{requires:["array-extras","text-accentfold","text-wordbreak"]});