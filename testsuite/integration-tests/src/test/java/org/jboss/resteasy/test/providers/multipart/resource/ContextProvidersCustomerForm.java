package org.jboss.resteasy.test.providers.multipart.resource;


import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

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
