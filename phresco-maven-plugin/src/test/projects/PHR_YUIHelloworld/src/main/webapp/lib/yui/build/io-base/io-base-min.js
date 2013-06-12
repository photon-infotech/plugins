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
YUI.add("io-base",function(a){var i=a.Lang.isNumber,j=a.Lang.isObject,g=["start","complete","end","success","failure"],b=["status","statusText","responseText","responseXML"],f=a.config.win,d=f.XMLHttpRequest,h=f.XDomainRequest,e=0;function c(k){var l=this;l._uid="io:"+e++;l._init(k);a.io._map[l._uid]=l;}c.prototype={_id:0,_headers:{"X-Requested-With":"XMLHttpRequest"},_timeout:{},_init:function(k){var m=this,l;m.cfg=k||{};a.augment(m,a.EventTarget);for(l=0;l<5;l++){m.publish("io:"+g[l],a.merge({broadcast:1},k));m.publish("io-trn:"+g[l],k);}},_create:function(l,r){var q=this,o={id:i(r)?r:q._id++,uid:q._uid},m=l.xdr,k=m&&m.use,n=(m&&m.use==="native"&&h),p=q._transport;if(!k){k=(l.form&&l.form.upload)?"iframe":"xhr";}switch(k){case"native":case"xhr":o.c=n?new h():d?new d():new ActiveXObject("Microsoft.XMLHTTP");o.t=n?true:false;break;default:o.c=(p&&p[k])||{};o.t=true;}return o;},_destroy:function(k){if(f&&!k.t){if(d){k.c.onreadystatechange=null;}else{if(a.UA.ie&&!k.e){k.c.abort();}}}k=k.c=null;},_evt:function(o,l,k){var q=this,m,r=k["arguments"],s=q.cfg.emitFacade,n="io:"+o,p="io-trn:"+o;if(l.e){l.c={status:0,statusText:l.e};}m=[(s)?{id:l.id,data:l.c,cfg:k,"arguments":r}:l.id];if(!s){if(o===g[0]||o===g[2]){if(r){m.push(r);}}else{m.push(l.c);if(r){m.push(r);}}}m.unshift(n);q.fire.apply(q,m);if(k.on){m[0]=p;q.once(p,k.on[o],k.context||a);q.fire.apply(q,m);}},start:function(l,k){this._evt(g[0],l,k);},complete:function(l,k){this._evt(g[1],l,k);},end:function(l,k){this._evt(g[2],l,k);this._destroy(l);},success:function(l,k){this._evt(g[3],l,k);this.end(l,k);},failure:function(l,k){this._evt(g[4],l,k);this.end(l,k);},_retry:function(m,l,k){this._destroy(m);k.xdr.use="flash";return this.send(l,k,m.id);},_concat:function(k,l){k+=(k.indexOf("?")===-1?"?":"&")+l;return k;},setHeader:function(k,l){if(l){this._headers[k]=l;}else{delete this._headers[k];}},_setHeaders:function(l,k){k=a.merge(this._headers,k);a.Object.each(k,function(n,m){if(n!=="disable"){l.setRequestHeader(m,k[m]);}});},_startTimeout:function(l,k){var m=this;m._timeout[l.id]=f.setTimeout(function(){m._abort(l,"timeout");},k);},_clearTimeout:function(k){f.clearTimeout(this._timeout[k]);delete this._timeout[k];},_result:function(n,l){var k;try{k=n.c.status;}catch(m){k=0;}if(k>=200&&k<300||k===304||k===1223){this.success(n,l);}else{this.failure(n,l);}},_rS:function(l,k){var m=this;if(l.c.readyState===4){if(k.timeout){m._clearTimeout(l.id);}f.setTimeout(function(){m.complete(l,k);m._result(l,k);},0);}},_abort:function(l,k){if(l&&l.c){l.e=k;l.c.abort();}},send:function(m,n,l){var o,k,r,s,w,q,v=this,x=m,p={};n=n?a.Object(n):{};o=v._create(n,l);k=n.method?n.method.toUpperCase():"GET";w=n.sync;q=n.data;if(j(q)){q=a.QueryString.stringify(q);}if(n.form){if(n.form.upload){return v.upload(o,m,n);}else{q=v._serialize(n.form,q);}}if(q){switch(k){case"GET":case"HEAD":case"DELETE":x=v._concat(x,q);q="";break;case"POST":case"PUT":n.headers=a.merge({"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"},n.headers);break;}}if(o.t){return v.xdr(x,o,n);}if(!w){o.c.onreadystatechange=function(){v._rS(o,n);};}try{o.c.open(k,x,!w,n.username||null,n.password||null);v._setHeaders(o.c,n.headers||{});v.start(o,n);if(n.xdr&&n.xdr.credentials){if(!a.UA.ie){o.c.withCredentials=true;}}o.c.send(q);if(w){for(r=0,s=b.length;r<s;++r){p[b[r]]=o.c[b[r]];}p.getAllResponseHeaders=function(){return o.c.getAllResponseHeaders();};p.getResponseHeader=function(u){return o.c.getResponseHeader(u);};v.complete(o,n);v._result(o,n);return p;}}catch(t){if(o.t){return v._retry(o,m,n);}else{v.complete(o,n);v._result(o,n);}}if(n.timeout){v._startTimeout(o,n.timeout);}return{id:o.id,abort:function(){return o.c?v._abort(o,"abort"):false;},isInProgress:function(){return o.c?(o.c.readyState%4):false;},io:v};}};a.io=function(l,k){var m=a.io._map["io:0"]||new c();return m.send.apply(m,[l,k]);};a.io.header=function(k,l){var m=a.io._map["io:0"]||new c();m.setHeader(k,l);};a.IO=c;a.io._map={};},"3.4.1",{requires:["event-custom-base","querystring-stringify-simple"]});