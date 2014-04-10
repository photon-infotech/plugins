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
YUI.add("loader-rollup",function(a){a.Loader.prototype._rollup=function(){var k,h,g,o,b=this.required,e,f=this.moduleInfo,d,l,n;if(this.dirty||!this.rollups){this.rollups={};for(k in f){if(f.hasOwnProperty(k)){g=this.getModule(k);if(g&&g.rollup){this.rollups[k]=g;}}}this.forceMap=(this.force)?a.Array.hash(this.force):{};}for(;;){d=false;for(k in this.rollups){if(this.rollups.hasOwnProperty(k)){if(!b[k]&&((!this.loaded[k])||this.forceMap[k])){g=this.getModule(k);o=g.supersedes||[];e=false;if(!g.rollup){continue;}l=0;for(h=0;h<o.length;h++){n=f[o[h]];if(this.loaded[o[h]]&&!this.forceMap[o[h]]){e=false;break;}else{if(b[o[h]]&&g.type==n.type){l++;e=(l>=g.rollup);if(e){break;}}}}if(e){b[k]=true;d=true;this.getRequires(g);}}}}if(!d){break;}}};},"3.4.1",{requires:["loader-base"]});