package org.jboss.resteasy.resteasy719;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.plugins.hibernatevalidator.i18n.Messages;

@Path("/")
public class TestResource
{
   @Context ServletContext context;

   @POST
   @Path("setLocale/{language}/{country}")
   public void setLocale(@PathParam("language") String language, @PathParam("country") String country)
   {
      System.out.println("default locale: " + Locale.getDefault());
      Locale.getDefault();
      if (country == null)
      {
         Locale locale = new Locale(language);
         Locale.setDefault(locale);
         System.out.println("Set defaule locale to " + locale);
      }
      else
      {
         Locale locale = new Locale(language, country);
         Locale.setDefault(locale);
         System.out.println("Set defaule locale to " + locale);
      }
   }
   
   @POST
   @Path("testLocale")
   public String testLocale() throws IOException
   {
      System.out.println("testLocale(): default locale: " + Locale.getDefault());
      return Messages.MESSAGES.validatorCannotBeNull();
   }
}
