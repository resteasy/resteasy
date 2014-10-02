package org.jboss.resteasy.testi18n;

import java.util.Locale;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * 
 * Copyright Sep 26, 2014
 */
public class TestLogMessages_xx_YY extends TestLogMessages_Abstract
{
   @Override
   protected Locale getLocale()
   {
      return new Locale("xx", "YY");
   }

   @Override
   protected String failedExecuting()
   {
      return "fallito in esecuzione";
   }

   @Override
   protected String couldNotDeleteFile()
   {
      return "Impossibile eliminare il file '%s' per la richiesta:";
   }

   @Override
   protected String deploying()
   {
      return "Distribuzione de ";
   }

   @Override
   protected String creatingContextObject()
   {
      return "Creazione dello oggetto di contesto <%s : %s>";
   }  
}
