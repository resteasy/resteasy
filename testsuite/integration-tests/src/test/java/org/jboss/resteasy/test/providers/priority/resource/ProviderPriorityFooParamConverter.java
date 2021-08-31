package org.jboss.resteasy.test.providers.priority.resource;

import jakarta.ws.rs.ext.ParamConverter;

public class ProviderPriorityFooParamConverter implements ParamConverter<ProviderPriorityFoo> {
   private String foo;

   public ProviderPriorityFooParamConverter(final String foo) {
      this.foo = foo;
   }

   @Override
   public ProviderPriorityFoo fromString(String value)
   {
      return new ProviderPriorityFoo(foo);
   }

   @Override
   public String toString(ProviderPriorityFoo value)
   {
      return value.getFoo();
   }
}
