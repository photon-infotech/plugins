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
YUI.add("dump",function(g){var b=g.Lang,c="{...}",f="f(){...}",a=", ",d=" => ",e=function(p,n){var j,h,l=[],k=b.type(p);if(!b.isObject(p)){return p+"";}else{if(k=="date"){return p;}else{if(p.nodeType&&p.tagName){return p.tagName+"#"+p.id;}else{if(p.document&&p.navigator){return"window";}else{if(p.location&&p.body){return"document";}else{if(k=="function"){return f;}}}}}}n=(b.isNumber(n))?n:3;if(k=="array"){l.push("[");for(j=0,h=p.length;j<h;j=j+1){if(b.isObject(p[j])){l.push((n>0)?b.dump(p[j],n-1):c);}else{l.push(p[j]);}l.push(a);}if(l.length>1){l.pop();}l.push("]");}else{if(k=="regexp"){l.push(p.toString());}else{l.push("{");for(j in p){if(p.hasOwnProperty(j)){try{l.push(j+d);if(b.isObject(p[j])){l.push((n>0)?b.dump(p[j],n-1):c);}else{l.push(p[j]);}l.push(a);}catch(m){l.push("Error: "+m.message);}}}if(l.length>1){l.pop();}l.push("}");}}return l.join("");};g.dump=e;b.dump=e;},"3.4.1",{requires:["yui-base"]});