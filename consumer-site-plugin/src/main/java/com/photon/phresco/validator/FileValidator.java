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

public class FileValidator extends BaseValidator {

	private static final String NAME = "name";
	private List<String> localDirectories;

	public FileValidator(List<String> localDirectories) {
		this.localDirectories = localDirectories;
	}

	public List<ValidationStatus> validate() throws MojoExecutionException, JDOMException, IOException {
		List<Element> validationList = ((List<Element>) XPath.selectNodes(doc, "//validations/structure"));
		List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		for (Element validation : validationList) {
			List<Element> folderList = validation.getChildren("folder");
			List<Element> fileList = validation.getChildren("file");
			for (Element file : fileList) { // five nested levels
				// String fileTypeValue = file.getAttributeValue("type");
				String fileNameValue = file.getAttributeValue(NAME);
				String fileRequiredValue = file.getAttributeValue("required");
				validationStatusList.addAll(checkIfFileExists(File.separator + fileNameValue, fileRequiredValue));
			}
			for (Element folder : folderList) {
				String nameValue = folder.getAttributeValue(NAME);
				List<Element> folderListOne = folder.getChildren("folder");
				List<Element> fileListOne = folder.getChildren("file");
				for (Element fileOne : fileListOne) {
					// String fileTypeValue =
					// file.getAttributeValue("type");
					String fileNameValueOne = fileOne.getAttributeValue(NAME);
					String fileRequiredValueOne = fileOne.getAttributeValue("required");
					validationStatusList.addAll(checkIfFileExists(File.separator + nameValue + File.separator
							+ fileNameValueOne, fileRequiredValueOne));
				}
				for (Element folderOne : folderListOne) {
					String nameValueOne = folderOne.getAttributeValue(NAME);
					List<Element> folderListTwo = folderOne.getChildren("folder");
					List<Element> fileListTwo = folderOne.getChildren("file");
					for (Element fileTwo : fileListTwo) {
						// String fileTypeValue =
						// file.getAttributeValue("type");
						String fileNameValueTwo = fileTwo.getAttributeValue(NAME);
						String fileRequiredValueTwo = fileTwo.getAttributeValue("required");
						validationStatusList.addAll(checkIfFileExists(File.separator + nameValue + File.separator
								+ nameValueOne + File.separator + fileNameValueTwo, fileRequiredValueTwo));
					}
					for (Element folderTwo : folderListTwo) {
						String nameValueTwo = folderTwo.getAttributeValue(NAME);
						List<Element> folderListThree = folderTwo.getChildren("folder");
						List<Element> fileListThree = folderTwo.getChildren("file");
						for (Element fileThree : fileListThree) {
							// String fileTypeValue =
							// file.getAttributeValue("type");
							String fileNameValueThree = fileThree.getAttributeValue(NAME);
							String fileRequiredValueThree = fileThree.getAttributeValue("required");
							validationStatusList.addAll(checkIfFileExists(File.separator + nameValue + File.separator
									+ nameValueOne + File.separator + nameValueTwo + File.separator
									+ fileNameValueThree, fileRequiredValueThree));
						}
						for (Element folderThree : folderListThree) {
							String nameValueThree = folderThree.getAttributeValue(NAME);
							List<Element> folderListFour = folderThree.getChildren("folder");
							List<Element> fileListFour = folderThree.getChildren("file");
							for (Element fileFour : fileListFour) { // five
																	// nested
																	// levels
								// String fileTypeValue =
								// file.getAttributeValue("type");
								String fileNameValueFour = fileFour.getAttributeValue(NAME);
								String fileRequiredValueFour = fileFour.getAttributeValue("required");
								validationStatusList.addAll(checkIfFileExists(File.separator + nameValue
										+ File.separator + nameValueOne + File.separator + nameValueTwo
										+ File.separator + nameValueThree + File.separator + fileNameValueFour,
										fileRequiredValueFour));
							}
							for (Element folderFour : folderListFour) {
								String nameValueFour = folderFour.getAttributeValue(NAME);
								List<Element> fileListFive = validation.getChildren("file");
								for (Element fileFive : fileListFive) {
									// String fileTypeValue =
									// file.getAttributeValue("type");
									String fileNameValueFive = folderFour.getAttributeValue(NAME);
									String fileRequiredValueFive = fileFive.getAttributeValue("required");
									validationStatusList.addAll(checkIfFileExists(File.separator + nameValue
											+ File.separator + nameValueOne + File.separator + nameValueTwo
											+ File.separator + nameValueThree + File.separator + nameValueFour
											+ File.separator + fileNameValueFive, fileRequiredValueFive));
								}
							}
						}
					}
				}
			}
		}
		return validationStatusList;
	}

	private List<ValidationStatus> checkIfFileExists(String stringFile, String required) throws MojoExecutionException,
			IOException {
		ArrayList<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		if (stringFile.indexOf('!') == -1) {
			File file = new File(loc + stringFile);
			if (new Boolean(required)) {
				if (file.exists()) {
					validationStatusList.add(new ValidationStatus(true, stringFile + " exists"));

				} else {
					validationStatusList.add(new ValidationStatus(false, stringFile + " does not Exist"));
				}
			}
			return validationStatusList;
		} else if (stringFile.indexOf('!') >= 0) {
			for (String localDirectory : localDirectories) {
				String replacementStringFile = "";
				replacementStringFile = stringFile.replace("!languages", localDirectory);
				File file = new File(loc + replacementStringFile);
				if (new Boolean(required)) {
					if (file.exists()) {
						validationStatusList.add(new ValidationStatus(true, replacementStringFile + " exists"));

					} else {
						System.out.println(replacementStringFile + " does not Exist");
						validationStatusList
								.add(new ValidationStatus(false, replacementStringFile + " does not Exist"));
					}
				}
			}
		} else {
			validationStatusList.add(new ValidationStatus(true, stringFile + " is ignored"));
		}
		return validationStatusList;
	}
}
