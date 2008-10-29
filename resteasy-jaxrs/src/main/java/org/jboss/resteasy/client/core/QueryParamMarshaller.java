package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringConverter;

import java.util.Collection;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamMarshaller implements Marshaller
{
   private String paramName;
   private ResteasyProviderFactory factory;

   public QueryParamMarshaller(String paramName, ResteasyProviderFactory factory)
   {
      this.paramName = paramName;
      this.factory = factory;
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
   }

   protected String toString(Object object)
   {
      StringConverter converter = factory.getStringConverter(object.getClass());
      if (converter != null) return converter.toString(object);
      else return object.toString();

   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
      if (object == null) return;
      if (object instanceof Collection)
      {
         for (Object obj : (Collection) object)
         {
            uri.queryParam(paramName, toString(obj));
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
               uri.queryParam(paramName, toString(obj));

            }
         }
      }
      else
      {
         uri.queryParam(paramName, toString(object));
      }
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
   }
}