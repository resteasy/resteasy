package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.test.client.ClientFormParamTest;

import javax.ws.rs.core.Form;

public class ClientFormResource implements ClientFormParamTest.ClientFormResourceInterface {

   public String put(String value) {
      return value;
   }

   @Override
   public Form post(Form form) {
      return form;
   }
}
