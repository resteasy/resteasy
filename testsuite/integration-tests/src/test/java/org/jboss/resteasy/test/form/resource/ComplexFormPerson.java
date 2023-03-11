package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;

import org.jboss.resteasy.annotations.Form;

public class ComplexFormPerson {
    @FormParam("name")
    public String name;

    @Form(prefix = "invoice")
    public ComplexFormAddress invoice;

    @Form(prefix = "shipping")
    public ComplexFormAddress shipping;

    @Override
    public String toString() {
        return new StringBuilder("name:'").append(name).append("', invoice:'").append(invoice.street).append("', shipping:'")
                .append(shipping.street).append("'").toString();
    }
}
