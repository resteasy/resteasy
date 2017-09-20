package org.jboss.resteasy.client.jaxrs.internal.proxy;

import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.ClientContext;
import org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.ProcessorFactory;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;
import org.jboss.resteasy.util.FeatureContextDelegate;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.WeightedMediaType;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInvoker implements MethodInvoker
{
   protected String httpMethod;
   protected Method method;
   protected Class<?> declaring;
   protected MediaType[] accepts;
   protected Object[] processors;
   protected ResteasyWebTarget webTarget;
   protected boolean followRedirects;
   @SuppressWarnings("rawtypes")
   protected EntityExtractor extractor;
   protected DefaultEntityExtractorFactory entityExtractorFactory;
   protected ClientConfiguration invokerConfig;


   public ClientInvoker(ResteasyWebTarget parent, Class<?> declaring, Method method, ProxyConfig config)
   {
      // webTarget must be a clone so that it has a cloned ClientConfiguration so we can apply DynamicFeature
      if (method.isAnnotationPresent(Path.class))
      {
         this.webTarget = parent.path(method);
      }
      else
      {
         this.webTarget = parent.clone();
      }
      this.declaring = declaring;
      this.method = method;
      invokerConfig = (ClientConfiguration) this.webTarget.getConfiguration();
      ResourceInfo info = new ResourceInfo()
      {
         @Override
         public Method getResourceMethod()
         {
            return ClientInvoker.this.method;
         }

         @Override
         public Class<?> getResourceClass()
         {
            return ClientInvoker.this.declaring;
         }
      };
      for (DynamicFeature feature : invokerConfig.getDynamicFeatures())
      {
         feature.configure(info, new FeatureContextDelegate(invokerConfig));
      }

      entityExtractorFactory = new DefaultEntityExtractorFactory();
      this.extractor = entityExtractorFactory.createExtractor(method);

      MediaType defaultConsumes = checkConsumesMediaTypes(config);

      this.processors = ProcessorFactory.createProcessors(declaring, method, invokerConfig, defaultConsumes);
      accepts = MediaTypeHelper.getProduces(declaring, method, config.getDefaultProduces());

      checkProducesMediaTypes();
   }

   /**
    * Traverse the parent tree checking for a class associated Consumes annotation.
    * @param config
    * @return
    */
   private MediaType checkConsumesMediaTypes(ProxyConfig config)
   {

      MediaType defaultConsumes = config.getDefaultConsumes();
      if (defaultConsumes == null)
      {
         WeightedMediaType weightedMediaType = null;
         Queue<Class<?>> interfaces = new LinkedList<>();
         for (Class<?> superClass = declaring; superClass != null; superClass = superClass.getSuperclass())
         {
            Collections.addAll(interfaces, superClass.getInterfaces());
            for (Class<?> next = interfaces.poll(); next != null; next = interfaces.poll())
            {
               Consumes annotation = (Consumes) next.getAnnotation(Consumes.class);
               if (annotation != null) {
                  String[] values = annotation.value();
                  for (String value : values)
                  {
                     WeightedMediaType candidateWeightedMediaType = WeightedMediaType.valueOf(value);
                     if (weightedMediaType == null || candidateWeightedMediaType.compareTo(weightedMediaType) < 0) {
                        weightedMediaType = candidateWeightedMediaType;
                     }
                  }
               }
               Collections.addAll(interfaces, next.getInterfaces());
            }
         }
         defaultConsumes = weightedMediaType;
      }

      return defaultConsumes;
   }

   /**
    * Traverse the parent tree checking for a class associated Produces annotation.
    */
   private void checkProducesMediaTypes()
   {
      if (accepts == null)
      {
         Set<WeightedMediaType> weightedMediaTypes = new TreeSet<>();
         Queue<Class<?>> interfaces = new LinkedList<>();
         for (Class<?> superClass = declaring; superClass != null; superClass = superClass.getSuperclass())
         {
            Collections.addAll(interfaces, superClass.getInterfaces());
            for (Class<?> next = interfaces.poll(); next != null; next = interfaces.poll())
            {
               Produces annotation = (Produces) next.getAnnotation(Produces.class);
               if (annotation != null) {
                  String[] values = annotation.value();
                  for (String value : values)
                  {
                     WeightedMediaType weightedMediaType = WeightedMediaType.valueOf(value);
                     weightedMediaTypes.add(weightedMediaType);
                  }
               }
               Collections.addAll(interfaces, next.getInterfaces());
            }
         }

         if (!weightedMediaTypes.isEmpty())
         {
            accepts = weightedMediaTypes.toArray(new WeightedMediaType[weightedMediaTypes.size()]);
         }
      }
   }

   public MediaType[] getAccepts()
   {
      return accepts;
   }

   public Method getMethod()
   {
      return method;
   }

   public Class<?> getDeclaring()
   {
      return declaring;
   }

   public Object invoke(Object[] args)
   {
      ClientInvocation request = createRequest(args);
      ClientResponse response = (ClientResponse)request.invoke();
      ClientContext context = new ClientContext(request, response, entityExtractorFactory);
      return extractor.extractEntity(context);
   }

   protected ClientInvocation createRequest(Object[] args)
   {
	  WebTarget target = this.webTarget;
      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof WebTargetProcessor)
         {
            WebTargetProcessor processor = (WebTargetProcessor)processors[i];
            target = processor.build(target, args[i]);

         }
      }

      ClientConfiguration parentConfiguration=(ClientConfiguration) target.getConfiguration();
      ClientInvocation clientInvocation = new ClientInvocation(this.webTarget.getResteasyClient(), target.getUri(),
    		  new ClientRequestHeaders(parentConfiguration), parentConfiguration);
      if (accepts != null)
      {
         clientInvocation.getHeaders().accept(accepts);
      }
      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof InvocationProcessor)
         {
            InvocationProcessor processor = (InvocationProcessor)processors[i];
            processor.process(clientInvocation, args[i]);

         }
      }
      clientInvocation.setMethod(httpMethod);
      return clientInvocation;
   }

   public String getHttpMethod()
   {
      return httpMethod;
   }

   public void setHttpMethod(String httpMethod)
   {
      this.httpMethod = httpMethod;
   }

   public boolean isFollowRedirects()
   {
      return followRedirects;
   }

   public void setFollowRedirects(boolean followRedirects)
   {
      this.followRedirects = followRedirects;
   }

   public void followRedirects()
   {
      setFollowRedirects(true);
   }
}