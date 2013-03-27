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
YUI.add("node-pluginhost",function(a){a.Node.plug=function(){var b=a.Array(arguments);b.unshift(a.Node);a.Plugin.Host.plug.apply(a.Base,b);return a.Node;};a.Node.unplug=function(){var b=a.Array(arguments);b.unshift(a.Node);a.Plugin.Host.unplug.apply(a.Base,b);return a.Node;};a.mix(a.Node,a.Plugin.Host,false,null,1);a.NodeList.prototype.plug=function(){var b=arguments;a.NodeList.each(this,function(c){a.Node.prototype.plug.apply(a.one(c),b);});};a.NodeList.prototype.unplug=function(){var b=arguments;a.NodeList.each(this,function(c){a.Node.prototype.unplug.apply(a.one(c),b);});};},"3.4.1",{requires:["node-base","pluginhost"]});