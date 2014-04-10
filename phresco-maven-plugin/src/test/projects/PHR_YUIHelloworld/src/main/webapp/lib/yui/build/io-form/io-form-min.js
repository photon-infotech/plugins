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
YUI.add("io-form",function(b){var a=encodeURIComponent;b.mix(b.IO.prototype,{_serialize:function(w,B){var q=[],y=w.useDisabled||false,A=0,g=(typeof w.id==="string")?w.id:w.id.getAttribute("id"),t,r,k,z,u,p,x,l,m,h;if(!g){g=b.guid("io:");w.id.setAttribute("id",g);}r=b.config.doc.getElementById(g);for(p=0,x=r.elements.length;p<x;++p){t=r.elements[p];u=t.disabled;k=t.name;if(y?k:k&&!u){k=a(k)+"=";z=a(t.value);switch(t.type){case"select-one":if(t.selectedIndex>-1){h=t.options[t.selectedIndex];q[A++]=k+a(h.attributes.value&&h.attributes.value.specified?h.value:h.text);}break;case"select-multiple":if(t.selectedIndex>-1){for(l=t.selectedIndex,m=t.options.length;l<m;++l){h=t.options[l];if(h.selected){q[A++]=k+a(h.attributes.value&&h.attributes.value.specified?h.value:h.text);}}}break;case"radio":case"checkbox":if(t.checked){q[A++]=k+z;}break;case"file":case undefined:case"reset":case"button":break;case"submit":default:q[A++]=k+z;}}}return B?q.join("&")+"&"+B:q.join("&");}},true);},"3.4.1",{requires:["io-base","node-base"]});