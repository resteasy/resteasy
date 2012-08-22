package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Verifications;
import org.jboss.resteasy.annotations.security.doseta.Verify;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractDigitalVerificationHeaderDecorator
{
   protected Verify verify;
   protected Verifications verifications;

   public Verifier create()
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
      return verifier;
   }

   protected Verification createVerification(Verify v)
   {
      Verification verification = new Verification();
      if (v.identifierName() != null && !v.identifierName().trim().equals(""))
         verification.setIdentifierName(v.identifierName());
      if (v.identifierValue() != null && !v.identifierValue().trim().equals(""))
         verification.setIdentifierValue(v.identifierValue());

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
      verification.setBodyHashRequired(v.bodyHashRequired());
      return verification;
   }
}
