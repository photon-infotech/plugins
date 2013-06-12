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

YUI.add('config-test', function(Y) {
        var testCore = new Y.Test.Case({

            name: "Config tests",
        
            test_config: function() {
                Y.Assert.areEqual(YUI_config.gfilter, Y.config.filter);
                Y.Assert.isObject(Y.config.logExclude);
                Y.Assert.isTrue(Y.config.gconfig);
                YUI().use('node', function(Y2) {
                    Y.Assert.isUndefined(Y2.config.logExclude);
                    Y.Assert.isUndefined(Y2.config.filter);
                    Y.Assert.isTrue(Y2.config.gconfig);
                });

                var o1 = { a: 1 };
                var o2 = { b: 1 };
                var o3 = { c: 1 };
                var o4 = { d: 1 };
                var o5 = { e: 1 };
                var o6 = { f: 1 };

                YUI(o1, o2, o3, o4, o5, o6).use('node', function(Y2) {
                    Y.Assert.areEqual(1, Y2.config.f);
                    Y.Assert.isTrue(Y2.config.gconfig);
                });
            }
        });

        Y.SeedTests.add(testCore);
    
});
