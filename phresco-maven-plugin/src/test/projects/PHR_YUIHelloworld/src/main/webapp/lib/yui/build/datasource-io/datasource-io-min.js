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
YUI.add("datasource-io",function(b){var a=function(){a.superclass.constructor.apply(this,arguments);};b.mix(a,{NAME:"dataSourceIO",ATTRS:{io:{value:b.io,cloneDefaultValue:false},ioConfig:{value:null}}});b.extend(a,b.DataSource.Local,{initializer:function(c){this._queue={interval:null,conn:null,requests:[]};},successHandler:function(h,c,g){var d=this.get("ioConfig"),f=g.details[0];delete b.DataSource.Local.transactions[g.tId];f.data=c;this.fire("data",f);if(d&&d.on&&d.on.success){d.on.success.apply(d.context||b,arguments);}},failureHandler:function(h,c,g){var d=this.get("ioConfig"),f=g.details[0];delete b.DataSource.Local.transactions[g.tId];f.error=new Error("IO data failure");f.data=c;this.fire("data",f);if(d&&d.on&&d.on.failure){d.on.failure.apply(d.context||b,arguments);}},_queue:null,_defRequestFn:function(h){var g=this.get("source"),i=this.get("io"),d=this.get("ioConfig"),f=h.request,c=b.merge(d,h.cfg,{on:b.merge(d,{success:this.successHandler,failure:this.failureHandler}),context:this,"arguments":h});if(b.Lang.isString(f)){if(c.method&&(c.method.toUpperCase()==="POST")){c.data=c.data?c.data+f:f;}else{g+=f;}}b.DataSource.Local.transactions[h.tId]=i(g,c);return h.tId;}});b.DataSource.IO=a;},"3.4.1",{requires:["datasource-local","io-base"]});