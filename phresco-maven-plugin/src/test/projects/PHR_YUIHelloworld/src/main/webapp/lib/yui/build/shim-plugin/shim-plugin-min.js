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
YUI.add("shim-plugin",function(b){function a(c){this.init(c);}a.CLASS_NAME="yui-node-shim";a.TEMPLATE='<iframe class="'+a.CLASS_NAME+'" frameborder="0" title="Node Stacking Shim"'+'src="javascript:false" tabindex="-1" role="presentation"'+'style="position:absolute; z-index:-1;"></iframe>';a.prototype={init:function(c){this._host=c.host;this.initEvents();this.insert();this.sync();},initEvents:function(){this._resizeHandle=this._host.on("resize",this.sync,this);},getShim:function(){return this._shim||(this._shim=b.Node.create(a.TEMPLATE,this._host.get("ownerDocument")));},insert:function(){var c=this._host;this._shim=c.insertBefore(this.getShim(),c.get("firstChild"));},sync:function(){var d=this._shim,c=this._host;if(d){d.setAttrs({width:c.getStyle("width"),height:c.getStyle("height")});}},destroy:function(){var c=this._shim;if(c){c.remove(true);}this._resizeHandle.detach();}};a.NAME="Shim";a.NS="shim";b.namespace("Plugin");b.Plugin.Shim=a;},"3.4.1",{requires:["node-style","node-pluginhost"]});