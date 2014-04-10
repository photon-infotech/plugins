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
YUI({
    base: '../../../../build/',
    lang: "en",
    filter: "raw"
}).use('calendar',  function(Y) {

         Y.CalendarBase.CONTENT_TEMPLATE = Y.CalendarBase.TWO_PANE_TEMPLATE;
         var calendar = new Y.Calendar({
	        contentBox: "#mycalendar",
			width:'600px',
			showPrevMonth: false,
			showNextMonth: false,
			selectionMode: 'multiple',
			date: new Date(2011, 6)}).render();

		 var rules = {
		 	"all": {
		 		"all": {
		 			"all": {
		 				"2, 5": "work_from_home",
		 				"0, 6": "all_weekends"
		 			}
		 		}
		 	}
		 };

		 calendar.set("customRenderer", {rules: rules, 
			                             filterFunction: function (date, node, rules) {
			                             	               if (Y.Array.indexOf(rules, 'all_weekends') >= 0) {
			                             	               	node.addClass("redtext");
			                             	               }
			                                             }
			                            });
    
	     var curDate = calendar.get("date");

 
	     calendar.set("headerRenderer", function (curDate) {
	     	var ydate = Y.DataType.Date,
	     	    output = ydate.format(curDate, {format: "%B %Y"}) +
	     	             " &mdash; " +
	     	             ydate.format(ydate.addMonths(curDate, calendar._paneNumber-1), {format: "%B %Y"});

	     	return output;
	     });
 
 			
		Y.one("#updateCalendar").on('click', function () {
	      curDate = new Date(1950 + Math.round(Math.random()*100), 
		                                Math.round(Math.random()*12.49), 1);
		  calendar.set('date', curDate);	
		  Y.one("#currentDate").setContent(calendar.get("date").toString());	
		});

		Y.one("#togglePrevMonth").on('click', function () {
		  calendar.set('showPrevMonth', !(calendar.get("showPrevMonth")));			
		});
		Y.one("#toggleNextMonth").on('click', function () {
		  calendar.set('showNextMonth', !(calendar.get("showNextMonth")));			
		});

		Y.one("#getSelectedDates").on('click', function () {
		  Y.log(calendar.get('selectedDates'));
		});

		calendar.on("selectionChange", function (ev) {Y.log(ev);});

});

