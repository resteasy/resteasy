package org.jboss.resteasy.spi;

import java.io.OutputStream;
import java.util.concurrent.CompletionStage;

public abstract class AsyncOutputStream extends OutputStream {
    public abstract CompletionStage<Void> rxFlush();
    public abstract CompletionStage<Void> rxWrite(byte[] bytes);
}
