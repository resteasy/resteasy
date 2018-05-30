package org.jboss.resteasy.core;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.PathSegment;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamInjector extends StringParameterInjector implements ValueInjector
{
   private boolean encode;
   
   public MatrixParamInjector(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue, boolean encode, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      super(type, genericType, paramName, MatrixParam.class, defaultValue, target, annotations, factory);
      this.encode = encode;
   }

   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      ArrayList<String> values = new ArrayList<String>();
      if (encode)
      {
         for (PathSegment segment : request.getUri().getPathSegments(false))
         {
            List<String> list = segment.getMatrixParameters().get(paramName);
            if (list != null) values.addAll(list);
         }
      }
      else
      {
         for (PathSegment segment : request.getUri().getPathSegments())
         {
            List<String> list = segment.getMatrixParameters().get(paramName);
            if (list != null) values.addAll(list);
         }
      }
      if (values.size() == 0) return CompletableFuture.completedFuture(extractValues(null));
      else return CompletableFuture.completedFuture(extractValues(values));
   }

   @Override
   protected void throwProcessingException(String message, Throwable cause)
   {
      throw new NotFoundException(message, cause);

   }

   @Override
   public CompletionStage<Object> inject(boolean unwrapAsync)
   {
      throw new RuntimeException(Messages.MESSAGES.illegalToInjectMatrixParam());
   }
}