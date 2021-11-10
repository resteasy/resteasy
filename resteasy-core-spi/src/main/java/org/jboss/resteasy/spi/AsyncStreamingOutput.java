package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.core.Response;

/**
 * A type that may be used as a resource method return value or as the entity
 * in a {@link Response} when the application wishes to stream the output.
 * This is a lightweight alternative to a
 * {@link AsyncMessageBodyWriter}.
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see AsyncMessageBodyWriter
 * @see jakarta.ws.rs.core.Response
 */
public interface AsyncStreamingOutput {

    /**
     * Called to write the message body.
     *
     * @param output the OutputStream to write to.
     * @throws java.io.IOException if an IO error is encountered
     * @throws jakarta.ws.rs.WebApplicationException
     *                             if a specific
     *                             HTTP error response needs to be produced. Only effective if thrown prior
     *                             to any bytes being written to output.
     */
    CompletionStage<Void> asyncWrite(AsyncOutputStream output);
}
