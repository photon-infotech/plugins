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
YUI.add('app-benchmark', function (Y) {

var suite = Y.BenchmarkSuite = new Benchmark.Suite();

// -- Y.Model ------------------------------------------------------------------
suite.add('Y.Model: Instantiate a bare model', function () {
    var model = new Y.Model();
});

suite.add('Y.Model: Subclass and instantiate a bare model', function () {
    var MyModel = Y.Base.create('myModel', Y.Model, []),
        model   = new MyModel();
});

// -- Y.View -------------------------------------------------------------------
suite.add('Y.View: Instantiate a bare view', function () {
    var view = new Y.View();
});

suite.add('Y.View: Instantiate and subclass a bare view', function () {
    var MyView = Y.Base.create('myView', Y.View, []),
        view   = new MyView();
});

}, '@VERSION@', {requires: ['app']});
