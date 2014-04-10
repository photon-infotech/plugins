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
YUI.add("autocomplete-filters",function(d){var c=d.Array,e=d.Object,a=d.Text.WordBreak,b=d.mix(d.namespace("AutoCompleteFilters"),{charMatch:function(i,h,f){if(!i){return h;}var g=c.unique((f?i:i.toLowerCase()).split(""));return c.filter(h,function(j){j=j.text;if(!f){j=j.toLowerCase();}return c.every(g,function(k){return j.indexOf(k)!==-1;});});},charMatchCase:function(g,f){return b.charMatch(g,f,true);},phraseMatch:function(h,g,f){if(!h){return g;}if(!f){h=h.toLowerCase();}return c.filter(g,function(i){return(f?i.text:i.text.toLowerCase()).indexOf(h)!==-1;});},phraseMatchCase:function(g,f){return b.phraseMatch(g,f,true);},startsWith:function(h,g,f){if(!h){return g;}if(!f){h=h.toLowerCase();}return c.filter(g,function(i){return(f?i.text:i.text.toLowerCase()).indexOf(h)===0;});},startsWithCase:function(g,f){return b.startsWith(g,f,true);},subWordMatch:function(i,g,f){if(!i){return g;}var h=a.getUniqueWords(i,{ignoreCase:!f});return c.filter(g,function(j){var k=f?j.text:j.text.toLowerCase();return c.every(h,function(l){return k.indexOf(l)!==-1;});});},subWordMatchCase:function(g,f){return b.subWordMatch(g,f,true);},wordMatch:function(j,h,f){if(!j){return h;}var g={ignoreCase:!f},i=a.getUniqueWords(j,g);return c.filter(h,function(k){var l=c.hash(a.getUniqueWords(k.text,g));return c.every(i,function(m){return e.owns(l,m);});});},wordMatchCase:function(g,f){return b.wordMatch(g,f,true);}});},"3.4.1",{requires:["array-extras","text-wordbreak"]});