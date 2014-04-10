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
YUI.add("attribute-complex",function(b){var a=b.Object,c=".";b.Attribute.Complex=function(){};b.Attribute.Complex.prototype={_normAttrVals:function(g){var i={},h={},j,d,f,e;if(g){for(e in g){if(g.hasOwnProperty(e)){if(e.indexOf(c)!==-1){j=e.split(c);d=j.shift();f=h[d]=h[d]||[];f[f.length]={path:j,value:g[e]};}else{i[e]=g[e];}}}return{simple:i,complex:h};}else{return null;}},_getAttrInitVal:function(m,j,p){var e=j.value,o=j.valueFn,d,f,h,g,q,n,k;if(o){if(!o.call){o=this[o];}if(o){e=o.call(this);}}if(!j.readOnly&&p){d=p.simple;if(d&&d.hasOwnProperty(m)){e=d[m];}f=p.complex;if(f&&f.hasOwnProperty(m)){k=f[m];for(h=0,g=k.length;h<g;++h){q=k[h].path;n=k[h].value;a.setValue(e,q,n);}}}return e;}};b.mix(b.Attribute,b.Attribute.Complex,true,null,1);},"3.4.1",{requires:["attribute-base"]});