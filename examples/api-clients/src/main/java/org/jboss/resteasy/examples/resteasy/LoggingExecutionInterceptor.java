package org.jboss.resteasy.examples.resteasy;

import org.apache.commons.lang.time.StopWatch;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public class LoggingExecutionInterceptor implements ClientExecutionInterceptor,
        MessageBodyReaderInterceptor
{
   private final static Logger logger = Logger
           .getLogger(LoggingExecutionInterceptor.class);

   @SuppressWarnings("unchecked")
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      String uri = ctx.getRequest().getUri();
      logger.info(String.format("Reading url %s", uri));
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      ClientResponse response = ctx.proceed();
      stopWatch.stop();
      String contentLength = (String) response.getMetadata().getFirst(
              HttpHeaderNames.CONTENT_LENGTH);
      logger.info(String.format("Read url %s in %d ms size %s.", uri,
              stopWatch.getTime(), contentLength));
      return response;
   }

   public Object read(MessageBodyReaderContext ctx) throws IOException,
           WebApplicationException
   {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      try
      {
         return ctx.proceed();
      }
      finally
      {
         stopWatch.stop();
         logger.info(String.format("Read mediaType %s as %s in %d ms.", ctx
                 .getMediaType().toString(), ctx.getType().getName(),
                 stopWatch.getTime()));
      }
   }
}
