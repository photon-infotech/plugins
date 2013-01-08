package com.photon.phresco.validator;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Document;
import org.jdom2.JDOMException;

import com.photon.phresco.status.ValidationStatus;

public abstract class BaseValidator implements IValidator {

	protected String loc;

	protected Document doc;

	public abstract List<ValidationStatus> validate()
			throws MojoExecutionException, JDOMException, IOException;

	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

}
