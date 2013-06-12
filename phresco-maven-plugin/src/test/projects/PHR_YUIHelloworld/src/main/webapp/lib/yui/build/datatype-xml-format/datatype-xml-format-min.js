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
YUI.add("datatype-xml-format",function(b){var a=b.Lang;b.mix(b.namespace("DataType.XML"),{format:function(c){try{if(!a.isUndefined(XMLSerializer)){return(new XMLSerializer()).serializeToString(c);}}catch(d){if(c&&c.xml){return c.xml;}else{return(a.isValue(c)&&c.toString)?c.toString():"";}}}});},"3.4.1");