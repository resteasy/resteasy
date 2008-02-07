package org.resteasy;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamExtractor extends StringParameterExtractor
{
   public MatrixParamExtractor(Method method, String paramName, int index, String defaultValue)
   {
      super(index, method, paramName, "@" + MatrixParam.class.getSimpleName(), defaultValue);
   }

   public Object extract(HttpInput request)
   {
      ArrayList<String> values = new ArrayList<String>();
      for (PathSegment segment : request.getUri().getPathSegments())
      {
         List<String> list = segment.getMatrixParameters().get(paramName);
         if (list != null) values.addAll(list);
      }
      if (values.size() == 0) return extractValues(null);
      else return extractValues(values);
   }
}