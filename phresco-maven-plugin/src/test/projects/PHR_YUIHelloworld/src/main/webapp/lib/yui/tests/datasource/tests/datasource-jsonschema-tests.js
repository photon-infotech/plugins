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
YUI.add('datasource-jsonschema-tests', function(Y) {

var Assert = Y.Assert,

    suite = new Y.Test.Suite("Plugin.DataSourceJSONSchema Test Suite"),

    jsonData = '{"ResultSet":{"Result":[{"Title":"1"},{"Title":"2"},{"Title":"3"},{"Title":"4"},{"Title":"5"},{"Title":"6"},{"Title":"7"},{"Title":"8"},{"Title":"9"},{"Title":"10"}]}}';


suite.add(new Y.Test.Case({
    name: "DataSource JSONSchema Plugin Tests",

    testJSONSchema: function() {
        var ds = new Y.DataSource.Local({ source: jsonData }),
            request = null, response;

        ds.plug(Y.Plugin.DataSourceJSONSchema, {
            schema: {
                resultListLocator: "ResultSet.Result",
                resultFields: ["Title"]
            }
        });

        ds.sendRequest({
            callback: {
                success: function (e) {
                    request  = e.request;
                    response = e.response;
                }
            }
        });

        Assert.isUndefined(request, "Expected undefined request.");
        Assert.isObject(response, "Expected normalized response object.");
        Assert.isArray(response.results, "Expected results array.");
        Assert.areSame(10, response.results.length, "Expected 10 results.");
        Assert.isNotUndefined(response.results[0].Title, "Expected Title property");
    },

    testSchemaError: function() {
        var ds = new Y.DataSource.Local({ source: jsonData }),
            request = null, response, error;

        ds.plug(Y.Plugin.DataSourceJSONSchema);

        ds.sendRequest({
            callback: {
                failure: function (e) {
                    response = e.response;
                    error    = e.error;
                }
            }
        });

        Assert.isObject(response, "Expected normalized response object.");
        Assert.isObject(error, "Expected response error.");
    }
}));

Y.Test.Runner.add(suite);


}, '@VERSION@' ,{requires:['datasource-jsonschema', 'test']});
