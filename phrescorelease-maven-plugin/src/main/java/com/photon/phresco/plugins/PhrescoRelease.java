package com.photon.phresco.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Query;

import com.photon.phresco.commons.model.ArtifactGroup;
import com.photon.phresco.commons.model.ArtifactGroup.Type;
import com.photon.phresco.commons.model.ArtifactInfo;
import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.service.api.Converter;
import com.photon.phresco.service.converters.ConvertersFactory;
import com.photon.phresco.service.dao.ArtifactGroupDAO;

/**
 * Goal which touches a timestamp file.
 *
 * @goal release
 * 
 * @phase process-sources
 */
public class PhrescoRelease extends AbstractMojo {
    
	/**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;
    
    /**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
    public void execute() throws MojoExecutionException {
    	DbService service = new DbService();
    	try {
			service.saveArtifact(createArtifactGroup());
		} catch (PhrescoException e) {
			throw new MojoExecutionException(e.getErrorMessage());
		}
    }

	private ArtifactGroup createArtifactGroup() {
		ArtifactGroup artifactGroup = new ArtifactGroup();
		artifactGroup.setArtifactId(project.getArtifactId());
		artifactGroup.setGroupId(project.getGroupId());
		artifactGroup.setName(project.getName());
		artifactGroup.setPackaging(project.getPackaging());
		artifactGroup.setType(Type.FEATURE);
		List<ArtifactInfo> artifactInfos = new ArrayList<ArtifactInfo>();
		ArtifactInfo artifactInfo = new ArtifactInfo();
		artifactInfo.setArtifactGroupId(artifactGroup.getId());
		artifactInfo.setVersion(project.getVersion());
		artifactInfos.add(artifactInfo);
		artifactGroup.setVersions(artifactInfos);
		return artifactGroup;
	}
}

class DbService extends com.photon.phresco.service.impl.DbService {
	
	public DbService() {
		super();
	}

	public void saveArtifact(ArtifactGroup artifactToCreate) throws PhrescoException {
        Converter<ArtifactGroupDAO, ArtifactGroup> converter = 
            (Converter<ArtifactGroupDAO, ArtifactGroup>) ConvertersFactory.getConverter(ArtifactGroupDAO.class);
        ArtifactGroupDAO moduleGroupDAO = converter.convertObjectToDAO(artifactToCreate);
        
        List<com.photon.phresco.commons.model.ArtifactInfo> moduleGroupVersions = artifactToCreate.getVersions();
        List<String> versionIds = new ArrayList<String>();
        
        ArtifactGroupDAO moduleDAO = mongoOperation.findOne(ARTIFACT_GROUP_COLLECTION_NAME, 
		        new Query(Criteria.where(REST_API_NAME).is(moduleGroupDAO.getName())), ArtifactGroupDAO.class);
        
        com.photon.phresco.commons.model.ArtifactInfo newVersion = artifactToCreate.getVersions().get(0);
        if(moduleDAO != null) {
        	moduleGroupDAO.setId(moduleDAO.getId());
        	versionIds.addAll(moduleDAO.getVersionIds());
        	List<com.photon.phresco.commons.model.ArtifactInfo> info = mongoOperation.find(ARTIFACT_INFO_COLLECTION_NAME, 
        			new Query(Criteria.where(DB_COLUMN_ARTIFACT_GROUP_ID).is(moduleDAO.getId())), 
        			com.photon.phresco.commons.model.ArtifactInfo.class);
        	
        	List<com.photon.phresco.commons.model.ArtifactInfo> versions = new ArrayList<com.photon.phresco.commons.model.ArtifactInfo>();
        	newVersion.setArtifactGroupId(moduleDAO.getId());
        	versions.add(newVersion);
        	info.addAll(versions);
        	
        	String id = checkVersionAvailable(info, newVersion.getVersion());
        	if(id == newVersion.getId()) {
        		versionIds.add(newVersion.getId());
        	}
			newVersion.setId(id);
    		mongoOperation.save(ARTIFACT_INFO_COLLECTION_NAME, newVersion);
        }  else {
        		versionIds.add(newVersion.getId());
        		newVersion.setArtifactGroupId(moduleGroupDAO.getId());
                mongoOperation.save(ARTIFACT_INFO_COLLECTION_NAME, newVersion);
        }
        moduleGroupDAO.setVersionIds(versionIds);
        mongoOperation.save(ARTIFACT_GROUP_COLLECTION_NAME, moduleGroupDAO);
    }
	
	private String checkVersionAvailable(List<com.photon.phresco.commons.model.ArtifactInfo> info, String version) {
		for (com.photon.phresco.commons.model.ArtifactInfo artifactInfo : info) {
			if(artifactInfo.getVersion().equals(version)) {
				return artifactInfo.getId();
			}
		}
		return null;
	}
}