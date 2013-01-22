package com.photon.phresco.convertor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.photon.phresco.plugin.commons.MavenProjectInfo;

public class ThemeConvertor {

	public ThemeConvertor() {
	
	}
	public void convert(MavenProjectInfo mavenProjectInfo) throws IOException {
		copyDirectory(new File(mavenProjectInfo.getProject().getBasedir()
				+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.target.dir")), 
				new File(mavenProjectInfo.getProject().getBasedir()
				+ mavenProjectInfo.getProject().getProperties().getProperty("phresco.theme.upload.dir")));
	};

	public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
		System.out.println("sourceLocation " + sourceLocation.getAbsolutePath());
		System.out.println("targetLocation " + targetLocation.getAbsolutePath());
		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				System.out.println("Filename : " + children[i]);
				if (children[i].toLowerCase().equals("thumbs.db")) {
					File thumbFile = new File(sourceLocation + File.separator + children[i]);
					thumbFile.delete();
					continue;
				}
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else {
			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);
			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
				out.flush();
			}
			in.close();
			out.close();
		}
	}
}
