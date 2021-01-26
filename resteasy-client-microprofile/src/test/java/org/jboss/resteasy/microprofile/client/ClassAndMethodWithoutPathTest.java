package org.jboss.resteasy.microprofile.client;

import java.io.Closeable;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Test;

/**
 * Regression test for RESTEASY-2801
 */
public class ClassAndMethodWithoutPathTest {

   /**
    * Unlike, test(), the close() method added by Closeable has no @PathParam's.
    * Failure to re-initialize variable template in RestClientBuilderImpl.verifyInterface()
    * causes a  RestClientDefinitionException("Parameters and variables don't match on " ...).
    */
   public interface MyInterface extends Closeable {
      @GET
      @Path("{n}")
      Response test(@PathParam("n") int n);
   }

   @Test
   public void testGet() throws Exception {
      RestClientBuilder.newBuilder()
            .baseUrl(new URL("http://localhost:8081/"))
            .build(MyInterface.class);
   }
}