import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Provider
public class FormDataInterceptor implements ReaderInterceptor {

    public static final String BYPASS_HEADER = "X-RESTEASY-BYPASS";

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {

        if (context.getHeaders().getFirst(BYPASS_HEADER) != null) {
            return context.proceed();
        } else {
            MultivaluedMap<String, String> form = new MultivaluedMapImpl<String, String>();
            String key = "INTERCEPTOR";
            form.add(key, "X");
            form.add(key, "Y");
            return form;
        }
    }
}

