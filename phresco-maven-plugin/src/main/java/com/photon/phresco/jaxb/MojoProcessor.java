package com.photon.phresco.jaxb;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class MojoProcessor {

    private Model model;
    
    private File file;
    
    public MojoProcessor(File pomFile) throws JAXBException, IOException {
        if(pomFile.exists()){
            JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            model = (Model) ((JAXBElement)jaxbUnmarshaller.unmarshal(pomFile)).getValue();
        } else {
            pomFile.createNewFile();
            model = new Model();
        }
        file = pomFile;
    }
    
    public void method() {
        Mojo mojo = new Mojo();
        mojo.setGoal("Suresh");
        model.setMojo(mojo);
    }
    
    public void save() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Model.class);
        Marshaller marshal = jaxbContext.createMarshaller();
        marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshal.marshal(model, file);
    }
    public static void main(String[] args) throws JAXBException, IOException {
        MojoProcessor p = new MojoProcessor(new File("D:\\mojo\\selectedinfos.xml"));
        p.method();
        p.save();
    }
}
