package com.photon.phresco.plugins;

/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.HashMap;
import java.util.Collection;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class Scriptlet extends JRDefaultScriptlet {

	private static final String SONAR_REPORT = "SonarReport";

	public Boolean updateTOC(String title, int pageNo)  throws JRScriptletException {
		try {
			HashMap headingsMap = null;
			
			if (this.variablesMap.containsKey("headingsMap")) {
				headingsMap = (HashMap)this.getVariableValue("headingsMap");
				if (headingsMap != null) {
					headingsMap.put(title, pageNo + 1);
				}
			}
			
			if (this.parametersMap.containsKey("headingsMap")) {
				headingsMap = (HashMap)this.getParameterValue("headingsMap");
				if (headingsMap != null) {
					if (headingsMap.size() == 0 && SONAR_REPORT.equals(title)) {
						headingsMap.put(title, pageNo);
					} else {
						headingsMap.put(title, pageNo + 1);
					}
				}
			}
			return Boolean.FALSE;
		} catch (JRScriptletException e) {
			System.out.println("Error here " + e.getLocalizedMessage());
			return Boolean.FALSE;
		}
	}
	
	public Boolean updateHeadingMapTOC(String groupName, String title, int pageNo)  throws JRScriptletException {
		try {
			Collection headingsCollection = null;
			Integer type = null;
			String text = null;
			String reference = null;
			Integer pageIndex = pageNo;
			
			// Group name specification
			if ("1".equals(groupName)) { // application wise
				type = new Integer(1);
			} else if ("2".equals(groupName)) { // report wise
				type = new Integer(2);
			}
			
			// Heading collection
			if (this.variablesMap.containsKey("HeadingsCollection")) {
				headingsCollection = (Collection)this.getVariableValue("HeadingsCollection");
				text = title;
				reference = title + pageNo;
				
				if (headingsCollection != null) {
					headingsCollection.add(new HeadingBean(type, text, reference, pageIndex));
				}
			}
			
			if (this.parametersMap.containsKey("HeadingsCollection")) {
				headingsCollection = (Collection)this.getParameterValue("HeadingsCollection");
				text = title;
				reference = title + pageNo;
				
				if (headingsCollection != null) {
					headingsCollection.add(new HeadingBean(type, text, reference, pageIndex));
				}
			}
			
			return Boolean.FALSE;
		} catch (JRScriptletException e) {
			System.out.println("Error here " + e.getLocalizedMessage());
			return Boolean.FALSE;
		}
	}
}
