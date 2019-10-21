package org.jboss.resteasy.test.providers.multipart.resource;


import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class ContextProvidersCustomerFormNewAnnotationOnField {
   @org.jboss.resteasy.annotations.jaxrs.FormParam
   @PartType("application/xml")
   private ContextProvidersCustomer customer;

   public ContextProvidersCustomer getCustomer() {
      return customer;
   }

   public void setCustomer(ContextProvidersCustomer cust) {
      this.customer = cust;
   }
}
