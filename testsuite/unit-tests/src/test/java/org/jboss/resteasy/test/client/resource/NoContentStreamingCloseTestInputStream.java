package org.jboss.resteasy.test.client.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NoContentStreamingCloseTestInputStream extends ByteArrayInputStream {
    private boolean closed;

    public NoContentStreamingCloseTestInputStream(final byte[] b) {
        super(b);
    }

    public void close() throws IOException {
        super.close();
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }
}
