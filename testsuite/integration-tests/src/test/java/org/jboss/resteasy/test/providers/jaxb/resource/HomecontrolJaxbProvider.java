package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
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
