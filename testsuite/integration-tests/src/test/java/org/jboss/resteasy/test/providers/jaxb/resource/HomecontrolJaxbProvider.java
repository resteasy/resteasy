package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;

@Provider
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class HomecontrolJaxbProvider implements ContextResolver<JAXBContext> {

   @Override
   public JAXBContext getContext(Class<?> type) {
      return new HomecontrolCustomJAXBContext(type);
   }
}