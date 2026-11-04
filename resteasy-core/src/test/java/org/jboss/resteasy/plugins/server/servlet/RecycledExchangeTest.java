package org.jboss.resteasy.plugins.server.servlet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.junit.jupiter.api.Test;

/**
 * When an async request is resumed after the client has disconnected, a servlet container may
 * recycle the exchange before RESTEasy finishes delivering the response (Jetty ee11 does this).
 * Both {@link HttpServletResponse#isCommitted()} and {@link AsyncContext#complete()} then throw
 * {@link IllegalStateException}. The servlet plugin must treat a recycled exchange as finished
 * instead of raising an unhandled asynchronous exception and logging a spurious 500.
 */
public class RecycledExchangeTest {

    private HttpServletResponseWrapper wrap(final HttpServletResponse response) {
        return new HttpServletResponseWrapper(response, mock(HttpServletRequest.class),
                new ResteasyProviderFactoryImpl());
    }

    @Test
    public void isCommittedReportsRecycledExchangeAsCommitted() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.isCommitted())
                .thenThrow(new IllegalStateException("Request/Response does not exist (likely recycled)"));

        assertTrue(wrap(response).isCommitted());
    }

    @Test
    public void isCommittedDelegatesForLiveExchange() {
        HttpServletResponse committed = mock(HttpServletResponse.class);
        when(committed.isCommitted()).thenReturn(true);
        assertTrue(wrap(committed).isCommitted());

        HttpServletResponse open = mock(HttpServletResponse.class);
        when(open.isCommitted()).thenReturn(false);
        assertFalse(wrap(open).isCommitted());
    }

    @Test
    public void completeIgnoresRecycledExchange() {
        AsyncContext asyncContext = mock(AsyncContext.class);
        doThrow(new IllegalStateException("AsyncContext completed and/or Request lifecycle recycled"))
                .when(asyncContext).complete();

        Servlet3AsyncHttpRequest.completeRecycleSafe(asyncContext);

        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void completeDelegatesForLiveExchange() {
        AsyncContext asyncContext = mock(AsyncContext.class);
        Servlet3AsyncHttpRequest.completeRecycleSafe(asyncContext);
        verify(asyncContext, times(1)).complete();
    }

    @Test
    public void completeDoesNotSwallowUnrelatedFailures() {
        AsyncContext asyncContext = mock(AsyncContext.class);
        RuntimeException boom = new RuntimeException("write failed");
        doThrow(boom).when(asyncContext).complete();

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> Servlet3AsyncHttpRequest.completeRecycleSafe(asyncContext));
        assertSame(boom, thrown);
    }
}
