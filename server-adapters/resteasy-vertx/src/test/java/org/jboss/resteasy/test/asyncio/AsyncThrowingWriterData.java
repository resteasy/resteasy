package org.jboss.resteasy.test.asyncio;

public class AsyncThrowingWriterData {
    public boolean throwNow;

    public AsyncThrowingWriterData(final boolean throwNow) {
        this.throwNow = throwNow;
    }
}
