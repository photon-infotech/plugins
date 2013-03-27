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
YUI.add("anim-color",function(b){var a=Number;b.Anim.behaviors.color={set:function(f,d,i,h,c,g,e){i=b.Color.re_RGB.exec(b.Color.toRGB(i));h=b.Color.re_RGB.exec(b.Color.toRGB(h));if(!i||i.length<3||!h||h.length<3){b.error("invalid from or to passed to color behavior");}f._node.setStyle(d,"rgb("+[Math.floor(e(c,a(i[1]),a(h[1])-a(i[1]),g)),Math.floor(e(c,a(i[2]),a(h[2])-a(i[2]),g)),Math.floor(e(c,a(i[3]),a(h[3])-a(i[3]),g))].join(", ")+")");},get:function(d,c){var e=d._node.getComputedStyle(c);e=(e==="transparent")?"rgb(255, 255, 255)":e;return e;}};b.each(["backgroundColor","borderColor","borderTopColor","borderRightColor","borderBottomColor","borderLeftColor"],function(c,d){b.Anim.behaviors[c]=b.Anim.behaviors.color;});},"3.4.1",{requires:["anim-base"]});