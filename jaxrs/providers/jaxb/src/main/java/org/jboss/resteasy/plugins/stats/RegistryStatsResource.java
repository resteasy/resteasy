package org.jboss.resteasy.plugins.stats;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/resteasy/registry")
public class RegistryStatsResource
{
   @GET
   @Produces({"application/xml", "application/json"})
   public RegistryData get() throws JAXBException
   {
      ResourceMethodRegistry registry = (ResourceMethodRegistry) ResteasyProviderFactory.getContextData(Registry.class);

      RegistryData data = new RegistryData();

      for (String key : registry.getBounded().keySet())
      {
         List<ResourceInvoker> invokers = registry.getBounded().get(key);

         RegistryEntry entry = new RegistryEntry();
         data.getEntries().add(entry);
         entry.setUriTemplate(key);

         for (ResourceInvoker invoker : invokers)
         {
            if (invoker instanceof ResourceMethodInvoker)
            {
               ResourceMethodInvoker rm = (ResourceMethodInvoker) invoker;
               for (String httpMethod : rm.getHttpMethods())
               {
                  ResourceMethodEntry method = null;
                  if (httpMethod.equals("GET")) method = new GetResourceMethod();
                  else if (httpMethod.equals("PUT")) method = new PutResourceMethod();
                  else if (httpMethod.equals("DELETE")) method = new DeleteResourceMethod();
                  else if (httpMethod.equals("POST")) method = new PostResourceMethod();
                  else if (httpMethod.equals("OPTIONS")) method = new OptionsResourceMethod();
                  else if (httpMethod.equals("TRACE")) method = new TraceResourceMethod();
                  else if (httpMethod.equals("HEAD")) method = new HeadResourceMethod();

                  method.setClazz(rm.getResourceClass().getName());
                  method.setMethod(rm.getMethod().getName());
                  AtomicLong stat = rm.getStats().get(httpMethod);
                  if (stat != null) method.setInvocations(stat.longValue());
                  else method.setInvocations(0);

                  if (rm.getProduces() != null)
                  {
                     for (MediaType mediaType : rm.getProduces())
                     {
                        method.getProduces().add(mediaType.toString());
                     }
                  }
                  if (rm.getConsumes() != null)
                  {
                     for (MediaType mediaType : rm.getConsumes())
                     {
                        method.getConsumes().add(mediaType.toString());
                     }
                  }
                  entry.getMethods().add(method);

               }

            }
            else
            {
               ResourceLocatorInvoker rl = (ResourceLocatorInvoker) invoker;
               SubresourceLocator locator = new SubresourceLocator();
               locator.setClazz(rl.getMethod().getDeclaringClass().getName());
               locator.setMethod(rl.getMethod().getName());
               entry.setLocator(locator);
            }

         }

      }

      return data;
   }
}
