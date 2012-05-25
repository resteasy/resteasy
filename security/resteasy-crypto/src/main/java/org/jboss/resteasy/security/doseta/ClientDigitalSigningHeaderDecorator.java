package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@HeaderDecoratorPrecedence
public class ClientDigitalSigningHeaderDecorator extends DigitalSigningHeaderDecorator implements ClientExecutionInterceptor
{

   @Override
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      KeyRepository repository = (KeyRepository) ctx.getRequest().getAttributes().get(KeyRepository.class.getName());
      if (repository == null)
      {
         repository = ResteasyProviderFactory.getContextData(KeyRepository.class);
      }
      DKIMSignature header = createHeader(repository);
      ctx.getRequest().header(DKIMSignature.DKIM_SIGNATURE, header);
      return ctx.proceed();
   }

}
