package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.spi.ReaderException;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JAXBUnmarshalException extends ReaderException
{
   public JAXBUnmarshalException(final String s)
   {
      super(s);
   }

   public JAXBUnmarshalException(final String s, final Response response)
   {
      super(s, response);
   }

   public JAXBUnmarshalException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public JAXBUnmarshalException(final String s, final Throwable throwable)
   {
      super(s, throwable);
   }

   public JAXBUnmarshalException(final Throwable throwable)
   {
      super(throwable);
   }

   public JAXBUnmarshalException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }
}
