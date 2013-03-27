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
YUI.add("resize-proxy",function(c){var n="activeHandleNode",i="cursor",g="dragCursor",l="host",k="parentNode",f="proxy",d="proxyNode",b="resize",a="resize-proxy",j="wrapper",e=c.ClassNameManager.getClassName,m=e(b,f);function h(){h.superclass.constructor.apply(this,arguments);}c.mix(h,{NAME:a,NS:f,ATTRS:{proxyNode:{setter:c.one,valueFn:function(){return c.Node.create(this.PROXY_TEMPLATE);}}}});c.extend(h,c.Plugin.Base,{PROXY_TEMPLATE:'<div class="'+m+'"></div>',initializer:function(){var o=this;o.afterHostEvent("resize:start",o._afterResizeStart);o.beforeHostMethod("_resize",o._beforeHostResize);o.afterHostMethod("_resizeEnd",o._afterHostResizeEnd);},destructor:function(){var o=this;o.get(d).remove(true);},_afterHostResizeEnd:function(q){var o=this,p=q.dragEvent.target;p.actXY=[];o._syncProxyUI();o.get(d).hide();},_afterResizeStart:function(p){var o=this;o._renderProxy();},_beforeHostResize:function(q){var o=this,p=this.get(l);p._handleResizeAlignEvent(q.dragEvent);o._syncProxyUI();return new c.Do.Prevent();},_renderProxy:function(){var o=this,q=this.get(l),p=o.get(d);if(!p.inDoc()){q.get(j).get(k).append(p.hide());}},_syncProxyUI:function(){var o=this,q=this.get(l),s=q.info,r=q.get(n),p=o.get(d),t=r.getStyle(i);p.show().setStyle(i,t);q.delegate.dd.set(g,t);p.sizeTo(s.offsetWidth,s.offsetHeight);p.setXY([s.left,s.top]);}});c.namespace("Plugin");c.Plugin.ResizeProxy=h;},"3.4.1",{requires:["resize-base","plugin"],skinnable:false});