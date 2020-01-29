package org.jboss.resteasy.spi;

import java.io.OutputStream;
import java.util.concurrent.CompletionStage;

public abstract class AsyncOutputStream extends OutputStream {
    public abstract CompletionStage<Void> rxFlush();

    public CompletionStage<Void> rxWrite(byte[] bytes) {
        return rxWrite(bytes, 0, bytes.length);
    }

    public abstract CompletionStage<Void> rxWrite(byte[] bytes, int offset, int length);
}
