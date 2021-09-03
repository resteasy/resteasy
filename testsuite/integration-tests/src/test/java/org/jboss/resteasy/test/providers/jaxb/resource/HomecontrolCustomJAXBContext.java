package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class HomecontrolCustomJAXBContext extends JAXBContext {

   private JAXBContext delegate;
   private static final Logger LOG = Logger.getLogger(HomecontrolCustomJAXBContext.class);

   public HomecontrolCustomJAXBContext(final Class<?> type) {
      try {
         this.delegate = JAXBContext.newInstance(type.getPackage().getName());
      } catch (JAXBException e) {
         throw new IllegalStateException("Error creating JAXBContext", e);
      }
   }

   @Override
   public Unmarshaller createUnmarshaller() throws JAXBException {
      LOG.info("Creating unmarshaller");
      return this.delegate.createUnmarshaller();
   }

   @Override
   public Marshaller createMarshaller() throws JAXBException {
      LOG.info("Creating marshaller");
      return this.delegate.createMarshaller();
   }

   @SuppressWarnings("deprecation")
   @Override
   public jakarta.xml.bind.Validator createValidator() throws JAXBException {
      return this.delegate.createValidator();
   }
}
