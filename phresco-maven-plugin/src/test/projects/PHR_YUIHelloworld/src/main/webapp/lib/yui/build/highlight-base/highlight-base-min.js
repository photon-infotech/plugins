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
YUI.add("highlight-base",function(h){var g=h.Array,e=h.Escape,d=h.Text.WordBreak,a=h.Lang.isArray,f={},c="(&[^;\\s]*)?",b={_REGEX:c+"(%needles)",_REPLACER:function(i,k,j){return k&&!(/\s/).test(j)?i:b._TEMPLATE.replace(/\{s\}/g,j);},_START_REGEX:"^"+c+"(%needles)",_TEMPLATE:'<b class="'+h.ClassNameManager.getClassName("highlight")+'">{s}</b>',all:function(r,j,s){var p=[],o,m,n,l,q,k;if(!s){s=f;}o=s.escapeHTML!==false;q=s.startsWith?b._START_REGEX:b._REGEX;k=s.replacer||b._REPLACER;j=a(j)?j:[j];for(m=0,n=j.length;m<n;++m){l=j[m];if(l){p.push(e.regex(o?e.html(l):l));}}if(o){r=e.html(r);}if(!p.length){return r;}return r.replace(new RegExp(q.replace("%needles",p.join("|")),s.caseSensitive?"g":"gi"),k);},allCase:function(k,j,i){return b.all(k,j,h.merge(i||f,{caseSensitive:true}));},start:function(k,j,i){return b.all(k,j,h.merge(i||f,{startsWith:true}));},startCase:function(j,i){return b.start(j,i,{caseSensitive:true});},words:function(m,l,j){var i,o,k=b._TEMPLATE,n;if(!j){j=f;}i=!!j.caseSensitive;l=g.hash(a(l)?l:d.getUniqueWords(l,{ignoreCase:!i}));o=j.mapper||function(q,p){if(p.hasOwnProperty(i?q:q.toLowerCase())){return k.replace(/\{s\}/g,e.html(q));}return e.html(q);};n=d.getWords(m,{includePunctuation:true,includeWhitespace:true});return g.map(n,function(p){return o(p,l);}).join("");},wordsCase:function(j,i){return b.words(j,i,{caseSensitive:true});}};h.Highlight=b;},"3.4.1",{requires:["array-extras","classnamemanager","escape","text-wordbreak"]});