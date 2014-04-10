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
package com.photon.phresco.manager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.status.ValidationStatus;
import com.photon.phresco.validator.IValidator;

public class ValidationManager {

	private Document doc;
	private String loc;
	IValidator validator;
	private SAXBuilder builder;

	public ValidationManager(MavenProjectInfo mavenProjectInfo, String manifestFileName, String phrescoTargetDir)
			throws IOException, JDOMException {
		builder = new SAXBuilder();
		// disabling xml validation
		builder.setValidation(false);
		builder.setIgnoringElementContentWhitespace(true);
		loc = mavenProjectInfo.getBaseDir() + File.separator + phrescoTargetDir;
		doc = builder.build(new File(loc + manifestFileName));
	}

	public void addValidator(IValidator validator) {
		this.validator = validator;
		validator.setDoc(doc);
		validator.setLoc(loc);
	}

	public List<ValidationStatus> validate() throws MojoExecutionException, JDOMException, IOException {
		return validator.validate();
	}

}
