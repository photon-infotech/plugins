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
YUI.add("swfdetect",function(c){var k=0,i=c.UA,f=c.Lang,n="ShockwaveFlash",b,h,d,o,a;function m(e){return parseInt(e,10);}function g(e){if(f.isNumber(m(e[0]))){i.flashMajor=e[0];}if(f.isNumber(m(e[1]))){i.flashMinor=e[1];}if(f.isNumber(m(e[2]))){i.flashRev=e[2];}}if(i.gecko||i.webkit||i.opera){if((b=navigator.mimeTypes["application/x-shockwave-flash"])){if((h=b.enabledPlugin)){d=h.description.replace(/\s[rd]/g,".").replace(/[A-Za-z\s]+/g,"").split(".");g(d);}}}else{if(i.ie){try{o=new ActiveXObject(n+"."+n+".6");o.AllowScriptAccess="always";}catch(j){if(o!==null){k=6;}}if(k===0){try{a=new ActiveXObject(n+"."+n);d=a.GetVariable("$version").replace(/[A-Za-z\s]+/g,"").split(",");g(d);}catch(l){}}}}c.SWFDetect={getFlashVersion:function(){return(String(i.flashMajor)+"."+String(i.flashMinor)+"."+String(i.flashRev));},isFlashVersionAtLeast:function(r,t,s){var p=m(i.flashMajor),q=m(i.flashMinor),e=m(i.flashRev);r=m(r||0);t=m(t||0);s=m(s||0);if(r===p){if(t===q){return s<=e;}return t<q;}return r<p;}};},"3.4.1",{requires:["yui-base"]});