package org.jboss.resteasy.grpc.runtime.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

public class MockServletOutputStream extends ServletOutputStream {
    protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private boolean closed;

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    @Override
    public void write(int b) throws IOException {
        baos.write(b);
    }

    public ByteArrayOutputStream getDelegate() {
        return baos;
    }

    public void close() throws IOException {
        super.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
