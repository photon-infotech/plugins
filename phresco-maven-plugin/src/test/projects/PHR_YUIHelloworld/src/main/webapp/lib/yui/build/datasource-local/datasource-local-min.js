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
YUI.add("datasource-local",function(c){var b=c.Lang,a=function(){a.superclass.constructor.apply(this,arguments);};c.mix(a,{NAME:"dataSourceLocal",ATTRS:{source:{value:null}},_tId:0,transactions:{},issueCallback:function(h,d){var f=h.on||h.callback,i=f&&f.success,g=h.details[0];g.error=(h.error||h.response.error);if(g.error){d.fire("error",g);i=f&&f.failure;}if(i){i(g);}}});c.extend(a,c.Base,{initializer:function(d){this._initEvents();},_initEvents:function(){this.publish("request",{defaultFn:c.bind("_defRequestFn",this),queuable:true});this.publish("data",{defaultFn:c.bind("_defDataFn",this),queuable:true});this.publish("response",{defaultFn:c.bind("_defResponseFn",this),queuable:true});},_defRequestFn:function(g){var d=this.get("source"),f=g.details[0];if(b.isUndefined(d)){f.error=new Error("Local source undefined");}f.data=d;this.fire("data",f);},_defDataFn:function(i){var f=i.data,h=i.meta,d={results:(b.isArray(f))?f:[f],meta:(h)?h:{}},g=i.details[0];g.response=d;this.fire("response",g);},_defResponseFn:function(d){a.issueCallback(d,this);},sendRequest:function(e){var f=a._tId++,d;e=e||{};d=e.on||e.callback;this.fire("request",{tId:f,request:e.request,on:d,callback:d,cfg:e.cfg||{}});return f;}});c.namespace("DataSource").Local=a;},"3.4.1",{requires:["base"]});