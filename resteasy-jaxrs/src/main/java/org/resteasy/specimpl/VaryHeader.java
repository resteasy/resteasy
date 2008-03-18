package org.resteasy.specimpl;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VaryHeader
{
   private boolean accept;
   private boolean acceptLanguage;
   private boolean acceptEncoding;

   public void accept()
   {
      accept = true;
   }

   public void acceptLanguage()
   {
      acceptLanguage = true;
   }

   public void acceptEncoding()
   {
      acceptEncoding = true;
   }

   /**
    * @return null if nothing set
    */
   public String vary()
   {
      String header = null;

      if (accept)
      {
         if (header == null) header = "Accept";
         else header += " Accept";
      }

      if (acceptLanguage)
      {
         if (header == null) header = "Accept-Language";
         else header += " Accept-Language";
      }

      if (acceptEncoding)
      {
         if (header == null) header = "Accept-Encoding";
         else header += " Accept-Encoding";
      }
      return header;
   }
}
