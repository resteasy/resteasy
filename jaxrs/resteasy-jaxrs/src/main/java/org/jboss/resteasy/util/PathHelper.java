/**
 *
 */
package org.jboss.resteasy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A utility class for handling URI template parameters. As the Java
 * regulare expressions package does not handle named groups, this
 * class attempts to simulate that functionality by using groups.
 *
 * @author Ryan J. McDonough
 * @since 1.0
 *        Nov 8, 2006
 */
public class PathHelper
{
   /**
    * The default regex group for URI template parameters
    */
   public static final String DEFAULT_URI_PARAM_PATTERN = "([^}]+)";

   /**
    * A regex pattern that searches for a URI template parameter in the form of {someId}
    */
   public static final Pattern URI_TEMPLATE_PATTERN = Pattern.compile("(\\{([^}]+)\\})");

   public static final String URI_TEMPLATE_REPLACE_PATTERN = "(.*?)";

   public static String createRegularExpressionFromPathExpression(String path)
   {
      Matcher matcher = URI_TEMPLATE_PATTERN.matcher(path);
      StringBuffer regex = new StringBuffer();
      while (matcher.find())
      {
         matcher.appendReplacement(regex, Matcher.quoteReplacement("(.*?)"));
      }
      matcher.appendTail(regex);
      return regex.append("$").toString();

   }

   public static String getEncodedPathInfo(String path, String contextPath)
   {
      if (contextPath != null && !"".equals(contextPath))
      {
         path = path.substring(contextPath.length());
      }
      return path;

   }


}