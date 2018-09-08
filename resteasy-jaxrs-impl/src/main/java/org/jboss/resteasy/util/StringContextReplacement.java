package org.jboss.resteasy.util;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import java.util.regex.Pattern;

/**
 * Utility to replace predefined expressions within a string with  values from the HTTP request;
 * <p>
 * ${basepath} - UriInfo.getBaseUri().getRawPath()
 * ${absolutepath} - UriInfo.getAbsolutePath().getRawPath()
 * ${absoluteuri} - UriInfo.getAbsolutePath().toString()
 * ${baseuri} - UriInfo.getBaseUri().toString()
 * ${contextpath} - HttpServletRequest.getContextPath()
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StringContextReplacement
{
   private static final Pattern basepath = Pattern.compile("\\$\\{basepath\\}");
   private static final Pattern absolutepath = Pattern.compile("\\$\\{absolutepath\\}");
   private static final Pattern absoluteUri = Pattern.compile("\\$\\{absoluteuri\\}");
   private static final Pattern baseUri = Pattern.compile("\\$\\{baseuri\\}");
   private static final Pattern contextPath = Pattern.compile("\\$\\{contextpath\\}");

   /**
    * Utility to replace predefined expressions within a string with values from the HTTP request;
    * <p>
    * ${basepath} - UriInfo.getBaseUri().getRawPath()
    * ${absolutepath} - UriInfo.getAbsolutePath().getRawPath()
    * ${absoluteuri} - UriInfo.getAbsolutePath().toString()
    * ${baseuri} - UriInfo.getBaseUri().toString()
    * ${contextpath} - HttpServletRequest.getContextPath()
    *
    * @param original original string
    * @return string with replaced expression
    */
   public static String replace(String original)
   {
      UriInfo uriInfo = ResteasyProviderFactory.getContextData(UriInfo.class);
      if (uriInfo != null)
      {
         String base = uriInfo.getBaseUri().getRawPath();
         String abs = uriInfo.getAbsolutePath().getRawPath();
         String absU = uriInfo.getAbsolutePath().toString();
         String baseU = uriInfo.getBaseUri().toString();

         original = basepath.matcher(original).replaceAll(base);
         original = absolutepath.matcher(original).replaceAll(abs);
         original = absoluteUri.matcher(original).replaceAll(absU);
         original = baseUri.matcher(original).replaceAll(baseU);

      }
      HttpServletRequest request = ResteasyProviderFactory.getContextData(HttpServletRequest.class);
      if (request != null)
      {
         original = contextPath.matcher(original).replaceAll(request.getContextPath());

      }
      return original;
   }

}
