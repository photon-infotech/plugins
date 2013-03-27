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
YUI.add("anim-curve",function(a){a.Anim.behaviors.curve={set:function(f,c,i,h,b,g,e){i=i.slice.call(i);h=h.slice.call(h);var d=e(b,0,100,g)/100;h.unshift(i);f._node.setXY(a.Anim.getBezier(h,d));},get:function(c,b){return c._node.getXY();}};a.Anim.getBezier=function(f,e){var g=f.length;var d=[];for(var c=0;c<g;++c){d[c]=[f[c][0],f[c][1]];}for(var b=1;b<g;++b){for(c=0;c<g-b;++c){d[c][0]=(1-e)*d[c][0]+e*d[parseInt(c+1,10)][0];d[c][1]=(1-e)*d[c][1]+e*d[parseInt(c+1,10)][1];}}return[d[0][0],d[0][1]];};},"3.4.1",{requires:["anim-xy"]});