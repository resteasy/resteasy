package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.util.HttpResponseCodes;

@SuppressWarnings("serial")
public class NoMessageBodyWriterFoundFailure extends LoggableFailure
{
   private ResponseInvoker responseInvoker;

   public NoMessageBodyWriterFoundFailure(ResponseInvoker responseInvoker)
   {
      super(
            String
                  .format(
                        "Could not find MessageBodyWriter for response object of type: %s of media type: %s",
                        responseInvoker.getType().getName(), responseInvoker
                              .getContentType()),
            HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      this.responseInvoker = responseInvoker;
   }

   public ResponseInvoker getResponseInvoker()
   {
      return responseInvoker;
   }
}
