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

	public ValidationManager(MavenProjectInfo mavenProjectInfo,
			String manifestFileName, String phrescoTargetDir)
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

	public List<ValidationStatus> validate() throws MojoExecutionException,
			JDOMException, IOException {
		return validator.validate();
	}

}
