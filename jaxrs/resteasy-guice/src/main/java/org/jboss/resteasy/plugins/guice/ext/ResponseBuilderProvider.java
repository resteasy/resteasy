package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

public class ResponseBuilderProvider implements Provider<Response.ResponseBuilder>
{
   private final RuntimeDelegate runtimeDelegate;

   @Inject
   public ResponseBuilderProvider(final RuntimeDelegate runtimeDelegate)
   {
      this.runtimeDelegate = runtimeDelegate;
   }

   public Response.ResponseBuilder get()
   {
      return runtimeDelegate.createResponseBuilder();
   }
}