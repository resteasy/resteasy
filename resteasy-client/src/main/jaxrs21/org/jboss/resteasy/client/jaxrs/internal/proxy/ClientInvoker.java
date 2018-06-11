package org.jboss.resteasy.client.jaxrs.internal.proxy;

import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.ClientContext;
import org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.EntityExtractor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.ProcessorFactory;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;
import org.jboss.resteasy.util.FeatureContextDelegate;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.RxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientInvoker implements MethodInvoker
{
   protected String httpMethod;
   protected Method method;
   protected Class<?> declaring;
   protected MediaType accepts;
   protected Object[] processors;
   protected ResteasyWebTarget webTarget;
   protected boolean followRedirects;
   @SuppressWarnings("rawtypes")
   protected EntityExtractor extractor;
   protected DefaultEntityExtractorFactory entityExtractorFactory;
   protected ClientConfiguration invokerConfig;
   protected RxInvokerProvider<?> rxInvokerProvider;
   protected SyncInvoker syncInvoker;


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


      this.processors = ProcessorFactory.createProcessors(declaring, method, invokerConfig, config.getDefaultConsumes());
      accepts = MediaTypeHelper.getProduces(declaring, method, config.getDefaultProduces());
      entityExtractorFactory = new DefaultEntityExtractorFactory();
      this.extractor = entityExtractorFactory.createExtractor(method);
      rxInvokerProvider = invokerConfig.getRxInvokerProviderFromReactiveClass(method.getReturnType());
   }

   public MediaType getAccepts()
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
      return rxInvokerProvider != null ? invokeAsync(args) : invokeSync(args);
    }
   
   protected Object invokeAsync(final Object[] args)
   {
      ClientInvocationBuilder builder = (ClientInvocationBuilder) webTarget.request();
      ClientInvocation request = createRequest(args);
      builder.setInvocation(request);
      ExecutorService executor = webTarget.getResteasyClient().getScheduledExecutor();
      if (executor == null)
      {
         executor = webTarget.getResteasyClient().asyncInvocationExecutor();         
      }
      RxInvoker<?> rxInvoker = (RxInvoker<?>) rxInvokerProvider.getRxInvoker(builder, executor);
      Type type = method.getGenericReturnType();
      if (type instanceof ParameterizedType)
      {
         type = ((ParameterizedType) type).getActualTypeArguments()[0];
      }
      GenericType<?> gt = new GenericType(type);
      Object e = request.getEntity();
      Object o = null;
      if (e != null)
      {
         o = rxInvoker.method(getHttpMethod(), Entity.entity(e, request.getHeaders().getMediaType()), gt);
      }
      else
      {
         o = rxInvoker.method(getHttpMethod(), gt);
      }
      return o;
   }
       
   protected Object invokeSync(Object[] args)
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

      ClientInvocationBuilder builder = null;
      if (accepts != null)
      {
         builder = (ClientInvocationBuilder)target.request(accepts);
      }
      else
      {
         builder = (ClientInvocationBuilder)target.request();
      }

      for (int i = 0; i < processors.length; i++)
      {
         if (processors != null && processors[i] instanceof InvocationProcessor)
         {
            InvocationProcessor processor = (InvocationProcessor)processors[i];
            processor.process(builder, args[i]);

         }
      }
      ClientInvocation clientInvocation = (ClientInvocation)builder.build(httpMethod);
      clientInvocation.setClientInvoker(this);
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
   
   public SyncInvoker getSyncInvoker()
   {
      return syncInvoker;
   }
   
   public void setSyncInvoker(SyncInvoker syncInvoker)
   {
      this.syncInvoker = syncInvoker;
   }
}