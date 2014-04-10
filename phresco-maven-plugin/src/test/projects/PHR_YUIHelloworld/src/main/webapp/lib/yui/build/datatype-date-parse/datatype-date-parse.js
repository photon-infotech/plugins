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
YUI.add('datatype-date-parse', function(Y) {

/**
 * Parse number submodule.
 *
 * @module datatype
 * @submodule datatype-date-parse
 * @for DataType.Date
 */
var LANG = Y.Lang;

Y.mix(Y.namespace("DataType.Date"), {
    /**
     * Converts data to type Date.
     *
     * @method parse
     * @param data {String | Number} Data to convert. Values supported by the Date constructor are supported.
     * @return {Date} A Date, or null.
     */
    parse: function(data) {
        var date = null;

        //Convert to date
        if(!(LANG.isDate(data))) {
            date = new Date(data);
        }
        else {
            return date;
        }

        // Validate
        if(LANG.isDate(date) && (date != "Invalid Date") && !isNaN(date)) { // Workaround for bug 2527965
            return date;
        }
        else {
            return null;
        }
    }
});

// Add Parsers shortcut
Y.namespace("Parsers").date = Y.DataType.Date.parse;


}, '3.4.1' );
