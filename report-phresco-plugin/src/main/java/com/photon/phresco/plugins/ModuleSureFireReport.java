/**
 * report-phresco-plugin
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
package com.photon.phresco.plugins;

import java.util.List;

public class ModuleSureFireReport {
		private String moduleOrTechName;
		private String moduleOrTechLabel;
		
		private List<SureFireReport> sureFireReport;
		public String getModuleOrTechName() {
			return moduleOrTechName;
		}
		public void setModuleOrTechName(String moduleOrTechName) {
			this.moduleOrTechName = moduleOrTechName;
		}
		public String getModuleOrTechLabel() {
			return moduleOrTechLabel;
		}
		public void setModuleOrTechLabel(String moduleOrTechLabel) {
			this.moduleOrTechLabel = moduleOrTechLabel;
		}
		public List<SureFireReport> getSureFireReport() {
			return sureFireReport;
		}
		public void setSureFireReport(List<SureFireReport> sureFireReport) {
			this.sureFireReport = sureFireReport;
		}
}
