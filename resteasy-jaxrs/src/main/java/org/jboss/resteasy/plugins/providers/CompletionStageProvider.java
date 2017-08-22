package org.jboss.resteasy.plugins.providers;

import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.AsyncResponseProvider;

public class CompletionStageProvider implements AsyncResponseProvider<CompletionStage<?>> {

   @SuppressWarnings("rawtypes")
   @Override
   public CompletionStage toCompletionStage(CompletionStage<?> asyncResponse)
   {
      return asyncResponse;
   }

}
