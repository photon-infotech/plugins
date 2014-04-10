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
YUI.add("datatype-date-math",function(b){var a=b.Lang;b.mix(b.namespace("DataType.Date"),{isValidDate:function(c){if(a.isDate(c)&&(isFinite(c))&&(c!="Invalid Date")&&!isNaN(c)&&(c!=null)){return true;}else{return false;}},areEqual:function(d,c){return(this.isValidDate(d)&&this.isValidDate(c)&&(d.getTime()==c.getTime()));},isGreater:function(d,c){return(this.isValidDate(d)&&this.isValidDate(c)&&(d.getTime()>c.getTime()));},isGreaterOrEqual:function(d,c){return(this.isValidDate(d)&&this.isValidDate(c)&&(d.getTime()>=c.getTime()));},addMonths:function(f,c){var e=f.getFullYear();var g=f.getMonth()+c;e=Math.floor(e+g/12);g=(g%12+12)%12;var d=new Date(f.getTime());d.setYear(e);d.setMonth(g);return d;},addYears:function(f,e){var d=f.getFullYear()+e;var c=new Date(f.getTime());c.setYear(d);return c;},listOfDatesInMonth:function(g){if(!this.isValidDate(g)){return[];}var e=this.daysInMonth(g),f=g.getFullYear(),h=g.getMonth(),d=[];for(var c=1;c<=e;c++){d.push(new Date(f,h,c,12,0,0));}return d;},daysInMonth:function(e){if(!this.isValidDate(e)){return 0;}var d=e.getMonth();var f=[31,28,31,30,31,30,31,31,30,31,30,31];if(d!=1){return f[d];}else{var c=e.getFullYear();if(c%400===0){return 29;}else{if(c%100===0){return 28;}else{if(c%4===0){return 29;}else{return 28;}}}}}});},"3.4.1",{requires:["yui-base"]});