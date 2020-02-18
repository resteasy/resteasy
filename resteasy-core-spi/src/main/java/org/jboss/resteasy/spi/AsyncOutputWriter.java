package org.jboss.resteasy.spi;

import java.nio.charset.Charset;
import java.util.concurrent.CompletionStage;

/**
 * Utility class to write to an {@link AsyncOutputStream} using a given {@link Charset}.
 */
public class AsyncOutputWriter {

    private AsyncOutputStream asyncOutputStream;
    private Charset charset;

    /**
     * Creates a new async writer using the default charset.
     * @param asyncOutputStream the async output stream on which to write
     */
    public AsyncOutputWriter(final AsyncOutputStream asyncOutputStream) {
        this(asyncOutputStream, Charset.defaultCharset());
    }

    /**
     * Creates a new async writer using the specified charset.
     * @param asyncOutputStream the async output stream on which to write
     * @param charset the charset to use
     */
    public AsyncOutputWriter(final AsyncOutputStream asyncOutputStream, final Charset charset) {
        this.asyncOutputStream = asyncOutputStream;
        this.charset = charset;
    }

    /**
     * Writes the given string to the underlying async output stream.
     * @param string the string to write, using the specified charset
     * @return a {@link CompletionStage} indicating completion.
     */
    public CompletionStage<Void> asyncWrite(String s){
        return asyncOutputStream.asyncWrite(s.getBytes(charset));
    }

    /**
     * Flushes the underlying async output stream.
     * @return a {@link CompletionStage} indicating completion.
     */
    public CompletionStage<Void> asyncFlush(){
        return asyncOutputStream.asyncFlush();
    }
}
