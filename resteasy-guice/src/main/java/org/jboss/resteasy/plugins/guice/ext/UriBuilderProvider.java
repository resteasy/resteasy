package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

public class UriBuilderProvider implements Provider<UriBuilder>
{
   private final RuntimeDelegate runtimeDelegate;

   @Inject
   public UriBuilderProvider(final RuntimeDelegate runtimeDelegate)
   {
      this.runtimeDelegate = runtimeDelegate;
   }

   public UriBuilder get()
   {
      return runtimeDelegate.createUriBuilder();
   }
}
