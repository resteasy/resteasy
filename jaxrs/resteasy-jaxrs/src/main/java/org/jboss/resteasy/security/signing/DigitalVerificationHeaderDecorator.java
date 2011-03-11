package org.jboss.resteasy.security.signing;

import org.jboss.resteasy.annotations.interception.HeaderDecoratorPrecedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.annotations.security.signature.After;
import org.jboss.resteasy.annotations.security.signature.Verifications;
import org.jboss.resteasy.annotations.security.signature.Verify;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

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
@HeaderDecoratorPrecedence
public class DigitalVerificationHeaderDecorator implements MessageBodyReaderInterceptor, AcceptedByMethod
{
   protected Verify verify;
   protected Verifications verifications;

   public boolean accept(Class declaring, Method method)
   {
      verify = (Verify) method.getAnnotation(Verify.class);
      verifications = (Verifications) method.getAnnotation(Verifications.class);

      return verify != null || verifications != null;
   }

   @Override
   public Object read(MessageBodyReaderContext context) throws IOException, WebApplicationException
   {
      // Currently we create verifier every time so that the verifications can hold state related to failures
      // todo create a VerifyResult object for each verification.
      Verifier verifier = new Verifier();
      if (verify != null)
      {
         Verification v = createVerification(verify);
         verifier.getVerifications().add(v);
      }
      if (verifications != null)
      {
         for (Verify ver : verifications.value())
         {
            Verification v = createVerification(ver);
            verifier.getVerifications().add(v);
         }
      }
      context.setAttribute(Verifier.class.getName(), verifier);
      return context.proceed();
   }

   protected Verification createVerification(Verify v)
   {
      Verification verification = new Verification();
      if (v.algorithm() != null && !v.algorithm().trim().equals("")) verification.setAlgorithm(v.algorithm());
      if (v.signer() != null && !v.signer().trim().equals("")) verification.setSigner(v.signer());
      if (v.id() != null && !v.id().trim().equals("")) verification.setId(v.id());
      if (v.keyAlias() != null && !v.keyAlias().trim().equals("")) verification.setKeyAlias(v.keyAlias());
      if (verification.getKeyAlias() == null) verification.setAttributeAlias(v.attributeKeyAlias());

      verification.setIgnoreExpiration(v.ignoreExpiration());
      After staleAfter = v.stale();
      if (staleAfter.seconds() > 0
              || staleAfter.minutes() > 0
              || staleAfter.hours() > 0
              || staleAfter.days() > 0
              || staleAfter.months() > 0
              || staleAfter.years() > 0)
      {
         verification.setStaleCheck(true);
         verification.setStaleSeconds(staleAfter.seconds());
         verification.setStaleMinutes(staleAfter.minutes());
         verification.setStaleHours(staleAfter.hours());
         verification.setStaleDays(staleAfter.days());
         verification.setStaleMonths(staleAfter.months());
         verification.setStaleYears(staleAfter.years());
      }
      return verification;
   }

}
