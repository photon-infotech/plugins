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
YUI.add("datasource-get",function(b){var a=function(){a.superclass.constructor.apply(this,arguments);};b.DataSource.Get=b.extend(a,b.DataSource.Local,{_defRequestFn:function(j){var h=this.get("source"),f=this.get("get"),d=b.guid().replace(/\-/g,"_"),g=this.get("generateRequestCallback"),i=j.details[0],c=this;this._last=d;YUI.Env.DataSource.callbacks[d]=function(e){delete YUI.Env.DataSource.callbacks[d];delete b.DataSource.Local.transactions[j.tId];var k=c.get("asyncMode")!=="ignoreStaleResponses"||c._last===d;if(k){i.data=e;c.fire("data",i);}else{}};h+=j.request+g.call(this,d);b.DataSource.Local.transactions[j.tId]=f.script(h,{autopurge:true,onFailure:function(e){delete YUI.Env.DataSource.callbacks[d];delete b.DataSource.Local.transactions[j.tId];i.error=new Error(e.msg||"Script node data failure");c.fire("data",i);},onTimeout:function(e){delete YUI.Env.DataSource.callbacks[d];delete b.DataSource.Local.transactions[j.tId];i.error=new Error(e.msg||"Script node data timeout");c.fire("data",i);}});return j.tId;},_generateRequest:function(c){return"&"+this.get("scriptCallbackParam")+"=YUI.Env.DataSource.callbacks."+c;}},{NAME:"dataSourceGet",ATTRS:{get:{value:b.Get,cloneDefaultValue:false},asyncMode:{value:"allowAll"},scriptCallbackParam:{value:"callback"},generateRequestCallback:{value:function(){return this._generateRequest.apply(this,arguments);}}}});YUI.namespace("Env.DataSource.callbacks");},"3.4.1",{requires:["datasource-local","get"]});