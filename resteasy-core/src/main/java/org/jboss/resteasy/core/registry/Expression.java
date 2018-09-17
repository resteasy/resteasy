package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.util.PathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class Expression implements Comparable<Expression>
{
   // [^?] is in expression to ignore non-capturing group
   public static final Pattern GROUP = Pattern.compile("[^\\\\]\\([^?]");
   protected String pathExpression;
   protected String regex;
   protected Pattern pattern;
   protected List<Group> groups = new ArrayList<Group>();
   protected int literalCharacters;
   protected int numCapturingGroups;
   protected int numNonDefaultGroups;

   public Expression(String segment, String additionalRegex)
   {
      this.pathExpression = segment;
      CharSequence replacedCurlySegment = PathHelper.replaceEnclosedCurlyBracesCS(segment);
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
      pattern = Pattern.compile(getRegex());
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
      return 0;
   }

   /**
    * Find the number of groups in the regular expression
    * don't count escaped '('.
    *
    * @param regex regular expression
    * @return number of groups
    */
   protected static int groupCount(String regex)
   {
      regex = " " + regex; // add a space because GROUP regex trans to match a non-preceding slash.
      // if the grouping characters in the range block ignore them.
      int idxOpen = regex.indexOf('[');
      if (idxOpen != -1)
      {
         int idxClose = regex.indexOf(']', idxOpen);
         if (idxClose != -1)
         {
            regex = regex.substring(0, idxOpen) + regex.substring(idxClose + 1);
         }
      }
      Matcher matcher = GROUP.matcher(regex);
      int groupCount = 0;
      while (matcher.find()) groupCount++;
      return groupCount;
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

   public Pattern getPattern()
   {
      return pattern;
   }

   protected static class Group
   {
      int group;
      String name;
      boolean storePathSegment;

      protected Group(int group, String name)
      {
         this.group = group;
         this.name = name;
      }

      protected Group(int group, String name, boolean storePathSegment)
      {
         this.group = group;
         this.name = name;
         this.storePathSegment = storePathSegment;
      }
   }
}
