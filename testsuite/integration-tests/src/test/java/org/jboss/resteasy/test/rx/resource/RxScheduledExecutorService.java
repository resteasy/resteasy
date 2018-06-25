package org.jboss.resteasy.test.rx.resource;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class RxScheduledExecutorService implements ScheduledExecutorService {
   
   public static class DaemonThreadFactory implements ThreadFactory
   {
      private static final AtomicInteger poolNumber = new AtomicInteger(1);
      private final ThreadGroup group;
      private final AtomicInteger threadNumber = new AtomicInteger(1);
      private final String namePrefix;

      DaemonThreadFactory()
      {
         SecurityManager s = System.getSecurityManager();
         group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         namePrefix = "resteasy-sse-eventsource" + poolNumber.getAndIncrement() + "-thread-";
      }

      public Thread newThread(Runnable r)
      {
         Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
         t.setDaemon(true);
         return t;
      }
   }

   private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
   static public boolean used;

   @Override
   public void shutdown() {
      used = true;
      executor.shutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      used = true;
      return executor.shutdownNow();
   }

   @Override
   public boolean isShutdown() {
      used = true;
      return executor.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      used = true;
      return executor.isTerminated();
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      used = true;
      return executor.awaitTermination(timeout, unit);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      used = true;
      return executor.submit(task);
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      used = true;
      return executor.submit(task, result);
   }

   @Override
   public Future<?> submit(Runnable task) {
      used = true;
      return executor.submit(task);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      used = true;
      return executor.invokeAll(tasks);
   }

   @Override
   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException {
      used = true;
      return executor.invokeAll(tasks, timeout, unit);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException {
      used = true;
      return executor.invokeAny(tasks);
   }

   @Override
   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
      used = true;
      return executor.invokeAny(tasks, timeout, unit);
   }

   @Override
   public void execute(Runnable command) {
      used = true;
      executor.execute(command);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      used = true;
      return executor.schedule(command, delay, unit);
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      used = true;
      return executor.schedule(callable, delay, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      used = true;
      return executor.scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      used = true;
      return executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
   }
}
