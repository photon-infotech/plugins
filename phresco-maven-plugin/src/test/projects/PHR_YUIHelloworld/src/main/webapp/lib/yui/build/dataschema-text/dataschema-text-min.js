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
YUI.add("dataschema-text",function(e){var c=e.Lang,a=c.isString,d=c.isUndefined,b={apply:function(h,i){var f=i,g={results:[],meta:{}};if(a(i)&&h&&a(h.resultDelimiter)){g=b._parseResults.call(this,h,f,g);}else{g.error=new Error("Text schema parse failure");}return g;},_parseResults:function(g,q,h){var o=g.resultDelimiter,n=a(g.fieldDelimiter)&&g.fieldDelimiter,p=g.resultFields||[],m=[],f=e.DataSchema.Base.parse,r,u,x,w,s,v,t,l,k;if(q.slice(-o.length)===o){q=q.slice(0,-o.length);}r=q.split(g.resultDelimiter);if(n){for(l=r.length-1;l>=0;--l){x={};w=r[l];u=w.split(g.fieldDelimiter);for(k=p.length-1;k>=0;--k){s=p[k];v=(!d(s.key))?s.key:s;t=(!d(u[v]))?u[v]:u[k];x[v]=f.call(this,t,s);}m[l]=x;}}else{m=r;}h.results=m;return h;}};e.DataSchema.Text=e.mix(b,e.DataSchema.Base);},"3.4.1",{requires:["dataschema-base"]});