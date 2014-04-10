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
YUI.add("history-hash",function(a){var c=a.HistoryBase,f=a.Lang,l=a.Array,j=a.Object,k=YUI.namespace("Env.HistoryHash"),b="hash",e,d,i,h=a.config.win,m=h.location,n=a.config.useHistoryHTML5;function g(){g.superclass.constructor.apply(this,arguments);}a.extend(g,c,{_init:function(o){var p=g.parseHash();o=o||{};this._initialState=o.initialState?a.merge(o.initialState,p):p;a.after("hashchange",a.bind(this._afterHashChange,this),h);g.superclass._init.apply(this,arguments);},_change:function(q,p,o){j.each(p,function(s,r){if(f.isValue(s)){p[r]=s.toString();}});return g.superclass._change.call(this,q,p,o);},_storeState:function(r,q){var p=g.decode,o=g.createHash(q);g.superclass._storeState.apply(this,arguments);if(r!==b&&p(g.getHash())!==p(o)){g[r===c.SRC_REPLACE?"replaceHash":"setHash"](o);}},_afterHashChange:function(o){this._resolveChanges(b,g.parseHash(o.newHash),{});}},{NAME:"historyHash",SRC_HASH:b,hashPrefix:"",_REGEX_HASH:/([^\?#&]+)=([^&]+)/g,createHash:function(q){var o=g.encode,p=[];j.each(q,function(s,r){if(f.isValue(s)){p.push(o(r)+"="+o(s));}});return p.join("&");},decode:function(o){return decodeURIComponent(o.replace(/\+/g," "));},encode:function(o){return encodeURIComponent(o).replace(/%20/g,"+");},getHash:(a.UA.gecko?function(){var p=/#(.*)$/.exec(m.href),q=p&&p[1]||"",o=g.hashPrefix;return o&&q.indexOf(o)===0?q.replace(o,""):q;}:function(){var p=m.hash.substring(1),o=g.hashPrefix;return o&&p.indexOf(o)===0?p.replace(o,""):p;}),getUrl:function(){return m.href;},parseHash:function(r){var o=g.decode,s,v,t,p,q={},u=g.hashPrefix,w;r=f.isValue(r)?r:g.getHash();if(u){w=r.indexOf(u);if(w===0||(w===1&&r.charAt(0)==="#")){r=r.replace(u,"");}}t=r.match(g._REGEX_HASH)||[];for(s=0,v=t.length;s<v;++s){p=t[s].split("=");q[o(p[0])]=o(p[1]);}return q;},replaceHash:function(p){var o=m.href.replace(/#.*$/,"");if(p.charAt(0)==="#"){p=p.substring(1);}m.replace(o+"#"+(g.hashPrefix||"")+p);},setHash:function(o){if(o.charAt(0)==="#"){o=o.substring(1);}m.hash=(g.hashPrefix||"")+o;}});e=k._notifiers;if(!e){e=k._notifiers=[];}a.Event.define("hashchange",{on:function(q,o,p){if(q.compareTo(h)||q.compareTo(a.config.doc.body)){e.push(p);}},detach:function(r,p,q){var o=l.indexOf(e,q);if(o!==-1){e.splice(o,1);}}});d=g.getHash();i=g.getUrl();if(c.nativeHashChange){a.Event.attach("hashchange",function(q){var o=g.getHash(),p=g.getUrl();l.each(e.concat(),function(r){r.fire({_event:q,oldHash:d,oldUrl:i,newHash:o,newUrl:p});});d=o;i=p;},h);}else{if(!k._hashPoll){if(a.UA.webkit&&!a.UA.chrome&&navigator.vendor.indexOf("Apple")!==-1){a.on("unload",function(){},h);}k._hashPoll=a.later(50,null,function(){var p=g.getHash(),o,q;if(d!==p){q=g.getUrl();o={oldHash:d,oldUrl:i,newHash:p,newUrl:q};d=p;i=q;l.each(e.concat(),function(r){r.fire(o);});}},null,true);}}a.HistoryHash=g;if(n===false||(!a.History&&n!==true&&(!c.html5||!a.HistoryHTML5))){a.History=g;}},"3.4.1",{requires:["event-synthetic","history-base","yui-later"]});