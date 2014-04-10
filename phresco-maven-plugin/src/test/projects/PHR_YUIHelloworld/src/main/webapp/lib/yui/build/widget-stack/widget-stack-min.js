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
YUI.add("widget-stack",function(e){var m=e.Lang,s=e.UA,B=e.Node,f=e.Widget,A="zIndex",o="shim",y="visible",C="boundingBox",v="renderUI",g="bindUI",r="syncUI",p="offsetWidth",d="offsetHeight",l="parentNode",a="firstChild",w="ownerDocument",h="width",u="height",k="px",n="shimdeferred",D="shimresize",x="visibleChange",c="widthChange",j="heightChange",z="shimChange",b="zIndexChange",i="contentUpdate",q="stacked";function t(E){this._stackNode=this.get(C);this._stackHandles={};e.after(this._renderUIStack,this,v);e.after(this._syncUIStack,this,r);e.after(this._bindUIStack,this,g);}t.ATTRS={shim:{value:(s.ie==6)},zIndex:{value:1,setter:function(E){return this._setZIndex(E);}}};t.HTML_PARSER={zIndex:function(E){return E.getStyle(A);}};t.SHIM_CLASS_NAME=f.getClassName(o);t.STACKED_CLASS_NAME=f.getClassName(q);t.SHIM_TEMPLATE='<iframe class="'+t.SHIM_CLASS_NAME+'" frameborder="0" title="Widget Stacking Shim" src="javascript:false" tabindex="-1" role="presentation"></iframe>';t.prototype={_syncUIStack:function(){this._uiSetShim(this.get(o));this._uiSetZIndex(this.get(A));},_bindUIStack:function(){this.after(z,this._afterShimChange);this.after(b,this._afterZIndexChange);},_renderUIStack:function(){this._stackNode.addClass(t.STACKED_CLASS_NAME);},_setZIndex:function(E){if(m.isString(E)){E=parseInt(E,10);}if(!m.isNumber(E)){E=0;}return E;},_afterShimChange:function(E){this._uiSetShim(E.newVal);},_afterZIndexChange:function(E){this._uiSetZIndex(E.newVal);},_uiSetZIndex:function(E){this._stackNode.setStyle(A,E);},_uiSetShim:function(E){if(E){if(this.get(y)){this._renderShim();}else{this._renderShimDeferred();}if(s.ie==6){this._addShimResizeHandlers();}}else{this._destroyShim();}},_renderShimDeferred:function(){this._stackHandles[n]=this._stackHandles[n]||[];var F=this._stackHandles[n],E=function(G){if(G.newVal){this._renderShim();}};F.push(this.on(x,E));},_addShimResizeHandlers:function(){this._stackHandles[D]=this._stackHandles[D]||[];var F=this.sizeShim,E=this._stackHandles[D];E.push(this.after(x,F));E.push(this.after(c,F));E.push(this.after(j,F));E.push(this.after(i,F));},_detachStackHandles:function(E){var F=this._stackHandles[E],G;if(F&&F.length>0){while((G=F.pop())){G.detach();}}},_renderShim:function(){var E=this._shimNode,F=this._stackNode;if(!E){E=this._shimNode=this._getShimTemplate();F.insertBefore(E,F.get(a));this._detachStackHandles(n);this.sizeShim();}},_destroyShim:function(){if(this._shimNode){this._shimNode.get(l).removeChild(this._shimNode);this._shimNode=null;this._detachStackHandles(n);this._detachStackHandles(D);}},sizeShim:function(){var F=this._shimNode,E=this._stackNode;if(F&&s.ie===6&&this.get(y)){F.setStyle(h,E.get(p)+k);F.setStyle(u,E.get(d)+k);}},_getShimTemplate:function(){return B.create(t.SHIM_TEMPLATE,this._stackNode.get(w));}};e.WidgetStack=t;},"3.4.1",{requires:["base-build","widget"]});