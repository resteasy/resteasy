package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.*;

public class HomecontrolCustomJAXBContext extends JAXBContext {

   private JAXBContext delegate;

   public HomecontrolCustomJAXBContext(Class<?> type) {
      try {
         this.delegate = JAXBContext.newInstance(type.getPackage().getName());
      } catch (JAXBException e) {
         throw new IllegalStateException("Error creating JAXBContext", e);
      }
   }

   @Override
   public Unmarshaller createUnmarshaller() throws JAXBException {
      System.out.println("Creating unmarshaller");
      return this.delegate.createUnmarshaller();
   }

   @Override
   public Marshaller createMarshaller() throws JAXBException {
      System.out.println("Creating marshaller");
      return this.delegate.createMarshaller();
   }

   @Override
   public Validator createValidator() throws JAXBException {
      return this.delegate.createValidator();
   }
}

