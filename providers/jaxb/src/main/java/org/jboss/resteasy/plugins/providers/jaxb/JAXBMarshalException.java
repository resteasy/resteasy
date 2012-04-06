package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.spi.WriterException;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JAXBMarshalException extends WriterException
{
   public JAXBMarshalException(String s)
   {
      super(s);
   }

   public JAXBMarshalException(String s, Response response)
   {
      super(s, response);
   }

   public JAXBMarshalException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public JAXBMarshalException(String s, Throwable throwable)
   {
      super(s, throwable);
   }

   public JAXBMarshalException(Throwable throwable)
   {
      super(throwable);
   }

   public JAXBMarshalException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }
}
