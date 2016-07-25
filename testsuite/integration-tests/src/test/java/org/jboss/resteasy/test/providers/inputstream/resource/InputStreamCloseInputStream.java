package org.jboss.resteasy.test.providers.inputstream.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class InputStreamCloseInputStream extends ByteArrayInputStream {
    private boolean closed;

    public InputStreamCloseInputStream(final byte[] b) {
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
