package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.security.doseta.Verifications;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.client.ClientResponse;
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
public class DigitalVerificationHeaderDecoratorClientExecutionInterceptor extends AbstractDigitalVerificationHeaderDecorator implements ClientExecutionInterceptor, AcceptedByMethod
{

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of interceptor : org.jboss.resteasy.security.doseta.DigitalVerificationHeaderDecoratorClientExecutionInterceptor , method call : accept .")
   public boolean accept(Class declaring, Method method)
   {
      verify = (Verify) method.getAnnotation(Verify.class);
      verifications = (Verifications) method.getAnnotation(Verifications.class);

      return verify != null || verifications != null;
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of interceptor : org.jboss.resteasy.security.doseta.DigitalVerificationHeaderDecoratorClientExecutionInterceptor , method call : execute .")
   public ClientResponse execute(ClientExecutionContext ctx) throws Exception
   {
      ClientResponse response = ctx.proceed();
      response.getAttributes().put(Verifier.class.getName(), create());

      return response;
   }

}
