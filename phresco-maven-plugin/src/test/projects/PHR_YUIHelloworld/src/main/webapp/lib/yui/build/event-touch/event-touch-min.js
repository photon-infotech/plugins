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
YUI.add("event-touch",function(d){var c="scale",a="rotation",b="identifier";d.DOMEventFacade.prototype._touch=function(n,m,o){var g,f,h,k,j;if(n.touches){this.touches=[];j={};for(g=0,f=n.touches.length;g<f;++g){k=n.touches[g];j[d.stamp(k)]=this.touches[g]=new d.DOMEventFacade(k,m,o);}}if(n.targetTouches){this.targetTouches=[];for(g=0,f=n.targetTouches.length;g<f;++g){k=n.targetTouches[g];h=j&&j[d.stamp(k,true)];this.targetTouches[g]=h||new d.DOMEventFacade(k,m,o);}}if(n.changedTouches){this.changedTouches=[];for(g=0,f=n.changedTouches.length;g<f;++g){k=n.changedTouches[g];h=j&&j[d.stamp(k,true)];this.changedTouches[g]=h||new d.DOMEventFacade(k,m,o);}}if(c in n){this[c]=n[c];}if(a in n){this[a]=n[a];}if(b in n){this[b]=n[b];}};if(d.Node.DOM_EVENTS){d.mix(d.Node.DOM_EVENTS,{touchstart:1,touchmove:1,touchend:1,touchcancel:1,gesturestart:1,gesturechange:1,gestureend:1});}},"3.4.1",{requires:["node-base"]});