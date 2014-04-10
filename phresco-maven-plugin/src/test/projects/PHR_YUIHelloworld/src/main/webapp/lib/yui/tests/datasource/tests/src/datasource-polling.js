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
var Assert = Y.Assert,

    suite = new Y.Test.Suite("Polling DataSource Test Suite");


suite.add(new Y.Test.Case({
    name: "DataSource Polling Tests",

    testClass: function() {
        var ds = new Y.DataSource.Local();
        Assert.isNotUndefined(ds.setInterval, "Expected setInterval() method on DataSource.Local.");
        Assert.isNotUndefined(ds.clearInterval, "Expected clearInterval() method on DataSource.Local.");
    },

    testSetAndClear: function() {
        var test = this,
            ds = new Y.DataSource.Local(),
            count = 0,
            resumed, intervalId;
        
        intervalId = ds.setInterval(50, {
            callback: {
                success: function (e) {
                    count++;
                }
            }
        });
        
        Assert.isNumber(intervalId, "Expected interval id.");

        this.wait(function () {
            var currentCount = count;

            Assert.isTrue((count > 1));

            ds.clearInterval(intervalId);

            test.wait(function () {
                Assert.areSame(currentCount, count);
            }, 300);
        }, 300);
    },

    testClearAll: function() {
        var ds = new Y.DataSource.Local(),
            countA = 0,
            countB = 0,
            countC = 0;

        ds.setInterval(50, {
            callback: {
                success: function () {
                    countA++;
                }
            }
        });
        ds.setInterval(50, {
            callback: {
                success: function () {
                    countB++;
                }
            }
        });
        ds.setInterval(50, {
            callback: {
                success: function () {
                    countC++;
                }
            }
        });

        this.wait(function(){
            Y.assert((countA > 1));
            Y.assert((countB > 1));
            Y.assert((countC > 1));

            var currentA = countA,
                currentB = countB,
                currentC = countC;

            ds.clearAllIntervals();

            this.wait(function(){
                Assert.areSame(countA, currentA);
                Assert.areSame(countB, currentB);
                Assert.areSame(countC, currentC);
            }, 300);
        }, 300);
    },

    "setInterval should fire first sendRequest immediately, async": function () {
        var ds = new Y.DataSource.Local(),
            count = 0,
            interval;

        interval = ds.setInterval(100, {
            callback: {
                success: function () {
                    count++;
                }
            }
        });

        Assert.areSame(0, count, "first sendRequest should be async");

        this.wait(function(){
            Assert.areSame(1, count);

            this.wait(function(){
                Y.assert((count > 1));

                ds.clearInterval(interval);
            }, 300);
        }, 50);
    }
}));

Y.Test.Runner.add(suite);
