package org.jboss.resteasy.test.undertow;

public class AsyncWriterData {

    public final boolean expectOnIoThread;
    public final boolean simulateSlowIo;
    public final Thread requestThread;

    public AsyncWriterData(final boolean expectOnIoThread, final boolean simulateSlowIo) {
        this.expectOnIoThread = expectOnIoThread;
        this.simulateSlowIo = simulateSlowIo;
        this.requestThread = Thread.currentThread();
    }

}
