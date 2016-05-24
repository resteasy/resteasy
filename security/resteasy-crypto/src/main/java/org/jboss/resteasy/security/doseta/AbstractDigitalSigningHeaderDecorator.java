package org.jboss.resteasy.security.doseta;

import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AbstractDigitalSigningHeaderDecorator
{
   protected Signed signed;

   protected DKIMSignature createHeader(KeyRepository repository)
   {
      DKIMSignature header = new DKIMSignature();
      if (signed.domain() != null && !signed.domain().equals(""))
      {
         header.setDomain(signed.domain());
      }
      else
      {
         if (repository != null)
         {
            header.setDomain(repository.getDefaultPrivateDomain());
         }
      }
      if (signed.algorithm() != null && !signed.algorithm().equals(""))
      {
         header.setAlgorithm(signed.algorithm());
      }
      if (signed.selector() != null && !signed.selector().equals(""))
      {
         header.setSelector(signed.selector());
      }
      else
      {
         if (repository != null)
         {
            header.setSelector(repository.getDefaultPrivateSelector());
         }

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
      return header;
   }
}
