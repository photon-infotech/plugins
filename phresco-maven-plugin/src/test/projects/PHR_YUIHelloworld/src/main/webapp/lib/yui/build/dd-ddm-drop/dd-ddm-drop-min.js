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
YUI.add("dd-ddm-drop",function(a){a.mix(a.DD.DDM,{_noShim:false,_activeShims:[],_hasActiveShim:function(){if(this._noShim){return true;}return this._activeShims.length;},_addActiveShim:function(b){this._activeShims[this._activeShims.length]=b;},_removeActiveShim:function(c){var b=[];a.each(this._activeShims,function(e,d){if(e._yuid!==c._yuid){b[b.length]=e;}});this._activeShims=b;},syncActiveShims:function(b){a.later(0,this,function(c){var d=((c)?this.targets:this._lookup());a.each(d,function(f,e){f.sizeShim.call(f);},this);},b);},mode:0,POINT:0,INTERSECT:1,STRICT:2,useHash:true,activeDrop:null,validDrops:[],otherDrops:{},targets:[],_addValid:function(b){this.validDrops[this.validDrops.length]=b;return this;},_removeValid:function(b){var c=[];a.each(this.validDrops,function(e,d){if(e!==b){c[c.length]=e;}});this.validDrops=c;return this;},isOverTarget:function(c){if(this.activeDrag&&c){var g=this.activeDrag.mouseXY,f,b=this.activeDrag.get("dragMode"),e,d=c.shim;if(g&&this.activeDrag){e=this.activeDrag.region;if(b==this.STRICT){return this.activeDrag.get("dragNode").inRegion(c.region,true,e);}else{if(c&&c.shim){if((b==this.INTERSECT)&&this._noShim){f=((e)?e:this.activeDrag.get("node"));return c.get("node").intersect(f,c.region).inRegion;}else{if(this._noShim){d=c.get("node");}return d.intersect({top:g[1],bottom:g[1],left:g[0],right:g[0]},c.region).inRegion;}}else{return false;}}}else{return false;}}else{return false;}},clearCache:function(){this.validDrops=[];this.otherDrops={};this._activeShims=[];},_activateTargets:function(){this._noShim=true;this.clearCache();a.each(this.targets,function(c,b){c._activateShim([]);if(c.get("noShim")==true){this._noShim=false;}},this);this._handleTargetOver();},getBestMatch:function(f,d){var c=null,e=0,b;a.each(f,function(i,h){var g=this.activeDrag.get("dragNode").intersect(i.get("node"));i.region.area=g.area;if(g.inRegion){if(g.area>e){e=g.area;c=i;}}},this);if(d){b=[];a.each(f,function(h,g){if(h!==c){b[b.length]=h;}},this);return[c,b];}else{return c;}},_deactivateTargets:function(){var b=[],c,e=this.activeDrag,d=this.activeDrop;if(e&&d&&this.otherDrops[d]){if(!e.get("dragMode")){b=this.otherDrops;delete b[d];}else{c=this.getBestMatch(this.otherDrops,true);d=c[0];b=c[1];}e.get("node").removeClass(this.CSS_PREFIX+"-drag-over");if(d){d.fire("drop:hit",{drag:e,drop:d,others:b});e.fire("drag:drophit",{drag:e,drop:d,others:b});}}else{if(e&&e.get("dragging")){e.get("node").removeClass(this.CSS_PREFIX+"-drag-over");e.fire("drag:dropmiss",{pageX:e.lastXY[0],pageY:e.lastXY[1]});}else{}}this.activeDrop=null;a.each(this.targets,function(g,f){g._deactivateShim([]);},this);},_dropMove:function(){if(this._hasActiveShim()){this._handleTargetOver();}else{a.each(this.otherDrops,function(c,b){c._handleOut.apply(c,[]);});}},_lookup:function(){if(!this.useHash||this._noShim){return this.validDrops;}var b=[];a.each(this.validDrops,function(d,c){if(d.shim&&d.shim.inViewportRegion(false,d.region)){b[b.length]=d;}});return b;},_handleTargetOver:function(){var b=this._lookup();a.each(b,function(d,c){d._handleTargetOver.call(d);},this);},_regTarget:function(b){this.targets[this.targets.length]=b;},_unregTarget:function(c){var b=[],d;a.each(this.targets,function(f,e){if(f!=c){b[b.length]=f;}},this);this.targets=b;d=[];a.each(this.validDrops,function(f,e){if(f!==c){d[d.length]=f;}});this.validDrops=d;},getDrop:function(c){var b=false,d=a.one(c);if(d instanceof a.Node){a.each(this.targets,function(f,e){if(d.compareTo(f.get("node"))){b=f;}});}return b;}},true);},"3.4.1",{skinnable:false,requires:["dd-ddm"]});