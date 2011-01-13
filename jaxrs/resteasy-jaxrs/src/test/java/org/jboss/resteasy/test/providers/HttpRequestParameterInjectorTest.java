package org.jboss.resteasy.test.providers;

import static org.jboss.resteasy.test.TestPortProvider.createClientRequest;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.FindAnnotation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HttpRequestParameterInjectorTest extends BaseResourceTest
{
   @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   public @interface ClassicParam
   {
      String value();
   }

   @Path("/foo")
   public static class RequestParamResource
   {
      @GET
      @POST
      @Produces("text/plain")
      public String get(@ClassicParam("param") String param)
      {
         return param;
      }
   }

   @Before
   public void setUp() throws Exception
   {
      getProviderFactory().registerProvider(ParamInjectorFactoryImpl.class);
      getRegistry().addPerRequestResource(RequestParamResource.class);
   }

   @Test
   public void testCustomInjectorFactory() throws Exception
   {
      String getResult = createClientRequest("/foo").queryParameter("param", "getValue").accept(
            "text/plain").get(String.class).getEntity();
      Assert.assertEquals("getValue", getResult);

      String postResult = createClientRequest("/foo").queryParameter("param", "postValue").accept(
            "text/plain").post(String.class).getEntity();
      Assert.assertEquals("postValue", postResult);
   }

   public static class ParamInjectorFactoryImpl extends InjectorFactoryImpl
   {
      public ParamInjectorFactoryImpl(ResteasyProviderFactory factory)
      {
         super(factory);
      }

      @SuppressWarnings("unchecked")
      @Override
      public ValueInjector createParameterExtractor(Class injectTargetClass,
            AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations)
      {
         final ClassicParam param = FindAnnotation.findAnnotation(annotations, ClassicParam.class);
         if (param == null)
         {
            return super.createParameterExtractor(injectTargetClass, injectTarget, type,
                  genericType, annotations);
         }
         else
         {
            return new ValueInjector()
            {
               public Object inject(HttpRequest request, HttpResponse response)
               {
                  return ResteasyProviderFactory.getContextData(HttpServletRequest.class)
                        .getParameter(param.value());
               }

               public Object inject()
               {
                  // do nothing.
                  return null;
               }
            };
         }
      }
   };
}
