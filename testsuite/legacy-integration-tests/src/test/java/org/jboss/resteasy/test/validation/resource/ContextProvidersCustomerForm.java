package org.jboss.resteasy.test.validation.resource;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;

public class ContextProvidersCustomerForm {
    @FormParam("customer")
    @PartType("application/xml")
    private ContextProvidersCustomer customer;

    public ContextProvidersCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(ContextProvidersCustomer cust) {
        this.customer = cust;
    }
}
