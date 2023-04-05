package org.jboss.resteasy.grpc.runtime.servlet;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

public class MockServletInputStream extends ServletInputStream {
    private InputStream is;

    public MockServletInputStream(final InputStream is) {
        this.is = is;
    }

    @Override
    public boolean isFinished() {
        try {
            return is.available() > 0;
        } catch (IOException e) {
            return true;
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }
}
