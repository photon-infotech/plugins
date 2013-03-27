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
YUI.add('namespace-test', function(Y) {
    
    var testNamespace = new Y.Test.Case({
        name: "Namespace tests",


    test_create_namespace: function () {

            // set up Y.my.namespace
            var ns = Y.namespace("my.namespace");

            // use the returned reference, assign a value
            ns.test = "yahoo_my_namespace_test";

            // check for the assigned value using the full path
            Y.Assert.areEqual(Y.my.namespace.test, "yahoo_my_namespace_test", "The namespace was not set up correctly");

            // assign a value to my to test that it doesn't get wiped out
            Y.my.test = "yahoo_my_test";

            // create another namespace on my
            var ns2 = Y.namespace("my.namespace2");

            // make sure my stays the same
            Y.Assert.areEqual(Y.my.test, "yahoo_my_test", "The namespace was obliterated");
        } 

    });
        

    Y.SeedTests.add(testNamespace);
    
});
