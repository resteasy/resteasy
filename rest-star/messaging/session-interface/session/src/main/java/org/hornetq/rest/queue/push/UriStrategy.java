package org.hornetq.rest.queue.push;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.core.logging.Logger;
import org.hornetq.rest.queue.push.xml.BasicAuth;
import org.hornetq.rest.queue.push.xml.PushRegistration;
import org.hornetq.rest.queue.push.xml.XmlHttpHeader;
import org.hornetq.rest.util.HttpMessageHelper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

import javax.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriStrategy implements PushStrategy
{
   private static final Logger log = Logger.getLogger(UriStrategy.class);
   protected HttpClient client = new HttpClient();
   protected ApacheHttpClientExecutor executor = new ApacheHttpClientExecutor(client);
   protected PushRegistration registration;
   protected UriBuilder targetUri;
   protected String method;
   protected String contentType;

   @Override
   public void setRegistration(PushRegistration reg)
   {
      this.registration = reg;
   }

   public void start() throws Exception
   {
      initAuthentication();
      method = registration.getTarget().getMethod();
      if (method == null) method = "POST";
      contentType = registration.getTarget().getType();
      targetUri = UriBuilderImpl.fromTemplate(registration.getTarget().getHref());
   }

   protected void initAuthentication()
   {
      if (registration.getAuthenticationMechanism() != null)
      {
         if (registration.getAuthenticationMechanism().getType() instanceof BasicAuth)
         {
            BasicAuth basic = (BasicAuth) registration.getAuthenticationMechanism().getType();
            //log.info("Setting Basic Auth: " + basic.getUsername());
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(
                    //new AuthScope(null, 8080, "Test"),
                    new AuthScope(AuthScope.ANY),
                    new UsernamePasswordCredentials(basic.getUsername(), basic.getPassword())
            );
         }
      }
   }

   @Override
   public void stop()
   {
   }

   @Override
   public boolean push(ClientMessage message)
   {
      String uri = createUri(message);
      for (int i = 0; i < 3; i++)
      {
         int wait = 0;
         ClientRequest request = executor.createRequest(uri);
         request.followRedirects(false);

         for (XmlHttpHeader header : registration.getHeaders())
         {
            request.header(header.getName(), header.getValue());
         }
         HttpMessageHelper.buildMessage(message, request, contentType);
         ClientResponse res = null;
         try
         {
            log.debug(method + " " + uri);
            res = request.httpMethod(method);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
         if (res.getStatus() == 503)
         {
            String retryAfter = (String) res.getHeaders().getFirst("Retry-After");
            if (retryAfter != null)
            {
               wait = Integer.parseInt(retryAfter);
            }
         }
         else if (res.getStatus() == 307)
         {
            uri = res.getLocation().getHref();
         }
         else if ((res.getStatus() >= 200 && res.getStatus() < 299) || res.getStatus() == 303 || res.getStatus() == 304)
         {
            log.debug("Success");
            return true;
         }
         else
         {
            throw new RuntimeException("failed to push message to: " + uri + " status code: " + res.getStatus());
         }
      }
      return false;
   }

   protected String createUri(ClientMessage message)
   {
      String uri = targetUri.build().toString();
      return uri;
   }
}
