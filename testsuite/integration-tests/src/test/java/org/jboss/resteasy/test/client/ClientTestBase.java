package org.jboss.resteasy.test.client;

import java.net.URI;

import org.jboss.arquillian.test.api.ArquillianResource;

/**
 * @author Tomaz Cerar (c) 2016 Red Hat Inc.
 */
public abstract class ClientTestBase {

   @ArquillianResource
   URI baseUri;

   protected String generateURL(String path) {
      if (path.startsWith("/")){
         path = path.substring(1);
      }
      return baseUri.resolve(path).toString();
   }
}
