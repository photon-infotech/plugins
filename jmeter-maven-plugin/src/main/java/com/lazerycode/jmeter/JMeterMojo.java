package com.lazerycode.jmeter;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.lazerycode.jmeter.testrunner.TestManager;

/**
 * JMeter Maven plugin.
 *
 * @author Tim McCune
 * @goal jmeter
 * @requiresProject true
 */
@SuppressWarnings("JavaDoc")
public class JMeterMojo extends JMeterAbstractMojo {

	/**
	 * Run all the JMeter tests.
	 *
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skipTests) {
			getLog().info(" ");
			getLog().info("-------------------------------------------------------");
			getLog().info(" S K I P P I N G    T E S T   -   S C R I P T");
			getLog().info("-------------------------------------------------------");
			getLog().info(" ");
			return;
		}
		getLog().info(" ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" E X E C U T I N G   -   T E S T   -   S C R I P T     ");
		getLog().info("-------------------------------------------------------");
		getLog().info(" ");
		generateJMeterDirectoryTree();
		setJMeterResultFileFormat();
		propertyConfiguration();
		populateJMeterDirectoryTree();
		initialiseJMeterArgumentsArray(true);
		TestManager jMeterTestManager = new TestManager(testArgs, logsDir, testFilesDirectory, testFilesIncluded, testFilesExcluded, remoteConfig, suppressJMeterOutput, resultFilesDirectory, baseDir, pluginTypes, resultFilesName);
		getLog().info(" ");
		getLog().info(this.proxyConfig.toString());
		jMeterTestManager.mapResultNames();
		List<String> testResults = jMeterTestManager.executeTests();
		parseTestResults(testResults);


		// JMeter sets this system property. to "org.apache.commons.logging.impl.LogKitLogger".
		// If another plugin is executed after this plugin that also uses (a third-party library that uses) commons-logging, but doesn't supply the same logger, execution will fail.
		// TODO: may not work if SecurityManager is enabled. Needs PropertyPermission "key", "read,write" to work.
		System.clearProperty("org.apache.commons.logging.Log");
	}

	/**
	 * Scan JMeter result files for "error" and "failure" messages
	 *
	 * @param results List of JMeter result files.
	 * @throws MojoExecutionException
	 * @throws MojoFailureException
	 */
	void parseTestResults(List<String> results) throws MojoExecutionException, MojoFailureException {
		FailureScanner failureScanner = new FailureScanner(ignoreResultFailures);
		int totalFailureCount = 0;
		boolean failed = false;
		for (String file : results) {
			try {
				if (failureScanner.hasTestFailed(new File(file))) {
					totalFailureCount += failureScanner.getFailureCount();
					failed = true;
				}
			} catch (IOException e) {
				throw new MojoExecutionException(e.getMessage());
			}
		}
		getLog().info(" ");
		getLog().info("Test Results:");
		getLog().info(" ");
		getLog().info("Tests Run: " + results.size() + ", Failures: " + totalFailureCount);
		getLog().info(" ");
		if (failed) {
			//TODO add absolute path to JMeter logs to make life easy?
			throw new MojoFailureException("There were " + totalFailureCount + " test failures.  See the JMeter logs for details.");
		}
	}
}