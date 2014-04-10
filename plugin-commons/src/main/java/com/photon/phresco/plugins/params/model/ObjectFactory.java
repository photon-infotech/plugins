/**
 * Phresco Plugin Commons
 *
 * Copyright (C) 1999-2014 Photon Infotech Inc.
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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.12.05 at 12:27:19 PM IST 
//


package com.photon.phresco.plugins.params.model;

import javax.xml.bind.annotation.XmlRegistry;

import com.photon.phresco.plugins.model.Assembly;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.photon.phresco.war.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.photon.phresco.war.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Assembly.FileSets.FileSet }
     * 
     */
    public Assembly.FileSets.FileSet createAssemblyFileSetsFileSet() {
        return new Assembly.FileSets.FileSet();
    }

    /**
     * Create an instance of {@link Assembly.FileSets.FileSet.Excludes }
     * 
     */
    public Assembly.FileSets.FileSet.Excludes createAssemblyFileSetsFileSetExcludes() {
        return new Assembly.FileSets.FileSet.Excludes();
    }

    /**
     * Create an instance of {@link Assembly }
     * 
     */
    public Assembly createAssembly() {
        return new Assembly();
    }

    /**
     * Create an instance of {@link Assembly.FileSets.FileSet.Includes }
     * 
     */
    public Assembly.FileSets.FileSet.Includes createAssemblyFileSetsFileSetIncludes() {
        return new Assembly.FileSets.FileSet.Includes();
    }

    /**
     * Create an instance of {@link Assembly.FileSets }
     * 
     */
    public Assembly.FileSets createAssemblyFileSets() {
        return new Assembly.FileSets();
    }

    /**
     * Create an instance of {@link Assembly.Formats }
     * 
     */
    public Assembly.Formats createAssemblyFormats() {
        return new Assembly.Formats();
    }

}
