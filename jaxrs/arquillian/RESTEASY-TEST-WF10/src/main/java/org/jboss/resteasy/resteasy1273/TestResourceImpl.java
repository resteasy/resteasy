package org.jboss.resteasy.resteasy1273;

import org.jboss.resteasy.spi.NoLogWebApplicationException;

public class TestResourceImpl implements TestResource
{
       public String get() {
           return "hello world";
       }

       public String error() {
           throw new NoLogWebApplicationException(404);
       }

       public String getData(String data) {
           return "Here is your string:" + data;
       }
}
