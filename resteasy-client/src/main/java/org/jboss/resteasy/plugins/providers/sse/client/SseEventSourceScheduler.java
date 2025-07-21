package org.jboss.resteasy.plugins.providers.sse.client;

import java.util.concurrent.Phaser;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.resteasy.concurrent.ContextualExecutors;
import org.jboss.resteasy.concurrent.ContextualScheduledExecutorService;

/**
 *
 * @author Nicolas NESMON
 *
 */
class SseEventSourceScheduler {

    private static class DaemonThreadFactory implements ThreadFactory {

        private final ThreadGroup group;

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        DaemonThreadFactory(final String name) {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = name + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            t.setDaemon(true);
            return t;
        }
    }

    private final ContextualScheduledExecutorService scheduledExecutorService;

    private final Phaser phaser;

    private final AtomicBoolean closed;

    SseEventSourceScheduler(final ScheduledExecutorService scheduledExecutorService, final String threadName) {
        this.scheduledExecutorService = scheduledExecutorService == null
                ? ContextualExecutors.scheduledThreadPool(1, new DaemonThreadFactory(threadName))
                : ContextualExecutors.wrap(scheduledExecutorService, true);
        this.phaser = new Phaser(1);
        this.closed = new AtomicBoolean(false);
    }

    void schedule(final Runnable runnable, long delay, TimeUnit unit) throws RejectedExecutionException {
        if (this.closed.get()) {
            return;
        }
        try {
            this.scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    if (SseEventSourceScheduler.this.closed.get()) {
                        return;
                    }
                    int registrationPhase = SseEventSourceScheduler.this.phaser.register();
                    try {
                        // If this phaser is terminated (registrationPhase < 0)
                        // (no more registered parties) or the current
                        // registration phase is other than 0 it means that
                        // shutdownNow has been called already.
                        if (registrationPhase != 0) {
                            return;
                        }
                        runnable.run();
                    } finally {
                        // We can invoke 'arriveAndDeregister()' safely since
                        // this method has no effect if the phaser is already
                        // terminated.
                        SseEventSourceScheduler.this.phaser.arriveAndDeregister();
                    }
                }
            }, delay, unit);
        } catch (RejectedExecutionException e) {
            if (!scheduledExecutorService.isManaged() && this.closed.get()) {
                // At this stage the RejectedExecutionException can be either a
                // normal consequence of the
                // 'scheduledExecutorService.shutdownNow(...)' method
                // invocation and in this case it's not an error at all, or a
                // real error.
                // So instead of throwing exception that may not be an
                // unexpected error at all, it is acceptable to do nothing since
                // user already asked for shutdown.
                return;
            }
            throw e;
        }
    }

    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (!this.closed.get()) {
            return false;
        }
        try {
            this.phaser.awaitAdvanceInterruptibly(0, timeout, unit);
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    void shutdownNow() {
        if (this.closed.compareAndSet(false, true)) {
            this.phaser.arriveAndDeregister();
            if (!scheduledExecutorService.isManaged()) {
                this.scheduledExecutorService.shutdownNow();
            }
        }
    }

}
