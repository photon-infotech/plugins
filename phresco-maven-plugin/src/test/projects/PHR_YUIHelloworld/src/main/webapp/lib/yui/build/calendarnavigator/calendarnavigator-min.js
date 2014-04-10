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
YUI.add("calendarnavigator",function(a){var m="contentBox",o="host",k="rendered",b=a.ClassNameManager.getClassName,h=a.substitute,c=a.Node,g=c.create,n="calendar",f="calendarnav",i=b(n,"header"),d=b(f,"prevmonth"),e=b(f,"nextmonth"),l=a.DataType.Date;function j(p){j.superclass.constructor.apply(this,arguments);}j.NS="navigator";j.NAME="pluginCalendarNavigator";j.ATTRS={shiftByMonths:{value:1}};j.CALENDARNAV_STRINGS={prev_month_class:d,next_month_class:e};j.PREV_MONTH_CONTROL_TEMPLATE='<div class="yui3-u {prev_month_class}" style="width:15px;">'+"&#9668;"+"</div>";j.NEXT_MONTH_CONTROL_TEMPLATE='<div class="yui3-u {next_month_class}" style="width:15px;">'+"&#9658;"+"</div>";a.extend(j,a.Plugin.Base,{initializer:function(p){this.afterHostMethod("renderUI",this._initNavigationControls);},destructor:function(){},_subtractMonths:function(r){var q=this.get(o);var p=q.get("date");q.set("date",l.addMonths(p,-1*this.get("shiftByMonths")));r.preventDefault();},_addMonths:function(r){var q=this.get(o);var p=q.get("date");q.set("date",l.addMonths(p,this.get("shiftByMonths")));r.preventDefault();},_renderPrevControls:function(){var p=g(h(j.PREV_MONTH_CONTROL_TEMPLATE,j.CALENDARNAV_STRINGS));p.on("click",this._subtractMonths,this);p.on("selectstart",function(q){q.preventDefault();});return p;},_renderNextControls:function(){var p=g(h(j.NEXT_MONTH_CONTROL_TEMPLATE,j.CALENDARNAV_STRINGS));p.on("click",this._addMonths,this);p.on("selectstart",function(q){q.preventDefault();});return p;},_initNavigationControls:function(){var p=this.get(o);var q=p.get(m).one("."+i);q.prepend(this._renderPrevControls(p));q.append(this._renderNextControls(p));}});a.namespace("Plugin").CalendarNavigator=j;},"3.4.1",{requires:["plugin","classnamemanager","datatype-date","node","substitute"]});