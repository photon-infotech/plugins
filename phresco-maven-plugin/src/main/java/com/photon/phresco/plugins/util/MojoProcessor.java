package com.photon.phresco.plugins.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.photon.phresco.exception.PhrescoException;
import com.photon.phresco.plugins.model.Mojos;
import com.photon.phresco.plugins.model.Mojos.Mojo.Configuration;

public class MojoProcessor {

	private Mojos mojos;
	
	private File file;
	
	public MojoProcessor(File infoFile) throws PhrescoException {
        try {
    		if(infoFile.exists()){
    			JAXBContext jaxbContext = JAXBContext.newInstance(Mojos.class);
    			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    			mojos = (Mojos) jaxbUnmarshaller.unmarshal(infoFile);
    		} else {
    			infoFile.createNewFile();
    			mojos = new Mojos();
    		}
    		file = infoFile;
        } catch (JAXBException e) {
            throw new PhrescoException(e);
        } catch (IOException e) {
            throw new PhrescoException(e);
        }
	}
	
	public Configuration getConfiguration() {
	    return mojos.getMojo().getConfiguration();
	}
	
	public void save() throws PhrescoException {
        try {
    		JAXBContext jaxbContext = JAXBContext.newInstance(Mojos.class);
    		Marshaller marshal = jaxbContext.createMarshaller();
    		marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    		marshal.marshal(mojos, file);
        } catch (JAXBException e) {
            throw new PhrescoException(e);
        }
	}
}
