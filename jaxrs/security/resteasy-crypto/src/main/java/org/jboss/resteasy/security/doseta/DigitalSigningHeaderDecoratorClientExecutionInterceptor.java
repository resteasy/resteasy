package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.security.doseta.i18n.*;

import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
@Provider
@HeaderDecoratorPrecedence
public class DigitalSigningHeaderDecoratorClientExecutionInterceptor extends AbstractDigitalSigningHeaderDecorator implements ClientExecutionInterceptor, AcceptedByMethod
{

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of interceptor : org.jboss.resteasy.security.doseta.DigitalSigningHeaderDecoratorClientExecutionInterceptor , method call : accept .")
   public boolean accept(Class declaring, Method method)
   {
      signed = method.getAnnotation(Signed.class);
      if (signed == null)
      {
         signed = (Signed) declaring.getAnnotation(Signed.class);
      }

      return signed != null;
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of interceptor : org.jboss.resteasy.security.doseta.DigitalSigningHeaderDecoratorClientExecutionInterceptor , method call : execute .")
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
