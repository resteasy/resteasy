package org.jboss.resteasy.test.providers.sse;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.SseBroadcasterImpl;
import org.jboss.resteasy.plugins.providers.sse.SseEventOutputImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.SseEventSink;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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

      AtomicBoolean onCloseListenerInvoked = new AtomicBoolean(false);
      sseBroadcasterImpl.onClose(ses -> {
         onCloseListenerInvoked.set(true);
      });

      CountDownLatch countDownLatch = new CountDownLatch(2);
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
      if (onCloseListenerInvoked.get())
      {
         Assert.fail("Close listeners should not have been notified");
      }
   }

   @Before
   public void before(){
      HttpRequest request = mock(HttpRequest.class);
      ResteasyAsynchronousContext resteasyAsynchronousContext = mock(ResteasyAsynchronousContext.class);
      doReturn(resteasyAsynchronousContext).when(request).getAsyncContext();

      //prevent NPE in SseEventOutputImpl ctr
      ResteasyContext.pushContext(org.jboss.resteasy.spi.HttpRequest.class, request);
   }

   @Test
   public void testRemoveDisconnectedEventSink() throws Exception {
      SseBroadcasterImpl sseBroadcasterImpl = new SseBroadcasterImpl();

      final ConcurrentLinkedQueue<SseEventSink> outputQueue = getOutputQueue(sseBroadcasterImpl);
      CountDownLatch countDownLatch = new CountDownLatch(2);

      //we want to test against actual SseEventOutputImpl
      final SseEventSink sseEventSink1 = new SseEventOutputImpl(null);
      final SseEventSink sseEventSink2 = new SseEventOutputImpl(null);


      sseBroadcasterImpl.register(sseEventSink1);
      sseBroadcasterImpl.register(sseEventSink2);


      sseBroadcasterImpl.onClose(ses -> {
         countDownLatch.countDown();
      });

      sseBroadcasterImpl.onError((ses, error) -> {
         //error is an NPE thrown by SseEventOutputImpl#send
         countDownLatch.countDown();
      });

      sseEventSink2.close();

      sseBroadcasterImpl.broadcast(new OutboundSseEventImpl.BuilderImpl().data("Test").build());

      if (!countDownLatch.await(5, TimeUnit.SECONDS))
      {
         fail("All close listeners should have been notified");
      } else {
         Assert.assertTrue(outputQueue.size() == 1);
         Assert.assertSame(outputQueue.peek(), sseEventSink1);
      }
   }

   @SuppressWarnings("unchecked")
   private ConcurrentLinkedQueue<SseEventSink> getOutputQueue(SseBroadcasterImpl sseBroadcasterImpl) throws NoSuchFieldException, IllegalAccessException {
      Field fld = SseBroadcasterImpl.class.getDeclaredField("outputQueue");
      fld.setAccessible(true);
      return (ConcurrentLinkedQueue<SseEventSink>) fld.get(sseBroadcasterImpl);
   }

   @org.junit.After
   public void after(){
      //revert contextual data
      ResteasyContext.pushContext(org.jboss.resteasy.spi.HttpRequest.class, null);
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
