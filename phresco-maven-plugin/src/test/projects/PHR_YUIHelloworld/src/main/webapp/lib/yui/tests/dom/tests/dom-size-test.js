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
YUI.add('dom-size-test', function(Y) {
    var Assert = Y.Assert,
        ArrayAssert = Y.ArrayAssert;

    Y.Test.Runner.add(new Y.Test.Case({
        name: 'Y.DOM._setSize',

        'should set the node offsetWidth to the given value': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            Y.DOM._setSize(node, 'width', 100);

            Assert.areEqual(100, node.offsetWidth);
            document.body.removeChild(node);
        },

        'should set the node offsetHeight to the given value': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            Y.DOM._setSize(node, 'height', 100);

            Assert.areEqual(100, node.offsetHeight);
            document.body.removeChild(node);
        },

        'should set the node offsetWidth to zero if given a negative number': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            Y.DOM._setSize(node, 'width', -100);

            Assert.areEqual(0, node.offsetWidth);
            document.body.removeChild(node);
        },

        'should set the node offsetHeight to zero if given a negative number': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            Y.DOM._setSize(node, 'height', -100);

            Assert.areEqual(0, node.offsetHeight);
            document.body.removeChild(node);
        },

        'should set the offsetWidth via setWidth': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            Y.DOM.setWidth(node, 100);

            Assert.areEqual(100, node.offsetWidth);
            document.body.removeChild(node);
        },

        'should set the offsetHeight via setHeight': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            Y.DOM.setHeight(node, 100);

            Assert.areEqual(100, node.offsetHeight);
            document.body.removeChild(node);
        },

        'should set offsetWidth accounting for padding': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            node.style.padding = '10px';
            Y.DOM.setWidth(node, 100);

            Assert.areEqual(100, node.offsetWidth);
            document.body.removeChild(node);

        },

        'should set offsetHeight accounting for padding': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            node.style.padding = '10px';
            Y.DOM.setHeight(node, 100);

            Assert.areEqual(100, node.offsetHeight);
            document.body.removeChild(node);
        },

        'should set offsetWidth to padding when setting to zero': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            node.style.padding = '10px';
            Y.DOM.setWidth(node, 0);

            Assert.areEqual(20, node.offsetWidth);
            document.body.removeChild(node);
        },

        'should set offsetHeight to padding when setting to zero': function() {
            var node = document.createElement('div');

            document.body.appendChild(node);
            node.style.padding = '10px';
            Y.DOM.setHeight(node, 0);

            Assert.areEqual(20, node.offsetHeight);
            document.body.removeChild(node);
        }
    }));

}, '@VERSION@' ,{requires:['dom-base', 'test']});
