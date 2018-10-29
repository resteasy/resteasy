package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.spi.WriterException;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JAXBMarshalException extends WriterException
{
   public JAXBMarshalException(final String s)
   {
      super(s);
   }

   public JAXBMarshalException(final String s, final Response response)
   {
      super(s, response);
   }

   public JAXBMarshalException(final String s, final Throwable throwable, final Response response)
   {
      super(s, throwable, response);
   }

   public JAXBMarshalException(final String s, final Throwable throwable)
   {
      super(s, throwable);
   }

   public JAXBMarshalException(final Throwable throwable)
   {
      super(throwable);
   }

   public JAXBMarshalException(final Throwable throwable, final Response response)
   {
      super(throwable, response);
   }
}
