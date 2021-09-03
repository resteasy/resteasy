package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ProxyBuilderImpl;
import org.jboss.resteasy.test.core.smoke.resource.ResourceWithInterfaceSimpleClient;

@Path("/cl")
public class ClassloaderResource
{

   @GET
   @Path("cl")
   @Produces("text/plain")
   public String test(@QueryParam("param") String targetBase) throws Exception
   {
      ClassLoader orig = Thread.currentThread().getContextClassLoader();
      //create the client...
      ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
      try
      {
         //replace the TCCL with the classloader from the resteasy-client module
         Thread.currentThread().setContextClassLoader(ProxyBuilderImpl.class.getClassLoader());
         //try building the proxy; the TCCL does not have visibility over the deployment classes
         ResourceWithInterfaceSimpleClient proxy = client.target(targetBase)
               .proxyBuilder(ResourceWithInterfaceSimpleClient.class).build();

         return proxy.getBasic();
      }
      finally
      {
         client.close();
         //restore original TCCL
         Thread.currentThread().setContextClassLoader(orig);
      }
   }

}
