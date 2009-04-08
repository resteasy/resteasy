package org.jboss.resteasy.examples.resteasy;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.time.StopWatch;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.interception.ClientExecutionContext;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyReaderContext;
import org.jboss.resteasy.core.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExecutionInterceptor implements ClientExecutionInterceptor,
      MessageBodyReaderInterceptor
{
   private final static Logger logger = LoggerFactory
         .getLogger(LoggingExecutionInterceptor.class);

   @SuppressWarnings("unchecked")
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      StopWatch stopWatch = new StopWatch();
      stopWatch.start();
      String uri = ctx.getRequest().getUri();
      ClientResponse response = ctx.proceed();
      stopWatch.stop();
      String contentLength = (String) response.getMetadata().getFirst(
            HttpHeaderNames.CONTENT_LENGTH);
      logger.info(String.format("Read url %s in %d ms size %s.", uri, stopWatch
            .getTime(), contentLength));
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
               .getMediaType().toString(), ctx.getType().getName(), stopWatch
               .getTime()));
      }
   }
}
