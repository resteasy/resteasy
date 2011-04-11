package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ServerInterceptor
@ClientInterceptor
@HeaderDecoratorPrecedence
public class DigitalSigningHeaderDecorator implements MessageBodyWriterInterceptor, AcceptedByMethod
{
   protected Signed signed;

   public boolean accept(Class declaring, Method method)
   {
      signed = method.getAnnotation(Signed.class);
      if (signed == null)
      {
         signed = (Signed) declaring.getAnnotation(Signed.class);
      }
      return signed != null;
   }

   public void write(MessageBodyWriterContext context) throws IOException, WebApplicationException
   {
      DosetaSignature header = new DosetaSignature();
      if (signed.keyAlias() != null && !signed.keyAlias().equals(""))
      {
         header.setKeyAlias(signed.keyAlias());
      }
      if (signed.domain() != null && !signed.domain().equals(""))
      {
         header.setDomainIdentity(signed.domain());
      }
      if (signed.algorithm() != null && !signed.algorithm().equals(""))
      {
         header.setAlgorithm(signed.algorithm());
      }
      if (signed.timestamped())
      {
         header.setTimestamp();
      }

      After after = signed.expires();
      if (after.seconds() > 0
              || after.minutes() > 0
              || after.hours() > 0
              || after.days() > 0
              || after.months() > 0
              || after.years() > 0)
      {
         header.setExpiration(after.seconds(), after.minutes(), after.hours(), after.days(), after.months(), after.years());
      }

      context.getHeaders().add(DosetaSignature.DOSETA_SIGNATURE, header);
      context.proceed();
      return;
   }

}
