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
YUI.add("yql",function(b){var a=function(e,f,d,c){if(!d){d={};}d.q=e;if(!d.format){d.format=b.YQLRequest.FORMAT;}if(!d.env){d.env=b.YQLRequest.ENV;}this._params=d;this._opts=c;this._callback=f;};a.prototype={_jsonp:null,_opts:null,_callback:null,_params:null,send:function(){var c=[],d=((this._opts&&this._opts.proto)?this._opts.proto:b.YQLRequest.PROTO);b.each(this._params,function(g,f){c.push(f+"="+encodeURIComponent(g));});c=c.join("&");d+=((this._opts&&this._opts.base)?this._opts.base:b.YQLRequest.BASE_URL)+c;var e=(!b.Lang.isFunction(this._callback))?this._callback:{on:{success:this._callback}};if(e.allowCache!==false){e.allowCache=true;}if(!this._jsonp){this._jsonp=b.jsonp(d,e);}else{this._jsonp.url=d;if(e.on&&e.on.success){this._jsonp._config.on.success=e.on.success;}this._jsonp.send();}return this;}};a.FORMAT="json";a.PROTO="http";a.BASE_URL=":/"+"/query.yahooapis.com/v1/public/yql?";a.ENV="http:/"+"/datatables.org/alltables.env";b.YQLRequest=a;b.YQL=function(e,f,d,c){return new b.YQLRequest(e,f,d,c).send();};},"3.4.1",{requires:["jsonp","jsonp-url"]});