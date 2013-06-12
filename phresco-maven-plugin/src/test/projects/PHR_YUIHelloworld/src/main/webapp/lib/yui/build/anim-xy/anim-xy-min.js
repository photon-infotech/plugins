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
YUI.add("anim-xy",function(b){var a=Number;b.Anim.behaviors.xy={set:function(f,d,i,h,c,g,e){f._node.setXY([e(c,a(i[0]),a(h[0])-a(i[0]),g),e(c,a(i[1]),a(h[1])-a(i[1]),g)]);},get:function(c){return c._node.getXY();}};},"3.4.1",{requires:["anim-base","node-screen"]});