/**
 *
 */
package org.resteasy.util;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
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

   /**
    * Utility method to extract the URL template paramater names and map it
    * back to the group position. This Map is cached by a {@link com.damnhandy.resteasy.core.ResourceInvoker} so that
    * it can extract the the URI template parameter values.
    *
    * @param path
    * @return a map
    * @see #extractURLParameterValuesFromRequest(String,java.util.regex.Pattern)
    * @see com.damnhandy.resteasy.core.ResourceInvoker
    */
   public static Map<String, Integer> extractURLTemplateNames(String path)
   {
      Matcher matcher = URI_TEMPLATE_PATTERN.matcher(path);
      Map<String, Integer> paramMap = new HashMap<String, Integer>();
      int counter = 1;
      while (matcher.find())
      {
         String value = matcher.group();
         paramMap.put(value.substring(1, (value.length() - 1)), counter);
         counter++;
      }
      return paramMap;
   }

   /**
    * <p/>
    * Extracts the URI template parameter values from the requested URI. The Map returned
    * contains the position of the parameter and the value of the parameter. The
    * ResourceInvoker will take care of mapping the positions to the correct names.
    * </p>
    *
    * @param path    the requested path
    * @param pattern the regex which matches the request
    * @return a Map of
    * @see #extractURLTemplateNames(String)
    */
   public static Map<Integer, String> extractURLParameterValuesFromRequest(String path, Pattern pattern)
   {
      Matcher matcher = pattern.matcher(path);
      Set<String> names = new LinkedHashSet<String>();
      int count = matcher.groupCount();
      while (matcher.find())
      {
         for (int i = 0; i < count; i++)
         {
            int index = i + 1;
            String value = matcher.group(index);
            if (!value.equals(path))
            {
               names.add(value);
            }
         }
      }
      String[] namesArray = names.toArray(new String[names.size()]);
      Map<Integer, String> values = new HashMap<Integer, String>();
      for (int i = 0; i < namesArray.length; i++)
      {
         values.put((i + 1), namesArray[i]);
      }
      return values;
   }

   /**
    * <p/>
    * Takes the URL template and create a regex that will match the template
    * </p>
    *
    * @param orginalPathExpression
    * @return
    */
   public static String replaceURLTemplateIDs(String orginalPathExpression)
   {
      Matcher matcher = URI_TEMPLATE_PATTERN.matcher(orginalPathExpression);
      StringBuffer reviesedPath = new StringBuffer();
      int count = 1;
      while (matcher.find())
      {
         String value = matcher.group();
         value = value.substring(1, (value.length() - 1));
         matcher.appendReplacement(reviesedPath, Matcher.quoteReplacement(DEFAULT_URI_PARAM_PATTERN));
         count++;
      }
      matcher.appendTail(reviesedPath);
      return reviesedPath.append("$").toString();
   }


}