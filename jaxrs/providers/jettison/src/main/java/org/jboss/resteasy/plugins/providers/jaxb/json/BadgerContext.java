package org.jboss.resteasy.plugins.providers.jaxb.json;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("deprecation")
public class BadgerContext extends JAXBContext
{
   private JAXBContext context;

   public BadgerContext(Class... clazz)
   {
      try
      {
         context = JAXBContext.newInstance(clazz);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public BadgerContext(String contextPath)
   {
      try
      {
         context = JAXBContext.newInstance(contextPath);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public Unmarshaller createUnmarshaller() throws JAXBException
   {
      return new BadgerUnmarshaller(context);
   }

   public Marshaller createMarshaller() throws JAXBException
   {
      return new BadgerMarshaller(context);
   }

   public Validator createValidator() throws JAXBException
   {
      return context.createValidator();
   }


}
