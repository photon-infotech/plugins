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
YUI.add('yql-tests', function(Y) {

    var template = {
        name: 'YQL Test',

        setUp : function() {
        },

        tearDown : function() {
        },

        test_load: function() {
            Y.Assert.isFunction(Y.YQL);
            Y.Assert.isFunction(Y.YQLRequest);
        },

        test_query: function() {
            var test = this;
            
            Y.YQL('select * from weather.forecast where location=62896', function(r) {
                test.resume(function() {
                    Y.Assert.isObject(r, 'Query Failure');
                    Y.Assert.isObject(r.query, 'Query object not present');
                    Y.Assert.areEqual(1, r.query.count, 'Query Count not correct');
                });
            });

            this.wait();
        },

        test_https: function() {
            var test = this;

            Y.YQL('select * from weather.forecast where location=62896', function(r) {

                test.resume(function() {
                    Y.Assert.isObject(r, 'Query Failure');
                    Y.Assert.isObject(r.query, 'Query object not present');
                    Y.Assert.areEqual(1, r.query.count, 'Query Count not correct');
                });

            }, {}, {proto:"https"});

            this.wait();
        },

        test_failed: function() {
            var test = this;

            Y.YQL('select * from weatherFOO.forecast where location=62896', function(r) {
                test.resume(function() {
                    Y.Assert.isObject(r, 'Query Failure');
                    Y.Assert.isObject(r.error, 'Query did not produce an error object');
                });
            });

            this.wait();
        },
        test_escaped: function() {
            var test = this;
            
            Y.YQL("select * from html where url = \"http://instantwatcher.com/genres/506\" and xpath='//div[@id=\"titles\"]/ul/li/a'", function(r) {
                test.resume(function() {
                    Y.Assert.isObject(r, 'Query Failure');
                    Y.Assert.isObject(r.query, 'Query object not present');
                });
            });

            this.wait();
        }
    };
    var suite = new Y.Test.Suite("YQL");
    
    suite.add(new Y.Test.Case(template));
    Y.Test.Runner.add(suite);

});

