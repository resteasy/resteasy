package org.jboss.resteasy.spi.metadata;

import org.jboss.resteasy.util.Types;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
abstract public class Parameter
{
   public enum ParamType
   {
      BEAN_PARAM,
      CONTEXT,
      COOKIE_PARAM,
      FORM_PARAM,
      HEADER_PARAM,
      MATRIX_PARAM,
      MESSAGE_BODY,
      PATH_PARAM,
      QUERY_PARAM,
      SUSPENDED,
      UNKNOWN,
      // resteasy specific
      FORM,
      QUERY,
      SUSPEND // deprecated
   }

   protected ResourceClass resourceClass;
   protected Class<?> type;
   protected Type genericType;
   protected ParamType paramType = ParamType.UNKNOWN;
   protected String paramName;
   protected boolean encoded;
   protected String defaultValue;
   protected long suspendTimeout; // deprecated

   protected Parameter(ResourceClass resourceClass, Class<?> type, Type genericType)
   {
      this.resourceClass = resourceClass;
      this.genericType = Types.resolveTypeVariables(resourceClass.getClazz(), genericType);
      this.type = Types.getRawType(this.genericType);
   }

   public ResourceClass getResourceClass()
   {
      return resourceClass;
   }

   public Class<?> getType()
   {
      return type;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public ParamType getParamType()
   {
      return paramType;
   }

   public String getParamName()
   {
      return paramName;
   }

   public boolean isEncoded()
   {
      return encoded;
   }

   public String getDefaultValue()
   {
      return defaultValue;
   }

   public long getSuspendTimeout()
   {
      return suspendTimeout;
   }

   public abstract AccessibleObject getAccessibleObject();
   public abstract Annotation[] getAnnotations();
}
