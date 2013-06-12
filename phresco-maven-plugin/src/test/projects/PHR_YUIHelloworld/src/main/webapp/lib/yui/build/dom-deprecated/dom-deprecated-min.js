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
YUI.add("dom-deprecated",function(a){a.mix(a.DOM,{children:function(d,b){var c=[];if(d){b=b||"*";c=a.Selector.query("> "+b,d);}return c;},firstByTag:function(b,c){var d;c=c||a.config.doc;if(b&&c.getElementsByTagName){d=c.getElementsByTagName(b)[0];}return d||null;},previous:function(b,d,c){return a.DOM.elementByAxis(b,"previousSibling",d,c);},next:function(b,d,c){return a.DOM.elementByAxis(b,"nextSibling",d,c);}});},"3.4.1",{requires:["dom-base"]});