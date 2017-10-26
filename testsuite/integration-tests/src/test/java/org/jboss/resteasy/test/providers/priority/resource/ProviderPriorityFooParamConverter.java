package org.jboss.resteasy.test.providers.priority.resource;

import javax.ws.rs.ext.ParamConverter;

public class ProviderPriorityFooParamConverter implements ParamConverter<ProviderPriorityFoo> {
   private String foo;
   
   public ProviderPriorityFooParamConverter(String foo) {
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
