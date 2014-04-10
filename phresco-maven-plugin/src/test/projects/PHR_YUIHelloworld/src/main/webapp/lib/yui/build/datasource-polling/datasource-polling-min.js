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
YUI.add("datasource-polling",function(b){function a(){this._intervals={};}a.prototype={_intervals:null,setInterval:function(e,d){var c=b.later(e,this,this.sendRequest,[d],true);this._intervals[c.id]=c;b.later(0,this,this.sendRequest,[d]);return c.id;},clearInterval:function(d,c){d=c||d;if(this._intervals[d]){this._intervals[d].cancel();delete this._intervals[d];}},clearAllIntervals:function(){b.each(this._intervals,this.clearInterval,this);}};b.augment(b.DataSource.Local,a);},"3.4.1",{requires:["datasource-local"]});