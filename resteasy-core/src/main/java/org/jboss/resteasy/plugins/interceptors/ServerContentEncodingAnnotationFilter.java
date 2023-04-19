package org.jboss.resteasy.plugins.interceptors;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.spi.AsyncWriterInterceptor;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.util.AcceptParser;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
@Priority(Priorities.HEADER_DECORATOR)
public class ServerContentEncodingAnnotationFilter implements AsyncWriterInterceptor {
    protected @Context HttpRequest request;

    Set<String> encodings;

    public ServerContentEncodingAnnotationFilter(final Set<String> encodings) {
        this.encodings = encodings;
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        setHeader(context.getHeaders());
        context.proceed();
    }

    private void setHeader(MultivaluedMap<String, Object> headers) {
        List<String> acceptEncoding = request.getHttpHeaders().getRequestHeaders().get(HttpHeaders.ACCEPT_ENCODING);
        if (acceptEncoding != null) {
            StringBuffer buf = new StringBuffer();
            for (String accept : acceptEncoding) {
                if (buf.length() > 0)
                    buf.append(",");
                buf.append(accept);
            }
            List<String> accepts = AcceptParser.parseAcceptHeader(buf.toString());
            for (String encoding : accepts) {
                if (encodings.contains(encoding.toLowerCase())) {
                    headers.putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
                    break;
                }
            }
        }
    }

    @Override
    public CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context) {
        setHeader(context.getHeaders());
        return context.asyncProceed();
    }
}
