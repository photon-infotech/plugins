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
YUI.add("anim-scroll",function(b){var a=Number;b.Anim.behaviors.scroll={set:function(f,g,i,j,k,e,h){var d=f._node,c=([h(k,a(i[0]),a(j[0])-a(i[0]),e),h(k,a(i[1]),a(j[1])-a(i[1]),e)]);if(c[0]){d.set("scrollLeft",c[0]);}if(c[1]){d.set("scrollTop",c[1]);}},get:function(d){var c=d._node;return[c.get("scrollLeft"),c.get("scrollTop")];}};},"3.4.1",{requires:["anim-base"]});