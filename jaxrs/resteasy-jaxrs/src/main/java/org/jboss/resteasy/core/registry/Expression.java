package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.core.ResourceInvoker;
import org.jboss.resteasy.core.ResourceLocatorInvoker;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.PathHelper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Expression implements Comparable<Expression>
{
   protected String pathExpression;
   protected String regex;
   protected Pattern pattern;
   protected List<Group> groups = new ArrayList<Group>();
   protected int literalCharacters;
   protected int numCapturingGroups;
   protected int numNonDefaultGroups;
   protected SegmentNode parent;
   protected ResourceInvoker invoker;

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

   public int compareTo(Expression expression)
   {
      // as per spec sort first by literal characters, then numCapturing groups, then num non-default groups

      if (literalCharacters > expression.literalCharacters) return -1;
      if (literalCharacters < expression.literalCharacters) return 1;

      if (numCapturingGroups > expression.numCapturingGroups) return -1;
      if (numCapturingGroups < expression.numCapturingGroups) return 1;

      if (numNonDefaultGroups > expression.numNonDefaultGroups) return -1;
      if (numNonDefaultGroups < expression.numNonDefaultGroups) return 1;
      if (invoker.getClass().equals(expression.invoker.getClass())) return 0;

      if (invoker instanceof ResourceMethodInvoker) return -1;
      else return 1;
   }

   // [^?] is in expression to ignore non-capturing group
   public static final Pattern GROUP = Pattern.compile("[^\\\\]\\([^?]");

   /**
    * Find the number of groups in the regular expression
    * don't count escaped '('
    *
    * @param regex
    * @return
    */
   private static int groupCount(String regex)
   {
      regex = " " + regex; // add a space because GROUP regex trans to match a non-preceding slash.
      // if the grouping characters in the range block ignore them.
      int idxOpen = regex.indexOf('[');
      if (idxOpen != -1) {
    	  int idxClose = regex.indexOf(']', idxOpen);
    	  if (idxClose != -1) {
    		  regex = regex.substring(0, idxOpen) + regex.substring(idxClose+1);
    	  }
      }
      Matcher matcher = GROUP.matcher(regex);
      int groupCount = 0;
      while (matcher.find()) groupCount++;
      return groupCount;
   }

   public Expression(SegmentNode parent, String segment, ResourceInvoker invoker)
   {
      this(parent, segment, invoker, null);
   }

   public Expression(SegmentNode parent, String segment, ResourceInvoker invoker, String additionalRegex)
   {
      this.parent = parent;
      this.invoker = invoker;
      this.pathExpression = segment;
      String replacedCurlySegment = PathHelper.replaceEnclosedCurlyBraces(segment);
      literalCharacters = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment).replaceAll("").length();

      String[] split = PathHelper.URI_PARAM_PATTERN.split(replacedCurlySegment);
      Matcher withPathParam = PathHelper.URI_PARAM_PATTERN.matcher(replacedCurlySegment);
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
            buffer.append("[^/]+");
            groups.add(new Group(groupNumber++, name, true));
         }
         else
         {
            String expr = withPathParam.group(3);
            expr = PathHelper.recoverEnclosedCurlyBraces(expr);
            buffer.append(expr);
            numNonDefaultGroups++;
            groups.add(new Group(groupNumber++, name));
            groupNumber += groupCount(expr);
         }
         buffer.append(")");
         if (i < split.length) buffer.append(Pattern.quote(split[i++]));
      }
      if (additionalRegex != null) buffer.append(additionalRegex);
      regex = buffer.toString();
      pattern = Pattern.compile(regex);
      numCapturingGroups = groups.size();
   }

   public int getNumGroups()
   {
      return groups.size();
   }

   public String getRegex()
   {
      return regex;
   }

   public String getPathExpression()
   {
      return pathExpression;
   }

   public void populatePathParams(HttpRequest request, Matcher matcher, String path)
   {
      ResteasyUriInfo uriInfo = (ResteasyUriInfo) request.getUri();
      for (Group group : groups)
      {
         String value = matcher.group(group.group);
         uriInfo.addEncodedPathParameter(group.name, value);
         int index = matcher.start(group.group);

         int start = 0;
         if (path.charAt(0) == '/') start++;
         int segmentIndex = 0;

         if (start < path.length())
         {
            int count = 0;
            for (int i = start; i < index && i < path.length(); i++)
            {
               if (path.charAt(i) == '/') count++;
            }
            segmentIndex = count;
         }

         int numSegments = 1;
         for (int i = 0; i < value.length(); i++)
         {
            if (value.charAt(i) == '/') numSegments++;
         }

         if (segmentIndex + numSegments > request.getUri().getPathSegments().size())
         {

            throw new BadRequestException("Number of matched segments greater than actual");
         }
         PathSegment[] encodedSegments = new PathSegment[numSegments];
         PathSegment[] decodedSegments = new PathSegment[numSegments];
         for (int i = 0; i < numSegments; i++)
         {
            decodedSegments[i] = request.getUri().getPathSegments().get(segmentIndex + i);
            encodedSegments[i] = request.getUri().getPathSegments(false).get(segmentIndex + i);
         }
         uriInfo.getEncodedPathParameterPathSegments().add(group.name, encodedSegments);
         uriInfo.getPathParameterPathSegments().add(group.name, decodedSegments);
      }
   }

   public boolean isLocator()
   {
      return invoker instanceof ResourceLocatorInvoker;
   }

   public Pattern getPattern()
   {
      return pattern;
   }

   public ResourceInvoker getInvoker()
   {
      return invoker;
   }
}
