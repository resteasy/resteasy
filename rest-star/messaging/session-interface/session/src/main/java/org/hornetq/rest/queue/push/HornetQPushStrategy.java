package org.hornetq.rest.queue.push;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.rest.queue.push.xml.XmlHttpHeader;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.Link;

/**
 * Forwarding to a HornetQ/REST-* endpoing
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HornetQPushStrategy extends UriTemplateStrategy
{
   protected boolean initialized = false;

   public void start() throws Exception
   {
      // initialize();
   }

   protected void initialize()
           throws Exception
   {
      super.start();
      initialized = true;
      initAuthentication();
      ClientRequest request = executor.createRequest(registration.getTarget().getHref());
      for (XmlHttpHeader header : registration.getHeaders())
      {
         request.header(header.getName(), header.getValue());
      }
      ClientResponse res = request.head();
      if (res.getStatus() != 200)
      {
         throw new RuntimeException("Failed to query HornetQ REST destination for init information.  Status: " + res.getStatus());
      }
      String url = (String) res.getHeaders().getFirst("msg-create-with-id");
      if (url == null)
      {
         if (res.getLinkHeader() == null)
         {
            throw new RuntimeException("Could not find create-with-id URL");
         }
         Link link = res.getLinkHeader().getLinkByTitle("create-with-id");
         if (link == null)
         {
            throw new RuntimeException("Could not find create-with-id URL");
         }
         url = link.getHref();
      }
      targetUri = UriBuilderImpl.fromTemplate(url);
   }

   @Override
   public boolean push(ClientMessage message)
   {
      // we initialize lazily just in case target is in same VM
      if (!initialized)
      {
         try
         {
            initialize();
            initialized = true;
         }
         catch (Exception e)
         {
            throw new RuntimeException("Failed to initialize.", e);
         }
      }
      return super.push(message);
   }
}
