package org.resteasy.mock;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Delegating implementation of {@link javax.servlet.ServletOutputStream}.
 * <p/>
 * <p>Used by {@link org.springframework.mock.web.MockHttpServletResponse}; typically not directly
 * used for testing application controllers.
 *
 * @author Juergen Hoeller
 * @see org.springframework.mock.web.MockHttpServletResponse
 * @since 1.0.2
 */
public class DelegatingServletOutputStream extends ServletOutputStream {

    private final OutputStream targetStream;


    /**
     * Create a DelegatingServletOutputStream for the given target stream.
     *
     * @param targetStream the target stream (never <code>null</code>)
     */
    public DelegatingServletOutputStream(OutputStream targetStream) {
        Assert.notNull(targetStream, "Target OutputStream must not be null");
        this.targetStream = targetStream;
    }

    /**
     * Return the underlying target stream (never <code>null</code>).
     */
    public final OutputStream getTargetStream() {
        return this.targetStream;
    }


    public void write(int b) throws IOException {
        this.targetStream.write(b);
    }

    public void flush() throws IOException {
        super.flush();
        this.targetStream.flush();
    }

    public void close() throws IOException {
        super.close();
        this.targetStream.close();
	}

}
