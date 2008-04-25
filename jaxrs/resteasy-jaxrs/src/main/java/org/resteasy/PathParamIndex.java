package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.util.PathHelper;

import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamIndex
{
   protected String path;
   protected Map<String, List<Integer>> uriParams = new HashMap<String, List<Integer>>();
   protected int offset;

   public PathParamIndex(String path, int offset)
   {
      this.offset = offset;
      this.path = path;
      if (path.startsWith("/")) path = path.substring(1);
      String[] paths = path.split("/");
      int i = offset;
      for (String p : paths)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(p);
         if (matcher.matches())
         {
            String uriParamName = matcher.group(2);
            List<Integer> paramIndexes = uriParams.get(uriParamName);
            if (paramIndexes == null)
            {
               paramIndexes = new ArrayList<Integer>();
               uriParams.put(uriParamName, paramIndexes);
            }
            paramIndexes.add(i);
         }
         i++;
      }
   }

   public void populateUriInfoTemplateParams(HttpRequest input)
   {
      UriInfo uriInfo = input.getUri();
      for (String paramName : uriParams.keySet())
      {
         List<Integer> indexes = uriParams.get(paramName);
         for (int i : indexes)
         {
            String value = uriInfo.getPathSegments().get(i).getPath();
            uriInfo.getTemplateParameters().add(paramName, value);
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
}
