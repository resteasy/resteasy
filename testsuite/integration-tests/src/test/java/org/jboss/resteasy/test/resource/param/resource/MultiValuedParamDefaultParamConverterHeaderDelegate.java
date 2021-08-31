package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class MultiValuedParamDefaultParamConverterHeaderDelegate implements HeaderDelegate<MultiValuedParamDefaultParamConverterHeaderDelegateClass> {

   @Override
   public MultiValuedParamDefaultParamConverterHeaderDelegateClass fromString(String value) {
       MultiValuedParamDefaultParamConverterHeaderDelegateClass hd = new MultiValuedParamDefaultParamConverterHeaderDelegateClass();
       hd.setS("h" + value);
       return hd;
   }

   @Override
   public String toString(MultiValuedParamDefaultParamConverterHeaderDelegateClass value) {
       return "h" + value.getS();
   }
}
