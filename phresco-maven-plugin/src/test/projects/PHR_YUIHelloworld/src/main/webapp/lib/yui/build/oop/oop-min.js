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
YUI.add("oop",function(h){var d=h.Lang,c=h.Array,b=Object.prototype,a="_~yuim~_",e=b.hasOwnProperty,g=b.toString;function f(l,k,m,i,j){if(l&&l[j]&&l!==h){return l[j].call(l,k,m);}else{switch(c.test(l)){case 1:return c[j](l,k,m);case 2:return c[j](h.Array(l,0,true),k,m);default:return h.Object[j](l,k,m,i);}}}h.augment=function(i,k,r,o,s){var n=i.prototype,m=n&&k,q=k.prototype,v=n||i,j,u,p,l,t;s=s?h.Array(s):[];if(m){u={};p={};l={};j=function(x,w){if(r||!(w in n)){if(g.call(x)==="[object Function]"){l[w]=x;u[w]=p[w]=function(){return t(this,x,arguments);};}else{u[w]=x;}}};t=function(w,y,z){for(var x in l){if(e.call(l,x)&&w[x]===p[x]){w[x]=l[x];}}k.apply(w,s);return y.apply(w,z);};if(o){h.Array.each(o,function(w){if(w in q){j(q[w],w);}});}else{h.Object.each(q,j,null,true);}}h.mix(v,u||q,r,o);if(!m){k.apply(v,s);}return i;};h.aggregate=function(k,j,i,l){return h.mix(k,j,i,l,0,true);};h.extend=function(l,k,i,n){if(!k||!l){h.error("extend failed, verify dependencies");}var m=k.prototype,j=h.Object(m);l.prototype=j;j.constructor=l;l.superclass=m;if(k!=Object&&m.constructor==b.constructor){m.constructor=k;}if(i){h.mix(j,i,true);}if(n){h.mix(l,n,true);}return l;};h.each=function(k,j,l,i){return f(k,j,l,i,"each");};h.some=function(k,j,l,i){return f(k,j,l,i,"some");};h.clone=function(l,m,r,s,k,q){if(!d.isObject(l)){return l;}if(h.instanceOf(l,YUI)){return l;}var n,j=q||{},i,p=h.each;switch(d.type(l)){case"date":return new Date(l);case"regexp":return l;case"function":return l;case"array":n=[];break;default:if(l[a]){return j[l[a]];}i=h.guid();n=(m)?{}:h.Object(l);l[a]=i;j[i]=l;}if(!l.addEventListener&&!l.attachEvent){p(l,function(t,o){if((o||o===0)&&(!r||(r.call(s||this,t,o,this,l)!==false))){if(o!==a){if(o=="prototype"){}else{this[o]=h.clone(t,m,r,s,k||l,j);}}}},n);}if(!q){h.Object.each(j,function(t,o){if(t[a]){try{delete t[a];}catch(u){t[a]=null;}}},this);j=null;}return n;};h.bind=function(i,k){var j=arguments.length>2?h.Array(arguments,2,true):null;return function(){var m=d.isString(i)?k[i]:i,l=(j)?j.concat(h.Array(arguments,0,true)):arguments;return m.apply(k||m,l);};};h.rbind=function(i,k){var j=arguments.length>2?h.Array(arguments,2,true):null;return function(){var m=d.isString(i)?k[i]:i,l=(j)?h.Array(arguments,0,true).concat(j):arguments;return m.apply(k||m,l);};};},"3.4.1",{requires:["yui-base"]});