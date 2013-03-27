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
YUI.add('dom-region-test', function(Y) {
    var Assert = Y.Assert;
        ArrayAssert = Y.ArrayAssert;

    Y.Test.Runner.add(new Y.Test.Case({
        name: 'Y.DOM.region',

        'should return a region containing the correct data': function() {
            var node = document.body,
                r = Y.DOM.region(node),
                xy = Y.DOM.getXY(node);

            Assert.areEqual(node.offsetWidth, r.width);
            Assert.areEqual(node.offsetHeight, r.height);

            Assert.areEqual(xy[0], r.left);
            Assert.areEqual(xy[1], r.top);
            
            Assert.areEqual(node.offsetWidth + xy[0], r.right);
            Assert.areEqual(node.offsetHeight + xy[1], r.bottom);
            
            Assert.areEqual(xy[0], r[0]);
            Assert.areEqual(xy[1], r[1]);
        },

        'should return false for bad input': function() {
            Assert.isFalse(Y.DOM.region());
            Assert.isFalse(Y.DOM.region(document));
        }
    }));
}, '@VERSION@' ,{requires:['dom-screen', 'test']});
