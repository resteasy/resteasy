package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.Failure;
import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.PathHelper;

import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamSegment extends Segment
{
   protected String pathExpression;
   protected String regex;
   protected Pattern pattern;
   protected List<Group> groups = new ArrayList<Group>();

   private static class Group
   {
      int group;
      String name;
      boolean storePathSegment;

      private Group(int group, String name)
      {
         this.group = group;
         this.name = name;
      }

      private Group(int group, String name, boolean storePathSegment)
      {
         this.group = group;
         this.name = name;
         this.storePathSegment = storePathSegment;
      }
   }

   public static final Pattern GROUP = Pattern.compile("[^\\\\]\\(");

   /**
    * Find the number of groups int he regular expression
    * don't count escaped '('
    *
    * @param regex
    * @return
    */
   private static int groupCount(String regex)
   {
      Matcher matcher = GROUP.matcher(regex);
      int groupCount = 0;
      if (regex.startsWith("(")) groupCount++; // couldn't find a good regex to match start
      while (matcher.find()) groupCount++;
      return groupCount;
   }

   public PathParamSegment(String segment)
   {
      this.pathExpression = segment;
      String[] split = PathHelper.URI_PARAM_PATTERN.split(segment);
      Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(segment);
      int i = 0;
      StringBuffer buffer = new StringBuffer();
      if (i < split.length) buffer.append(Pattern.quote(split[i++]));
      int groupNumber = 1;

      while (withPathParam.find())
      {
         String name = withPathParam.group(1);
         buffer.append("(");
         if (withPathParam.group(3) == null)
         {
            buffer.append("[^/]*");
            groups.add(new Group(groupNumber++, name, true));
         }
         else
         {
            String expr = withPathParam.group(3);
            buffer.append(expr);
            groups.add(new Group(groupNumber++, name));
            groupNumber += groupCount(expr);
         }
         buffer.append(")");
         if (i < split.length) buffer.append(Pattern.quote(split[i++]));
      }
      regex = buffer.toString();
      pattern = Pattern.compile(regex);
   }

   public String getRegex()
   {
      return regex;
   }

   public String getPathExpression()
   {
      return pathExpression;
   }

   protected void populatePathParams(HttpRequest request, Matcher matcher, String path)
   {
      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      for (Group group : groups)
      {
         String value = matcher.group(group.group);
         uriInfo.addEncodedPathParameter(group.name, value);
         if (group.storePathSegment)
         {
            int index = matcher.start(group.group);

            int start = 0;
            if (path.startsWith("/")) start++;
            int segmentIndex = pathSegmentIndex(path, start, index);
            PathSegment decoded = request.getUri().getPathSegments().get(segmentIndex);
            PathSegment encoded = request.getUri().getPathSegments(false).get(segmentIndex);
            uriInfo.getEncodedPathParameterPathSegments().add(group.name, encoded);
            uriInfo.getPathParameterPathSegments().add(group.name, decoded);
         }
      }
   }

   public ResourceInvoker matchPattern(HttpRequest request, String path, int start)
   {
      if (path.charAt(start) == '/') start++;

      UriInfoImpl uriInfo = (UriInfoImpl) request.getUri();
      Matcher matcher = pattern.matcher(path);
      matcher.region(start, path.length());

      if (matcher.matches())
      {
         // we consumed entire path string
         ResourceInvoker invoker = match(request.getHttpMethod(), request.getHttpHeaders().getMediaType(), request.getHttpHeaders().getAcceptableMediaTypes());
         if (invoker == null) throw new Failure("Could not find resource for: " + path, HttpResponseCodes.SC_NOT_FOUND);
         uriInfo.pushMatchedURI(path, Encode.decode(path));
         populatePathParams(request, matcher, path);
         return invoker;
      }
      if (locator == null)
      {
         throw new Failure("Could not find resource for: " + path, HttpResponseCodes.SC_NOT_FOUND);
      }
      if (matcher.find(start))
      {
         // a non-matched locator path must have a '/' immediately after.  A locator cannot match a partial segment
         if (path.charAt(start + matcher.group(0).length()) == '/')
         {
            String matched = path.substring(0, start + matcher.group(0).length());
            uriInfo.pushMatchedURI(matched, Encode.decode(matched));
            populatePathParams(request, matcher, path);
            return locator;
         }
      }
      throw new Failure("Could not find resource for: " + path, HttpResponseCodes.SC_NOT_FOUND);
   }

   public static int pathSegmentIndex(String string, int start, int stop)
   {
      if (start >= string.length()) return 0;
      int count = 0;
      for (int i = start; i < stop && i < string.length(); i++)
      {
         if (string.charAt(i) == '/') count++;
      }
      return count;
   }

   public static void main(String[] args) throws Exception
   {
      System.out.println("".split("/").length);
      System.out.println("/".split("/").length);
      System.out.println("//".split("/").length);
   }
}
