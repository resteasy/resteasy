package org.jboss.resteasy.plugins.server.resourcefactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * Used for component jndi-based resources like enterprise beans.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JndiComponentResourceFactory implements ResourceFactory {
    private String jndiName;
    private InitialContext ctx;
    private volatile Object reference;
    private Class<?> scannable;
    private boolean cache;

    public JndiComponentResourceFactory(final String jndiName, final Class<?> scannable, final boolean cacheReference) {
        this.jndiName = jndiName;
        this.scannable = scannable;
        this.cache = cacheReference;
        try {
            ctx = new InitialContext();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void registered(ResteasyProviderFactory factory) {
    }

    public Object createResource(HttpRequest request, HttpResponse response, ResteasyProviderFactory factory) {
        Object ref = reference;
        if (ref == null) {
            try {
                ref = ctx.lookup(jndiName);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
            if (cache) {
                synchronized (this) {
                    reference = ref;
                }
            }
        }
        return ref;
    }

    public void unregistered() {
    }

    public Class<?> getScannableClass() {
        return scannable;
    }

    public void requestFinished(HttpRequest request, HttpResponse response, Object resource) {
    }
}
