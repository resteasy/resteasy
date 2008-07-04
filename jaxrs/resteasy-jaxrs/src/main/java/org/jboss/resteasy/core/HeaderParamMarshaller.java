package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ClientHttpOutput;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamMarshaller implements Marshaller
{
   private String paramName;

   public HeaderParamMarshaller(String paramName)
   {
      this.paramName = paramName;
   }

   public void marshall(Object object, UriBuilderImpl uri, ClientHttpOutput output)
   {
      if (object == null) return;
      if (object instanceof List)
      {
         for (Object obj : (List) object)
         {
            output.getOutputHeaders().add(paramName, obj.toString());
         }
      }
      else if (object.getClass().isArray())
      {
         if (object.getClass().getComponentType().isPrimitive())
         {
            Class componentType = object.getClass().getComponentType();
            if (componentType.equals(boolean.class))
            {
               for (Boolean bool : (boolean[]) object) output.getOutputHeaders().add(paramName, bool.toString());
            }
            else if (componentType.equals(byte.class))
            {
               for (Byte val : (byte[]) object) output.getOutputHeaders().add(paramName, val.toString());
            }
            else if (componentType.equals(short.class))
            {
               for (Short val : (short[]) object) output.getOutputHeaders().add(paramName, val.toString());
            }
            else if (componentType.equals(int.class))
            {
               for (Integer val : (int[]) object) output.getOutputHeaders().add(paramName, val.toString());
            }
            else if (componentType.equals(long.class))
            {
               for (Long val : (long[]) object) output.getOutputHeaders().add(paramName, val.toString());
            }
            else if (componentType.equals(float.class))
            {
               for (Float val : (float[]) object) output.getOutputHeaders().add(paramName, val.toString());
            }
            else if (componentType.equals(double.class))
            {
               for (Double val : (double[]) object) output.getOutputHeaders().add(paramName, val.toString());
            }
         }
         else
         {
            Object[] objs = (Object[]) object;
            for (Object obj : objs)
            {
               output.getOutputHeaders().add(paramName, obj.toString());

            }
         }
      }
      else
      {
         output.getOutputHeaders().add(paramName, object.toString());
      }
   }
}