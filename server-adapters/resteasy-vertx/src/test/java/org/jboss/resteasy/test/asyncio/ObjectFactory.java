package org.jboss.resteasy.test.asyncio;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    public JAXBElement<AsyncIOResource.JaxbXmlType> create(AsyncIOResource.JaxbXmlType param) {
        return new JAXBElement<>(QName.valueOf("jaxbXmlType"), AsyncIOResource.JaxbXmlType.class, param);
    }
}
