/*
 * JS Test Runner for JUnit
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
/*global $, QUnit, requestJSON */

var module = QUnit.module;
var equals = QUnit.equals;
var expect = QUnit.expect;
var ok = QUnit.ok;
var test = QUnit.test;

module("AjaxUtilsTest");

/**
 * Test requesting JSON. We mock the jQuery ajax function and call its success
 * and error callbacks so that we can test their logic.
 */
test("requestJSON", function() {
	var actualAjax, failureCount;

	failureCount = 0;

	actualAjax = $.ajax;
	$.ajax = function(opts) {
		opts.success('{"a": 1}');
		opts.error();
		opts.error();
	};
	try {
		requestJSON("GET", "something", "data", function(data) {
			equals(data.a, 1);
		}, function() {
			++failureCount;
		});

	} finally {
		$.ajax = actualAjax;
	}

	equals(failureCount, 1);

	expect(2);
});