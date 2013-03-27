/**
 * Xcodebuild Command-Line Wrapper
 *
 * Copyright (C) 1999-2013 Photon Infotech Inc.
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
package com.photon.phresco.plugins.xcode.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XcodeUtil {
	
	public static void copyFolder(File src, File dest) throws IOException{

		if(src.isDirectory()){
			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		} else {
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes 
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}
			//copy metadata information. otherwise the copied output is not executable by ios simulator or device.
			dest.setExecutable(src.canExecute(),false);
			dest.setWritable(src.canWrite());

			in.close();
			out.close();
		}
	}
	
	public static void copyFiles(File srcFile, File destFolder) throws IOException {
		InputStream in = new FileInputStream(srcFile);
		if(!destFolder.exists()) {
			destFolder.mkdirs();
		}
		File destFile = new File(destFolder, srcFile.getName());
		OutputStream out = new FileOutputStream(destFile); 

		byte[] buffer = new byte[1024];

		int length;
		//copy the file content in bytes 
		while ((length = in.read(buffer)) > 0){
			out.write(buffer, 0, length);
		}
		//copy metadata information. otherwise the copied output is not executable by ios simulator or device.
		destFile.setExecutable(srcFile.canExecute(),false);
		destFile.setWritable(srcFile.canWrite());

		in.close();
		out.close();
	}

}
