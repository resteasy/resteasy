package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

public class HomecontrolCustomJAXBContext extends JAXBContext {

   private JAXBContext delegate;
   private static final Logger LOG = Logger.getLogger(HomecontrolCustomJAXBContext.class);

   public HomecontrolCustomJAXBContext(Class<?> type) {
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

   @Override
   public Validator createValidator() throws JAXBException {
      return this.delegate.createValidator();
   }
}

