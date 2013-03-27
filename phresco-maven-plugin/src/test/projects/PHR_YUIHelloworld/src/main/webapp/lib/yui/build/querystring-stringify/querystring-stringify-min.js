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
YUI.add("querystring-stringify",function(d){var c=d.namespace("QueryString"),b=[],a=d.Lang;c.escape=encodeURIComponent;c.stringify=function(k,o,e){var g,j,m,h,f,t,r=o&&o.sep?o.sep:"&",p=o&&o.eq?o.eq:"=",q=o&&o.arrayKey?o.arrayKey:false;if(a.isNull(k)||a.isUndefined(k)||a.isFunction(k)){return e?c.escape(e)+p:"";}if(a.isBoolean(k)||Object.prototype.toString.call(k)==="[object Boolean]"){k=+k;}if(a.isNumber(k)||a.isString(k)){return c.escape(e)+p+c.escape(k);}if(a.isArray(k)){t=[];e=q?e+"[]":e;h=k.length;for(m=0;m<h;m++){t.push(c.stringify(k[m],o,e));}return t.join(r);}for(m=b.length-1;m>=0;--m){if(b[m]===k){throw new Error("QueryString.stringify. Cyclical reference");}}b.push(k);t=[];g=e?e+"[":"";j=e?"]":"";for(m in k){if(k.hasOwnProperty(m)){f=g+m+j;t.push(c.stringify(k[m],o,f));}}b.pop();t=t.join(r);if(!t&&e){return e+"=";}return t;};},"3.4.1",{requires:["yui-base"]});