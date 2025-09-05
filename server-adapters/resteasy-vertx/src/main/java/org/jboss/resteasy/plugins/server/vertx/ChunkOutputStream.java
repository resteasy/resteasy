package org.jboss.resteasy.plugins.server.vertx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.plugins.server.vertx.i18n.Messages;
import org.jboss.resteasy.spi.AsyncOutputStream;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;

/**
 * Class to help application that are built to write to an
 * OutputStream to chunk the content
 *
 * <pre>
 * {@code
 * DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
 * HttpHeaders.setTransferEncodingChunked(response);
 * response.headers().set(CONTENT_TYPE, "application/octet-stream");
 * //other headers
 * ctx.write(response);
 * // code of the application that use the ChunkOutputStream
 * // Don't forget to close the ChunkOutputStream after use!
 * ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT).addListener(ChannelFutureListener.CLOSE);
 * }
 * </pre>
 *
 * @author tbussier
 * @deprecated use new dependencies
 */
@Deprecated(forRemoval = true, since = "6.2.13.Final")
public class ChunkOutputStream extends AsyncOutputStream {
    private Buffer buffer;
    private final VertxHttpResponse response;
    private final int chunkSize;

    ChunkOutputStream(final VertxHttpResponse response, final int chunksize) {
        this.response = response;
        if (chunksize < 1) {
            throw new IllegalArgumentException(Messages.MESSAGES.chunkSizeMustBeAtLeastOne());
        }
        this.chunkSize = chunksize;
        this.buffer = Buffer.buffer(chunksize);
    }

    @Override
    public void write(int b) throws IOException {
        if (buffer.length() >= chunkSize - 1) {
            flush();
        }
        buffer.appendByte((byte) b);
    }

    public void reset() {
        if (response.isCommitted())
            throw new IllegalStateException(Messages.MESSAGES.responseIsCommitted());
        buffer = Buffer.buffer(chunkSize);
    }

    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        write(b, off, len, null);
    }

    private void write(byte[] b, int off, int len, Handler<AsyncResult<CompositeFuture>> handler) throws IOException {
        int dataLengthLeftToWrite = len;
        int dataToWriteOffset = off;
        int spaceLeftInCurrentChunk;
        List<Future> futures;
        if (handler != null) {
            futures = new ArrayList<>();
        } else {
            futures = null;
        }
        while ((spaceLeftInCurrentChunk = chunkSize - buffer.length()) < dataLengthLeftToWrite) {
            buffer.appendBytes(b, dataToWriteOffset, spaceLeftInCurrentChunk);
            dataToWriteOffset = dataToWriteOffset + spaceLeftInCurrentChunk;
            dataLengthLeftToWrite = dataLengthLeftToWrite - spaceLeftInCurrentChunk;
            Promise<Void> promise;
            if (handler != null) {
                promise = Promise.promise();
                futures.add(promise.future());
            } else {
                promise = null;
            }
            flush(promise);
        }
        if (dataLengthLeftToWrite > 0) {
            buffer.appendBytes(b, dataToWriteOffset, dataLengthLeftToWrite);
        }
        if (handler != null) {
            CompositeFuture.all(futures).onComplete(handler);
        }
    }

    @Override
    public void flush() throws IOException {
        flush(null);
    }

    private void flush(Handler<AsyncResult<Void>> handler) throws IOException {
        int readable = buffer.length();
        if (readable == 0) {
            if (handler != null)
                handler.handle(Future.succeededFuture());
            return;
        }
        if (!response.isCommitted())
            response.prepareChunkStream();
        response.checkException();
        response.response.write(buffer, handler);
        buffer = Buffer.buffer();
        super.flush();
    }

    @Override
    public CompletionStage<Void> asyncFlush() {
        CompletableFuture<Void> ret = new CompletableFuture<>();
        try {
            flush(res -> {
                if (res.succeeded())
                    ret.complete(null);
                else
                    ret.completeExceptionally(res.cause());
            });
        } catch (IOException e) {
            ret.completeExceptionally(e);
        }
        return ret;
    }

    @Override
    public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length) {
        CompletableFuture<Void> ret = new CompletableFuture<>();
        try {
            write(bytes, offset, length, res -> {
                if (res.succeeded())
                    ret.complete(null);
                else
                    ret.completeExceptionally(res.cause());
            });
        } catch (IOException e) {
            ret.completeExceptionally(e);
        }
        return ret;
    }

}
