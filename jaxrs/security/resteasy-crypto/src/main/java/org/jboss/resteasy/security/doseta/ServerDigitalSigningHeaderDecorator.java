package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@HeaderDecoratorPrecedence
public class ServerDigitalSigningHeaderDecorator extends DigitalSigningHeaderDecorator implements PostProcessInterceptor
{

   @Override
   public void postProcess(ServerResponse response)
   {
      KeyRepository repository = ResteasyProviderFactory.getContextData(KeyRepository.class);
      DKIMSignature header = createHeader(repository);
      response.getMetadata().add(DKIMSignature.DKIM_SIGNATURE, header);
   }

}
