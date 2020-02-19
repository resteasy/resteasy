package org.jboss.resteasy.test.undertow;

public class BlockingWriterData {

    public Thread requestThread;

    public BlockingWriterData() {
        requestThread = Thread.currentThread();
    }
}
