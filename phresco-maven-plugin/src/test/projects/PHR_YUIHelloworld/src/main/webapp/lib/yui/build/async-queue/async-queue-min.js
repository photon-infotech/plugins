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
YUI.add("async-queue",function(g){g.AsyncQueue=function(){this._init();this.add.apply(this,arguments);};var e=g.AsyncQueue,c="execute",b="shift",d="promote",h="remove",a=g.Lang.isObject,f=g.Lang.isFunction;e.defaults=g.mix({autoContinue:true,iterations:1,timeout:10,until:function(){this.iterations|=0;return this.iterations<=0;}},g.config.queueDefaults||{});g.extend(e,g.EventTarget,{_running:false,_init:function(){g.EventTarget.call(this,{prefix:"queue",emitFacade:true});this._q=[];this.defaults={};this._initEvents();},_initEvents:function(){this.publish({"execute":{defaultFn:this._defExecFn,emitFacade:true},"shift":{defaultFn:this._defShiftFn,emitFacade:true},"add":{defaultFn:this._defAddFn,emitFacade:true},"promote":{defaultFn:this._defPromoteFn,emitFacade:true},"remove":{defaultFn:this._defRemoveFn,emitFacade:true}});},next:function(){var i;while(this._q.length){i=this._q[0]=this._prepare(this._q[0]);if(i&&i.until()){this.fire(b,{callback:i});i=null;}else{break;}}return i||null;},_defShiftFn:function(i){if(this.indexOf(i.callback)===0){this._q.shift();}},_prepare:function(k){if(f(k)&&k._prepared){return k;}var i=g.merge(e.defaults,{context:this,args:[],_prepared:true},this.defaults,(f(k)?{fn:k}:k)),j=g.bind(function(){if(!j._running){j.iterations--;}if(f(j.fn)){j.fn.apply(j.context||g,g.Array(j.args));}},this);return g.mix(j,i);},run:function(){var j,i=true;for(j=this.next();i&&j&&!this.isRunning();j=this.next()){i=(j.timeout<0)?this._execute(j):this._schedule(j);}if(!j){this.fire("complete");}return this;},_execute:function(j){this._running=j._running=true;j.iterations--;this.fire(c,{callback:j});var i=this._running&&j.autoContinue;this._running=j._running=false;return i;},_schedule:function(i){this._running=g.later(i.timeout,this,function(){if(this._execute(i)){this.run();}});return false;},isRunning:function(){return !!this._running;},_defExecFn:function(i){i.callback();},add:function(){this.fire("add",{callbacks:g.Array(arguments,0,true)});return this;},_defAddFn:function(j){var k=this._q,i=[];g.Array.each(j.callbacks,function(l){if(a(l)){k.push(l);i.push(l);}});j.added=i;},pause:function(){if(a(this._running)){this._running.cancel();}this._running=false;return this;},stop:function(){this._q=[];return this.pause();},indexOf:function(m){var k=0,j=this._q.length,l;for(;k<j;++k){l=this._q[k];if(l===m||l.id===m){return k;}}return -1;},getCallback:function(k){var j=this.indexOf(k);return(j>-1)?this._q[j]:null;},promote:function(k){var j={callback:k},i;if(this.isRunning()){i=this.after(b,function(){this.fire(d,j);i.detach();},this);}else{this.fire(d,j);}return this;},_defPromoteFn:function(l){var j=this.indexOf(l.callback),k=(j>-1)?this._q.splice(j,1)[0]:null;l.promoted=k;if(k){this._q.unshift(k);}},remove:function(k){var j={callback:k},i;if(this.isRunning()){i=this.after(b,function(){this.fire(h,j);i.detach();},this);}else{this.fire(h,j);}return this;},_defRemoveFn:function(k){var j=this.indexOf(k.callback);k.removed=(j>-1)?this._q.splice(j,1)[0]:null;},size:function(){if(!this.isRunning()){this.next();}return this._q.length;}});},"3.4.1",{requires:["event-custom"]});