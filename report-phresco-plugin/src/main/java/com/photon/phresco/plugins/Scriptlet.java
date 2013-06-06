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

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class Scriptlet extends JRDefaultScriptlet {

	private static final String SONAR_REPORT = "SonarReport";

	public Boolean updateTOC(String title, int pageNo)  throws JRScriptletException {
		try {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");
			HashMap headingsMap = null;
			
			if (this.variablesMap.containsKey("headingsMap")) {
				System.out.println("Variable MAP Called........ ");
				headingsMap = (HashMap)this.getVariableValue("headingsMap");
				System.out.println("Existing values : " + headingsMap);
				if (headingsMap != null) {
					headingsMap.put(title, pageNo + 1);
				}
				System.out.println("After values : " + headingsMap);
				System.out.println("Variable MAP Completed ........ ");
			}
			
			if (this.parametersMap.containsKey("headingsMap")) {
				System.out.println("Parametere MAP Called........ ");
				headingsMap = (HashMap)this.getParameterValue("headingsMap");
				System.out.println("Existing values : " + headingsMap);
				if (headingsMap != null) {
					if (headingsMap.size() == 0 && SONAR_REPORT.equals(title)) {
						headingsMap.put(title, pageNo);
					} else {
						headingsMap.put(title, pageNo + 1);
					}
				}
				System.out.println("After values : " + headingsMap);
				System.out.println("Parametere MAP Completed ........ ");
			}
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");
			return Boolean.FALSE;
		} catch (JRScriptletException e) {
			System.out.println("I found the error here " + e.getLocalizedMessage());
			throw e;
		}
	}
}
