package org.jboss.resteasy.spi;

import java.io.OutputStream;
import java.util.concurrent.CompletionStage;

public abstract class AsyncOutputStream extends OutputStream {
    public abstract CompletionStage<Void> asyncFlush();

    public CompletionStage<Void> asyncWrite(byte[] bytes) {
        return asyncWrite(bytes, 0, bytes.length);
    }

    public abstract CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length);
}
