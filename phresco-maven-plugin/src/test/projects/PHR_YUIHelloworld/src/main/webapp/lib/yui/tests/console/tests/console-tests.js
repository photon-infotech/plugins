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
YUI.add('console-tests', function(Y) {

var suite = new Y.Test.Suite("Y.Console");

suite.add( new Y.Test.Case({
    name: "Lifecycle",

    "test default construction": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "API",

    setUp: function () {
    },

    tearDown: function () {
    },

    "test log": function () {
    },

    "test clearConsole": function () {
    },

    "test reset": function () {
    },

    "test collapse": function () {
    },

    "test expand": function () {
    },

    "test scrollToLatest": function () {
    },

    "test render": function () {
    },

    "test printBuffer": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "Attributes",

    "logEvent should be writeOnce": function () {
    },

    "logSource should be writeOnce": function () {
    },

    "test strings": function () {
    },

    "test paused": function () {
    },

    "test defaultCategory": function () {
    },

    "test defaultSource": function () {
    },

    "test entryTemplate": function () {
    },

    "test logLevel": function () {
    },

    "test printTimeout": function () {
    },

    "test printLimit": function () {
    },

    "test consoleLimit": function () {
    },

    "test newestOnTop": function () {
    },

    "test scrollIntoView": function () {
    },

    "test collapsed": function () {
    },

    "test style": function () {
    }

}));

suite.add( new Y.Test.Case({
    name: "Runtime expectations",

    setUp: function () {
    },

    tearDown: function () {
    },

    "test ": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "Bugs",

    setUp: function () {
    },

    tearDown: function () {
    },

    "test ": function () {
    }
}));

Y.Test.Runner.add( suite );


}, '@VERSION@' ,{requires:['console', 'test']});
