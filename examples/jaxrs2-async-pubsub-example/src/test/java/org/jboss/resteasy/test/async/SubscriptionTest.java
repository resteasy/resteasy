package org.jboss.resteasy.test.async;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SubscriptionTest
{
   @Test
   public void testIt() throws Exception
   {
      ExecutorService executor = Executors.newCachedThreadPool();

      final int INCREMENT = 10;

      for (int pass = 0; pass < 5; pass++)
      {
         for (int i = 0; i < INCREMENT; i++)
         {
            Runnable runnable = create(pass * INCREMENT + i);
            executor.execute(runnable);
         }
         int iterations = 3;
         int threads = INCREMENT + pass * INCREMENT;
         execute(threads, iterations);
         System.out.println();
      }
   }

   private AtomicReference<CountDownLatch> countdown = new AtomicReference<CountDownLatch>();
   private AtomicLong counter = new AtomicLong();


   private Runnable create(int subid)
   {
      Client client = ClientBuilder.newClient();
      Response response = client.target("http://localhost:8080/subscribers")
              .request()
              .post(Entity.form(new Form().param("name", Integer.toString(subid))));
      response.close();
      if (response.getStatus() != 201)
      {
         throw new RuntimeException("Failure: " + response.getStatus());
      }
      final WebTarget target = client.target("http://localhost:8080/subscribers/").path(Integer.toString(subid));
      Runnable t = new Runnable()
      {
         @Override
         public void run()
         {
            for (; ; )
            {
               try
               {

                  target.request().get(String.class);
                  counter.incrementAndGet();
               }
               catch (Exception ex)
               {
                  //ex.printStackTrace();
               }
               finally
               {
                  countdown.get().countDown();
               }
            }
         }
      };
      return t;

   }

   private void execute(int NUM_THREADS, final int ITERATIONS) throws InterruptedException
   {
      counter.set(0);
      countdown.set(new CountDownLatch(NUM_THREADS * ITERATIONS));
      long start = 0;
      start = System.currentTimeMillis();
      System.out.println("***** Threads: " + NUM_THREADS + " Iterations: " + ITERATIONS);

      long start2 = System.currentTimeMillis();

      Client client = ClientBuilder.newClient();
      try
      {
         for (int i = 0; i < ITERATIONS; i++)
         {
            Response response = client.target("http://localhost:8080/subscription").request().post(Entity.text(Integer.toString(i)));
            Assert.assertEquals(204, response.getStatus());
            response.close();
         }
         countdown.get().await();
         System.out.println("--- total failures: " + (NUM_THREADS * ITERATIONS - counter.get()));
         System.out.println("--- Post time took: " + (System.currentTimeMillis() - start2));
      }
      finally
      {
         client.close();
      }
   }
}

