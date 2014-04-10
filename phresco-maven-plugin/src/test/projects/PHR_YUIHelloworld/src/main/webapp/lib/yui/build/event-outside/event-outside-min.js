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
YUI.add("event-outside",function(b){var a=["blur","change","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select","submit"];b.Event.defineOutside=function(e,d){d=d||(e+"outside");var c={on:function(h,f,g){f.handle=b.one("doc").on(e,function(i){if(this.isOutside(h,i.target)){i.currentTarget=h;g.fire(i);}},this);},detach:function(h,f,g){f.handle.detach();},delegate:function(i,g,h,f){g.handle=b.one("doc").delegate(e,function(j){if(this.isOutside(i,j.target)){h.fire(j);}},f,this);},isOutside:function(f,g){return g!==f&&!g.ancestor(function(h){return h===f;});}};c.detachDelegate=c.detach;b.Event.define(d,c);};b.Array.each(a,function(c){b.Event.defineOutside(c);});},"3.4.1",{requires:["event-synthetic"]});