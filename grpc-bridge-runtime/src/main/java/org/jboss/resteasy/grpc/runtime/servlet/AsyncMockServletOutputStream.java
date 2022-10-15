package org.jboss.resteasy.grpc.runtime.servlet;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class AsyncMockServletOutputStream extends MockServletOutputStream {

   private CountDownLatch latch = new CountDownLatch(1);

   public void await() throws InterruptedException {
      while (true) {
         try {
            latch.await();
            return;
         } catch (InterruptedException e) {
            //
         }
      }
   }

   public void release() throws IOException {
      latch.countDown();
   }
}
