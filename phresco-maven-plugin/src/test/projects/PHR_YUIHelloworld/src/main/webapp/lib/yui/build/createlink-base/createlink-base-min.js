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
YUI.add("createlink-base",function(b){var a={};a.STRINGS={PROMPT:"Please enter the URL for the link to point to:",DEFAULT:"http://"};b.namespace("Plugin");b.Plugin.CreateLinkBase=a;b.mix(b.Plugin.ExecCommand.COMMANDS,{createlink:function(i){var h=this.get("host").getInstance(),e,c,g,f,d=prompt(a.STRINGS.PROMPT,a.STRINGS.DEFAULT);if(d){f=h.config.doc.createElement("div");d=d.replace(/"/g,"").replace(/'/g,"");d=h.config.doc.createTextNode(d);f.appendChild(d);d=f.innerHTML;this.get("host")._execCommand(i,d);g=new h.Selection();e=g.getSelected();if(!g.isCollapsed&&e.size()){c=e.item(0).one("a");if(c){e.item(0).replace(c);}if(b.UA.gecko){if(c.get("parentNode").test("span")){if(c.get("parentNode").one("br.yui-cursor")){c.get("parentNode").insert(c,"before");}}}}else{this.get("host").execCommand("inserthtml",'<a href="'+d+'">'+d+"</a>");}}return c;}});},"3.4.1",{skinnable:false,requires:["editor-base"]});