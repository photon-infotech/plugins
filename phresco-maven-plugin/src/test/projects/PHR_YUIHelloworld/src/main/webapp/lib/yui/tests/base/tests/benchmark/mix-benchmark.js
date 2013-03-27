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
YUI.add('mix-benchmark', function (Y) {

var suite = Y.BenchmarkSuite = new Benchmark.Suite();

var _wlhash = function(r, s, wlhash) {
   var p;
   for (p in s) {
        if(wlhash[p]) { 
            r[p] = s[p];
        }
   }
   return r;
};

var _wlarr = function(r, s, wl) {
   var i, l, p;
   for (i = 0, l = wl.length; i < l; i++) {
       p = wl[i];
       r[p] = s[p];
   }
   return r;
};

var _wlarrcheck = function(r, s, wl) {
   var i, l, p;
   for (i = 0, l = wl.length; i < l; i++) {
       p = wl[i];
       if (p in s) {
        r[p] = s[p];
       }
   }
   return r;
};

var _wlarrcheck2 = function(r, s, wl) {
   var i, l, p;
   for (i = 0, l = wl.length; i < l; i++) {
       p = wl[i];
       if (s.hasOwnProperty(p)) {
        r[p] = s[p];
       }
   }
   return r;
};

var s = {
        value: 10,
        setter: function() {},
        getter: function() {}
};

var props = [
    "setter",
    "getter",
    "validator", 
    "value",
    "valueFn",
    "writeOnce",
    "readOnly",
    "lazyAdd",
    "broadcast",
    "_bypassProxy",
    "cloneDefaultValue"
];

var hash = Y.Array.hash(props);

suite.add('Y.mix wl', function () {
    var o = Y.mix({}, s, true, props);    
});

suite.add('wlHash', function () {
    var o = _wlhash({}, s, hash);
});

suite.add('wlArr', function () {
    var o = _wlarr({}, s, props);
});

suite.add('wlArrCheck - in', function () {
    var o = _wlarrcheck({}, s, props);
});

suite.add('wlArrCheck - hasOwnProp', function () {
    var o = _wlarrcheck2({}, s, props);
});

suite.add('Y.Object', function () {
    var o = Y.Object(s);
});

suite.on('cycle', function() {
  Y.log("s is still:" + Y.dump(s), "status");
});

}, '@VERSION@', {requires: ['dump', 'base']});
