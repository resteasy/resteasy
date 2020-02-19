package org.jboss.resteasy.spi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


public class BlockingAsyncOutputStream extends AsyncOutputStream {

    private OutputStream outputStream;

    public BlockingAsyncOutputStream(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public CompletionStage<Void> asyncFlush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            CompletableFuture<Void> ret = new CompletableFuture<>();
            ret.completeExceptionally(e);
            return ret;
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length) {
        try {
            outputStream.write(bytes, offset, length);
        } catch (IOException e) {
            CompletableFuture<Void> ret = new CompletableFuture<>();
            ret.completeExceptionally(e);
            return ret;
        }
        return CompletableFuture.completedFuture(null);
    }

    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    public int hashCode() {
        return outputStream.hashCode();
    }

    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    public boolean equals(Object obj) {
        return outputStream.equals(obj);
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public void close() throws IOException {
        outputStream.close();
    }

    public String toString() {
        return outputStream.toString();
    }

}
