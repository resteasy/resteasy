package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.annotations.interception.ClientInterceptor;
import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.annotations.security.signature.After;
import org.jboss.resteasy.annotations.security.signature.Signed;
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
      ContentSignatures signatures = new ContentSignatures();
      if (context.getHeaders().containsKey("Content-Signature"))
      {
         Object obj = context.getHeaders().getFirst("Content-Signature");
         if (obj instanceof ContentSignatures)
         {
            signatures = (ContentSignatures) obj;
         }
         else
         {
            context.proceed();
            return;
         }
      }
      else
      {
         context.getHeaders().putSingle("Content-Signature", signatures);
      }
      ContentSignature header = signatures.addNew();
      if (signed.useKey() != null && !signed.useKey().equals(""))
      {
         header.setKeyAlias(signed.useKey());
      }
      if (signed.signer() != null && !signed.signer().equals("")) header.setSigner(signed.signer(), true, true);
      if (signed.algorithm() != null && !signed.algorithm().equals(""))
         header.setAlgorithm(signed.algorithm(), true, true);
      if (signed.id() != null && !signed.id().equals("")) header.setAlgorithm(signed.id(), true, true);
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


      context.proceed();
      return;
   }

}
