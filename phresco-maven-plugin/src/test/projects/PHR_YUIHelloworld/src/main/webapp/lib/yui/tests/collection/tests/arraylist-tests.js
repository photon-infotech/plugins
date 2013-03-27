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
YUI.add('arraylist-tests', function(Y) {

var suite = new Y.Test.Suite("Y.ArrayList");

suite.add( new Y.Test.Case({
    name: "Lifecycle",

    "construct with array should not error": function () {
    },

    "construct after augmented class instantiation should not clobber items": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "API",

    setUp: function () {
    },

    "test item": function () {
    },

    "test each": function () {
    },

    "test some": function () {
    },

    "test indexOf": function () {
    },

    "test size": function () {
    },

    "test isEmpty": function () {
    },

    "test _item": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "Additional API",

    setUp: function () {
    },

    "test add": function () {
    },

    "test remove": function () {
    },

    "test filter": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "addMethod",

    "test addMethod": function () {
    },

    "methods should be chainable unless a value is returned": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "Array.invoke",

    "test Y.Array.invoke": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "Runtime expectations",

    "test ": function () {
    }
}));

suite.add( new Y.Test.Case({
    name: "Bugs",

    "test ": function () {
    }
}));

Y.Test.Runner.add( suite );


}, '@VERSION@' ,{requires:['arraylist', 'test']});
