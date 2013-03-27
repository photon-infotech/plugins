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
YUI.add("arraylist",function(e){var d=e.Array,c=d.each,a;function b(f){if(f!==undefined){this._items=e.Lang.isArray(f)?f:d(f);}else{this._items=this._items||[];}}a={item:function(f){return this._items[f];},each:function(g,f){c(this._items,function(j,h){j=this.item(h);g.call(f||j,j,h,this);},this);return this;},some:function(g,f){return d.some(this._items,function(j,h){j=this.item(h);return g.call(f||j,j,h,this);},this);},indexOf:function(f){return d.indexOf(this._items,f);},size:function(){return this._items.length;},isEmpty:function(){return !this.size();},toJSON:function(){return this._items;}};a._item=a.item;b.prototype=a;e.mix(b,{addMethod:function(f,g){g=d(g);c(g,function(h){f[h]=function(){var j=d(arguments,0,true),i=[];c(this._items,function(m,l){m=this._item(l);var k=m[h].apply(m,j);if(k!==undefined&&k!==m){i[l]=k;}},this);return i.length?i:this;};});}});e.ArrayList=b;},"3.4.1",{requires:["yui-base"]});