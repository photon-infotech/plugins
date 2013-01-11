package com.photon.phresco.validator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.xpath.XPath;

import com.photon.phresco.status.ValidationStatus;

public class FileDimensionValidator extends BaseValidator {

	private List<String> localDirectories;

	public FileDimensionValidator(List<String> localDirectories) {
		this.localDirectories = localDirectories;
	}

	private static final String HEIGHT = "height";
	private static final String WIDTH = "width";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String IMAGE = "image";

	public List<ValidationStatus> validate() throws MojoExecutionException, JDOMException, IOException {
		List<Element> validationList = ((List<Element>) XPath.selectNodes(doc, "//validations/structure"));
		List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		for (Element validation : validationList) {
			List<Element> folderList = validation.getChildren("folder");
			List<Element> fileList = validation.getChildren("file");

			for (Element file : fileList) { // five nested levels
				String fileTypeValue = file.getAttributeValue(TYPE);
				String fileNameValue = file.getAttributeValue(NAME);
				if (fileTypeValue.equals(IMAGE)) {
					String expectedWidthString = file.getChild(WIDTH).getValue();
					String expectedHeightString = file.getChild(HEIGHT).getValue();
					if (expectedHeightString != null && expectedHeightString.length() > 0) {
						int expectedHeight = Integer.parseInt(expectedHeightString);
						validationStatusList.addAll(checkImageHeight(File.separator + fileNameValue, expectedHeight));
					}

					if (expectedWidthString != null && expectedWidthString.length() > 0) {
						int expectedWidth = Integer.parseInt(expectedWidthString);
						validationStatusList.addAll(checkImageWidth(File.separator + fileNameValue, expectedWidth));
					}
				}
			}
			for (Element folder : folderList) {
				String nameValue = folder.getAttributeValue(NAME);
				List<Element> folderListOne = folder.getChildren("folder");
				List<Element> fileListOne = folder.getChildren("file");
				for (Element fileOne : fileListOne) {
					String fileTypeValue = fileOne.getAttributeValue(TYPE);
					String fileNameValueOne = fileOne.getAttributeValue(NAME);
					if (fileTypeValue.equals(IMAGE)) {
						String expectedWidthString = fileOne.getChild(WIDTH).getValue();
						String expectedHeightString = fileOne.getChild(HEIGHT).getValue();
						if (expectedHeightString != null && expectedHeightString.length() > 0) {
							int expectedHeight = Integer.parseInt(expectedHeightString);
							validationStatusList.addAll(checkImageHeight(File.separator + nameValue + File.separator
									+ fileNameValueOne, expectedHeight));
						}

						if (expectedWidthString != null && expectedWidthString.length() > 0) {
							int expectedWidth = Integer.parseInt(expectedWidthString);
							validationStatusList.addAll(checkImageWidth(File.separator + nameValue + File.separator
									+ fileNameValueOne, expectedWidth));
						}
					}
				}
				for (Element folderOne : folderListOne) {
					String nameValueOne = folderOne.getAttributeValue(NAME);
					List<Element> folderListTwo = folderOne.getChildren("folder");
					List<Element> fileListTwo = folderOne.getChildren("file");
					for (Element fileTwo : fileListTwo) {
						String fileTypeValue = fileTwo.getAttributeValue(TYPE);
						String fileNameValueTwo = fileTwo.getAttributeValue(NAME);
						if (fileTypeValue.equals(IMAGE)) {
							String expectedWidthString = fileTwo.getChild(WIDTH).getValue();
							String expectedHeightString = fileTwo.getChild(HEIGHT).getValue();
							if (expectedHeightString != null && expectedHeightString.length() > 0) {
								int expectedHeight = Integer.parseInt(expectedHeightString);
								validationStatusList.addAll(checkImageHeight(File.separator + nameValue
										+ File.separator + nameValueOne + File.separator + fileNameValueTwo,
										expectedHeight));
							}

							if (expectedWidthString != null && expectedWidthString.length() > 0) {
								int expectedWidth = Integer.parseInt(expectedWidthString);
								validationStatusList.addAll(checkImageWidth(File.separator + nameValue + File.separator
										+ nameValueOne + File.separator + fileNameValueTwo, expectedWidth));
							}
						}
					}
					for (Element folderTwo : folderListTwo) {
						String nameValueTwo = folderTwo.getAttributeValue(NAME);
						List<Element> folderListThree = folderTwo.getChildren("folder");
						List<Element> fileListThree = folderTwo.getChildren("file");
						for (Element fileThree : fileListThree) {
							String fileTypeValue = fileThree.getAttributeValue(TYPE);
							String fileNameValueThree = fileThree.getAttributeValue(NAME);
							if (fileTypeValue.equals(IMAGE)) {
								String expectedWidthString = fileThree.getChild(WIDTH).getValue();
								String expectedHeightString = fileThree.getChild(HEIGHT).getValue();
								if (expectedHeightString != null && expectedHeightString.length() > 0) {
									int expectedHeight = Integer.parseInt(expectedHeightString);
									validationStatusList.addAll(checkImageHeight(File.separator + nameValue
											+ File.separator + nameValueOne + File.separator + nameValueTwo
											+ File.separator + fileNameValueThree, expectedHeight));
								}

								if (expectedWidthString != null && expectedWidthString.length() > 0) {
									int expectedWidth = Integer.parseInt(expectedWidthString);
									validationStatusList.addAll(checkImageWidth(File.separator + nameValue
											+ File.separator + nameValueOne + File.separator + nameValueTwo
											+ File.separator + fileNameValueThree, expectedWidth));
								}
							}
						}
						for (Element folderThree : folderListThree) {
							String nameValueThree = folderThree.getAttributeValue(NAME);
							List<Element> folderListFour = folderThree.getChildren("folder");
							List<Element> fileListFour = folderThree.getChildren("file");
							for (Element fileFour : fileListFour) { // five
																	// nested
																	// levels
								String fileTypeValue = fileFour.getAttributeValue(TYPE);
								String fileNameValueFour = fileFour.getAttributeValue(NAME);
								if (fileTypeValue.equals(IMAGE)) {
									String expectedWidthString = fileFour.getChild(WIDTH).getValue();
									String expectedHeightString = fileFour.getChild(HEIGHT).getValue();
									if (expectedHeightString != null && expectedHeightString.length() > 0) {
										int expectedHeight = Integer.parseInt(expectedHeightString);
										validationStatusList.addAll(checkImageHeight(File.separator + nameValue
												+ File.separator + nameValueOne + File.separator + nameValueTwo
												+ File.separator + nameValueThree + File.separator + fileNameValueFour
												+ fileNameValueFour, expectedHeight));
									}

									if (expectedWidthString != null && expectedWidthString.length() > 0) {
										int expectedWidth = Integer.parseInt(expectedWidthString);
										validationStatusList.addAll(checkImageWidth(File.separator + nameValue
												+ File.separator + nameValueOne + File.separator + nameValueTwo
												+ File.separator + nameValueThree + File.separator + fileNameValueFour
												+ fileNameValueFour, expectedWidth));
									}
								}
							}
							for (Element folderFour : folderListFour) {
								String nameValueFour = folderFour.getAttributeValue(NAME);
								List<Element> fileListFive = folderFour.getChildren("file");
								for (Element fileFive : fileListFive) {
									String fileTypeValue = fileFive.getAttributeValue(TYPE);
									String fileNameValueFive = folderFour.getAttributeValue(NAME);
									if (fileTypeValue.equals(IMAGE)) {
										String expectedWidthString = fileFive.getChild(WIDTH).getValue();
										String expectedHeightString = fileFive.getChild(HEIGHT).getValue();
										if (expectedHeightString != null && expectedHeightString.length() > 0) {
											int expectedHeight = Integer.parseInt(expectedHeightString);
											validationStatusList.addAll(checkImageHeight(File.separator + nameValue
													+ File.separator + nameValueOne + File.separator + nameValueTwo
													+ File.separator + nameValueThree + File.separator + nameValueFour
													+ File.separator + fileNameValueFive, expectedHeight));
										}

										if (expectedWidthString != null && expectedWidthString.length() > 0) {
											int expectedWidth = Integer.parseInt(expectedWidthString);
											validationStatusList.addAll(checkImageWidth(File.separator + nameValue
													+ File.separator + nameValueOne + File.separator + nameValueTwo
													+ File.separator + nameValueThree + File.separator + nameValueFour
													+ File.separator + fileNameValueFive, expectedWidth));
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return validationStatusList;
	}

	private List<ValidationStatus> checkImageWidth(String filename, int expectedWidth) throws MojoExecutionException,
			IOException {
		List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		if (filename.indexOf('!') == -1) {
			BufferedImage bimg = ImageIO.read(new File(loc + filename));
			int width = bimg.getWidth();
			if (width != expectedWidth) {
				validationStatusList.add(new ValidationStatus(false, filename
						+ " does not have correct dimensions  in width"));
			} else {
				validationStatusList.add(new ValidationStatus(true, filename + " has exactly the correct width"));
			}
			return validationStatusList;
		} else {
			for (String localDirectory : localDirectories) {
				filename = filename.replace("!languages", localDirectory);
				System.out.println("!!! @ " + filename);
				BufferedImage bimg = ImageIO.read(new File(loc + filename));
				int width = bimg.getWidth();
				if (width != expectedWidth) {
					validationStatusList.add(new ValidationStatus(false, filename
							+ " does not have correct dimensions  in width"));
				} else {
					validationStatusList.add(new ValidationStatus(true, filename + " has exactly the correct width"));
				}
			}
		}
		return validationStatusList;
	}

	private List<ValidationStatus> checkImageHeight(String filename, int expectedHeight) throws MojoExecutionException,
			IOException {
		List<ValidationStatus> validationStatusList = new ArrayList<ValidationStatus>();
		if (filename.indexOf('!') == -1) {
			BufferedImage bimg = ImageIO.read(new File(loc + filename));
			int height = bimg.getHeight();
			if (height != expectedHeight) {
				validationStatusList.add(new ValidationStatus(false, filename
						+ " does not have correct dimensions in height"));
			} else {
				validationStatusList.add(new ValidationStatus(true, filename + " has exactly the correct height"));
			}
			return validationStatusList;
		} else if (filename.indexOf('!') >= 0) {
			for (String localDirectory : localDirectories) {
				String replacementFileName = "";
				replacementFileName = filename.replace("!languages", localDirectory);
				BufferedImage bimg = ImageIO.read(new File(loc + replacementFileName));
				int height = bimg.getHeight();
				if (height != expectedHeight) {
					validationStatusList.add(new ValidationStatus(false, replacementFileName
							+ " does not have correct dimensions in height"));
				} else {
					validationStatusList.add(new ValidationStatus(true, replacementFileName
							+ " has exactly the correct height"));
				}
			}
		} else {
			validationStatusList.add(new ValidationStatus(true, filename + " is ignored"));
		}
		return validationStatusList;
	}
}
