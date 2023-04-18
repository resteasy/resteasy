package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.core.Form;

import org.jboss.resteasy.test.client.ClientFormParamTest;

public class ClientFormResource implements ClientFormParamTest.ClientFormResourceInterface {

    public String put(String value) {
        return value;
    }

    @Override
    public Form post(Form form) {
        return form;
    }
}
