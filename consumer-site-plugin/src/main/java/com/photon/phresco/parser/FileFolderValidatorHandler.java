package com.photon.phresco.parser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPath;

import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.util.UtilConstant;

public class FileFolderValidatorHandler {
	private Document doc;
	private String loc;
	SAXBuilder builder;

	public FileFolderValidatorHandler(MavenProjectInfo mavenProjectInfo) throws Exception {
		try {
			builder = new SAXBuilder();
			// disabling xml validation
			builder.setValidation(false);
			builder.setIgnoringElementContentWhitespace(true);
			loc = mavenProjectInfo.getBaseDir() + File.separator + mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.target.dir");
			doc = builder.build(new File(loc+UtilConstant.manifestFileName));
		} catch (JDOMException e) {
			e.printStackTrace();
//			throw new Exception(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
//			throw new Exception(e.getMessage());
		}
	}

	public String[] validateFolderStructure() throws MojoExecutionException, JDOMException {
			List<Element> validationList = ((List<Element>) XPath.selectNodes(
					doc, "//validations/validation"));
			for (Element validation : validationList) {
				List<Element> folderList = validation.getChildren("folder");
				for (Element folder : folderList) { // five nested levels
					String nameValue = folder.getAttributeValue("name");
					String requiredValue = folder.getAttributeValue("required");
					checkIfFolderExists(File.separator + nameValue, requiredValue);
					List<Element> folderListOne = folder.getChildren("folder");
					for (Element folderOne : folderListOne) {
						String nameValueOne = folderOne
								.getAttributeValue("name");
						String requiredValueOne = folderOne
								.getAttributeValue("required");
						checkIfFolderExists(File.separator + nameValue + File.separator
								+ nameValueOne, requiredValueOne);
						List<Element> folderListTwo = folderOne
								.getChildren("folder");
						for (Element folderTwo : folderListTwo) {
							String nameValueTwo = folderTwo
									.getAttributeValue("name");
							String requiredValueTwo = folderTwo
									.getAttributeValue("required");
							checkIfFolderExists(File.separator + nameValue + File.separator
									+ nameValueOne + File.separator + nameValueTwo,
									requiredValueTwo);
							List<Element> folderListThree = folderTwo
									.getChildren("folder");
							for (Element folderThree : folderListThree) {
								String nameValueThree = folderThree
										.getAttributeValue("name");
								String requiredValueThree = folderThree
										.getAttributeValue("required");
								checkIfFolderExists(File.separator + nameValue + File.separator
										+ nameValueOne + File.separator + nameValueTwo
										+ File.separator + nameValueThree,
										requiredValueThree);
								List<Element> folderListFour = folderThree
										.getChildren("folder");
								for (Element folderFour : folderListFour) {
									String nameValueFour = folderFour
											.getAttributeValue("name");
									String requiredValueFour = folderFour
											.getAttributeValue("required");
									checkIfFolderExists(File.separator + nameValue + File.separator
											+ nameValueOne + File.separator
											+ nameValueTwo + File.separator
											+ nameValueThree + File.separator
											+ nameValueFour, requiredValueFour);
								}
							}
						}
					}
				}
			}
			return new String[]{};
	}

	public String[] validateFilePresence() throws MojoExecutionException, JDOMException {
			List<Element> validationList = ((List<Element>) XPath.selectNodes(
					doc, "//validations/validation"));
			for (Element validation : validationList) {
				List<Element> folderList = validation.getChildren("folder");
				List<Element> fileList = validation.getChildren("file");
				for (Element file : fileList) { // five nested levels
					// String fileTypeValue = file.getAttributeValue("type");
					String fileNameValue = file.getAttributeValue("name");
					String fileRequiredValue = file
							.getAttributeValue("required");
					checkIfFolderExists(File.separator + fileNameValue, fileRequiredValue);
				}
				for (Element folder : folderList) {
					String nameValue = folder.getAttributeValue("name");
					List<Element> folderListOne = folder.getChildren("folder");
					List<Element> fileListOne = folder.getChildren("file");
					for (Element fileOne : fileListOne) {
						// String fileTypeValue =
						// file.getAttributeValue("type");
						String fileNameValueOne = fileOne
								.getAttributeValue("name");
						String fileRequiredValueOne = fileOne
								.getAttributeValue("required");
						checkIfFolderExists(File.separator + nameValue + File.separator
								+ fileNameValueOne, fileRequiredValueOne);
					}
					for (Element folderOne : folderListOne) {
						String nameValueOne = folderOne
								.getAttributeValue("name");
						List<Element> folderListTwo = folderOne
								.getChildren("folder");
						List<Element> fileListTwo = folderOne
								.getChildren("file");
						for (Element fileTwo : fileListTwo) {
							// String fileTypeValue =
							// file.getAttributeValue("type");
							String fileNameValueTwo = fileTwo
									.getAttributeValue("name");
							String fileRequiredValueTwo = fileTwo
									.getAttributeValue("required");
							checkIfFolderExists(File.separator + nameValue + File.separator
									+ nameValueOne + File.separator + fileNameValueTwo,
									fileRequiredValueTwo);
						}
						for (Element folderTwo : folderListTwo) {
							String nameValueTwo = folderTwo
									.getAttributeValue("name");
							List<Element> folderListThree = folderTwo
									.getChildren("folder");
							List<Element> fileListThree = folderTwo
									.getChildren("file");
							for (Element fileThree : fileListThree) {
								// String fileTypeValue =
								// file.getAttributeValue("type");
								String fileNameValueThree = fileThree
										.getAttributeValue("name");
								String fileRequiredValueThree = fileThree
										.getAttributeValue("required");
								checkIfFolderExists(File.separator + nameValue + File.separator
										+ nameValueOne + File.separator + nameValueTwo
										+ File.separator + fileNameValueThree,
										fileRequiredValueThree);
							}
							for (Element folderThree : folderListThree) {
								String nameValueThree = folderThree
										.getAttributeValue("name");
								List<Element> folderListFour = folderThree
										.getChildren("folder");
								List<Element> fileListFour = folderThree
										.getChildren("file");
								for (Element fileFour : fileListFour) { // five
																		// nested
																		// levels
									// String fileTypeValue =
									// file.getAttributeValue("type");
									String fileNameValueFour = fileFour
											.getAttributeValue("name");
									String fileRequiredValueFour = fileFour
											.getAttributeValue("required");
									checkIfFolderExists(File.separator + nameValue + File.separator
											+ nameValueOne + File.separator
											+ nameValueTwo + File.separator
											+ nameValueThree + File.separator
											+ fileNameValueFour,
											fileRequiredValueFour);
								}
								for (Element folderFour : folderListFour) {
									String nameValueFour = folderFour
											.getAttributeValue("name");
									List<Element> fileListFive = validation
											.getChildren("file");
									for (Element file : fileListFive) { // five
																		// nested
																		// levels
										// String fileTypeValue =
										// file.getAttributeValue("type");
										String fileNameValueFive = folderFour
												.getAttributeValue("name");
										String fileRequiredValueFive = file
												.getAttributeValue("required");
										checkIfFolderExists(File.separator + nameValue
												+ File.separator + nameValueOne + File.separator
												+ nameValueTwo + File.separator
												+ nameValueThree + File.separator
												+ nameValueFour + File.separator
												+ fileNameValueFive,
												fileRequiredValueFive);
									}
								}
							}
						}
					}
				}
			}
		return new String[]{};
	}

	public String[] validateFileDimensions() throws MojoExecutionException, IOException, JDOMException {
//		try {
			List<Element> validationList = ((List<Element>) XPath.selectNodes(
					doc, "//validations/validation"));
			for (Element validation : validationList) {
				List<Element> folderList = validation.getChildren("folder");
				List<Element> fileList = validation.getChildren("file");
				for (Element file : fileList) { // five nested levels
					String fileTypeValue = file.getAttributeValue("type");
					String fileNameValue = file.getAttributeValue("name");
					if (fileTypeValue.equals("image")) {
						String expectedWidthString = file.getChild("width")
								.getValue();
						String expectedHeightString = file.getChild("height")
								.getValue();
						if (expectedHeightString != null
								&& expectedHeightString.length() > 0) {
							int expectedHeight = Integer
									.parseInt(expectedHeightString);
							checkImageHeight(File.separator + fileNameValue,
									expectedHeight);
						}

						if (expectedWidthString != null
								&& expectedWidthString.length() > 0) {
							int expectedWidth = Integer
									.parseInt(expectedWidthString);
							checkImageWidth(File.separator + fileNameValue, expectedWidth);
						}
					}
				}
				for (Element folder : folderList) {
					String nameValue = folder.getAttributeValue("name");
					List<Element> folderListOne = folder.getChildren("folder");
					List<Element> fileListOne = folder.getChildren("file");
					for (Element fileOne : fileListOne) {
						String fileTypeValue = fileOne
								.getAttributeValue("type");
						String fileNameValueOne = fileOne
								.getAttributeValue("name");
						if (fileTypeValue.equals("image")) {
							String expectedWidthString = fileOne.getChild(
									"width").getValue();
							String expectedHeightString = fileOne.getChild(
									"height").getValue();
							if (expectedHeightString != null
									&& expectedHeightString.length() > 0) {
								int expectedHeight = Integer
										.parseInt(expectedHeightString);
								checkImageHeight(File.separator + nameValue + File.separator
										+ fileNameValueOne, expectedHeight);
							}

							if (expectedWidthString != null
									&& expectedWidthString.length() > 0) {
								int expectedWidth = Integer
										.parseInt(expectedWidthString);
								checkImageWidth(File.separator + nameValue + File.separator
										+ fileNameValueOne, expectedWidth);
							}
						}
					}
					for (Element folderOne : folderListOne) {
						String nameValueOne = folderOne
								.getAttributeValue("name");
						List<Element> folderListTwo = folderOne
								.getChildren("folder");
						List<Element> fileListTwo = folderOne
								.getChildren("file");
						for (Element fileTwo : fileListTwo) {
							String fileTypeValue = fileTwo
									.getAttributeValue("type");
							String fileNameValueTwo = fileTwo
									.getAttributeValue("name");
							if (fileTypeValue.equals("image")) {
								String expectedWidthString = fileTwo.getChild(
										"width").getValue();
								String expectedHeightString = fileTwo.getChild(
										"height").getValue();
								if (expectedHeightString != null
										&& expectedHeightString.length() > 0) {
									int expectedHeight = Integer
											.parseInt(expectedHeightString);
									checkImageHeight(File.separator + nameValue + File.separator
											+ nameValueOne + File.separator
											+ fileNameValueTwo, expectedHeight);
								}

								if (expectedWidthString != null
										&& expectedWidthString.length() > 0) {
									int expectedWidth = Integer
											.parseInt(expectedWidthString);
									checkImageWidth(File.separator + nameValue + File.separator
											+ nameValueOne + File.separator
											+ fileNameValueTwo, expectedWidth);
								}
							}
						}
						for (Element folderTwo : folderListTwo) {
							String nameValueTwo = folderTwo
									.getAttributeValue("name");
							List<Element> folderListThree = folderTwo
									.getChildren("folder");
							List<Element> fileListThree = folderTwo
									.getChildren("file");
							for (Element fileThree : fileListThree) {
								String fileTypeValue = fileThree
										.getAttributeValue("type");
								String fileNameValueThree = fileThree
										.getAttributeValue("name");
								if (fileTypeValue.equals("image")) {
									String expectedWidthString = fileThree
											.getChild("width").getValue();
									String expectedHeightString = fileThree
											.getChild("height").getValue();
									if (expectedHeightString != null
											&& expectedHeightString.length() > 0) {
										int expectedHeight = Integer
												.parseInt(expectedHeightString);
										checkImageHeight(File.separator + nameValue
												+ File.separator + nameValueOne + File.separator
												+ nameValueTwo + File.separator
												+ fileNameValueThree,
												expectedHeight);
									}

									if (expectedWidthString != null
											&& expectedWidthString.length() > 0) {
										int expectedWidth = Integer
												.parseInt(expectedWidthString);
										checkImageWidth(File.separator + nameValue + File.separator
												+ nameValueOne + File.separator
												+ nameValueTwo + File.separator
												+ fileNameValueThree,
												expectedWidth);
									}
								}
							}
							for (Element folderThree : folderListThree) {
								String nameValueThree = folderThree
										.getAttributeValue("name");
								List<Element> folderListFour = folderThree
										.getChildren("folder");
								List<Element> fileListFour = folderThree
										.getChildren("file");
								for (Element fileFour : fileListFour) { // five
																		// nested
																		// levels
									String fileTypeValue = fileFour
											.getAttributeValue("type");
									String fileNameValueFour = fileFour
											.getAttributeValue("name");
									if (fileTypeValue.equals("image")) {
										String expectedWidthString = fileFour
												.getChild("width").getValue();
										String expectedHeightString = fileFour
												.getChild("height").getValue();
										if (expectedHeightString != null
												&& expectedHeightString
														.length() > 0) {
											int expectedHeight = Integer
													.parseInt(expectedHeightString);
											checkImageHeight(File.separator + nameValue
													+ File.separator + nameValueOne
													+ File.separator + nameValueTwo
													+ File.separator + nameValueThree
													+ File.separator + fileNameValueFour
													+ fileNameValueFour,
													expectedHeight);
										}

										if (expectedWidthString != null
												&& expectedWidthString.length() > 0) {
											int expectedWidth = Integer
													.parseInt(expectedWidthString);
											checkImageWidth(File.separator + nameValue
													+ File.separator + nameValueOne
													+ File.separator + nameValueTwo
													+ File.separator + nameValueThree
													+ File.separator + fileNameValueFour
													+ fileNameValueFour,
													expectedWidth);
										}
									}
								}
								for (Element folderFour : folderListFour) {
									String nameValueFour = folderFour
											.getAttributeValue("name");
									List<Element> fileListFive = validation
											.getChildren("file");
									for (Element fileFive : fileListFive) { // five
																			// nested
																			// levels
										String fileTypeValue = fileFive
												.getAttributeValue("type");
										String fileNameValueFive = folderFour
												.getAttributeValue("name");
										if (fileTypeValue.equals("image")) {
											String expectedWidthString = fileFive
													.getChild("width")
													.getValue();
											String expectedHeightString = fileFive
													.getChild("height")
													.getValue();
											if (expectedHeightString != null
													&& expectedHeightString
															.length() > 0) {
												int expectedHeight = Integer
														.parseInt(expectedHeightString);
												checkImageHeight(File.separator
														+ nameValue + File.separator
														+ nameValueOne + File.separator
														+ nameValueTwo + File.separator
														+ nameValueThree + File.separator
														+ nameValueFour + File.separator
														+ fileNameValueFive,
														expectedHeight);
											}

											if (expectedWidthString != null
													&& expectedWidthString
															.length() > 0) {
												int expectedWidth = Integer
														.parseInt(expectedWidthString);
												checkImageWidth(File.separator
														+ nameValue + File.separator
														+ nameValueOne + File.separator
														+ nameValueTwo + File.separator
														+ nameValueThree + File.separator
														+ nameValueFour + File.separator
														+ fileNameValueFive,
														expectedWidth);
											}
										}
									}
								}
							}
						}
					}
				}
			}
//		}
			return new String[]{};
	}

	private void checkIfFolderExists(String stringFile, String required)
			throws MojoExecutionException {
		File file = new File(loc + stringFile);
		if (new Boolean(required)) {
			if (file.exists()) {
				System.out.println(file.getAbsolutePath() + " exists");
			} else {
				System.out.println(file.getAbsolutePath() + " does not exist");
				throw new MojoExecutionException(file + " does not Exist");
			}
		}
	}

	private void checkImageWidth(String filename, int expectedWidth)  throws MojoExecutionException, IOException {
			BufferedImage bimg = ImageIO.read(new File(loc + filename));
			int width = bimg.getWidth();
			System.out.println("width " + width + " expectedWidth  "
					+ expectedWidth);
			if (width != expectedWidth) {
				throw new MojoExecutionException(filename
						+ " does not have correct dimensions  in width");
			} else {
				System.out.println(filename + " has exact width");
			}
		
	}

	private void checkImageHeight(String filename, int expectedHeight) throws MojoExecutionException, IOException{
			BufferedImage bimg = ImageIO.read(new File(loc + filename));
			int height = bimg.getHeight();
			if (height != expectedHeight) {
				throw new MojoExecutionException(filename
						+ " does not have correct dimensions in height");
			} else {
				System.out.println(filename + " has exact height");
			}
	}
}