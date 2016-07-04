package org.jboss.resteasy.test.validation.i18n;

import java.util.Locale;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 25, 2015
 */
public class TestMessages_en extends TestMessagesAbstract
{  
   @Override
   protected Locale getLocale()
   {
      return new Locale("en");
   }
}
