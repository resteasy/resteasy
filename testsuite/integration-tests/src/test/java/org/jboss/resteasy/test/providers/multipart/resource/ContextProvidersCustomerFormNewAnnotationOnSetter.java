package org.jboss.resteasy.test.providers.multipart.resource;


import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ContextProvidersCustomerFormNewAnnotationOnSetter {
   private ContextProvidersCustomer customer;

   @org.jboss.resteasy.annotations.jaxrs.FormParam
   @PartType("application/xml")
   public ContextProvidersCustomer getCustomer() {
      return customer;
   }

   @org.jboss.resteasy.annotations.jaxrs.FormParam
   @PartType("application/xml")
   public void setCustomer(ContextProvidersCustomer cust) {
      this.customer = cust;
   }
}
