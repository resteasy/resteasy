package org.jboss.resteasy.test.asyncio;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;

public class MyAsyncThrowingWriterInterceptor extends BlockingWriterInterceptor implements AsyncWriterInterceptor
{
   private boolean throwNow;

   public MyAsyncThrowingWriterInterceptor(final boolean throwNow) {
      this.throwNow = throwNow;
   }

   @Override
   public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context)
   {
      // do not throw when we're serialising the exception
      if("KO".equals(context.getEntity())) {
         WebApplicationException ex = new WebApplicationException(Response.ok("this is fine").build());
         if(throwNow)
            throw ex;
         CompletableFuture<Void> ret = new CompletableFuture<>();
         ret.completeExceptionally(ex);
         return ret;
      }
      return context.asyncProceed();
   }

}
