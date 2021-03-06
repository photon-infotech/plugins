/**
 * Phresco Plugins
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
package com.photon.phresco.status;

public class ValidationStatus implements IStatus {
	boolean status;
	String message;

	public ValidationStatus() {
		super();
	}

	public ValidationStatus(boolean status, String message) {
		this.status = status;
		this.message = message;
	}

	public ValidationStatus(String message) {
		this.message = message;
	}

	public boolean isStatus() {
		return status;
	}

	public String getMessage() {
		return message + " \n";
	}

	public String toString() {
		return message + " \n";
	}
}
