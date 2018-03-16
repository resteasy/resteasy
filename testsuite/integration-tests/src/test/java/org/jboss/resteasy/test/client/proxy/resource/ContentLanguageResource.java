package org.jboss.resteasy.test.client.proxy.resource;

public class ContentLanguageResource implements ContentLanguageInterface
{

   public String contentLang1(String contentLanguage, String subject)
   {
      return contentLanguage + subject;
   }

   public String contentLang2(String subject, String contentLanguage)
   {
      return subject + contentLanguage;

   }

}
