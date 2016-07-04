package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.spi.ReaderException;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JAXBUnmarshalException extends ReaderException
{
   public JAXBUnmarshalException(String s)
   {
      super(s);
   }

   public JAXBUnmarshalException(String s, Response response)
   {
      super(s, response);
   }

   public JAXBUnmarshalException(String s, Throwable throwable, Response response)
   {
      super(s, throwable, response);
   }

   public JAXBUnmarshalException(String s, Throwable throwable)
   {
      super(s, throwable);
   }

   public JAXBUnmarshalException(Throwable throwable)
   {
      super(throwable);
   }

   public JAXBUnmarshalException(Throwable throwable, Response response)
   {
      super(throwable, response);
   }
}
