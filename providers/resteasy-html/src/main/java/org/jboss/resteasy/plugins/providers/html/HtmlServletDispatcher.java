package org.jboss.resteasy.plugins.providers.html;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.HttpServletResponseWrapper;
import org.jboss.resteasy.spi.HttpResponse;

public class HtmlServletDispatcher extends HttpServletDispatcher {

    private static final long serialVersionUID = 3793362217679129985L;

    @Override
    protected HttpResponse createServletResponse(HttpServletResponse response, HttpServletRequest request) {
        return new HttpServletResponseWrapper(response, request, getDispatcher().getProviderFactory()) {

            protected OutputStream getSuperOuptutStream() throws IOException {
                return super.getOutputStream();
            }

            public OutputStream getOutputStream() throws IOException {
                return new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        getSuperOuptutStream().write(b);
                    }

                    @Override
                    public void write(byte[] b) throws IOException {
                        getSuperOuptutStream().write(b);
                    }

                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        getSuperOuptutStream().write(b, off, len);
                    }

                    @Override
                    public void flush() throws IOException {
                        getSuperOuptutStream().flush();
                    }

                    @Override
                    public void close() throws IOException {
                        getSuperOuptutStream().close();
                    }

                };
            }
        };
    }
}
