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
YUI.add("slider-value-range",function(f){var b="min",e="max",d="value",c=Math.round;function a(){this._initSliderValueRange();}f.SliderValueRange=f.mix(a,{prototype:{_factor:1,_initSliderValueRange:function(){},_bindValueLogic:function(){this.after({minChange:this._afterMinChange,maxChange:this._afterMaxChange,valueChange:this._afterValueChange});},_syncThumbPosition:function(){this._calculateFactor();this._setPosition(this.get(d));},_calculateFactor:function(){var j=this.get("length"),h=this.thumb.getStyle(this._key.dim),i=this.get(b),g=this.get(e);j=parseFloat(j)||150;h=parseFloat(h)||15;this._factor=(g-i)/(j-h);},_defThumbMoveFn:function(i){var g=this.get(d),h=this._offsetToValue(i.offset);if(g!==h){this.set(d,h,{positioned:true});}},_offsetToValue:function(h){var g=c(h*this._factor)+this.get(b);return c(this._nearestValue(g));},_valueToOffset:function(g){var h=c((g-this.get(b))/this._factor);return h;},getValue:function(){return this.get(d);},setValue:function(g){return this.set(d,g);},_afterMinChange:function(g){this._verifyValue();this._syncThumbPosition();},_afterMaxChange:function(g){this._verifyValue();this._syncThumbPosition();},_verifyValue:function(){var h=this.get(d),g=this._nearestValue(h);if(h!==g){this.set(d,g);}},_afterValueChange:function(g){if(!g.positioned){this._setPosition(g.newVal);}},_setPosition:function(g){this._uiMoveThumb(this._valueToOffset(g));},_validateNewMin:function(g){return f.Lang.isNumber(g);},_validateNewMax:function(g){return f.Lang.isNumber(g);},_setNewValue:function(g){return c(this._nearestValue(g));},_nearestValue:function(j){var i=this.get(b),g=this.get(e),h;h=(g>i)?g:i;i=(g>i)?i:g;g=h;return(j<i)?i:(j>g)?g:j;}},ATTRS:{min:{value:0,validator:"_validateNewMin"},max:{value:100,validator:"_validateNewMax"},value:{value:0,setter:"_setNewValue"}}},true);},"3.4.1",{requires:["slider-base"]});