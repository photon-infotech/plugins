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
YUI.add("event-valuechange",function(d){var b=d.Array,c="value",a={POLL_INTERVAL:50,TIMEOUT:10000,_history:{},_intervals:{},_notifiers:{},_timeouts:{},_poll:function(j,g,k){var i=j._node,f=i&&i.value,l=a._history[g],h;if(!i){a._stopPolling(j,g);return;}if(f!==l){a._history[g]=f;h={_event:k,newVal:f,prevVal:l};b.each(a._notifiers[g],function(e){e.fire(h);});a._refreshTimeout(j,g);}},_refreshTimeout:function(f,e){a._stopTimeout(f,e);a._timeouts[e]=setTimeout(function(){a._stopPolling(f,e);},a.TIMEOUT);},_startPolling:function(g,f,i,h){if(!f){f=d.stamp(g);}if(!h&&a._intervals[f]){return;}a._stopPolling(g,f);a._intervals[f]=setInterval(function(){a._poll(g,f,i);},a.POLL_INTERVAL);a._refreshTimeout(g,f,i);},_stopPolling:function(f,e){if(!e){e=d.stamp(f);}a._intervals[e]=clearInterval(a._intervals[e]);a._stopTimeout(f,e);},_stopTimeout:function(f,e){if(!e){e=d.stamp(f);}a._timeouts[e]=clearTimeout(a._timeouts[e]);},_onBlur:function(f){a._stopPolling(f.currentTarget);},_onFocus:function(g){var f=g.currentTarget;a._history[d.stamp(f)]=f.get(c);a._startPolling(f,null,g);},_onKeyDown:function(f){a._startPolling(f.currentTarget,null,f);},_onKeyUp:function(f){if(f.charCode===229||f.charCode===197){a._startPolling(f.currentTarget,null,f,true);}},_onMouseDown:function(f){a._startPolling(f.currentTarget,null,f);},_onSubscribe:function(i,h,g){var f=d.stamp(i),e=a._notifiers[f];a._history[f]=i.get(c);g._handles=i.on({blur:a._onBlur,focus:a._onFocus,keydown:a._onKeyDown,keyup:a._onKeyUp,mousedown:a._onMouseDown});if(!e){e=a._notifiers[f]=[];}e.push(g);},_onUnsubscribe:function(j,i,h){var g=d.stamp(j),e=a._notifiers[g],f=b.indexOf(e,h);h._handles.detach();if(f!==-1){e.splice(f,1);if(!e.length){a._stopPolling(j,g);delete a._notifiers[g];delete a._history[g];}}}};d.Event.define("valueChange",{detach:a._onUnsubscribe,on:a._onSubscribe,publishConfig:{emitFacade:true}});d.ValueChange=a;},"3.4.1",{requires:["event-focus","event-synthetic"]});