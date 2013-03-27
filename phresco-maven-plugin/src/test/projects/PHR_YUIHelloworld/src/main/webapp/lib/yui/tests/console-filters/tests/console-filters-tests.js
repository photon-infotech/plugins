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
YUI.add('console-filters-tests', function(Y) {

var suite = new Y.Test.Suite("Tests");

function setUp() {
    var testbed = Y.one('#testbed') ||
                  Y.one('body').appendChild('<div id="testbed"></div>');
}

function tearDown() {
    var testbed = Y.one('#testbed');
    if (testbed) {
        testbed.remove().destroy(true);
    }
}

suite.add(new Y.Test.Case({
    name : "resources",

    setUp: setUp,
    tearDown: tearDown,

    _should: {
        fail: {
            //test_skin_loaded: 2529194 // bug
        }
    },

    test_module_loaded: function () {
        Y.Assert.isFunction(Y.Plugin.ConsoleFilters);
    },

    test_skin_loaded: function () {
        var found = Y.all('link').some(function (node) {
            if (/console-filters.css/.test(node.get('href'))) {
                return true;
            }
        });

        Y.Assert.isTrue(found);
    }
}));

suite.add(new Y.Test.Case({
    name : "instantiation",
    
    setUp: setUp,
    tearDown: tearDown,

    test_instantiation : function () {
        var c = new Y.Console({
            newestOnTop: true,
            boundingBox: '#testbed'
        });
        
        c.plug(Y.Plugin.ConsoleFilters);

        c.destroy();
    },

    test_render : function () {
        var c = new Y.Console({
            newestOnTop: true,
            boundingBox: '#testbed'
        });
        
        c.plug(Y.Plugin.ConsoleFilters);

        c.render();

        c.destroy();
    }
}));

suite.add(new Y.Test.Case({
    name : "ui",

    "Filter controls should be in the footer" : function () {
    }
}));

Y.Test.Runner.add(suite);


}, '@VERSION@' ,{requires:['console-filters', 'test']});
