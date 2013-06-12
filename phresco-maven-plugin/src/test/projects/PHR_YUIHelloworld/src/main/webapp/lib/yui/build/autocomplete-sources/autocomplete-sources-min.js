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
YUI.add("autocomplete-sources",function(g){var a=g.AutoCompleteBase,f=g.Lang,b="_sourceSuccess",d="maxResults",e="requestTemplate",c="resultListLocator";g.mix(a.prototype,{_YQL_SOURCE_REGEX:/^(?:select|set|use)\s+/i,_beforeCreateObjectSource:function(h){if(h instanceof g.Node&&h.get("nodeName").toLowerCase()==="select"){return this._createSelectSource(h);}if(g.JSONPRequest&&h instanceof g.JSONPRequest){return this._createJSONPSource(h);}return this._createObjectSource(h);},_createIOSource:function(m){var j={},k={type:"io"},l=this,o,i,n;function h(p){var r=p.request,q=p.query;if(j[r]){l[b](j[r],p);return;}if(o&&o.isInProgress()){o.abort();}o=g.io(l._getXHRUrl(m,p),{on:{success:function(v,s){var u;try{u=g.JSON.parse(s.responseText);}catch(t){g.error("JSON parse error",t);}if(u){j[r]=u;l[b](u,p);}}}});}k.sendRequest=function(p){i=p;if(n){return;}n=true;g.use("io-base","json-parse",function(){k.sendRequest=h;h(i);});};return k;},_createJSONPSource:function(m){var j={},k={type:"jsonp"},l=this,i,n;function h(o){var q=o.request,p=o.query;if(j[q]){l[b](j[q],o);return;}m._config.on.success=function(r){j[q]=r;l[b](r,o);};m.send(p);}k.sendRequest=function(o){i=o;if(n){return;}n=true;g.use("jsonp",function(){if(!(m instanceof g.JSONPRequest)){m=new g.JSONPRequest(m,{format:g.bind(l._jsonpFormatter,l)});}k.sendRequest=h;h(i);});};return k;},_createSelectSource:function(i){var h=this;return{type:"select",sendRequest:function(k){var j=[];i.get("options").each(function(l){j.push({html:l.get("innerHTML"),index:l.get("index"),node:l,selected:l.get("selected"),text:l.get("text"),value:l.get("value")});});h[b](j,k);}};},_createStringSource:function(h){if(this._YQL_SOURCE_REGEX.test(h)){return this._createYQLSource(h);}else{if(h.indexOf("{callback}")!==-1){return this._createJSONPSource(h);}else{return this._createIOSource(h);}}},_createYQLSource:function(m){var j={},n={type:"yql"},l=this,i,o,k;if(!this.get(c)){this.set(c,this._defaultYQLLocator);}function h(t){var v=t.request,u=t.query,w,r,p,s,q;if(j[v]){l[b](j[v],t);return;}w=function(x){j[v]=x;l[b](x,t);};r=l.get("yqlEnv");p=l.get(d);s={proto:l.get("yqlProtocol")};q=f.sub(m,{maxResults:p>0?p:1000,query:u});if(k){k._callback=w;k._opts=s;k._params.q=q;if(r){k._params.env=r;}}else{k=new g.YQLRequest(q,{on:{success:w},allowCache:false},r?{env:r}:null,s);}k.send();}n.sendRequest=function(p){i=p;if(!o){o=true;g.use("yql",function(){n.sendRequest=h;h(i);});}};return n;},_defaultYQLLocator:function(i){var j=i&&i.query&&i.query.results,h;if(j&&f.isObject(j)){h=g.Object.values(j)||[];j=h.length===1?h[0]:h;if(!f.isArray(j)){j=[j];}}else{j=[];}return j;},_getXHRUrl:function(i,j){var h=this.get(d);if(j.query!==j.request){i+=j.request;}return f.sub(i,{maxResults:h>0?h:1000,query:encodeURIComponent(j.query)});},_jsonpFormatter:function(i,j,k){var h=this.get(d),l=this.get(e);if(l){i+=l(k);}return f.sub(i,{callback:j,maxResults:h>0?h:1000,query:encodeURIComponent(k)});}});g.mix(a.ATTRS,{yqlEnv:{value:null},yqlProtocol:{value:"http"}});g.mix(a.SOURCE_TYPES,{io:"_createIOSource",jsonp:"_createJSONPSource",object:"_beforeCreateObjectSource",select:"_createSelectSource",string:"_createStringSource",yql:"_createYQLSource"},true);},"3.4.1",{optional:["io-base","json-parse","jsonp","yql"],requires:["autocomplete-base"]});