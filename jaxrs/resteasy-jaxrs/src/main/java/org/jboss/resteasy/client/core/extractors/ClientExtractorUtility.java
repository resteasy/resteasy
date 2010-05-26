package org.jboss.resteasy.client.core.extractors;

public class ClientExtractorUtility
{

   public static final boolean isVoidReturnType(final Class<?> returnType)
   {
      return void.class.equals(returnType) || Void.class.equals(returnType);
   }

}
