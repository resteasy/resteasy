package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ClientHttpOutput;

import java.util.Collection;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamMarshaller implements Marshaller
{
   private String paramName;

   public QueryParamMarshaller(String paramName)
   {
      this.paramName = paramName;
   }

   public void marshall(Object object, UriBuilderImpl uri, ClientHttpOutput output)
   {
      if (object == null) return;
      if (object instanceof Collection)
      {
         for (Object obj : (Collection) object)
         {
            uri.queryParam(paramName, obj.toString());
         }
      }
      else if (object.getClass().isArray())
      {
         if (object.getClass().getComponentType().isPrimitive())
         {
            Class componentType = object.getClass().getComponentType();
            if (componentType.equals(boolean.class))
            {
               for (Boolean bool : (boolean[]) object) uri.queryParam(paramName, bool.toString());
            }
            else if (componentType.equals(byte.class))
            {
               for (Byte val : (byte[]) object) uri.queryParam(paramName, val.toString());
            }
            else if (componentType.equals(short.class))
            {
               for (Short val : (short[]) object) uri.queryParam(paramName, val.toString());
            }
            else if (componentType.equals(int.class))
            {
               for (Integer val : (int[]) object) uri.queryParam(paramName, val.toString());
            }
            else if (componentType.equals(long.class))
            {
               for (Long val : (long[]) object) uri.queryParam(paramName, val.toString());
            }
            else if (componentType.equals(float.class))
            {
               for (Float val : (float[]) object) uri.queryParam(paramName, val.toString());
            }
            else if (componentType.equals(double.class))
            {
               for (Double val : (double[]) object) uri.queryParam(paramName, val.toString());
            }
         }
         else
         {
            Object[] objs = (Object[]) object;
            for (Object obj : objs)
            {
               uri.queryParam(paramName, obj.toString());

            }
         }
      }
      else
      {
         uri.queryParam(paramName, object.toString());
      }
   }
}