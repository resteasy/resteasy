package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.FormParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormParamInjector extends StringParameterInjector implements ValueInjector
{
   private boolean encode;
   
   public FormParamInjector(Class type, Type genericType, AccessibleObject target, String header, String defaultValue, boolean encode, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      super(type, genericType, header, FormParam.class, defaultValue, target, annotations, factory);
      this.encode = encode;
   }
   
   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      List<String> list = request.getDecodedFormParameters().get(paramName);
      if (list == null)
      {
         // FIXME: looks like a bug, no?
         extractValues(null);
      }
      else if (encode)
      {
         List<String> encodedList = new ArrayList<String>();
         for (String s : list)
         {
            encodedList.add(URLEncoder.encode(s));
         }
         list = encodedList;
      }
      return CompletableFuture.completedFuture(extractValues(list));
   }

   @Override
   public CompletionStage<Object> inject(boolean unwrapAsync)
   {
      throw new RuntimeException(Messages.MESSAGES.illegalToInjectFormParam());
   }
}