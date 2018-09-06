package org.jboss.resteasy.plugins.guice.ext;

import com.google.inject.Binder;
import com.google.inject.Module;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;

public class JaxrsModule implements Module
{

   public void configure(final Binder binder)
   {
      binder.bind(ClientHttpEngine.class).to(ApacheHttpClient43Engine.class);
      binder.bind(RuntimeDelegate.class).toInstance(RuntimeDelegate.getInstance());
      binder.bind(Response.ResponseBuilder.class).toProvider(ResponseBuilderProvider.class);
      binder.bind(UriBuilder.class).toProvider(UriBuilderProvider.class);
      binder.bind(Variant.VariantListBuilder.class).toProvider(VariantListBuilderProvider.class);
   }
}
