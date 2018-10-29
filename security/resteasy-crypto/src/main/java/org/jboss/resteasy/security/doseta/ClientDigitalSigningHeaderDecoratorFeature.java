package org.jboss.resteasy.security.doseta;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.core.ResteasyContext;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.CLIENT)
public class ClientDigitalSigningHeaderDecoratorFeature implements DynamicFeature
{
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
   {
      Signed signed = resourceInfo.getResourceMethod().getAnnotation(Signed.class);
      if (signed == null)
      {
         signed = (Signed) resourceInfo.getResourceClass().getAnnotation(Signed.class);
      }
      if (signed == null) return;

      configurable.register(new DigitalSigningHeaderDecorator(signed));
   }

   /**
    * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
    * @version $Revision: 1 $
    */
   @Priority(Priorities.HEADER_DECORATOR)
   public static class DigitalSigningHeaderDecorator extends AbstractDigitalSigningHeaderDecorator implements ClientRequestFilter
   {
      public DigitalSigningHeaderDecorator(final Signed signed)
      {
         this.signed = signed;
      }

      @Override
      public void filter(ClientRequestContext requestContext) throws IOException
      {
         KeyRepository repository = (KeyRepository) requestContext.getProperty(KeyRepository.class.getName());
         if (repository == null)
         {
            repository = ResteasyContext.getContextData(KeyRepository.class);
         }
         DKIMSignature header = createHeader(repository);
         requestContext.getHeaders().add(DKIMSignature.DKIM_SIGNATURE, header);
      }

   }
}
