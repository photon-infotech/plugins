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
YUI.add("editor-tab",function(c){var b=function(){b.superclass.constructor.apply(this,arguments);},a="host";c.extend(b,c.Base,{_onNodeChange:function(f){var d="indent";if(f.changedType==="tab"){if(!f.changedNode.test("li, li *")){f.changedEvent.halt();f.preventDefault();if(f.changedEvent.shiftKey){d="outdent";}this.get(a).execCommand(d,"");}}},initializer:function(){this.get(a).on("nodeChange",c.bind(this._onNodeChange,this));}},{NAME:"editorTab",NS:"tab",ATTRS:{host:{value:false}}});c.namespace("Plugin");c.Plugin.EditorTab=b;},"3.4.1",{skinnable:false,requires:["editor-base"]});