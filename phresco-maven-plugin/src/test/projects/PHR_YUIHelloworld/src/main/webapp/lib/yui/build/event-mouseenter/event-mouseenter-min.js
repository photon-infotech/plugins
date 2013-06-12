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
YUI.add("event-mouseenter",function(f){var b=f.Env.evt.dom_wrappers,d=f.DOM.contains,c=f.Array,e=function(){},a={proxyType:"mouseover",relProperty:"fromElement",_notify:function(k,i,h){var g=this._node,j=k.relatedTarget||k[i];if(g!==j&&!d(g,j)){h.fire(new f.DOMEventFacade(k,g,b["event:"+f.stamp(g)+k.type]));}},on:function(k,i,j){var h=f.Node.getDOMNode(k),g=[this.proxyType,this._notify,h,null,this.relProperty,j];i.handle=f.Event._attach(g,{facade:false});},detach:function(h,g){g.handle.detach();},delegate:function(l,j,k,i){var h=f.Node.getDOMNode(l),g=[this.proxyType,e,h,null,k];j.handle=f.Event._attach(g,{facade:false});j.handle.sub.filter=i;j.handle.sub.relProperty=this.relProperty;j.handle.sub._notify=this._filterNotify;},_filterNotify:function(j,p,g){p=p.slice();if(this.args){p.push.apply(p,this.args);}var h=f.delegate._applyFilter(this.filter,p,g),q=p[0].relatedTarget||p[0][this.relProperty],o,k,m,n,l;if(h){h=c(h);for(k=0,m=h.length&&(!o||!o.stopped);k<m;++k){l=h[0];if(!d(l,q)){if(!o){o=new f.DOMEventFacade(p[0],l,g);o.container=f.one(g.el);}o.currentTarget=f.one(l);n=p[1].fire(o);if(n===false){break;}}}}return n;},detachDelegate:function(h,g){g.handle.detach();}};f.Event.define("mouseenter",a,true);f.Event.define("mouseleave",f.merge(a,{proxyType:"mouseout",relProperty:"toElement"}),true);},"3.4.1",{requires:["event-synthetic"]});