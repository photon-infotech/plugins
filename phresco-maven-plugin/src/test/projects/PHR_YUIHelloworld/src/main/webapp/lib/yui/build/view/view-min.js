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
YUI.add("view",function(b){function a(){a.superclass.constructor.apply(this,arguments);}b.View=b.extend(a,b.Base,{container:"<div/>",events:{},template:"",initializer:function(c){c||(c={});this.container=this.create(c.container||this.container);c.model&&(this.model=c.model);c.modelList&&(this.modelList=c.modelList);c.template&&(this.template=c.template);this.events=c.events?b.merge(this.events,c.events):this.events;this.attachEvents(this.events);},destructor:function(){this.container&&this.container.remove(true);},attachEvents:function(g){var d=this.container,i=b.Object.owns,h,e,f,c;for(c in g){if(!i(g,c)){continue;}e=g[c];for(f in e){if(!i(e,f)){continue;}h=e[f];if(typeof h==="string"){h=this[h];}d.delegate(f,h,c,this);}}},create:function(c){return typeof c==="string"?b.Node.create(c):b.one(c);},remove:function(){this.container&&this.container.remove();return this;},render:function(){return this;}},{NAME:"view"});},"3.4.1",{requires:["base-build","node-event-delegate"]});