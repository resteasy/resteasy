package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;

import java.util.Collection;

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

   public void buildUri(Object object, UriBuilderImpl uri)
   {
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
      if (object == null) return;
      if (object instanceof Collection)
      {
         for (Object obj : (Collection) object)
         {
            httpMethod.addRequestHeader(paramName, obj.toString());
         }
      }
      else if (object.getClass().isArray())
      {
         if (object.getClass().getComponentType().isPrimitive())
         {
            Class componentType = object.getClass().getComponentType();
            if (componentType.equals(boolean.class))
            {
               for (Boolean bool : (boolean[]) object) httpMethod.addRequestHeader(paramName, bool.toString());
            }
            else if (componentType.equals(byte.class))
            {
               for (Byte val : (byte[]) object) httpMethod.addRequestHeader(paramName, val.toString());
            }
            else if (componentType.equals(short.class))
            {
               for (Short val : (short[]) object) httpMethod.addRequestHeader(paramName, val.toString());
            }
            else if (componentType.equals(int.class))
            {
               for (Integer val : (int[]) object) httpMethod.addRequestHeader(paramName, val.toString());
            }
            else if (componentType.equals(long.class))
            {
               for (Long val : (long[]) object) httpMethod.addRequestHeader(paramName, val.toString());
            }
            else if (componentType.equals(float.class))
            {
               for (Float val : (float[]) object) httpMethod.addRequestHeader(paramName, val.toString());
            }
            else if (componentType.equals(double.class))
            {
               for (Double val : (double[]) object) httpMethod.addRequestHeader(paramName, val.toString());
            }
         }
         else
         {
            Object[] objs = (Object[]) object;
            for (Object obj : objs)
            {
               httpMethod.addRequestHeader(paramName, obj.toString());

            }
         }
      }
      else
      {
         httpMethod.addRequestHeader(paramName, object.toString());
      }
   }
}