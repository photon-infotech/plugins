/*
 * Phresco Maven Plugin
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
    filter: 'RAW',
    debug: true
}).use('calendar-base',  function(Y) {
   

Y.CalendarBase.CONTENT_TEMPLATE = '<div class="yui3-g {calendar_pane_class}" id="{calendar_id}">' +	
			                        '{header_template}' +
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' +
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' + 			
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' + 		
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' +
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' + 			
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' +
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' +
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' + 			
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' + 		
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' +
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' + 			
			                      '<div class="yui3-u-1-3">' +
 			                        '{calendar_grid_template}' +
 			                      '</div>' +  		                      			                                   
			           '</div>';


         var calendar = new Y.CalendarBase({
	        contentBox: "#mycalendar",
			height:'200px',
			width:'600px',
			showPrevMonth: true,
			showNextMonth: true,
			date: new Date(2029, 11)}).render();


	     var curDate = calendar.get("date");

	     calendar.set("headerRenderer", function (curDate) {
	     	var ydate = Y.DataType.Date,
	     	    output = ydate.format(curDate, {format: "%B, %Y"}) +
	     	             " &mdash; " +
	     	             ydate.format(ydate.addMonths(curDate, calendar._paneNumber-1), {format: "%B, %Y"});
	     	console.log("Output: " + output);
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

		Y.one("#toggleSelection").on('click', function () {
		  calendar.select(new Date (curDate.getFullYear(), curDate.getMonth(), 23));
		});

});

