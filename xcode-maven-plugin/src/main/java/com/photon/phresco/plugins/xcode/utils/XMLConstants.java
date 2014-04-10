/**
 * Xcodebuild Command-Line Wrapper
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
package com.photon.phresco.plugins.xcode.utils;

public interface XMLConstants {
	
	String NAME ="name";
	
	String TESTSUITE_NAME = "testsuite";
	
	String TESTSUITES_NAME = "testsuites";
	
	String TESTCASE_NAME = "testcase";
	
	String RESULT = "result";
	
	String FAILURE = "failure";
	
	String ELEM_FAILURE = "failure";
	
	String SUCCESS = "success";
	
	String LOGTYPE = "LogType";

	String PASS = "Pass";
	
	String FAIL = "Fail";
	
	String ERROR = "Error";
	
	String ELEM_ERROR = "error";
	
	String TIME = "time";
	
    String FAILURES = "failures";
	
	String TESTS = "tests";
	
	String TYPE = "type";
	
	String DICT_START = "<dict>";
	
	String DICT_END = "</dict>";
	
	String KEY_START = "<key>";
	
	String KEY_END = "</key>";
	
	String STRING_START = "<string>";
	
	String STRING_END = "</string>";
	
}
