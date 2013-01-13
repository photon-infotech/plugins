/*
 * ###
 * php-maven-plugin Maven Mojo
 * 
 * Copyright (C) 1999 - 2012 Photon Infotech Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ###
 */
package com.photon.phresco.plugins.php;

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryWalkListener;
import org.codehaus.plexus.util.DirectoryWalker;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

import com.photon.phresco.plugin.commons.MavenProjectInfo;
import com.photon.phresco.plugin.commons.PluginConstants;

public class PhpCompile implements DirectoryWalkListener, PluginConstants {

	private String phpExe = "php";
	private File srcDir;
	private Log log;
	private File baseDir;

	private ArrayList<Exception> compilerExceptions = new ArrayList<Exception>();
	private static final ArrayList<String> ERRORIDENTIFIERS = new ArrayList<String>();

	public void compile(MavenProjectInfo mavenProjectInfo, Log log) throws MojoExecutionException {
		this.log = log;
		baseDir = mavenProjectInfo.getBaseDir();
		init();
		try {
			executeCompile();
		} catch (MultipleCompileException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void init() throws MojoExecutionException {
		try {

			srcDir = new File(baseDir.getPath() + File.separator + PROJECT_FOLDER);
			if (!srcDir.exists()) {
				log.error(srcDir.getName() + " doesnot exists");
				throw new MojoExecutionException(srcDir.getName() + " doesnot exists");
			}

			fillErrorIdentifiers();

		} catch (Exception e) {
			log.error(e);
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void fillErrorIdentifiers() {
		ERRORIDENTIFIERS.add("Error");
		ERRORIDENTIFIERS.add("Parse error");
		ERRORIDENTIFIERS.add("Warning");
		ERRORIDENTIFIERS.add("Fatal error");
		ERRORIDENTIFIERS.add("Notice");
	}

	private void executeCompile() throws MultipleCompileException {
		//compile all the php files
		DirectoryWalker walker = new DirectoryWalker();
		log.info("Source Directory : "+ srcDir.getPath());
		walker.setBaseDir(srcDir);
		walker.addDirectoryWalkListener(this);
		walker.addSCMExcludes();
		walker.scan();
		if (compilerExceptions.size() != 0) {
			throw new MultipleCompileException(compilerExceptions);
		}
	}

	@Override
	public void directoryWalkStarting(File paramFile) {
		debug("Start compiling source folder: "+ baseDir.getAbsoluteFile());
	}

	@Override
	public void directoryWalkStep(int percentage, File file) {
		log.debug("percentage: " + percentage);
		try {
			if (file.isFile() && file.getName().endsWith(".php")) {
				compilePhpFile(file);
			}
		} catch (Exception e) {
			log.debug(e);
			compilerExceptions.add(e);
		}
	}

	@Override
	public void directoryWalkFinished() {
		debug("Compiling has finished.");
	}

	@Override
	public void debug(String message) {
		log.debug(message);
	}

	protected final void compilePhpFile(File file)
	throws PhpCompileException, CommandLineException {
		String commandString = phpExe + " -l  \"" + file.getAbsolutePath() + "\"";

		log.debug("Executing the command : " + commandString);
		final StringBuffer bufferErrBuffer = new StringBuffer();
		final StringBuffer bufferOutBuffer = new StringBuffer();
		Commandline commandLine = new Commandline(commandString);

		CommandLineUtils.executeCommandLine(commandLine, new StreamConsumer() {
			public void consumeLine(String line) {
				log.debug("php.out: " + line);
				if (isError(line) == true) {
					bufferErrBuffer.append(line);
				}
				bufferOutBuffer.append(line);
			}
		}, new StreamConsumer() {
			public void consumeLine(String line) {
				log.debug("php.err: " + line);
				bufferErrBuffer.append(line);
			}
		});

		if (StringUtils.isNotEmpty(bufferErrBuffer.toString())) {
			log.debug(bufferErrBuffer.toString());
			throw new PhpCompileException(commandString,
					PhpCompileException.ERROR, file, bufferErrBuffer.toString());
		}
	}

	private boolean isError(String line) {
		line = line.trim();
		for (int i = 0; i < ERRORIDENTIFIERS.size(); i++) {
			if (line.startsWith((String) ERRORIDENTIFIERS.get(i) + ":")
					|| line.startsWith("<b>" + (String) ERRORIDENTIFIERS.get(i)
							+ "</b>:")) {
				return true;
			}
		}
		return false;
	}

}