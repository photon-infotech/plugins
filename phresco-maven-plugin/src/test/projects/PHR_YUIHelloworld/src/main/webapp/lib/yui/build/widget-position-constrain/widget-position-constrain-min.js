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
YUI.add("widget-position-constrain",function(c){var f="constrain",d="constrain|xyChange",b="constrainChange",n="preventOverlap",e="align",o="",g="bindUI",i="xy",a="x",m="y",j=c.Node,p="viewportRegion",l="region",k;function h(q){if(!this._posNode){c.error("WidgetPosition needs to be added to the Widget, before WidgetPositionConstrain is added");}c.after(this._bindUIPosConstrained,this,g);}h.ATTRS={constrain:{value:null,setter:"_setConstrain"},preventOverlap:{value:false}};k=h._PREVENT_OVERLAP={x:{"tltr":1,"blbr":1,"brbl":1,"trtl":1},y:{"trbr":1,"tlbl":1,"bltl":1,"brtr":1}};h.prototype={getConstrainedXY:function(t,s){s=s||this.get(f);var r=this._getRegion((s===true)?null:s),q=this._posNode.get(l);return[this._constrain(t[0],a,q,r),this._constrain(t[1],m,q,r)];},constrain:function(u,r){var t,q,s=r||this.get(f);if(s){t=u||this.get(i);q=this.getConstrainedXY(t,s);if(q[0]!==t[0]||q[1]!==t[1]){this.set(i,q,{constrained:true});}}},_setConstrain:function(q){return(q===true)?q:j.one(q);},_constrain:function(q,r,z,s){if(s){if(this.get(n)){q=this._preventOverlap(q,r,z,s);}var v=(r==a),y=(v)?s.width:s.height,u=(v)?z.width:z.height,t=(v)?s.left:s.top,w=(v)?s.right-u:s.bottom-u;if(q<t||q>w){if(u<y){if(q<t){q=t;}else{if(q>w){q=w;}}}else{q=t;}}}return q;},_preventOverlap:function(r,s,C,t){var w=this.get(e),B=(s===a),z,v,u,y,A,q;if(w&&w.points&&k[s][w.points.join(o)]){v=this._getRegion(w.node);if(v){z=(B)?C.width:C.height;u=(B)?v.left:v.top;y=(B)?v.right:v.bottom;A=(B)?v.left-t.left:v.top-t.top;q=(B)?t.right-v.right:t.bottom-v.bottom;}if(r>u){if(q<z&&A>z){r=u-z;}}else{if(A<z&&q>z){r=y;}}}return r;},_bindUIPosConstrained:function(){this.after(b,this._afterConstrainChange);this._enableConstraints(this.get(f));},_afterConstrainChange:function(q){this._enableConstraints(q.newVal);},_enableConstraints:function(q){if(q){this.constrain();this._cxyHandle=this._cxyHandle||this.on(d,this._constrainOnXYChange);}else{if(this._cxyHandle){this._cxyHandle.detach();this._cxyHandle=null;}}},_constrainOnXYChange:function(q){if(!q.constrained){q.newVal=this.getConstrainedXY(q.newVal);}},_getRegion:function(q){var r;if(!q){r=this._posNode.get(p);}else{q=j.one(q);if(q){r=q.get(l);}}return r;}};c.WidgetPositionConstrain=h;},"3.4.1",{requires:["widget-position"]});