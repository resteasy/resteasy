package org.jboss.resteasy.spi;

import java.nio.charset.Charset;
import java.util.concurrent.CompletionStage;

public class AsyncOutputWriter {

    private AsyncOutputStream asyncOutputStream;
    private Charset charset;

    public AsyncOutputWriter(final AsyncOutputStream asyncOutputStream) {
        this.asyncOutputStream = asyncOutputStream;
        this.charset = Charset.defaultCharset();
    }

    public CompletionStage<Void> rxWrite(String s){
        return asyncOutputStream.rxWrite(s.getBytes(charset));
    }

    public CompletionStage<Void> rxFlush(){
        return asyncOutputStream.rxFlush();
    }
}
