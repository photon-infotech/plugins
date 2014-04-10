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
YUI.add("node-load",function(a){a.Node.prototype._ioComplete=function(g,c,d){var b=d[0],h=d[1],e,f;if(c&&c.responseText){f=c.responseText;if(b){e=a.DOM.create(f);f=a.Selector.query(b,e);}this.setContent(f);}if(h){h.call(this,g,c);}};a.Node.prototype.load=function(d,b,e){if(typeof b=="function"){e=b;b=null;}var c={context:this,on:{complete:this._ioComplete},arguments:[b,e]};a.io(d,c);return this;};},"3.4.1",{requires:["node-base","io-base"]});