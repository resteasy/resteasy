package org.jboss.resteasy.test.asyncio;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory
{
   public JAXBElement<AsyncIOResource.JaxbXmlType> create(AsyncIOResource.JaxbXmlType param) {
      return new JAXBElement<>(QName.valueOf("jaxbXmlType"), AsyncIOResource.JaxbXmlType.class, param);
   }
}
