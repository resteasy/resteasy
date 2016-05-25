package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.security.doseta.Verifications;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Priorities;
import javax.ws.rs.QueryParam;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class ServerDigitalVerificationHeaderDecoratorFeature implements DynamicFeature
{
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
   {
      Verify verify = resourceInfo.getResourceMethod().getAnnotation(Verify.class);
      Verifications verifications = resourceInfo.getResourceClass().getAnnotation(Verifications.class);

      resourceInfo.getResourceMethod();
      if (verify != null || verifications != null)
      {
         configurable.register(new DigitalVerificationHeaderDecorator(verify, verifications, hasEntityParameter(resourceInfo.getResourceMethod())));
      }

   }

   @Priority(Priorities.HEADER_DECORATOR)
   public static class DigitalVerificationHeaderDecorator extends AbstractDigitalVerificationHeaderDecorator implements ContainerRequestFilter
   {
      protected boolean hasEntityParameter;
      
      public DigitalVerificationHeaderDecorator(Verify verify, Verifications verifications, boolean hasEntityParameter)
      {
         this.verify = verify;
         this.verifications = verifications;
         this.hasEntityParameter = hasEntityParameter;
      }

      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         if (hasEntityParameter)
         {
            requestContext.setProperty(Verifier.class.getName(), create());
         }
         else
         {
            verify(requestContext, create());
         }
      }

   }
   
   static boolean hasEntityParameter(Method method)
   {
      Annotation[][] annotations = method.getParameterAnnotations();
      for (int i = 0; i < annotations.length; i++)
      {
         boolean match = false;
         for (int j = 0; j < annotations[i].length; j++)
         {
            if (annotations[i][j].annotationType().equals(MatrixParam.class)
             || annotations[i][j].annotationType().equals(QueryParam.class)
             || annotations[i][j].annotationType().equals(PathParam.class)
             || annotations[i][j].annotationType().equals(CookieParam.class)
             || annotations[i][j].annotationType().equals(HeaderParam.class)
             || annotations[i][j].annotationType().equals(Context.class)
             || annotations[i][j].annotationType().equals(FormParam.class))
            {
               match = true;
               break;
            }
         }
         if (!match)
         {
            return true;
         }
      }
      return false;
   }
   
   static protected void verify(ContainerRequestContext context, Verifier verifier)
   {
      MultivaluedMap<String, String> headers = context.getHeaders();
      List<String> strings = headers.get(DKIMSignature.DKIM_SIGNATURE);
      if (strings == null)
      {
         throw new UnauthorizedSignatureException(Messages.MESSAGES.thereWasNoSignatureHeader(DKIMSignature.DKIM_SIGNATURE));
      }

      List<DKIMSignature> signatures = new ArrayList<DKIMSignature>();
      for (String headerVal : strings)
      {
         try
         {
            signatures.add(new DKIMSignature(headerVal));
         }
         catch (Exception e)
         {
            throw new UnauthorizedSignatureException(Messages.MESSAGES.malformedSignatureHeader(DKIMSignature.DKIM_SIGNATURE));
         }
      }

      if (verifier.getRepository() == null)
      {
         KeyRepository repository = (KeyRepository) context.getProperty(KeyRepository.class.getName());
         if (repository == null)
         {
            repository = ResteasyProviderFactory.getContextData(KeyRepository.class);
         }
         verifier.setRepository(repository);
      }

      VerificationResults results = verifier.verify(signatures, headers, null);
      if (results.isVerified() == false)
      {
         throw new UnauthorizedSignatureException(results);
      }
   }
}
