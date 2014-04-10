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
YUI.add("selector-css3",function(a){a.Selector._reNth=/^(?:([\-]?\d*)(n){1}|(odd|even)$)*([\-+]?\d*)$/;a.Selector._getNth=function(d,o,q,h){a.Selector._reNth.test(o);var m=parseInt(RegExp.$1,10),c=RegExp.$2,j=RegExp.$3,k=parseInt(RegExp.$4,10)||0,p=[],l=a.Selector._children(d.parentNode,q),f;if(j){m=2;f="+";c="n";k=(j==="odd")?1:0;}else{if(isNaN(m)){m=(c)?1:0;}}if(m===0){if(h){k=l.length-k+1;}if(l[k-1]===d){return true;}else{return false;}}else{if(m<0){h=!!h;m=Math.abs(m);}}if(!h){for(var e=k-1,g=l.length;e<g;e+=m){if(e>=0&&l[e]===d){return true;}}}else{for(var e=l.length-k,g=l.length;e>=0;e-=m){if(e<g&&l[e]===d){return true;}}}return false;};a.mix(a.Selector.pseudos,{"root":function(b){return b===b.ownerDocument.documentElement;},"nth-child":function(b,c){return a.Selector._getNth(b,c);},"nth-last-child":function(b,c){return a.Selector._getNth(b,c,null,true);},"nth-of-type":function(b,c){return a.Selector._getNth(b,c,b.tagName);},"nth-last-of-type":function(b,c){return a.Selector._getNth(b,c,b.tagName,true);},"last-child":function(c){var b=a.Selector._children(c.parentNode);return b[b.length-1]===c;},"first-of-type":function(b){return a.Selector._children(b.parentNode,b.tagName)[0]===b;},"last-of-type":function(c){var b=a.Selector._children(c.parentNode,c.tagName);return b[b.length-1]===c;},"only-child":function(c){var b=a.Selector._children(c.parentNode);return b.length===1&&b[0]===c;},"only-of-type":function(c){var b=a.Selector._children(c.parentNode,c.tagName);return b.length===1&&b[0]===c;},"empty":function(b){return b.childNodes.length===0;},"not":function(b,c){return !a.Selector.test(b,c);},"contains":function(b,c){var d=b.innerText||b.textContent||"";return d.indexOf(c)>-1;},"checked":function(b){return(b.checked===true||b.selected===true);},enabled:function(b){return(b.disabled!==undefined&&!b.disabled);},disabled:function(b){return(b.disabled);}});a.mix(a.Selector.operators,{"^=":"^{val}","$=":"{val}$","*=":"{val}"});a.Selector.combinators["~"]={axis:"previousSibling"};},"3.4.1",{requires:["selector-native","selector-css2"]});