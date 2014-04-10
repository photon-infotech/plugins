/**
 * Xcodebuild Command-Line Wrapper
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
package com.photon.phresco.plugins.xcode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
/**
 * APP deploy
 * @goal runsimulator
 */
public class RunSimulator extends AbstractMojo {

	/**
	 * command line tool for iphone simulator
	 * @parameter expression="${simulator.home}" default-value="/Developer/Platforms/iPhoneSimulator.platform/Developer/Applications/iPhone Simulator.app/Contents/MacOS/iPhone Simulator"
	 */
	private String simHome;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
			getLog().info("Simulator home" + simHome);
			
			Runnable runnable = new Runnable() {
				public void run() {
					ProcessBuilder pb = new ProcessBuilder(simHome);
					// Include errors in output
					pb.redirectErrorStream(true);

					getLog().info("List of commands"+pb.command());
					Process child;
					try {
						child = pb.start();
						// Consume subprocess output and write to stdout for debugging
						InputStream is = new BufferedInputStream(child.getInputStream());
						int singleByte = 0;
						while ((singleByte = is.read()) != -1) {
							System.out.write(singleByte);
						}
					} catch (IOException e) {
						getLog().error(e);
					}
										
				}
			};
			
			Thread t = new Thread(runnable, "iPhoneSimulator");
			t.start();
		
	}
}
