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
YUI.add("datatable-sort",function(g){var f=g.ClassNameManager.getClassName,h="datatable",b="column",d="asc",c="desc",a='<a class="{link_class}" title="{link_title}" href="{link_href}">{value}</a>';function e(){e.superclass.constructor.apply(this,arguments);}g.mix(e,{NS:"sort",NAME:"dataTableSort",ATTRS:{trigger:{value:{event:"click",selector:"th"},writeOnce:"initOnly"},lastSortedBy:{setter:"_setLastSortedBy",lazyAdd:false},template:{value:a},strings:{valueFn:function(){return g.Intl.get("datatable-sort");}}}});g.extend(e,g.Plugin.Base,{initializer:function(j){var k=this.get("host"),i=this.get("trigger");k.get("recordset").plug(g.Plugin.RecordsetSort,{dt:k});k.get("recordset").sort.addTarget(k);this.doBefore("_createTheadThNode",this._beforeCreateTheadThNode);this.doBefore("_attachTheadThNode",this._beforeAttachTheadThNode);this.doBefore("_attachTbodyTdNode",this._beforeAttachTbodyTdNode);k.delegate(i.event,g.bind(this._onEventSortColumn,this),i.selector);k.after("recordsetSort:sort",function(){this._uiSetRecordset(this.get("recordset"));});this.on("lastSortedByChange",function(l){this._uiSetLastSortedBy(l.prevVal,l.newVal,k);});if(k.get("rendered")){k._uiSetColumnset(k.get("columnset"));this._uiSetLastSortedBy(null,this.get("lastSortedBy"),k);}},_setLastSortedBy:function(i){if(g.Lang.isString(i)){i={key:i,dir:"desc"};}if(i){return(i.dir==="desc")?{key:i.key,dir:"desc",notdir:"asc"}:{key:i.key,dir:"asc",notdir:"desc"};}else{return null;}},_uiSetLastSortedBy:function(n,m,l){var w=this.get("strings"),k=l.get("columnset"),x=n&&n.key,u=m&&m.key,i=n&&l.getClassName(n.dir),p=m&&l.getClassName(m.dir),r=k.keyHash[x],o=k.keyHash[u],q=l._tbodyNode,v=g.Lang.sub,j,t,s;if(r&&i){j=r.thNode;t=j.one("a");if(t){t.set("title",v(w.sortBy,{column:r.get("label")}));}j.removeClass(i);q.all("."+f(b,r.get("id"))).removeClass(i);}if(o&&p){j=o.thNode;t=j.one("a");if(t){s=(m.dir===d)?"reverseSortBy":"sortBy";t.set("title",v(w[s],{column:o.get("label")}));}j.addClass(p);q.all("."+f(b,o.get("id"))).addClass(p);}},_beforeCreateTheadThNode:function(k){var i,j;if(k.column.get("sortable")){i=this.get("lastSortedBy");j=(i&&i.dir===d&&i.key===k.column.get("key"))?"reverseSortBy":"sortBy";k.value=g.Lang.sub(this.get("template"),{link_class:k.link_class||"",link_title:g.Lang.sub(this.get("strings."+j),{column:k.column.get("label")}),link_href:"#",value:k.value});}},_beforeAttachTheadThNode:function(m){var l=this.get("lastSortedBy"),k=l&&l.key,i=l&&l.dir,j=l&&l.notdir;if(m.column.get("sortable")){m.th.addClass(f(h,"sortable"));}if(k&&(k===m.column.get("key"))){m.th.replaceClass(f(h,j),f(h,i));}},_beforeAttachTbodyTdNode:function(m){var l=this.get("lastSortedBy"),k=l&&l.key,i=l&&l.dir,j=l&&l.notdir;if(m.column.get("sortable")){m.td.addClass(f(h,"sortable"));}if(k&&(k===m.column.get("key"))){m.td.replaceClass(f(h,j),f(h,i));}},_onEventSortColumn:function(n){n.halt();var l=this.get("host"),k=l.get("columnset").idHash[n.currentTarget.get("id")],j,m,i,o,p;if(k.get("sortable")){j=k.get("key");m=k.get("field");i=this.get("lastSortedBy")||{};o=(i.key===j&&i.dir===d);p=k.get("sortFn");l.get("recordset").sort.sort(m,o,p);this.set("lastSortedBy",{key:j,dir:(o)?c:d});}}});g.namespace("Plugin").DataTableSort=e;},"3.4.1",{requires:["datatable-base","plugin","recordset-sort"],lang:["en"]});