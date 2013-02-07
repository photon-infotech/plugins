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
