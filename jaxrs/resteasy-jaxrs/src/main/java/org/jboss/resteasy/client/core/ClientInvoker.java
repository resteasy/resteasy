package org.jboss.resteasy.client.core;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyConfig;
import org.jboss.resteasy.client.core.extractors.ClientErrorHandler;
import org.jboss.resteasy.client.core.extractors.ClientRequestContext;
import org.jboss.resteasy.client.core.extractors.EntityExtractor;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.client.core.marshallers.ClientMarshallerFactory;
import org.jboss.resteasy.client.core.marshallers.Marshaller;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.MediaTypeHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Providers;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ClientInvoker extends ClientInterceptorRepositoryImpl implements MethodInvoker
{
   protected ResteasyProviderFactory providerFactory;
   protected String httpMethod;
   protected ResteasyUriBuilder uri;
   protected Method method;
   protected Class declaring;
   protected MediaType accepts;
   protected Marshaller[] marshallers;
   protected ClientExecutor executor;
   protected boolean followRedirects;
   protected EntityExtractor extractor;
   protected EntityExtractorFactory extractorFactory;
   protected URI baseUri;
   protected Map<String, Object> attributes = new HashMap<String, Object>();


   public ClientInvoker(URI baseUri, Class declaring, Method method, ResteasyProviderFactory providerFactory, ClientExecutor executor, EntityExtractorFactory extractorFactory)
   {
	   this(baseUri, declaring, method, new ProxyConfig(null, executor, providerFactory, extractorFactory, null, null, null));
   }
   
   public ClientInvoker(URI baseUri, Class declaring, Method method, ProxyConfig config)
   {
      this.declaring = declaring;
      this.method = method;
      this.marshallers = ClientMarshallerFactory.createMarshallers(declaring, method, providerFactory, config.getServerConsumes());
      this.providerFactory = config.getProviderFactory();
      this.executor = config.getExecutor();
      accepts = MediaTypeHelper.getProduces(declaring, method, config.getServerProduces());
      this.uri = new ResteasyUriBuilder();
      this.baseUri = baseUri;
      uri.uri(baseUri);
      if (declaring.isAnnotationPresent(Path.class)) uri.path(declaring);
      if (method.isAnnotationPresent(Path.class)) uri.path(method);
      this.extractorFactory = config.getExtractorFactory();
      this.extractor = extractorFactory.createExtractor(method);
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   public MediaType getAccepts()
   {
      return accepts;
   }

   public Method getMethod()
   {
      return method;
   }

   public Class getDeclaring()
   {
      return declaring;
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return providerFactory;
   }

   public Object invoke(Object[] args)
   {
      boolean isProvidersSet = ResteasyProviderFactory.getContextData(Providers.class) != null;
      if (!isProvidersSet) ResteasyProviderFactory.pushContext(Providers.class, providerFactory);

      try
      {
         if (uri == null) throw new RuntimeException(Messages.MESSAGES.baseURINotSetForClientProxy());
         

         ClientRequest request = createRequest(args);

         BaseClientResponse clientResponse = null;
         try
         {
            clientResponse = (BaseClientResponse) request.httpMethod(httpMethod);
         }
         catch (Exception e)
         {
            ClientExceptionMapper<Exception> mapper = providerFactory.getClientExceptionMapper(Exception.class);
            if (mapper != null)
            {
               throw mapper.toException(e);
            }
            throw new RuntimeException(e);
         }
         ClientErrorHandler errorHandler = new ClientErrorHandler(providerFactory.getClientErrorInterceptors());
         clientResponse.setAttributeExceptionsTo(method.toString());
         clientResponse.setAnnotations(method.getAnnotations());
         ClientRequestContext clientRequestContext = new ClientRequestContext(request, clientResponse, errorHandler, extractorFactory, baseUri);
         return extractor.extractEntity(clientRequestContext);
      }
      finally
      {
         if (!isProvidersSet) ResteasyProviderFactory.popContextData(Providers.class);
      }
   }

   protected ClientRequest createRequest(Object[] args)
   {
      ClientRequest request = new ClientRequest(uri, executor, providerFactory);
      request.getAttributes().putAll(attributes);
      if (accepts != null) request.header(HttpHeaders.ACCEPT, accepts.toString());
      this.copyClientInterceptorsTo(request);

      boolean isClientResponseResult = ClientResponse.class.isAssignableFrom(method.getReturnType());
      request.followRedirects(!isClientResponseResult || this.followRedirects);

      for (int i = 0; i < marshallers.length; i++)
      {
         marshallers[i].build(request, args[i]);
      }
      return request;
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