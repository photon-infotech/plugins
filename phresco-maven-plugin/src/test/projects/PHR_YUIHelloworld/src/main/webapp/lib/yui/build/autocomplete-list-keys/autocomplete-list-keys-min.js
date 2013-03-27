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
YUI.add("autocomplete-list-keys",function(g){var c=40,a=13,d=27,f=9,b=38;function e(){g.before(this._unbindKeys,this,"destructor");g.before(this._bindKeys,this,"bindUI");this._initKeys();}e.prototype={_initKeys:function(){var h={},i={};this._keyEvents=[];h[c]=this._keyDown;i[a]=this._keyEnter;i[d]=this._keyEsc;i[f]=this._keyTab;i[b]=this._keyUp;this._keys=h;this._keysVisible=i;},_bindKeys:function(){this._keyEvents.push(this._inputNode.on("keydown",this._onInputKey,this));},_unbindKeys:function(){while(this._keyEvents.length){this._keyEvents.pop().detach();}},_keyDown:function(){if(this.get("visible")){this._activateNextItem();}else{this.show();}},_keyEnter:function(i){var h=this.get("activeItem");if(h){this.selectItem(h,i);}else{return false;}},_keyEsc:function(){this.hide();},_keyTab:function(i){var h;if(this.get("tabSelect")){h=this.get("activeItem");if(h){this.selectItem(h,i);return true;}}return false;},_keyUp:function(){this._activatePrevItem();},_onInputKey:function(j){var h,i=j.keyCode;this._lastInputKey=i;if(this.get("results").length){h=this._keys[i];if(!h&&this.get("visible")){h=this._keysVisible[i];}if(h){if(h.call(this,j)!==false){j.preventDefault();}}}}};g.Base.mix(g.AutoCompleteList,[e]);},"3.4.1",{requires:["autocomplete-list","base-build"]});