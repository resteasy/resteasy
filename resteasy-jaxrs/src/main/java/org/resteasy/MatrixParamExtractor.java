package org.resteasy;

import org.resteasy.spi.HttpRequest;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamExtractor extends StringParameterExtractor implements ParameterExtractor
{
   public MatrixParamExtractor(Class type, Type genericType, AccessibleObject target, String paramName, String defaultValue)
   {
      super(type, genericType, paramName, "@" + MatrixParam.class.getSimpleName(), defaultValue, target);
   }

   public Object extract(HttpRequest request)
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