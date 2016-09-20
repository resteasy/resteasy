package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jboss.resteasy.plugins.guice.i18n.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

public class ResponseBuilderProvider implements Provider<Response.ResponseBuilder>
{
   private final RuntimeDelegate runtimeDelegate;

   @Inject
   public ResponseBuilderProvider(final RuntimeDelegate runtimeDelegate)
   {
      this.runtimeDelegate = runtimeDelegate;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.ResponseBuilderProvider , method call : get .")
   public Response.ResponseBuilder get()
   {
      return runtimeDelegate.createResponseBuilder();
   }
}
