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
YUI.add("substitute",function(c){var g=c.Lang,e="dump",h=" ",b="{",i="}",j=/(~-(\d+)-~)/g,a=/\{LBRACE\}/g,d=/\{RBRACE\}/g,f=function(B,m,u,l){var r,q,p,z,y,A,x=[],n,t,w=B.length;for(;;){r=B.lastIndexOf(b,w);if(r<0){break;}q=B.indexOf(i,r);if(r+1>=q){break;}n=B.substring(r+1,q);z=n;A=null;p=z.indexOf(h);if(p>-1){A=z.substring(p+1);z=z.substring(0,p);}y=m[z];if(u){y=u(z,y,A);}if(g.isObject(y)){if(!c.dump){y=y.toString();}else{if(g.isArray(y)){y=c.dump(y,parseInt(A,10));}else{A=A||"";t=A.indexOf(e);if(t>-1){A=A.substring(4);}if(y.toString===Object.prototype.toString||t>-1){y=c.dump(y,parseInt(A,10));}else{y=y.toString();}}}}else{if(g.isUndefined(y)){y="~-"+x.length+"-~";x.push(n);}}B=B.substring(0,r)+y+B.substring(q+1);if(!l){w=r-1;}}return B.replace(j,function(s,o,k){return b+x[parseInt(k,10)]+i;}).replace(a,b).replace(d,i);};c.substitute=f;g.substitute=f;},"3.4.1",{optional:["dump"],requires:["yui-base"]});