package com.photon.phresco.validator;

import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Document;
import org.jdom2.JDOMException;

import com.photon.phresco.status.ValidationStatus;

public interface IValidator {
	public List<ValidationStatus> validate() throws MojoExecutionException,
			JDOMException, IOException;

	public String getLoc();

	public void setLoc(String loc);

	public Document getDoc();

	public void setDoc(Document doc);

}
