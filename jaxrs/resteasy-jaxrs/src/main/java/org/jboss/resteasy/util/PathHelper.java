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
 * @author Bill Burke
 * @since 1.0
 *        Nov 8, 2006
 */
public class PathHelper
{
   public static final String URI_PARAM_NAME_REGEX = "\\w[\\w\\.-]*";
   public static final String URI_PARAM_REGEX_REGEX = "[^{}][^{}]*";
   public static final String URI_PARAM_REGEX = "\\{\\s*(" + URI_PARAM_NAME_REGEX + ")\\s*(:\\s*(" + URI_PARAM_REGEX_REGEX + "))?\\}";
   public static final String URI_PARAM_WITH_REGEX = "\\{\\s*(" + URI_PARAM_NAME_REGEX + ")\\s*(:\\s*(" + URI_PARAM_REGEX_REGEX + "))\\}";
   public static final String URI_PARAM_WITHOUT_REGEX = "\\{(" + URI_PARAM_NAME_REGEX + ")\\}";
   public static final Pattern URI_PARAM_PATTERN = Pattern.compile(URI_PARAM_REGEX);
   public static final Pattern URI_PARAM_WITH_REGEX_PATTERN = Pattern.compile(URI_PARAM_WITH_REGEX);
   public static final Pattern URI_PARAM_WITHOUT_REGEX_PATTERN = Pattern.compile(URI_PARAM_WITHOUT_REGEX);

   /**
    * A regex pattern that searches for a URI template parameter in the form of {*}
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
      if (contextPath != null && !"".equals(contextPath) && path.startsWith(contextPath))
      {
         path = path.substring(contextPath.length());
      }
      return path;

   }

   public static String uriParam(String path, String name, String value)
   {
      Matcher matcher = PathHelper.URI_PARAM_PATTERN.matcher(path);
      StringBuffer newPath = new StringBuffer();
      while (matcher.find())
      {
         String param = matcher.group(1);
         if (param.equals(name))
         {
            matcher.appendReplacement(newPath, value);
            matcher.appendTail(newPath);
            path = newPath.toString();
            return path;
         }
         else matcher.appendReplacement(newPath, "$0");
      }
      matcher.appendTail(newPath);
      path = newPath.toString();
      return path;

   }

   public static void main(String[] args) throws Exception
   {
   }


}