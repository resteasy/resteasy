package org.jboss.resteasy.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SegmentInfo implements Comparable<SegmentInfo>
{
   private Pattern pattern;
   private int literalCharacters;
   private int groups;
   private String expression;

   public SegmentInfo(String segment)
   {
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(segment);
         StringBuffer regex = new StringBuffer();
         while (matcher.find())
         {
            groups++;
            matcher.appendReplacement(regex, Matcher.quoteReplacement("(.*?)"));
         }
         matcher.appendTail(regex);


         expression = regex.append("$").toString();
         pattern = Pattern.compile(expression);
      }
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(expression);
         StringBuffer regex = new StringBuffer();
         while (matcher.find())
         {
            groups++;
            matcher.appendReplacement(regex, "");
         }
         matcher.appendTail(regex);
         literalCharacters = regex.toString().length();
      }
   }

   public int compareTo(SegmentInfo segmentInfo)
   {
      if (literalCharacters > segmentInfo.literalCharacters) return -1;
      if (literalCharacters < segmentInfo.literalCharacters) return 1;
      if (groups > segmentInfo.groups) return -1;
      if (groups < segmentInfo.groups) return 1;

      return 0;
   }

   public Pattern getPattern()
   {
      return pattern;
   }

   public String getExpression()
   {
      return expression;
   }

   public int getLiteralCharacters()
   {
      return literalCharacters;
   }

   public int getGroups()
   {
      return groups;
   }

   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      SegmentInfo that = (SegmentInfo) o;

      if (groups != that.groups) return false;
      if (literalCharacters != that.literalCharacters) return false;
      if (!expression.equals(that.expression)) return false;

      return true;
   }

   public int hashCode()
   {
      int result;
      result = literalCharacters;
      result = 31 * result + groups;
      result = 31 * result + expression.hashCode();
      return result;
   }
}
