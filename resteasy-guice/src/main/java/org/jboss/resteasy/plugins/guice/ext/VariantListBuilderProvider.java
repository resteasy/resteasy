package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Inject;
import com.google.inject.Provider;

import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.ext.RuntimeDelegate;

public class VariantListBuilderProvider implements Provider<Variant.VariantListBuilder>
{
   private final RuntimeDelegate runtimeDelegate;

   @Inject
   public VariantListBuilderProvider(final RuntimeDelegate runtimeDelegate)
   {
      this.runtimeDelegate = runtimeDelegate;
   }

   public Variant.VariantListBuilder get()
   {
      return runtimeDelegate.createVariantListBuilder();
   }
}
