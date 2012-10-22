package com.photon.phresco.plugin.commons;

import java.util.Comparator;

import com.photon.phresco.framework.model.BuildInfo;

public class BuildInfoComparator implements Comparator<BuildInfo>{
	
	public int compare(BuildInfo info1, BuildInfo info2) {
    	Integer build1 = info1.getBuildNo();
    	Integer build2 = info2.getBuildNo();
        return (build1 > build2) ? -1 : (build1 == build2 ? 0 : 1);
    }
}
