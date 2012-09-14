package com.photon.phresco.plugins.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.photon.phresco.plugins.model.Mojos;

public class MojoProcessor {

	private Mojos mojos;
	
	private File file;
	
	public MojoProcessor(File infoFile) throws JAXBException, IOException {
		if(infoFile.exists()){
			JAXBContext jaxbContext = JAXBContext.newInstance(Mojos.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			mojos = (Mojos) jaxbUnmarshaller.unmarshal(infoFile);
		} else {
			infoFile.createNewFile();
			mojos = new Mojos();
		}
		file = infoFile;
	}
	public void getMojoGoal() {
		System.out.println("Goal====> " + mojos.getMojo().getGoal());
		System.out.println("implementation=====> " + mojos.getMojo().getImplementation());
		System.out.println("Language========> " + mojos.getMojo().getLanguage());
		System.out.println("Name=========> " + mojos.getMojo().getConfiguration().getParameters().getParameter().getName());
		System.out.println("Key====> " + mojos.getMojo().getConfiguration().getParameters().getParameter().getKey());
		System.out.println("Type=======> " + mojos.getMojo().getConfiguration().getParameters().getParameter().getType());
	}
	public void save() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Mojos.class);
		Marshaller marshal = jaxbContext.createMarshaller();
		marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshal.marshal(mojos, file);
	}
}
