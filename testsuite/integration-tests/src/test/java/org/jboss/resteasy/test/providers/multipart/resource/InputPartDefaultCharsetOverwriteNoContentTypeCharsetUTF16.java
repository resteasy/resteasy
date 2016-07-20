package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.test.providers.multipart.InputPartDefaultCharsetOverwriteTest;

@Provider
public class InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16 implements ContainerRequestFilter {

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      requestContext.setProperty(InputPart.DEFAULT_CHARSET_PROPERTY, InputPartDefaultCharsetOverwriteTest.UTF_16);
   }
}
