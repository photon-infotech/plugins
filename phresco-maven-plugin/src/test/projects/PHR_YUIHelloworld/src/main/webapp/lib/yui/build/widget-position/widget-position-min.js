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
YUI.add("widget-position",function(a){var i=a.Lang,l=a.Widget,n="xy",j="position",g="positioned",k="boundingBox",h="relative",m="renderUI",f="bindUI",d="syncUI",c=l.UI_SRC,e="xyChange";function b(o){this._posNode=this.get(k);a.after(this._renderUIPosition,this,m);a.after(this._syncUIPosition,this,d);a.after(this._bindUIPosition,this,f);}b.ATTRS={x:{setter:function(o){this._setX(o);},getter:function(){return this._getX();},lazyAdd:false},y:{setter:function(o){this._setY(o);},getter:function(){return this._getY();},lazyAdd:false},xy:{value:[0,0],validator:function(o){return this._validateXY(o);}}};b.POSITIONED_CLASS_NAME=l.getClassName(g);b.prototype={_renderUIPosition:function(){this._posNode.addClass(b.POSITIONED_CLASS_NAME);},_syncUIPosition:function(){var o=this._posNode;if(o.getStyle(j)===h){this.syncXY();}this._uiSetXY(this.get(n));},_bindUIPosition:function(){this.after(e,this._afterXYChange);},move:function(){var o=arguments,p=(i.isArray(o[0]))?o[0]:[o[0],o[1]];this.set(n,p);},syncXY:function(){this.set(n,this._posNode.getXY(),{src:c});},_validateXY:function(o){return(i.isArray(o)&&i.isNumber(o[0])&&i.isNumber(o[1]));},_setX:function(o){this.set(n,[o,this.get(n)[1]]);},_setY:function(o){this.set(n,[this.get(n)[0],o]);},_getX:function(){return this.get(n)[0];},_getY:function(){return this.get(n)[1];},_afterXYChange:function(o){if(o.src!=c){this._uiSetXY(o.newVal);}},_uiSetXY:function(o){this._posNode.setXY(o);}};a.WidgetPosition=b;},"3.4.1",{requires:["base-build","node-screen","widget"]});