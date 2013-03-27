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
YUI.add("widget-base-ie",function(a){var d="boundingBox",f="contentBox",c="height",e="offsetHeight",g="",b=a.UA.ie,i=b<7,h=a.Widget.getClassName("tmp","forcesize"),j=a.Widget.getClassName("content","expanded");a.Widget.prototype._uiSizeCB=function(l){var n=this.get(d),k=this.get(f),m=this._bbs;if(m===undefined){this._bbs=m=!(b<8&&n.get("ownerDocument").get("compatMode")!="BackCompat");}if(m){k.toggleClass(j,l);}else{if(l){if(i){n.addClass(h);}k.set(e,n.get(e));if(i){n.removeClass(h);}}else{k.setStyle(c,g);}}};},"3.4.1",{requires:["widget-base"]});