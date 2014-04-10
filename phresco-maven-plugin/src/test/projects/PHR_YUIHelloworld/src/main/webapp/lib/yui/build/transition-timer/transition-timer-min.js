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
YUI.add("transition-timer",function(b){var a=b.Transition;b.mix(a.prototype,{_start:function(){if(a.useNative){this._runNative();}else{this._runTimer();}},_runTimer:function(){var c=this;c._initAttrs();a._running[b.stamp(c)]=c;c._startTime=new Date();a._startTimer();},_endTimer:function(){var c=this;delete a._running[b.stamp(c)];c._startTime=null;},_runFrame:function(){var c=new Date()-this._startTime;this._runAttrs(c);},_runAttrs:function(e){var o=this,n=o._node,u=o._config,f=b.stamp(n),m=a._nodeAttrs[f],h=a.behaviors,l=false,g=false,v,w,j,q,c,s,r,k,p;for(w in m){if((j=m[w])&&j.transition===o){r=j.duration;s=j.delay;c=(e-s)/1000;k=e;v={type:"propertyEnd",propertyName:w,config:u,elapsedTime:c};q=(p in h&&"set" in h[p])?h[p].set:a.DEFAULT_SETTER;l=(k>=r);if(k>r){k=r;}if(!s||e>=s){q(o,w,j.from,j.to,k-s,r-s,j.easing,j.unit);if(l){delete m[w];o._count--;if(u[w]&&u[w].on&&u[w].on.end){u[w].on.end.call(b.one(n),v);}if(!g&&o._count<=0){g=true;o._end(c);o._endTimer();}}}}}},_initAttrs:function(){var j=this,e=a.behaviors,l=b.stamp(j._node),q=a._nodeAttrs[l],d,i,k,n,g,c,m,o,p,f,h;for(c in q){if((d=q[c])&&d.transition===j){i=d.duration*1000;k=d.delay*1000;n=d.easing;g=d.value;if(c in j._node.style||c in b.DOM.CUSTOM_STYLES){f=(c in e&&"get" in e[c])?e[c].get(j,c):a.DEFAULT_GETTER(j,c);o=a.RE_UNITS.exec(f);m=a.RE_UNITS.exec(g);f=o?o[1]:f;h=m?m[1]:g;p=m?m[2]:o?o[2]:"";if(!p&&a.RE_DEFAULT_UNIT.test(c)){p=a.DEFAULT_UNIT;}if(typeof n==="string"){if(n.indexOf("cubic-bezier")>-1){n=n.substring(13,n.length-1).split(",");}else{if(a.easings[n]){n=a.easings[n];}}}d.from=Number(f);d.to=Number(h);d.unit=p;d.easing=n;d.duration=i+k;d.delay=k;}else{delete q[c];j._count--;}}}},destroy:function(){this.detachAll();this._node=null;}},true);b.mix(b.Transition,{_runtimeAttrs:{},RE_DEFAULT_UNIT:/^width|height|top|right|bottom|left|margin.*|padding.*|border.*$/i,DEFAULT_UNIT:"px",intervalTime:20,behaviors:{left:{get:function(d,c){return b.DOM._getAttrOffset(d._node,c);}}},DEFAULT_SETTER:function(f,g,i,j,l,e,h,k){i=Number(i);j=Number(j);var d=f._node,c=a.cubicBezier(h,l/e);c=i+c[0]*(j-i);if(d){if(g in d.style||g in b.DOM.CUSTOM_STYLES){k=k||"";b.DOM.setStyle(d,g,c+k);}}else{f._end();}},DEFAULT_GETTER:function(e,c){var d=e._node,f="";if(c in d.style||c in b.DOM.CUSTOM_STYLES){f=b.DOM.getComputedStyle(d,c);}return f;},_startTimer:function(){if(!a._timer){a._timer=setInterval(a._runFrame,a.intervalTime);}},_stopTimer:function(){clearInterval(a._timer);a._timer=null;},_runFrame:function(){var c=true,d;for(d in a._running){if(a._running[d]._runFrame){c=false;a._running[d]._runFrame();}}if(c){a._stopTimer();}},cubicBezier:function(s,m){var z=0,f=0,w=s[0],e=s[1],v=s[2],d=s[3],u=1,c=0,r=u-3*v+3*w-z,q=3*v-6*w+3*z,o=3*w-3*z,n=z,l=c-3*d+3*e-f,k=3*d-6*e+3*f,j=3*e-3*f,i=f,h=(((r*m)+q)*m+o)*m+n,g=(((l*m)+k)*m+j)*m+i;return[h,g];},easings:{ease:[0.25,0,1,0.25],linear:[0,0,1,1],"ease-in":[0.42,0,1,1],"ease-out":[0,0,0.58,1],"ease-in-out":[0.42,0,0.58,1]},_running:{},_timer:null,RE_UNITS:/^(-?\d*\.?\d*){1}(em|ex|px|in|cm|mm|pt|pc|%)*$/},true);a.behaviors.top=a.behaviors.bottom=a.behaviors.right=a.behaviors.left;b.Transition=a;},"3.4.1",{requires:["transition"]});