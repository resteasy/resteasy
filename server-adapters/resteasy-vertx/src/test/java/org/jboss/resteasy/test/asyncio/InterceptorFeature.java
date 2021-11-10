package org.jboss.resteasy.test.asyncio;

import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InterceptorFeature implements DynamicFeature
{

   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext context)
   {
      if(resourceInfo.getResourceMethod().isAnnotationPresent(WithBlockingWriterInterceptor.class))
         context.register(BlockingWriterInterceptor.class);
      if(resourceInfo.getResourceMethod().isAnnotationPresent(WithBlockingThrowingWriterInterceptor.class))
         context.register(BlockingThrowingWriterInterceptor.class);
      if(resourceInfo.getResourceMethod().isAnnotationPresent(WithAsyncWriterInterceptor.class))
         context.register(MyAsyncWriterInterceptor.class);
      if(resourceInfo.getResourceMethod().isAnnotationPresent(WithAsyncThrowingWriterInterceptor.class)) {
         WithAsyncThrowingWriterInterceptor annotation = resourceInfo.getResourceMethod().getAnnotation(WithAsyncThrowingWriterInterceptor.class);
         context.register(new MyAsyncThrowingWriterInterceptor(annotation.throwNow()));
      }
   }


}
