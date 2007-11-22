package org.resteasy.mock;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Delegating implementation of {@link javax.servlet.ServletInputStream}.
 * <p/>
 * <p>Used by {@link org.springframework.mock.web.MockHttpServletRequest}; typically not directly
 * used for testing application controllers.
 *
 * @author Juergen Hoeller
 * @see org.springframework.mock.web.MockHttpServletRequest
 * @since 1.0.2
 */
public class DelegatingServletInputStream extends ServletInputStream {

    private final InputStream sourceStream;


    /**
     * Create a DelegatingServletInputStream for the given source stream.
     *
     * @param sourceStream the source stream (never <code>null</code>)
     */
    public DelegatingServletInputStream(InputStream sourceStream) {
        Assert.notNull(sourceStream, "Source InputStream must not be null");
        this.sourceStream = sourceStream;
    }

    /**
     * Return the underlying source stream (never <code>null</code>).
     */
    public final InputStream getSourceStream() {
        return this.sourceStream;
    }


    public int read() throws IOException {
        return this.sourceStream.read();
    }

    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
	}

}
