package org.jboss.resteasy.test.providers.injection.resource;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncResponseProvider;

@SuppressWarnings("rawtypes")
@Provider
public class ApplicationInjectionAsyncResponseProvider implements AsyncResponseProvider<CompletionStage>
{
   @Context ApplicationInjectionApplicationParent application;

   @SuppressWarnings("unchecked")
   @Override
   public CompletionStage toCompletionStage(CompletionStage asyncResponse) {
      String appName = application.getName();
      return asyncResponse.thenApply(t -> ((String) t).concat(ApplicationInjectionAsyncResponseProvider.class + ":" + appName));

   }     
}
