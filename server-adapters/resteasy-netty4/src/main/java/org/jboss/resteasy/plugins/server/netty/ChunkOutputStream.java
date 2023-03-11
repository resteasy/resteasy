package org.jboss.resteasy.plugins.server.netty;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.spi.AsyncOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpContent;

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
 *
 */
public class ChunkOutputStream extends AsyncOutputStream {
    private final Object writeLock = new Object();
    private final ByteBuf buffer;
    private final ChannelHandlerContext ctx;
    private final NettyHttpResponse response;

    ChunkOutputStream(final NettyHttpResponse response, final ChannelHandlerContext ctx, final int chunksize) {
        this.response = response;
        if (chunksize < 1) {
            throw new IllegalArgumentException(Messages.MESSAGES.chunkSizeMustBeAtLeastOne());
        }
        this.buffer = Unpooled.buffer(0, chunksize);
        this.ctx = ctx;
    }

    @Override
    public void write(int b) throws IOException {
        synchronized (writeLock) {
            if (buffer.maxWritableBytes() < 1) {
                flush();
            }
            buffer.writeByte(b);
        }
    }

    public void reset() {
        if (response.isCommitted())
            throw new IllegalStateException(Messages.MESSAGES.responseIsCommitted());
        synchronized (writeLock) {
            buffer.clear();
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        write(b, off, len, ctx.newPromise());
    }

    private void write(byte[] b, int off, int len, ChannelPromise promise) throws IOException {
        int dataLengthLeftToWrite = len;
        int dataToWriteOffset = off;
        int spaceLeftInCurrentChunk;
        MultiPromise mp = new MultiPromise(ctx, promise);
        synchronized (writeLock) {
            while ((spaceLeftInCurrentChunk = buffer.maxWritableBytes()) < dataLengthLeftToWrite) {
                buffer.writeBytes(b, dataToWriteOffset, spaceLeftInCurrentChunk);
                dataToWriteOffset = dataToWriteOffset + spaceLeftInCurrentChunk;
                dataLengthLeftToWrite = dataLengthLeftToWrite - spaceLeftInCurrentChunk;
                flush(mp.newPromise());
            }
            if (dataLengthLeftToWrite > 0) {
                buffer.writeBytes(b, dataToWriteOffset, dataLengthLeftToWrite);
                flush(mp.newPromise());
            }
        }
        mp.readyToForward();
    }

    @Override
    public void flush() throws IOException {
        flush(ctx.newPromise());
    }

    private void flush(ChannelPromise promise) throws IOException {
        synchronized (writeLock) {
            int readable = buffer.readableBytes();
            if (readable == 0) {
                promise.setSuccess();
                return;
            }
            if (!response.isCommitted())
                response.prepareChunkStream();
            ctx.writeAndFlush(new DefaultHttpContent(buffer.copy()), promise);
            buffer.clear();
        }
        super.flush();
    }

    @Override
    public CompletionStage<Void> asyncFlush() {
        CompletableFuture<Void> ret = new CompletableFuture<>();
        try {
            ChannelPromise promise = ctx.newPromise();
            promise.addListener(v -> {
                if (v.isSuccess())
                    ret.complete(null);
                else
                    ret.completeExceptionally(v.cause());
            });
            flush(promise);
        } catch (IOException e) {
            ret.completeExceptionally(e);
        }
        return ret;
    }

    @Override
    public CompletionStage<Void> asyncWrite(byte[] bytes, int offset, int length) {
        CompletableFuture<Void> ret = new CompletableFuture<>();
        try {
            ChannelPromise promise = ctx.newPromise();
            promise.addListener(v -> {
                if (v.isSuccess())
                    ret.complete(null);
                else
                    ret.completeExceptionally(v.cause());
            });
            write(bytes, offset, length, promise);
        } catch (IOException e) {
            ret.completeExceptionally(e);
        }
        return ret;
    }
}
