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
/*global $ */

/**
 * Perform a request for non-cached JSON data.
 * 
 * @param type
 *            the type of request e.g. "get".
 * @param url
 *            the URL to request against.
 * @param data
 *            the parameters of the request or its payload.
 * @param success
 *            the handler to call on success.
 * @param failure
 *            the handler to call on failure.
 */
function requestJSON(type, url, data, success, failure) {

	var notifiedFailure = false;

	$.ajax({
		cache : false,
		data : data,
		dataType : "json",
		url : url,
		success : function(data) {
			if (typeof data === "string") {
				data = $.parseJSON(data);
			}
			success(data);
		},
		error : function() {
			if (!notifiedFailure) {
				failure();
				notifiedFailure = true;
			}
		}
	});
}