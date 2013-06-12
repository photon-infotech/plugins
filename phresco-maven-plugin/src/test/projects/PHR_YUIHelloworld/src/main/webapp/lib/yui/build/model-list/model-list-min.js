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
YUI.add("model-list",function(b){var f=b.Attribute.prototype,h=b.Lang,i=b.Array,c="add",g="error",e="reset",d="remove";function a(){a.superclass.constructor.apply(this,arguments);}b.ModelList=b.extend(a,b.Base,{model:null,initializer:function(k){k||(k={});var j=this.model=k.model||this.model;this.publish(c,{defaultFn:this._defAddFn});this.publish(e,{defaultFn:this._defResetFn});this.publish(d,{defaultFn:this._defRemoveFn});if(j){this.after("*:idChange",this._afterIdChange);}else{}this._clear();},destructor:function(){i.each(this._items,this._detachList,this);},add:function(k,j){if(h.isArray(k)){return i.map(k,function(l){return this._add(l,j);},this);}else{return this._add(k,j);}},create:function(l,k,m){var j=this;if(typeof k==="function"){m=k;k={};}if(!(l instanceof b.Model)){l=new this.model(l);}return l.save(k,function(n){if(!n){j.add(l,k);}m&&m.apply(null,arguments);});},get:function(j){if(this.attrAdded(j)){return f.get.apply(this,arguments);}return this.invoke("get",j);},getAsHTML:function(j){if(this.attrAdded(j)){return b.Escape.html(f.get.apply(this,arguments));}return this.invoke("getAsHTML",j);},getAsURL:function(j){if(this.attrAdded(j)){return encodeURIComponent(f.get.apply(this,arguments));}return this.invoke("getAsURL",j);},getByClientId:function(j){return this._clientIdMap[j]||null;},getById:function(j){return this._idMap[j]||null;},invoke:function(k){var j=[this._items,k].concat(i(arguments,1,true));return i.invoke.apply(i,j);},load:function(k,l){var j=this;if(typeof k==="function"){l=k;k={};}this.sync("read",k,function(n,m){if(!n){j.reset(j.parse(m),k);}l&&l.apply(null,arguments);});return this;},map:function(j,k){return i.map(this._items,j,k);},parse:function(j){if(typeof j==="string"){try{return b.JSON.parse(j)||[];}catch(k){this.fire(g,{error:k,response:j,src:"parse"});return null;}}return j||[];},remove:function(k,j){if(h.isArray(k)){return i.map(k,function(l){return this._remove(l,j);},this);}else{return this._remove(k,j);}},reset:function(l,j){l||(l=[]);j||(j={});var k=b.merge(j,{src:"reset",models:i.map(l,function(m){return m instanceof b.Model?m:new this.model(m);},this)});if(this.comparator){k.models.sort(b.bind(this._sort,this));}j.silent?this._defResetFn(k):this.fire(e,k);return this;},sort:function(j){var l=this._items.concat(),k;if(!this.comparator){return this;}j||(j={});l.sort(b.bind(this._sort,this));k=b.merge(j,{models:l,src:"sort"});j.silent?this._defResetFn(k):this.fire(e,k);return this;},sync:function(){var j=i(arguments,0,true).pop();if(typeof j==="function"){j();}},toArray:function(){return this._items.concat();},toJSON:function(){return this.map(function(j){return j.toJSON();});},_add:function(k,j){var l;j||(j={});if(!(k instanceof b.Model)){k=new this.model(k);}if(this._clientIdMap[k.get("clientId")]){this.fire(g,{error:"Model is already in the list.",model:k,src:"add"});return;}l=b.merge(j,{index:this._findIndex(k),model:k});j.silent?this._defAddFn(l):this.fire(c,l);return k;},_attachList:function(j){j.lists.push(this);j.addTarget(this);},_clear:function(){i.each(this._items,this._detachList,this);this._clientIdMap={};this._idMap={};this._items=[];},_detachList:function(k){var j=i.indexOf(k.lists,this);if(j>-1){k.lists.splice(j,1);k.removeTarget(this);}},_findIndex:function(n){var k=this.comparator,l=this._items,j=l.length,o=0,p,m,q;if(!k||!l.length){return l.length;}q=k(n);while(o<j){m=(o+j)>>1;p=l[m];if(k(p)<q){o=m+1;}else{j=m;}}return o;},_remove:function(l,k){var j=this.indexOf(l),m;k||(k={});if(j===-1){this.fire(g,{error:"Model is not in the list.",model:l,src:"remove"});return;}m=b.merge(k,{index:j,model:l});k.silent?this._defRemoveFn(m):this.fire(d,m);return l;},_sort:function(k,j){var m=this.comparator(k),l=this.comparator(j);return m<l?-1:(m>l?1:0);},_afterIdChange:function(j){h.isValue(j.prevVal)&&delete this._idMap[j.prevVal];h.isValue(j.newVal)&&(this._idMap[j.newVal]=j.target);},_defAddFn:function(k){var j=k.model,l=j.get("id");this._clientIdMap[j.get("clientId")]=j;if(h.isValue(l)){this._idMap[l]=j;}this._attachList(j);this._items.splice(k.index,0,j);},_defRemoveFn:function(k){var j=k.model,l=j.get("id");this._detachList(j);delete this._clientIdMap[j.get("clientId")];if(h.isValue(l)){delete this._idMap[l];}this._items.splice(k.index,1);},_defResetFn:function(j){if(j.src==="sort"){this._items=j.models.concat();return;}this._clear();if(j.models.length){this.add(j.models,{silent:true});}}},{NAME:"modelList"});b.augment(a,b.ArrayList);},"3.4.1",{requires:["array-extras","array-invoke","arraylist","base-build","escape","json-parse","model"]});