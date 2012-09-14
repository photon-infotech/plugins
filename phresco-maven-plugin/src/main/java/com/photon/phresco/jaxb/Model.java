package com.photon.phresco.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="mojos")
public class Model {
    
    private Mojo mojo;

    public Mojo getMojo() {
        return mojo;
    }

    public void setMojo(Mojo mojo) {
        this.mojo = mojo;
    }

}
