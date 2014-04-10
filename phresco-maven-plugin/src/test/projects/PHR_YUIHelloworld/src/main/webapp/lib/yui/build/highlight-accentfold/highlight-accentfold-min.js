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
YUI.add("highlight-accentfold",function(e){var d=e.Text.AccentFold,b=e.Escape,c={},a=e.mix(e.Highlight,{allFold:function(o,f,q){var n=a._TEMPLATE,g=[],l=0,m,h,k,j,p;q=e.merge({escapeHTML:false,replacer:function(r,t,s,u){var i;if(t&&!(/\s/).test(s)){return r;}i=s.length;g.push([o.substring(l,u),o.substr(u,i)]);l=u+i;}},q||c);a.all(d.fold(o),d.fold(f),q);if(l<o.length){g.push([o.substr(l)]);}for(h=0,k=g.length;h<k;++h){m=b.html(g[h][0]);if((j=g[h][1])){m+=n.replace(/\{s\}/g,b.html(j));}g[h]=m;}return g.join("");},startFold:function(g,f){return a.allFold(g,f,{startsWith:true});},wordsFold:function(h,g){var f=a._TEMPLATE;return a.words(h,d.fold(g),{mapper:function(j,i){if(i.hasOwnProperty(d.fold(j))){return f.replace(/\{s\}/g,b.html(j));}return b.html(j);}});}});},"3.4.1",{requires:["highlight-base","text-accentfold"]});