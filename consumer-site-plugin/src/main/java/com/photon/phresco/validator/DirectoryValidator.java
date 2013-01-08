package com.photon.phresco.validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.xpath.XPath;

import com.photon.phresco.status.ValidationStatus;

public class DirectoryValidator extends BaseValidator {

	private List<String> localDirectories;

	public DirectoryValidator(List<String> localDirectories) {
		this.localDirectories = localDirectories;
	}

	private static final String NAME = "name";

	public List<ValidationStatus> validate() throws MojoExecutionException,
			JDOMException, IOException {
		List<Element> validationList = ((List<Element>) XPath.selectNodes(doc,
				"//validations/structure"));
		List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		for (Element validation : validationList) {
			List<Element> folderList = validation.getChildren("folder");
			for (Element folder : folderList) { // five nested levels
				String nameValue = folder.getAttributeValue(NAME);
				String requiredValue = folder.getAttributeValue("required");
				validationStatusList.addAll(checkIfFolderExists(File.separator
						+ nameValue, requiredValue));
				List<Element> folderListOne = folder.getChildren("folder");
				for (Element folderOne : folderListOne) {
					String nameValueOne = folderOne.getAttributeValue(NAME);
					String requiredValueOne = folderOne
							.getAttributeValue("required");
					validationStatusList.addAll(checkIfFolderExists(
							File.separator + nameValue + File.separator
									+ nameValueOne, requiredValueOne));
					List<Element> folderListTwo = folderOne
							.getChildren("folder");
					for (Element folderTwo : folderListTwo) {
						String nameValueTwo = folderTwo.getAttributeValue(NAME);
						String requiredValueTwo = folderTwo
								.getAttributeValue("required");
						validationStatusList.addAll(checkIfFolderExists(
								File.separator + nameValue + File.separator
										+ nameValueOne + File.separator
										+ nameValueTwo, requiredValueTwo));
						List<Element> folderListThree = folderTwo
								.getChildren("folder");
						for (Element folderThree : folderListThree) {
							String nameValueThree = folderThree
									.getAttributeValue(NAME);
							String requiredValueThree = folderThree
									.getAttributeValue("required");
							validationStatusList.addAll(checkIfFolderExists(
									File.separator + nameValue + File.separator
											+ nameValueOne + File.separator
											+ nameValueTwo + File.separator
											+ nameValueThree,
									requiredValueThree));
							List<Element> folderListFour = folderThree
									.getChildren("folder");
							for (Element folderFour : folderListFour) {
								String nameValueFour = folderFour
										.getAttributeValue(NAME);
								String requiredValueFour = folderFour
										.getAttributeValue("required");
								validationStatusList
										.addAll(checkIfFolderExists(
												File.separator + nameValue
														+ File.separator
														+ nameValueOne
														+ File.separator
														+ nameValueTwo
														+ File.separator
														+ nameValueThree
														+ File.separator
														+ nameValueFour,
												requiredValueFour));
							}
						}
					}
				}
			}
		}
		return validationStatusList;
	}

	private List<ValidationStatus> checkIfFolderExists(String stringFile,
			String required) throws MojoExecutionException, IOException {
		List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		if (stringFile.indexOf('!') == -1) {
			File file = new File(loc + stringFile);
			if (new Boolean(required)) {
				if (file.exists()) {
					validationStatusList.add(new ValidationStatus(true,
							stringFile + " exists"));
				} else {
					validationStatusList.add(new ValidationStatus(false,
							stringFile + " does not Exist"));
				}
				return validationStatusList;
			}
		} else if (stringFile.indexOf('!') >= 0) {
			for (String localDirectory : localDirectories) {
				String replacementStringFile = "";
				replacementStringFile = stringFile.replace("!languages",
						localDirectory);
				File file = new File(loc + replacementStringFile);
				if (new Boolean(required)) {
					if (file.exists()) {
						validationStatusList.add(new ValidationStatus(true,
								replacementStringFile + " exists"));
					} else {
						validationStatusList.add(new ValidationStatus(false,
								replacementStringFile + " does not Exist"));
					}
				}
			}
		} else {
			validationStatusList.add(new ValidationStatus(true, stringFile
					+ " is ignored"));
		}
		return validationStatusList;
	}
}
