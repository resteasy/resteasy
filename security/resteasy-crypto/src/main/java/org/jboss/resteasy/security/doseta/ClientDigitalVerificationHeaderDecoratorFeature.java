package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.security.doseta.Verifications;
import org.jboss.resteasy.annotations.security.doseta.Verify;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ConstrainedTo(RuntimeType.CLIENT)
public class ClientDigitalVerificationHeaderDecoratorFeature implements DynamicFeature
{
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
   {
      Verify verify = resourceInfo.getResourceMethod().getAnnotation(Verify.class);
      Verifications verifications = resourceInfo.getResourceClass().getAnnotation(Verifications.class);

      if (verify != null || verifications != null)
      {
         configurable.register(new DigitalVerificationHeaderDecorator(verify, verifications));
      }

   }

   @Priority(Priorities.HEADER_DECORATOR)
   public static class DigitalVerificationHeaderDecorator extends AbstractDigitalVerificationHeaderDecorator implements ClientResponseFilter
   {
      public DigitalVerificationHeaderDecorator(Verify verify, Verifications verifications)
      {
         this.verify = verify;
         this.verifications = verifications;
      }

      @Override
      public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
      {
         requestContext.setProperty(Verifier.class.getName(), create());
      }

   }
}
