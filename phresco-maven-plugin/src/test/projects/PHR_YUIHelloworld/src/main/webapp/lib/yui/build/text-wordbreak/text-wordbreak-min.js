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
YUI.add("text-wordbreak",function(d){var n=d.Text,k=n.Data.WordBreak,g=0,e=1,l=2,s=3,q=4,c=5,i=6,m=7,t=8,o=9,r=10,f=11,p=12,h=[new RegExp(k.aletter),new RegExp(k.midnumlet),new RegExp(k.midletter),new RegExp(k.midnum),new RegExp(k.numeric),new RegExp(k.cr),new RegExp(k.lf),new RegExp(k.newline),new RegExp(k.extend),new RegExp(k.format),new RegExp(k.katakana),new RegExp(k.extendnumlet)],b="",a=new RegExp("^"+k.punctuation+"$"),u=/\s/,j={getWords:function(A,E){var z=0,v=j._classify(A),B=v.length,w=[],C=[],y,D,x;if(!E){E={};}if(E.ignoreCase){A=A.toLowerCase();}D=E.includePunctuation;x=E.includeWhitespace;for(;z<B;++z){y=A.charAt(z);w.push(y);if(j._isWordBoundary(v,z)){w=w.join(b);if(w&&(x||!u.test(w))&&(D||!a.test(w))){C.push(w);}w=[];}}return C;},getUniqueWords:function(w,v){return d.Array.unique(j.getWords(w,v));},isWordBoundary:function(w,v){return j._isWordBoundary(j._classify(w),v);},_classify:function(A){var x,w=[],z=0,y,C,v=A.length,D=h.length,B;for(;z<v;++z){x=A.charAt(z);B=p;for(y=0;y<D;++y){C=h[y];if(C&&C.test(x)){B=y;break;}}w.push(B);}return w;},_isWordBoundary:function(z,w){var v,x=z[w],A=z[w+1],y;if(w<0||(w>z.length-1&&w!==0)){return false;}if(x===g&&A===g){return false;}y=z[w+2];if(x===g&&(A===l||A===e)&&y===g){return false;}v=z[w-1];if((x===l||x===e)&&A===g&&v===g){return false;}if((x===q||x===g)&&(A===q||A===g)){return false;}if((x===s||x===e)&&A===q&&v===q){return false;}if(x===q&&(A===s||A===e)&&y===q){return false;}if(x===t||x===o||v===t||v===o||A===t||A===o){return false;}if(x===c&&A===i){return false;}if(x===m||x===c||x===i){return true;}if(A===m||A===c||A===i){return true;}if(x===r&&A===r){return false;}if(A===f&&(x===g||x===q||x===r||x===f)){return false;}if(x===f&&(A===g||A===q||A===r)){return false;}return true;}};n.WordBreak=j;},"3.4.1",{requires:["array-extras","text-data-wordbreak"]});