package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jboss.resteasy.plugins.guice.i18n.*;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

public class UriBuilderProvider implements Provider<UriBuilder>
{
   private final RuntimeDelegate runtimeDelegate;

   @Inject
   public UriBuilderProvider(final RuntimeDelegate runtimeDelegate)
   {
      this.runtimeDelegate = runtimeDelegate;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.guice.ext.UriBuilderProvider , method call : get .")
   public UriBuilder get()
   {
      return runtimeDelegate.createUriBuilder();
   }
}
