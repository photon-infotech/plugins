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
YUI.add("datatype-xml-parse",function(b){var a=b.Lang;b.mix(b.namespace("DataType.XML"),{parse:function(f){var d=null;if(a.isString(f)){try{if(!a.isUndefined(ActiveXObject)){d=new ActiveXObject("Microsoft.XMLDOM");d.async=false;d.loadXML(f);}}catch(c){try{if(!a.isUndefined(DOMParser)){d=new DOMParser().parseFromString(f,"text/xml");}}catch(g){}}}if((a.isNull(d))||(a.isNull(d.documentElement))||(d.documentElement.nodeName==="parsererror")){}return d;}});b.namespace("Parsers").xml=b.DataType.XML.parse;},"3.4.1");