/**
 * Android Maven Plugin - android-maven-plugin
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
package com.photon.maven.plugins.android.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Yet another helper class for dealing with XML.
 */
public class XmlHelper
{

    public static void removeDirectChildren( Node parent )
    {
        NodeList childNodes = parent.getChildNodes();
        while ( childNodes.getLength() > 0 )
        {
            parent.removeChild( childNodes.item( 0 ) );
        }
    }

    public static Element getOrCreateElement( Document doc, Element manifestElement, String elementName )
    {
        NodeList nodeList = manifestElement.getElementsByTagName( elementName );
        Element element = null;
        if ( nodeList.getLength() == 0 )
        {
            element = doc.createElement( elementName );
            manifestElement.appendChild( element );
        }
        else
        {
            element = ( Element ) nodeList.item( 0 );
        }
        return element;
    }
}
