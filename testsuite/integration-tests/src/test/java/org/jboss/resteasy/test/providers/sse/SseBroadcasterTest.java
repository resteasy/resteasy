package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.SseBroadcasterImpl;
import org.junit.Assert;
import org.junit.Test;

/***
 * 
 * @author Nicolas NESMON
 *
 */
public class SseBroadcasterTest
{

   // We are expecting this test to throw an IllegalStateException every time a
   // method from SseBroadcasterImpl is invoked on a closed instance.
   @Test
   public void testIllegalStateExceptionForClosedBroadcaster() throws Exception
   {
      SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();
      sseBroadcasterImpl.close();

      try
      {
         sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
         Assert.fail("Should have thrown IllegalStateException");
      }
      catch (IllegalStateException e)
      {
      }

      try
      {
         sseBroadcasterImpl.onClose(sseEventSink -> {
         });
         Assert.fail("Should have thrown IllegalStateException");
      }
      catch (IllegalStateException e)
      {
      }

      try
      {
         sseBroadcasterImpl.onError((sseEventSink, error) -> {
         });
         Assert.fail("Should have thrown IllegalStateException");
      }
      catch (IllegalStateException e)
      {
      }

      try
      {
         sseBroadcasterImpl.register(newSseEventSink());
         Assert.fail("Should have thrown IllegalStateException");
      }
      catch (IllegalStateException e)
      {
      }
   }

   // We are expecting this test to close all registered event sinks and invoke
   // close listeners when broadcaster is closed
   @Test
   public void testClose() throws Exception
   {
      SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

      SseEventSink sseEventSink1 = newSseEventSink();
      sseBroadcasterImpl.register(sseEventSink1);
      SseEventSink sseEventSink2 = newSseEventSink();
      sseBroadcasterImpl.register(sseEventSink2);

      CountDownLatch countDownLatch = new CountDownLatch(4);
      sseBroadcasterImpl.onClose(sseEventSink -> {
         countDownLatch.countDown();
      });
      sseBroadcasterImpl.onClose(sseEventSink -> {
         countDownLatch.countDown();
      });

      sseBroadcasterImpl.close();
      if (!countDownLatch.await(3, TimeUnit.SECONDS))
      {
         Assert.fail("All close listeners should have been notified");
      }
      Assert.assertTrue(sseEventSink1.isClosed());
      Assert.assertTrue(sseEventSink2.isClosed());
   }

   // We are expecting this test to invoke both close and error listeners when
   // event sink has been closed on server side
   @Test
   public void testCloseAndErrorListenersForClosedEventSink() throws Exception
   {
      SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

      SseEventSink sseEventSink = newSseEventSink();
      sseBroadcasterImpl.register(sseEventSink);
      sseEventSink.close();
      Assert.assertTrue(sseEventSink.isClosed());

      CountDownLatch countDownLatch = new CountDownLatch(3);
      sseBroadcasterImpl.onClose(ses -> {
         countDownLatch.countDown();
      });
      sseBroadcasterImpl.onError((ses, error) -> {
         countDownLatch.countDown();
      });
      sseBroadcasterImpl.onError((ses, error) -> {
         countDownLatch.countDown();
      });

      sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
      if (!countDownLatch.await(3, TimeUnit.SECONDS))
      {
         Assert.fail("All close and error listeners should have been notified");
      }
   }

   // We are expecting this test to invoke both close and error listeners when
   // event sink has been closed on client side (disconnected)
   @Test
   public void testCloseAndErrorListenersForDisconnectedEventSink() throws Exception
   {
      SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

      SseEventSink sseEventSink = newSseEventSink(new IOException());
      sseBroadcasterImpl.register(sseEventSink);
      Assert.assertFalse(sseEventSink.isClosed());

      CountDownLatch countDownLatch = new CountDownLatch(3);
      sseBroadcasterImpl.onClose(ses -> {
         countDownLatch.countDown();
      });
      sseBroadcasterImpl.onError((ses, error) -> {
         countDownLatch.countDown();
      });
      sseBroadcasterImpl.onError((ses, error) -> {
         countDownLatch.countDown();
      });

      sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
      if (!countDownLatch.await(3, TimeUnit.SECONDS))
      {
         Assert.fail("All close and error listeners should have been notified");
      }
   }

   // We are expecting this test to only invoke error listeners on broadcasting
   // error other than IOException
   @Test
   public void testErrorListeners() throws Exception
   {
      SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

      SseEventSink sseEventSink = newSseEventSink(new RuntimeException());
      sseBroadcasterImpl.register(sseEventSink);
      Assert.assertFalse(sseEventSink.isClosed());

      CountDownLatch countDownLatch = new CountDownLatch(2);
      sseBroadcasterImpl.onClose(ses -> {
         Assert.fail("Close listeners should not have been notified");
      });
      sseBroadcasterImpl.onError((ses, error) -> {
         countDownLatch.countDown();
      });
      sseBroadcasterImpl.onError((ses, error) -> {
         countDownLatch.countDown();
      });

      sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());
      if (!countDownLatch.await(5, TimeUnit.SECONDS))
      {
         Assert.fail("All error listeners should have been notified");
      }
   }

   private SseEventSink newSseEventSink()
   {
      return newSseEventSink(null);
   }

   private SseEventSink newSseEventSink(Throwable error)
   {
      return new SseEventSink()
      {

         private boolean closed;

         @Override
         public CompletionStage<?> send(OutboundSseEvent event)
         {
            if (closed)
            {
               throw new IllegalStateException();
            }
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            if (error == null)
            {
               completableFuture.complete(null);
            }
            else
            {
               completableFuture.completeExceptionally(error);
            }
            return completableFuture;
         }

         @Override
         public boolean isClosed()
         {
            return closed;
         }

         @Override
         public void close()
         {
            closed = true;
         }
      };
   }

}
