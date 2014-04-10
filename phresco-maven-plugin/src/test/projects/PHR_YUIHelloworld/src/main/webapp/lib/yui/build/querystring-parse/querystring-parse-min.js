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
YUI.add("querystring-parse",function(e){var b=e.namespace("QueryString"),d=function(f){return function g(l,n){var h,m,k,j,i;if(arguments.length!==2){l=l.split(f);return g(b.unescape(l.shift()),b.unescape(l.join(f)));}l=l.replace(/^\s+|\s+$/g,"");if(e.Lang.isString(n)){n=n.replace(/^\s+|\s+$/g,"");if(!isNaN(n)){m=+n;if(n===m.toString(10)){n=m;}}}h=/(.*)\[([^\]]*)\]$/.exec(l);if(!h){i={};if(l){i[l]=n;}return i;}j=h[2];k=h[1];if(!j){return g(k,[n]);}i={};i[j]=n;return g(k,i);};},c=function(g,f){return((!g)?f:(e.Lang.isArray(g))?g.concat(f):(!e.Lang.isObject(g)||!e.Lang.isObject(f))?[g].concat(f):a(g,f));},a=function(h,f){for(var g in f){if(g&&f.hasOwnProperty(g)){h[g]=c(h[g],f[g]);}}return h;};b.parse=function(g,h,f){return e.Array.reduce(e.Array.map(g.split(h||"&"),d(f||"=")),{},c);};b.unescape=function(f){return decodeURIComponent(f.replace(/\+/g," "));};},"3.4.1",{requires:["array-extras","yui-base"]});