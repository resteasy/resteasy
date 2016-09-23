package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.jboss.resteasy.plugins.guice.i18n.*;

import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

public class VariantListBuilderProvider implements Provider<Variant.VariantListBuilder>
{
   private final RuntimeDelegate runtimeDelegate;

   @Inject
   public VariantListBuilderProvider(final RuntimeDelegate runtimeDelegate)
   {
      this.runtimeDelegate = runtimeDelegate;
   }

   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.guice.ext.VariantListBuilderProvider , method call : get .")
   public Variant.VariantListBuilder get()
   {
      return runtimeDelegate.createVariantListBuilder();
   }
}
