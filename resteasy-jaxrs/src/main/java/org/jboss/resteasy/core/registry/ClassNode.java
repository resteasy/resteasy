package org.jboss.resteasy.core.registry;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import javax.ws.rs.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClassNode
{
   protected String segment;
   protected Map<String, ClassNode> children = new HashMap<String, ClassNode>();
   protected List<ClassExpression> targets = new ArrayList<ClassExpression>();

   public ClassNode(String segment)
   {
      this.segment = segment;
   }

   public RootNode match(HttpRequest request, int start)
   {
      String path = request.getUri().getMatchingPath();
      if (start < path.length() && path.charAt(start) == '/') start++;
      List<ClassExpression> potentials = new ArrayList<ClassExpression>();
      potentials(path, start, potentials);
      Collections.sort(potentials);

      for (ClassExpression expression : potentials)
      {
         Pattern pattern = expression.getPattern();
         Matcher matcher = pattern.matcher(path);
         matcher.region(start, path.length());

         if (matcher.matches())
         {
            ResteasyUriInfo uriInfo = request.getUri();
            int length = matcher.start(expression.getNumGroups() + 1);
            if (length == -1)
            {
               uriInfo.pushMatchedURI(path);
            }
            else
            {
               String substring = path.substring(0, length);
               uriInfo.pushMatchedURI(substring);
            }
            return expression.getRoot();
         }
      }
      throw new NotFoundException(Messages.MESSAGES.couldNotFindResourceForFullPath(request.getUri().getRequestUri()));      
   }

   public void potentials(String path, int start, List<ClassExpression> matches)
   {
      if (start == path.length()) // we've reached end of string
      {
         matches.addAll(targets);
         return;
      }

      if (start < path.length())
      {
         String simpleSegment = null;
         int endOfSegmentIndex = path.indexOf('/', start);
         if (endOfSegmentIndex > -1) simpleSegment = path.substring(start, endOfSegmentIndex);
         else simpleSegment = path.substring(start);
         ClassNode child = children.get(simpleSegment);
         if (child != null)
         {
            int next = start + simpleSegment.length();
            if (endOfSegmentIndex > -1) next++; // go past '/'
            child.potentials(path, next, matches);
         }
      }
      for (ClassExpression exp : targets)
      {
         matches.add(exp);
      }
   }
}
