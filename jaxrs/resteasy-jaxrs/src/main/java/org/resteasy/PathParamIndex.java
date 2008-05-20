package org.resteasy;

import org.resteasy.specimpl.UriInfoImpl;
import org.resteasy.spi.HttpRequest;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.util.PathHelper;

import javax.ws.rs.core.PathSegment;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamIndex
{
   private static class Segment
   {
      Pattern pattern;
      List<String> pathParams = new ArrayList<String>();
   }

   protected List<Segment> segments = new ArrayList<Segment>();

   protected String path;
   protected Map<String, List<Integer>> uriParams = new HashMap<String, List<Integer>>();
   protected int offset;
   protected boolean wildcard;

   public PathParamIndex(String path, int offset, boolean wildcard)
   {
      this.offset = offset;
      this.path = path;
      this.wildcard = wildcard;
      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      int i = offset;
      for (String p : paths)
      {
         Segment segment = new Segment();
         segments.add(segment);
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
         boolean found = false;
         while (matcher.find())
         {
            found = true;
            String uriParamName = matcher.group(2);
            segment.pathParams.add(uriParamName);
            List<Integer> paramIndexes = uriParams.get(uriParamName);
            if (paramIndexes == null)
            {
               paramIndexes = new ArrayList<Integer>();
               uriParams.put(uriParamName, paramIndexes);
            }
            paramIndexes.add(i);
         }
         if (found) segment.pattern = Pattern.compile(PathHelper.createRegularExpressionFromPathExpression(p));
         i++;
      }
   }


   public void populateUriInfoTemplateParams(HttpRequest input)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) input.getUri();
      List<PathSegment> pathSegments = input.getUri().getPathSegments(false);
      int i = offset;
      String lastName = null;
      String lastValue = null;
      for (int k = 0; i < segments.size() + offset && i < pathSegments.size(); i++, k++)
      {
         PathSegment pathSegment = pathSegments.get(i);
         Segment segment = segments.get(k);
         if (segment.pattern == null)
         {
            lastName = lastValue = null; // clear
            continue;
         }
         Matcher matcher = segment.pattern.matcher(pathSegment.getPath());

         if (!matcher.matches())
            throw new Failure("Path Segment does not match expression", HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
         int pathParamsIndex = 0;

         for (int g = 1; g <= matcher.groupCount(); g++)
         {
            String name = segment.pathParams.get(pathParamsIndex++);
            String value = matcher.group(g);
            uriInfo.addEncodedPathParameter(name, value);
            lastName = name;
            lastValue = value;
         }

      }

      if (wildcard && lastName != null && i < pathSegments.size())
      {
         for (; wildcard && i < pathSegments.size(); i++)
         {
            PathSegment pathSegment = pathSegments.get(i);
            lastValue += "/" + pathSegment.getPath();
         }
         {
            List<String> values = uriInfo.getPathParameters(false).get(lastName);
            values.set(values.size() - 1, lastValue);
         }
         {
            List<String> values = uriInfo.getPathParameters().get(lastName);
            try
            {
               values.set(values.size() - 1, URLDecoder.decode(lastValue, "UTF-8"));
            }
            catch (UnsupportedEncodingException e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

   public Map<String, List<Integer>> getUriParams()
   {
      return uriParams;
   }

   public int getOffset()
   {
      return offset;
   }

   public static void main(String[] args) throws Exception
   {
      /*
      {
         String segment = "{foo},{bar}";
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
         while (matcher.find() == true)
         {
            System.out.println(matcher.group());
         }
      }
      {
         String segment = "junk";
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
         while (matcher.find() == true)
         {
            System.out.println(matcher.group());
         }
      }
      */
      {
         String segment = "blahfoo,,,bar";
         Pattern pattern = Pattern.compile("blah(.*),(.*)");
         Matcher matcher = pattern.matcher(segment);
         if (matcher.matches())
         {
            System.out.println("group 1:" + matcher.group(1));
            System.out.println("group 2:" + matcher.group(2));
         }
         else System.out.println("Does not match");

      }

      {
         String segment = ",bar";
         Pattern pattern = Pattern.compile("(.*?),(.*?)");
         Matcher matcher = pattern.matcher(segment);
         if (matcher.matches())
         {
            System.out.println("group 1:" + matcher.group(1));
            System.out.println("group 2:" + matcher.group(2));
         }
         else System.out.println("Does not match");

      }

      Pattern pattern = Pattern.compile("^(.*?)$");
      String segment = "1234";
      Matcher matcher = pattern.matcher(segment);
      if (matcher.find())
      {
         System.out.println(matcher.group());
      }
   }

}
