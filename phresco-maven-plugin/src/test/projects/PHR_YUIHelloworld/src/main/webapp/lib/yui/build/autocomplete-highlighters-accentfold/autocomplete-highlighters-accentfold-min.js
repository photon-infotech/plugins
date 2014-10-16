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
YUI.add("autocomplete-highlighters-accentfold",function(c){var a=c.Highlight,b=c.Array;c.mix(c.namespace("AutoCompleteHighlighters"),{charMatchFold:function(f,e){var d=b.unique(f.split(""));return b.map(e,function(g){return a.allFold(g.text,d);});},phraseMatchFold:function(e,d){return b.map(d,function(f){return a.allFold(f.text,[e]);});},startsWithFold:function(e,d){return b.map(d,function(f){return a.allFold(f.text,[e],{startsWith:true});});},subWordMatchFold:function(f,d){var e=c.Text.WordBreak.getUniqueWords(f);return b.map(d,function(g){return a.allFold(g.text,e);});},wordMatchFold:function(e,d){return b.map(d,function(f){return a.wordsFold(f.text,e);});}});},"3.4.1",{requires:["array-extras","highlight-accentfold"]});