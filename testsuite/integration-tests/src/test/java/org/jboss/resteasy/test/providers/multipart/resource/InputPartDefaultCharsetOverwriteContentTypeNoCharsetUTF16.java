package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.test.providers.multipart.InputPartDefaultCharsetOverwriteTest;

@Provider
public class InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16 implements ContainerRequestFilter {

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      requestContext.setProperty(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, InputPartDefaultCharsetOverwriteTest.TEXT_HTTP_WITH_CHARSET_UTF_16);
   }
}
