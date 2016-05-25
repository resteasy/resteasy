package org.jboss.resteasy.jsapi;

import java.lang.annotation.Annotation;

public class MethodParamMetaData
{

   public static enum MethodParamType
   {
      QUERY_PARAMETER, HEADER_PARAMETER, COOKIE_PARAMETER, PATH_PARAMETER, MATRIX_PARAMETER, FORM_PARAMETER, FORM, ENTITY_PARAMETER
   }

   private Class<?> type;
   private Annotation[] annotations;

   private MethodParamType paramType;
   private String paramName;

   public MethodParamMetaData(Class<?> type, Annotation[] annotations,
         MethodParamType paramType, String paramName)
   {
      super();
      this.type = type;
      this.annotations = annotations;
      this.paramType = paramType;
      this.paramName = paramName;
   }

   public Class<?> getType()
   {
      return type;
   }

   public void setType(Class<?> type)
   {
      this.type = type;
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public MethodParamType getParamType()
   {
      return paramType;
   }

   public void setParamType(MethodParamType paramType)
   {
      this.paramType = paramType;
   }

   public String getParamName()
   {
      return paramName;
   }

   public void setParamName(String paramName)
   {
      this.paramName = paramName;
   }

}
