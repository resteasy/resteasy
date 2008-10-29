package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.FormParam;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormParamInjector extends StringParameterInjector implements ValueInjector
{

   public FormParamInjector(Class type, Type genericType, AccessibleObject target, String header, String defaultValue, ResteasyProviderFactory factory)
   {
      super(type, genericType, header, FormParam.class, defaultValue, target, factory);
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      List<String> list = request.getDecodedFormParameters().get(paramName);
      return extractValues(list);
   }

   public Object inject()
   {
      throw new RuntimeException("It is illegal to inject a @FormParam into a singleton");
   }
}